package factors.discrete;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import factors.Factor;
import org.apache.commons.lang3.tuple.Pair;
import primitives.Event;
import util.ListOps;
import util.Misc;

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
  protected double[] values;
  private int size;

  private List<Event> reductions;
  private Map<String, Integer> varCard;

  private ListMultimap<String, Integer> assignmentToIdx;
  private Map<String, Integer> rangeSize;

  public DiscreteFactor() {
    // empty constructor, empty set with probability 1
    this(Lists.newArrayList(),
        Lists.newArrayList(), new double[]{1.0});
  }

  public DiscreteFactor(List<String> variables, List<Integer> cardinality, double[] values) {
    this.setVariables(variables);
    this.setCardinality(cardinality);
    this.setValues(values);

    this.reductions = new ArrayList<>();
  }

  public String toString() {
    String header = Joiner.on(" | ").join(
        this.getScope().stream()
            .map(v -> String.format(" %s ", v))
        .collect(Collectors.toList())) +
        " | " + this.factorString();

    TreeMultimap<Integer, String> invertedMap = Multimaps.invertFrom(this.assignmentToIdx,
        TreeMultimap.create());

    List<String> bodyList = Lists.newArrayList();
    for(int i = 0;i < this.values.length;++i) {
      bodyList.add(Joiner.on(" | ").join(this.scopeSort(Lists.newArrayList(invertedMap.get(i)))) + " | " +
              String.format("%.4f", this.values[i])
      );
    }

    String body = Joiner.on("\n").join(bodyList);

    return Joiner.on("\n").join(header, body);
  }

  public String factorString() {
    String fString = "\u03C6(" + Joiner.on(",").join(this.getScope());

    if(this.reductions.size() > 0) {
      fString = fString.concat(" | " +
        Joiner.on(",").join(this.reductions.stream()
            .map(Event::toString)
            .collect(Collectors.toList())));
    }
    return fString.concat(")");
  }

  private List<String> scopeSort(List<String> assignments) {
    List<Pair<String, Integer>> assignmentsToOrder = new ArrayList<>();
    for(String assignment : assignments) {
      String variable = Splitter.on("=").splitToList(assignment).get(0);
      Integer position = this.getScope().indexOf(variable);
      assignmentsToOrder.add(Pair.of(assignment, position));
    }

    return assignmentsToOrder.stream()
        .sorted(Comparator.comparingInt(Pair::getRight))
        .map(Pair::getLeft)
        .collect(Collectors.toList());

  }

  public Factor copy() {
    DiscreteFactor df = new DiscreteFactor(this.getScope(), this.getCardinality(),
        this.values);
    df.reductions = Lists.newArrayList(this.reductions);

    return df;
  }

  public boolean equals(DiscreteFactor other) {
    boolean isEqual;
    if(!Iterables.elementsEqual(this.getScope(), other.getScope())) {
      isEqual = false;
    } else if(!Iterables.elementsEqual(this.getCardinality(), other.getCardinality())) {
      isEqual = false;
    } else {
      isEqual = Iterables.elementsEqual(Doubles.asList(this.values),
          Doubles.asList(other.values));
    }

    return isEqual;
  }

  protected void setVariables(List<String> variables) {
    this.variables = Lists.newArrayList(variables);
  }

  @Override public List<String> getScope() {
    return Lists.newArrayList(this.variables);
  }

  protected void setCardinality(List<Integer> cardinality) {
    Preconditions.checkArgument(this.variables.size() == cardinality.size(),
        "variables and cardinality must have the same size.");

    this.cardinality = Lists.newArrayList(cardinality);
    this.rangeSize = this.getRangeSize(this.variables, this.cardinality);
    this.size = this.cardinality.stream()
      .reduce(1, (a, b) -> a * b);
    this.assignmentToIdx = this.createAssignments(this.variables,
        this.cardinality,
        this.rangeSize,
        this.size);
  }

  public List<Integer> getCardinality() {
    return Lists.newArrayList(this.cardinality);
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

  protected void setValues(double[] values) {
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
        .forEach(i -> rangeSize.put(variables.get(i), ranges.get(i)));

    return rangeSize;
  }

  public double getValue(Event event) {
    return this.assignmentToIdx.get(event.toString()).stream()
        .mapToDouble(idx -> this.values[idx]).sum();
  }

  @Override public Factor normalize(boolean inPlace) {
    DiscreteFactor factor = inPlace ? this :
        (DiscreteFactor) this.copy();

    factor.setValues(ListOps.normalize(factor.values));

    return factor;
  }

  // TODO when reducing set new factorString value
  @Override public Factor reduce(List<Event> events,
      boolean inPlace) {
    HashSet<String> rVars = new HashSet<>();
    HashSet<Integer> rVarIdxs = new HashSet<>();
    HashSet reducedIdxs = new HashSet();
    reducedIdxs.addAll(IntStream.range(0, this.size).boxed().collect(Collectors.toList()));
    for(Event event : events) {
      String variable = event.getVariable();
      int vIdx = this.variables.indexOf(variable);

      rVars.add(variable);
      rVarIdxs.add(vIdx);
      List<Integer> assignedIdxs = this.assignmentToIdx.get(event.toString());
      reducedIdxs.retainAll(Sets.intersection(reducedIdxs,
          Sets.newHashSet(assignedIdxs)));
    }

    List<String> newScope = this.getScope().stream()
        .filter(v -> !rVars.contains(v))
        .collect(Collectors.toList());

    List<Integer> newCardinality = newScope.stream()
        .mapToInt(v -> this.getCardinality().get(this.variables.indexOf(v)))
        .boxed()
        .collect(Collectors.toList());

    double[] newValues = reducedIdxs.stream()
        .sorted()
        .mapToDouble(i -> this.values[(int) i])
        .toArray();

    DiscreteFactor result = inPlace ? this : (DiscreteFactor)this.copy();

    result.setVariables(newScope);
    result.setCardinality(newCardinality);
    result.setValues(newValues);
    result.reductions.addAll(events);

    return result;
  }

  @Override public Factor marginalize(List<String> variables, boolean inPlace) {
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
    result.setVariables(newScope);
    result.setCardinality(newCardinality);
    result.setValues(newValues);

    return result;
  }

  /**
   * Will construct a new DiscreteFactor that is the factor of this factor
   * with DiscreteFactor other:
   *  - The scope of the new factor is this union other with ordering consistent
   *  with a straight concatentation of the two factors with intersecting
   *  variables appearing as they do in this factor.
   *  - The cardinality will follow from the new scope.
   *  - The values will be a multiplication of the values in the existing
   *  factors with a size consistent with the new cardinality.
   *
   * @param other the DiscreteFactor to create a factor product with
   * @return a new DiscreteFactor that is the factor product of this and other
   */
  public DiscreteFactor product(DiscreteFactor other) {
    Set<String> overlapping = Sets.intersection(
        Sets.newHashSet(this.getScope()), Sets.newHashSet(other.getScope()));

    List<String> newScope = this.getScope();
    List<Integer> newCardinality = this.getCardinality();
    IntStream.range(0, other.getScope().size())
        .filter(i -> !overlapping.contains(other.getScope().get(i)))
        .forEach(i -> {
          newScope.add(other.getScope().get(i));
          newCardinality.add(other.getCardinality().get(i));
        });

    DiscreteFactor result = new DiscreteFactor();
    result.setVariables(newScope);
    result.setCardinality(newCardinality);

    double[] newValues = new double[result.size];
    TreeMultimap<Integer, String> invertedMap = Multimaps.invertFrom(
        result.assignmentToIdx, TreeMultimap.create());
    for(int i = 0;i < newValues.length;++i) {
      Set<Integer> thisIdx = Sets.newHashSet(IntStream.range(0, this.size).iterator());
      Set<Integer> otherIdx = Sets.newHashSet(IntStream.range(0, other.size).iterator());
      for(String assignment : invertedMap.get(i)) {
        Set<Integer> indices = Sets.newHashSet(this.assignmentToIdx.get(assignment));
        Set<Integer> oIndices = Sets.newHashSet(other.assignmentToIdx.get(assignment));

        if(indices.size() > 0) {
          thisIdx = Sets.intersection(thisIdx, indices);
        }
        if(oIndices.size() > 0) {
          otherIdx = Sets.intersection(otherIdx, oIndices);
        }
      }

      newValues[i] = this.values[(int)thisIdx.toArray()[0]] * other.values[(int)otherIdx.toArray()[0]];
    }

    result.setValues(newValues);

    Set<Event> reductions =
        Sets.union(Sets.newHashSet(result.reductions),
            Sets.newHashSet(other.reductions));
    result.reductions = Lists.newArrayList(reductions);

    return result;
  }
}
