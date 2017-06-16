package factors.discrete;

import factors.Factor;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Arrays;
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
  private String[] variables;
  private Object2ObjectOpenHashMap<String, String[]> assignments;
  private int[] cardinality;
  private double[] values;

  public DiscreteFactor(String[] variables, int[] cardinality, double[] values) {
    if (variables.length != cardinality.length) {
      throw new RuntimeException("variables and cardinality must have same size.");
    }
    this.variables = variables.clone();
    this.cardinality = cardinality.clone();

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
    this.values = values.clone();
  }

  @Override public String[] getScope() {
    return this.variables.clone();
  }

  public String[] getAssignment(String variable) {
    return this.assignments.get(variable);
  }

  public String[] getAssignment(int index) {
    return this.assignments.get(this.variables[index]);
  }

  @Override public Factor normalize(Factor dst) {
    return null;
  }

  @Override public Factor reduce(String[] variables, Factor dst) {
    return null;
  }

  @Override public Factor marginalize(String[] variables, Factor dst) {
    return null;
  }
}
