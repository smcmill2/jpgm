package factors;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Interface for a factor
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
public interface Factor {
  double threshold = 10e-8;
  /**
   * Return the scope of the factor
   *
   * @return the array of the variables for the factor
   */
  String[] getScope();

  /**
   * Normalize the values of the factor such that they sum to 1
   *
   * @param inPlace whether to normalize in place or return a new factor object
   *                that has been normalized.
   * @return the normalized dst factor or a normalized copy.
   */
  Factor normalize(boolean inPlace);

  /**
   * Reduce the factor by the specified variables, removing them from the scope.
   *
   * @param variables the list of variable, assignment pairs to reduce to
   * @param inPlace whether to reduce in place or return a new factor object
   *                that has been reduced.
   * @return the reduced dst factor or a copy.
   */
  Factor reduce(List<Pair<String, Integer>> variables, boolean inPlace);

  /**
   * Marginalize the factor with respect to the specified variables
   *
   * @param variables the array of variables to marginalize the factor with
   *                  with respect to.
   * @param inPlace whether to marginalize in place or return a new factor object
   *                that has been marginalized.
   * @return the marginalized factor dst or a copy.
   */
  Factor marginalize(String[] variables, boolean inPlace);
}
