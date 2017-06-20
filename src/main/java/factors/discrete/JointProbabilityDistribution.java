package factors.discrete;

import factors.Factor;
import org.apache.commons.lang3.tuple.Pair;

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
  public JointProbabilityDistribution(String[] variables, int[] cardinality,
      double[] values) {
    super(variables, cardinality, values);
    double totalProbability = Arrays.stream(values).sum();

    if(Math.abs(1.0 - totalProbability) > threshold) {
      throw new RuntimeException("Total probability does not sum to 1.0\n");
    }
  }

  @Override public Factor reduce(List<Pair<String, Integer>> variables,
      boolean inPlace) {
    Factor f = super.reduce(variables, inPlace);
    return f.normalize(inPlace);
  }
}
