package factors.discrete;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import factors.Factor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static util.ListOps.zip;
import static util.Misc.joinPair;

/**
 * Implementation of a discrete factor. This implementation keeps track of the
 * scope, cardinality, and values of a factor as a basic representation of
 * a joint probability table or conditional probability distribution.
 *
 * @see factors.Factor
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
public class DiscreteFactor implements Factor {
  private List<String> variables;
  private List<Integer> cardinality;
  private double[] values;
  private int size;

  private ListMultimap<String, Integer> assignmentToIdx;
  private Map<String, Integer> rangeSize;

  public DiscreteFactor(String[] variables, int[] cardinality, double[] values) {
    this.setVariables(variables);
    this.setCardinality(cardinality);
    this.setValues(values);
  }

  public Factor copy() {
    return new DiscreteFactor(this.getScope(), this.getCardinality(),
        this.getValues());
  }

  private void setVariables(String[] variables) {
    this.variables = Lists.newArrayList(variables);
  }

  @Override public String[] getScope() {
    return this.variables.stream().toArray(String[]::new);
  }

  private void setCardinality(int[] cardinality) {
    Preconditions.checkArgument(this.variables.size() == cardinality.length,
        "variables and cardinality must have the same size.");

    this.cardinality = Ints.asList(cardinality);
    this.rangeSize = this.getRangeSize(this.variables, this.cardinality);
    this.size = this.rangeSize.get(this.variables.get(0)) * this.cardinality.get(0);
    this.assignmentToIdx = this.createAssignments(this.variables,
        this.cardinality,
        this.rangeSize,
        this.size);
  }

  public int[] getCardinality() {
    return Ints.toArray(this.cardinality);
  }

  private ListMultimap<String, Integer> createAssignments(List<String> variables,
      List<Integer> cardinality, Map<String, Integer> rangeSize, int size) {
    ListMultimap<String, Integer> assignmentToIdx = ArrayListMultimap.create();

    for(int i = 0;i < size;++i) {
      for (Pair<String, Integer> varCardPair : zip(variables, cardinality)) {
        String variable = varCardPair.getLeft();
        Integer card = varCardPair.getRight();
        int assignment = (i / rangeSize.get(variable)) % card;

        assignmentToIdx.put(joinPair(variable, assignment, "="), i);
      }
    }

    return assignmentToIdx;
  }

  private void setValues(double[] values) {
    Preconditions.checkArgument(values.length == this.size,
          String.format("Incorrect size of values variables. Expecting array " +
          "of size %d. Instead received array of size %d.",
              this.size, values.length));
    this.values = Arrays.copyOf(values, values.length);
  }

  private HashMap<String, Integer> getRangeSize(List<String> variables,
      List<Integer> cardinality) {
    HashMap<String, Integer> rangeSize = new HashMap<>();
    AtomicInteger prod = new AtomicInteger(1);
    List<Integer> ranges = Lists.reverse(Lists.reverse(cardinality).stream()
        .mapToInt(c -> prod.getAndUpdate(p -> c * p))
        .boxed()
        .collect(Collectors.toList()));

    IntStream.range(0, variables.size())
        .forEach(i -> rangeSize.put(this.variables.get(i), ranges.get(i)));

    return rangeSize;
  }

  public double[] getValues() {
    return Arrays.copyOf(this.values, this.values.length);
  }

  @Override public Factor normalize(boolean inPlace) {
    DiscreteFactor result = inPlace ? this : (DiscreteFactor)this.copy();

    double sum = Arrays.stream(result.getValues()).sum();
    result.setValues(Doubles.asList(this.values).stream()
        .mapToDouble(v -> v / sum)
        .toArray());

    return result;
  }

  @Override public Factor reduce(List<Pair<String, Integer>> variables,
      boolean inPlace) {
    HashSet<String> rVars = new HashSet<>();
    HashSet<Integer> rVarIdxs = new HashSet<>();
    HashSet reducedIdxs = new HashSet();
    for(Pair<String, Integer> varAss : variables) {
      String variable = varAss.getLeft();
      String assignment = joinPair(varAss, "=");
      int vIdx = this.variables.indexOf(variable);

      rVars.add(variable);
      rVarIdxs.add(vIdx);
      List<Integer> assignedIdxs = this.assignmentToIdx.get(assignment);
      if(reducedIdxs.isEmpty()) {
        reducedIdxs.addAll(assignedIdxs);
      } else {
        reducedIdxs.retainAll(Sets.intersection(reducedIdxs,
            Sets.newHashSet(assignedIdxs)));
      }
    }

    String[] newScope = this.variables.stream()
        .filter(v -> !rVars.contains(v))
        .toArray(String[]::new);

    int[] newCardinality = Arrays.asList(newScope).stream()
        .mapToInt(v -> this.cardinality.get(this.variables.indexOf(v)))
        .toArray();

    double[] newValues = reducedIdxs.stream()
        .sorted()
        .mapToDouble(i -> this.values[(int) i])
        .toArray();

    DiscreteFactor result = inPlace ? this : (DiscreteFactor)this.copy();
    result.setVariables(newScope);
    result.setCardinality(newCardinality);
    result.setValues(newValues);

    return result;
  }

  @Override public Factor marginalize(String[] variables, boolean inPlace) {
    HashSet<String> mVars = Sets.newHashSet(variables);
    List<String> newScope = this.variables.stream()
        .filter(v -> !mVars.contains(v))
        .collect(Collectors.toList());

    List<Integer> newCardinality = newScope.stream()
        .mapToInt(v -> this.cardinality.get(this.variables.indexOf(v)))
        .boxed()
        .collect(Collectors.toList());

    HashMap<String, Integer> newRangeSizes =
        this.getRangeSize(newScope, newCardinality);

    int newSize = newCardinality.stream()
        .reduce(1, (a, b) -> a * b);

    double[] newValues = new double[newSize];
    ListMultimap<String, Integer> newAtoI =
        this.createAssignments(newScope, newCardinality, newRangeSizes, newSize);

    TreeMultimap<Integer, String> invertedMap = Multimaps.invertFrom(newAtoI,
        TreeMultimap.create());

    for(int i = 0;i < newSize;++i) {
      Set<Integer> oldIdxs = new HashSet<>();
      for(String assignment : invertedMap.get(i)) {
        List<Integer> assignedIdxs = this.assignmentToIdx.get(assignment);
        if(oldIdxs.isEmpty()) {
          oldIdxs.addAll(assignedIdxs);
        } else {
          oldIdxs.retainAll(Sets.intersection(oldIdxs,
              Sets.newHashSet(assignedIdxs)));
        }
      }
      newValues[i] = oldIdxs.stream()
          .mapToDouble(idx -> this.values[idx])
          .sum();
    }

    DiscreteFactor result = inPlace ? this : (DiscreteFactor)this.copy();
    result.setVariables(newScope.stream().toArray(String[]::new));
    result.setCardinality(Ints.toArray(newCardinality));
    result.setValues(newValues);

    return result;
  }
}
