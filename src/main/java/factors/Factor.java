package factors;

/**
 * Interface for a factor
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
public interface Factor {
  /**
   * Return the scope of the factor
   *
   * @return the array of the variables for the factor
   */
  String[] getScope();

  /**
   * Normalize the values of the factor such that they sum to 1
   *
   * @param dst the destination to put the normalized factor. If it is null
   *            then perform the operation in place and return a copy.
   * @return the normalized dst factor or a normalized copy.
   */
  Factor normalize(Factor dst);

  /**
   * Reduce the factor by the specified variables, removing them from the scope.
   *
   * @param variables the array of variables to remove from the scope
   * @param dst the destination to put the new factor in. If it is null
   *            then perform the operation in place and return a copy.
   * @return the reduced dst factor or a copy.
   */
  Factor reduce(String[] variables, Factor dst);

  /**
   * Marginalize the factor with respect to the specified variables
   *
   * @param variables the array of variables to marginalize the factor with
   *                  with respect to.
   * @param dst the destination to put the new factor in. If it is null
   *            then perform the operation in place and return a copy.
   * @return the marginalized factor dst or a copy.
   */
  Factor marginalize(String[] variables, Factor dst);
}
