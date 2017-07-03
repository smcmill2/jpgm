package factors.discrete;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import factors.Factor;
import org.apache.commons.lang3.tuple.Pair;
import util.ListOps;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
  private String variable;
  private int vCard;

  public ConditionalProbabilityDistribution() {
    this(null, 0, null, null, null);
  }

  public ConditionalProbabilityDistribution(String variable, int vCardinality,
      double[][] values) {
    this(variable, vCardinality, null, null, values);
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
      List<String> evidence, List<Integer> eCardinality, double[][] values) {
    super();

    List<String> variables = Lists.newArrayList();
    if(evidence != null) {
      variables.addAll(evidence);
    }
    variables.add(variable);

    List<Integer> cardinality = Lists.newArrayList();
    if(eCardinality != null) {
      cardinality.addAll(eCardinality);
    }
    cardinality.add(vCardinality);

    this.variable = variable;
    this.vCard = vCardinality;
    this.setVariables(variables);
    this.setCardinality(cardinality);
    this.setValues(Doubles.concat(transpose(values)));
  }

  public boolean equals(ConditionalProbabilityDistribution other) {
    boolean isEqual;
    if(!this.variable.equals(other.variable)) {
      isEqual = false;
    } else if(this.vCard != other.vCard) {
      isEqual = false;
    } else if(!Iterables.elementsEqual(this.getScope(), other.getScope())) {
      isEqual = false;
    } else if(!Iterables.elementsEqual(this.getCardinality(), other.getCardinality())) {
      isEqual = false;
    } else {
      isEqual = Iterables.elementsEqual(Doubles.asList(this.values),
          Doubles.asList(other.values));
    }

    return isEqual;
  }

  public Factor copy() {
    return new ConditionalProbabilityDistribution(this.getVariable(),
        this.getVariableCardinality(),
        this.getEvidence(), this.getEvidenceCardinality(), this.getValues());
  }

  public String getVariable() {
    return this.variable;
  }

  public int getVariableCardinality() { return this.vCard; }

  public List<String> getEvidence() {
    return this.getScope().stream()
        .filter(v -> !v.equals(this.getVariable()))
        .collect(Collectors.toList());
  }

  public List<Integer> getEvidenceCardinality() {
    return ListOps.zip(this.getScope(), this.getCardinality()).stream()
        .filter(pair -> !pair.getLeft().equals(this.getVariable()))
        .map(pair -> pair.getRight())
        .collect(Collectors.toList());
  }

  public double[][] getValues() {
    int rowLength = this.values.length / this.vCard;
    double[][] result = new double[this.vCard][rowLength];

    for(int r = 0;r < this.vCard;++r) {
      for(int c = 0;c < rowLength;++c) {
        result[r][c] = this.values[c * this.vCard + r];
      }
    }

    return result;
  }

  @Override
  public Factor normalize(boolean inPlace) {
    ConditionalProbabilityDistribution result = inPlace ? this :
        (ConditionalProbabilityDistribution) this.copy();

    double[][] transposedTable = transpose(result.getValues());


    for(int c = 0;c < transposedTable.length;++c) {
      double sum = Arrays.stream(transposedTable[c]).sum();
      transposedTable[c] = Arrays.stream(transposedTable[c])
          .map(v -> v / sum)
          .toArray();
    }

    result.setValues(Doubles.concat(transposedTable));

    return result;
  }

  @Override public Factor reduce(List<Pair<String, Integer>> variables,
      boolean inPlace) {
    Factor factor = super.reduce(variables, inPlace);
    ((ConditionalProbabilityDistribution)factor).vCard =
        ((ConditionalProbabilityDistribution) factor).getCardinality().get(
            factor.getScope().indexOf(this.variable));
    return factor.normalize(inPlace);
  }

  @Override public Factor marginalize(List<String> variables, boolean inPlace) {
    Factor factor = super.marginalize(variables, inPlace);
    return factor.normalize(inPlace);
  }

  /**
   * Flip the rows and columns of the 2D array
   * @param values
   * @return
   */
  private static double[][] transpose(double[][] values) {
    if(values.length == values[0].length) {
      double tmp;

      for (int i = 0; i < values.length / 2 + 1; ++i) {
        for (int j = i; j < values[0].length; ++j) {
          tmp = values[i][j];
          values[j][i] = values[i][j];
          values[i][j] = tmp;
        }
      }
    } else {
      double[][] transposed = new double[values[0].length][values.length];

      for (int i = 0; i < values.length; ++i) {
        for (int j = 0; j < values[0].length; ++j) {
          transposed[j][i] = values[i][j];
        }
      }

      values = transposed;
    }

    return values;
  }
}
