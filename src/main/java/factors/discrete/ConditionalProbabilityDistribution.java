package factors.discrete;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import java.util.Arrays;
import java.util.List;

/**
 * Implements a conditional probability distribution where one variable is
 * conditioned upon other variables or no variables.
 *
 * @see DiscreteFactor
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
public class ConditionalProbabilityDistribution extends DiscreteFactor {
  public ConditionalProbabilityDistribution(String[] variables,
      int[] cardinality, double[] values) {
    super(variables, cardinality, values);
  }

  public ConditionalProbabilityDistribution(String variable, int vCardinality,
      double[] values) {
    this(Arrays.asList(variable), Ints.asList(vCardinality), values);
  }

  public ConditionalProbabilityDistribution(List<String> variables,
      List<Integer> cardinality, double[] values) {
    super(variables, cardinality, values);
  }

  /**
   *
   * @param variable the variable
   * @param vCardinality the cardinality of the variable
   * @param evidence the variables upon which to condition
   * @param eCardinality the cardinality of the conditioned variables
   * @param values a 2D array where each row specifies the probability of
   *               the variable assignment given all evidence variables. The
   *               columns should all sum to 1.
   */
  public ConditionalProbabilityDistribution(String variable, int vCardinality,
      String[] evidence, int[] eCardinality, double[][] values) {
    super();
    List<String> variables = Arrays.asList(evidence);
    variables.add(variable);
    List<Integer> cardinality = Ints.asList(eCardinality);
    cardinality.add(vCardinality);
    this.setVariables(variables);
    this.setCardinality(cardinality);
    this.setValues(Doubles.concat(transpose(values)));
  }

  public double[][] getValues() {

    return null;
  }

  /**
   * Flip the rows and columns of the 2D array
   * @param values
   * @return
   */
  private static double[][] transpose(double[][] values) {
    double tmp;
    for(int i = 0;i < values.length/2 + 1;++i) {
      for(int j = i;j < values[0].length;++j) {
        tmp = values[i][j];
        values[j][i] = values[i][j];
        values[i][j] = tmp;
      }
    }

    return values;
  }
}
