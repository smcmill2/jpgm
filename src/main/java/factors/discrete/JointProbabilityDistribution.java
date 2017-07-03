package factors.discrete;

import com.google.common.primitives.Doubles;
import factors.Factor;
import org.apache.commons.lang3.tuple.Pair;
import util.ListOps;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of a joint probability distribution for discrete variables.
 * This class extends the DiscreteFactor class with the added requirement
 * that all the values total 1.0, a requirement of a joint probability
 * distribution.
 *
 * @see DiscreteFactor
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 *
 */
public class JointProbabilityDistribution extends DiscreteFactor {
  public JointProbabilityDistribution(List<String> variables, List<Integer> cardinality,
      double[] values) {
    super(variables, cardinality, values);
    double totalProbability = Arrays.stream(values).sum();

    if(Math.abs(1.0 - totalProbability) > threshold) {
      throw new RuntimeException("Total probability does not sum to 1.0\n");
    }
  }

  public Factor copy() {
    return new JointProbabilityDistribution(this.getScope(), this.getCardinality(),
        this.getValues());
  }

  public double[] getValues() {
    return Arrays.copyOf(this.values, this.values.length);
  }

  @Override public Factor reduce(List<Pair<String, Integer>> variables,
      boolean inPlace) {
    Factor factor = super.reduce(variables, inPlace);

    // Can't initialize a new JPD that doesn't sum to 1.0
    ((JointProbabilityDistribution)factor).setValues(
        ListOps.normalize(((JointProbabilityDistribution) factor).getValues()));

    return factor;
  }
}
