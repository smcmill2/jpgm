package factors.discrete;

import factors.Factor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Test class for DiscreteFactor
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
class DiscreteFactorTest {
  String[] variables = new String[]{"I", "D", "G"};
  int[] cardinality = new int[]{2, 2, 3};
  double[] values = new double[]{
      0.126, 0.168, 0.126,
      0.009, 0.045, 0.126,
      0.252, 0.224, 0.0056,
      0.06, 0.036, 0.024
  };

  DiscreteFactor discreteFactor = null;

  @BeforeEach void setUp() {
    discreteFactor = new DiscreteFactor(variables, cardinality, values);
  }

  @Test void testValidFactor() {
    String[] tooFewVars = new String[]{"I", "D"};
    int[] tooManyCard = new int[]{2, 2, 3, 4};
    double[] incorrectValueSize = new double[9];

    Assertions.assertThrows(RuntimeException.class, () -> {
      Factor f = new DiscreteFactor(tooFewVars, cardinality, values);
    });
    Assertions.assertThrows(RuntimeException.class, () -> {
      Factor f = new DiscreteFactor(variables, tooManyCard, values);
    });
    Assertions.assertThrows(RuntimeException.class, () -> {
      Factor f = new DiscreteFactor(variables, cardinality, incorrectValueSize);
    });
  }

  @Test void testGetScope() {
    Assertions.assertArrayEquals(variables, discreteFactor.getScope());
  }

  @Test void testGetVariableAssignmentByName() {
    String[] expectedI = new String[]{"0", "1"};
    String[] expectedG = new String[]{"0", "1", "2"};

    Assertions.assertArrayEquals(expectedI, discreteFactor.getAssignment("I"));
    Assertions.assertArrayEquals(expectedG, discreteFactor.getAssignment("G"));
  }

  @Test void testGetVariableAssignmentByIndex() {
    String[] expectedI = new String[]{"0", "1"};
    String[] expectedG = new String[]{"0", "1", "2"};

    Assertions.assertArrayEquals(expectedI, discreteFactor.getAssignment(0));
    Assertions.assertArrayEquals(expectedG, discreteFactor.getAssignment(2));
  }

  @Test void testNormalize() {
    Factor result = discreteFactor.normalize(null);
    Assertions.assertEquals(null, result);
  }

  @Test void testReduce() {
    Factor result = discreteFactor
        .reduce(new String[]{"null"}, null);
    Assertions.assertEquals(null, result);
  }

  @Test void testMarginalize() {
    Factor result = discreteFactor
        .marginalize(new String[]{"null"}, null);
    Assertions.assertEquals(null, result);
  }
}
