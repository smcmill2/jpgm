package factors.discrete;

import factors.Factor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Arrays;
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

  public DiscreteFactor(String[] variables, int[] cardinality, double[] values) {
    if (variables.length != cardinality.length) {
      throw new RuntimeException("variables and cardinality must have same size.");
    }
    this.variables = Arrays.copyOf(variables, variables.length);
    this.cardinality = Arrays.copyOf(cardinality, cardinality.length);

    this.assignments = new Object2ObjectOpenHashMap<>();
    IntStream.range(0, this.variables.length)
        .forEach(i -> this.assignments.put(this.variables[i],
            IntStream.range(0, this.cardinality[i])
                .mapToObj(Integer::toString)
                .toArray(String[]::new)));

    if (values.length != Arrays.stream(this.cardinality)
        .reduce(1, (a, b) -> a * b)) {
      throw new RuntimeException(
          String.format("Incorrect size of values variables. Expecting array " +
          "of size %d. Instead received array of size %d.",
              this.variables.length * this.cardinality.length, values.length));
    }
    this.values = Arrays.copyOf(values, values.length);
  }

  public Factor copy() {
    return new DiscreteFactor(this.getScope(), this.getCardinality(),
        this.getValues());
  }

  @Override public String[] getScope() {
    return Arrays.copyOf(this.variables, this.variables.length);
  }

  public int[] getCardinality() {
    return Arrays.copyOf(this.cardinality, this.cardinality.length);
  }

  public String[] getAssignment(String variable) {
    return this.assignments.get(variable);
  }

  public String[] getAssignment(int index) {
    return this.assignments.get(this.variables[index]);
  }

  public double[] getValues() {
    return Arrays.copyOf(this.values, this.values.length);
  }

  @Override public Factor normalize(boolean inPlace) {
    DiscreteFactor result = inPlace ? this : (DiscreteFactor)this.copy();

    // TODO replace with a stream?
    double sum = Arrays.stream(result.getValues()).sum();
    double[] normalizedValues = Arrays.stream(result.getValues())
        .map(v -> v / sum)
        .toArray();

    result.values = Arrays.copyOf(normalizedValues, normalizedValues.length);

    return result;
  }

  @Override public Factor reduce(String[] variables, boolean inPlace) {
    return null;
  }

  @Override public Factor marginalize(String[] variables, boolean inPlace) {
    return null;
  }
}
