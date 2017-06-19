package factors.discrete;

import factors.Factor;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
  private ObjectArrayList<String> variables;
  private Object2ObjectOpenHashMap<String, String[]> assignments;
  private int[] cardinality;
  private double[] values;
  private int size;

  private Object2IntOpenHashMap<String> varIdx;
  private Int2IntOpenHashMap indexOffset;
  private Object2ObjectOpenHashMap<String, Int2ObjectOpenHashMap<IntArrayList>> indices;

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
    this.variables = new ObjectArrayList<>();
    Arrays.stream(variables)
        .forEach(v -> this.variables.add(v));
  }

  @Override public String[] getScope() {
    return this.variables.stream().toArray(String[]::new);
  }

  private void setCardinality(int[] cardinality) {
    if (this.variables.size() != cardinality.length) {
      throw new RuntimeException("variables and cardinality must have same size.");
    }
    this.cardinality = cardinality.clone();

    this.setAssignments();
    this.createIndices();
  }

  public int[] getCardinality() {
    return this.cardinality.clone();
  }

  private void setAssignments() {
    this.assignments = new Object2ObjectOpenHashMap<>();
    IntStream.range(0, this.variables.size())
        .forEach(i -> this.assignments.put(this.variables.get(i),
            IntStream.range(0, this.cardinality[i])
                .mapToObj(Integer::toString)
                .toArray(String[]::new)));
  }

  private void createIndices() {
    this.varIdx = new Object2IntOpenHashMap<>(this.variables.size());
    this.size = Arrays.stream(this.cardinality)
        .reduce(1, (a, b) -> a * b);

    int i = 0;
    for(String variable : this.variables) {
      this.varIdx.put(variable, i++);
    }

    this.indexOffset = getOffsets(this.cardinality);
  }

  private Int2IntOpenHashMap getOffsets(int[] cardinality) {
    Int2IntOpenHashMap offsets = new Int2IntOpenHashMap();

    for(int i = 0;i < cardinality.length;++i) {
      int offset = 1;
      for(int c = i + 1;c < cardinality.length;++c) {
        offset *= cardinality[c];
      }
      offsets.put(i, offset);
    }

    return offsets;
  }

  public String[] getAssignment(String variable) {
    return this.assignments.get(variable);
  }

  public String[] getAssignment(int index) {
    return this.assignments.get(this.variables.get(index));
  }

  private void setValues(double[] values) {
    if (values.length != this.size) {
      throw new RuntimeException(
          String.format("Incorrect size of values variables. Expecting array " +
          "of size %d. Instead received array of size %d.",
              this.size, values.length));
    }

    this.values = values.clone();

    this.indices = new Object2ObjectOpenHashMap<>();
    for(int v = 0;v < this.variables.size();++v) {
      String variable = this.variables.get(v);
      this.indices.put(variable, new Int2ObjectOpenHashMap<>());
      IntStream.range(0, this.cardinality[v])
          .forEach(c -> this.indices.get(variable)
              .put(c, new IntArrayList()));
    }

    for(int i = 0;i < this.values.length;++i) {
      for(int v = 0;v < this.variables.size();++v) {
        String variable = this.variables.get(v);
        int vIdx = this.varIdx.getInt(variable);
        int offsetVal = this.indexOffset.get(vIdx);
        Int2ObjectOpenHashMap<IntArrayList> idxVal = this.indices.get(variable);

        idxVal.get((i / offsetVal) % this.cardinality[v]).add(i);
      }
    }
  }

  public double[] getValues() {
    return this.values.clone();
  }

  @Override public Factor normalize(boolean inPlace) {
    DiscreteFactor result = inPlace ? this : (DiscreteFactor)this.copy();

    double sum = Arrays.stream(result.getValues()).sum();
    double[] normalizedValues = Arrays.stream(result.getValues())
        .map(v -> v / sum)
        .toArray();

    result.setValues(normalizedValues);

    return result;
  }

  @Override public Factor reduce(List<Pair<String, Integer>> variables,
      boolean inPlace) {
    IntArrayList reductionVarIdxs = new IntArrayList();
    IntOpenHashSet reductionIdxs = new IntOpenHashSet();

    IntStream.range(0, this.variables.size())
            .forEach(reductionVarIdxs::add);

    for(Pair<String, Integer> varAss : variables) {
      String variable = varAss.getLeft();
      int assignment = varAss.getRight();

      if(!this.varIdx.containsKey(variable)) {
        throw new RuntimeException(String.format("Variable %s not in scope\n",
                variable));
      } else {
        reductionVarIdxs.rem(this.varIdx.getInt(variable));
        IntArrayList assIdx = this.indices.get(variable).get(assignment);
        if(assIdx == null) {
          throw new RuntimeException(String.format("Variable: %s has no " +
                  "assignment: %d\n", variable, assignment));
        } else {
          if (reductionIdxs.isEmpty()) {
            reductionIdxs.addAll(assIdx);
          } else {
            reductionIdxs.retainAll(assIdx);
          }
        }
      }
    }

    String[] newScope = this.variables.stream()
        .filter(v -> reductionVarIdxs.contains(this.varIdx.getInt(v)))
        .toArray(String[]::new);

    int[] newCardinality = Arrays.stream(reductionVarIdxs.toIntArray())
            .map(i -> this.cardinality[i])
            .toArray();

    int[] sortedReducedIdxs = reductionIdxs.toIntArray();
    Arrays.sort(sortedReducedIdxs);
    double[] newValues = Arrays.stream(sortedReducedIdxs)
            .mapToDouble(i -> this.values[i])
            .toArray();

    if(inPlace) {
      this.setVariables(newScope);
      this.setCardinality(newCardinality);
      this.setValues(newValues);
    }

    return inPlace ? this :
            new DiscreteFactor(newScope, newCardinality, newValues);
  }

  @Override public Factor marginalize(String[] variables, boolean inPlace) {
    ObjectArrayList<String> newScope = new ObjectArrayList<>();
    newScope.addAll(this.variables);
    IntArrayList newCardinality = new IntArrayList();
    newCardinality.addAll(Arrays.stream(this.cardinality)
        .boxed()
        .collect(Collectors.toList()));

    for(String variable : variables) {
      if (!newScope.contains(variable)) {
        throw new RuntimeException(String.format("Variable %s not in scope or"
                + "already removed during marginalization\n", variable));
      }
      int idx = newScope.indexOf(variable);
      newScope.remove(idx);
      newCardinality.removeInt(idx);
    }

    if(inPlace) {
      this.setVariables(Arrays.stream(newScope.toArray())
          .toArray(String[]::new));
      this.setCardinality(newCardinality.toIntArray());
    }

    Int2IntOpenHashMap newIdxOffset = this.getOffsets(newCardinality.toIntArray());

    DoubleArrayList marginalizedValues = new DoubleArrayList();
    for(int i = 0;i < this.size;++i) {
      IntOpenHashSet idxToSum = new IntOpenHashSet();
      for(int v = 0;v < this.variables.size();++v) {
        String variable = this.variables.get(v);
        int vIdx = this.varIdx.getInt(variable);
        int offset = newIdxOffset.get(vIdx);
        int assIdx = (i / offset) % this.cardinality[v];

        IntArrayList assignments = this.indices.get(variable).get(assIdx);

        if (idxToSum.isEmpty()) {
          idxToSum.addAll(assignments);
        } else {
          idxToSum.retainAll(assignments);
        }
      }

      marginalizedValues.add(idxToSum.stream()
          .mapToDouble(index -> this.values[index])
          .sum());
    }

    this.setValues(marginalizedValues.toDoubleArray());

    return this;
  }
}
