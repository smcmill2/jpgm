package util;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Implement miscellaneous helper functions.
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
public class Misc {
  public static <L, R> String joinPair(L l, R r, String joinVal) {
    return Joiner.on(joinVal).join(l.toString(), r.toString());
  }

  public static <L, R> String joinPair(Pair<L, R> pair, String joinVal) {
    return joinPair(pair.getLeft(), pair.getRight(), joinVal);
  }
}
