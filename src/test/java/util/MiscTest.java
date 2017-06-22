package util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MiscTest {
  @Test void testJoinPairStrings() {
    String result = Misc.joinPair("Hello", "World", " ");
    Assertions.assertTrue("Hello World".equals(result));
  }

  @Test void testJoinPairStringInteger() {
    String result = Misc.joinPair("x", 2, "=");
    Assertions.assertTrue("x=2".equals(result));
  }

  @Test void testJoinPair() {
    String result = Misc.joinPair(Pair.of("x", -2), "=");
    Assertions.assertTrue("x=-2".equals(result));
  }
}
