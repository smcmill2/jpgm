package factors.discrete;

import factors.Factor;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
  private String[] variables;
  private Object2ObjectOpenHashMap<String, String[]> assignments;
  private int[] cardinality;
  private double[] values;

  private Object2ObjectOpenHashMap<String, Int2ObjectOpenHashMap<int[]>> indices;

  public DiscreteFactor(String[] variables, int[] cardinality, double[] values) {
    this.setVariables(variables);
    this.setCardinality(cardinality);
    this.setValues(values);
  }

  public Factor copy() {
    return new DiscreteFactor(this.getScope(), this.getCardinality(),
        this.getValues());
  }

  public void setVariables(String[] variables) {
    this.variables = variables.clone();
  }

  @Override public String[] getScope() {
    return this.variables.clone();
  }

  public void setCardinality(int[] cardinality) {
    if (variables.length != cardinality.length) {
      throw new RuntimeException("variables and cardinality must have same size.");
    }
    this.cardinality = cardinality.clone();

    this.setAssignments();
  }

  public int[] getCardinality() {
    return this.cardinality.clone();
  }

  private void setAssignments() {
    this.assignments = new Object2ObjectOpenHashMap<>();
    IntStream.range(0, this.variables.length)
        .forEach(i -> this.assignments.put(this.variables[i],
            IntStream.range(0, this.cardinality[i])
                .mapToObj(Integer::toString)
                .toArray(String[]::new)));
  }

  public String[] getAssignment(String variable) {
    return this.assignments.get(variable);
  }

  public String[] getAssignment(int index) {
    return this.assignments.get(this.variables[index]);
  }

  public void setValues(double[] values) {
    if (values.length != Arrays.stream(this.cardinality)
        .reduce(1, (a, b) -> a * b)) {
      throw new RuntimeException(
          String.format("Incorrect size of values variables. Expecting array " +
          "of size %d. Instead received array of size %d.",
              this.variables.length * this.cardinality.length, values.length));
    }

    this.indices = new Object2ObjectOpenHashMap<>();
    for(int v = 0;v < this.variables.length;++v) {
      this.indices.put(this.variables[v], new Int2ObjectOpenHashMap<>());
    }

    this.values = values.clone();
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

  @Override public Factor reduce(List<Pair<String, Integer>> variables, boolean inPlace) {
    IntArrayList reductionVarIdxs = new IntArrayList();
    //IntArrayList reductionIdxs = new IntArrayList();
    int[] reductionIdxs = new int[]{6, 7, 8, 9, 10, 11};

    IntStream.range(0, this.variables.length)
            .forEach(v -> reductionVarIdxs.add(v));

    ObjectOpenHashSet<String> scope = new ObjectOpenHashSet<>();
    Arrays.stream(this.variables)
            .forEach(v -> scope.add(v));

    for(Pair<String, Integer> varAss : variables) {
      String variable = varAss.getLeft();
      int assignment = varAss.getRight();
      if(!scope.contains(variable)) {
        throw new RuntimeException(String.format("Variable %s not in scope\n",
                variable));
      }
    }

    String[] newScope = Arrays.stream(reductionVarIdxs.toIntArray())
            .mapToObj(i -> this.variables[i])
            .toArray(String[]::new);

    int[] newCardinality = Arrays.stream(reductionVarIdxs.toIntArray())
            .map(i -> this.cardinality[i])
            .toArray();

    double[] newValues = Arrays.stream(reductionIdxs)
            .mapToDouble(i -> this.values[i])
            .toArray();

    if(inPlace) {
      this.setVariables(newScope);
      this.setCardinality(newCardinality);
      this.setValues(newValues);
    }

    Factor result = inPlace ? this :
            new DiscreteFactor(newScope, newCardinality, newValues);

    return result;
  }

  @Override public Factor marginalize(String[] variables, boolean inPlace) {
    return null;
  }
}
