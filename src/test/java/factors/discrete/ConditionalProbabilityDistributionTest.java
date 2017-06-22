package factors.discrete;

import com.google.common.collect.ListMultimap;
import factors.Factor;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

class ConditionalProbabilityDistributionTest {
  private final double threshold = 10e-8;
  String[] variables = new String[]{"I", "D", "G"};
  int[] cardinality = new int[]{2, 2, 3};
  double[] values = new double[]{
      0.126, 0.168, 0.126,
      0.009, 0.045, 0.126,
      0.252, 0.0224, 0.0056,
      0.06, 0.036, 0.024
  };

  double[][] expectedNormedTable = null;

  ConditionalProbabilityDistribution cpd = null;

  @BeforeEach void setUp() {
    cpd = new ConditionalProbabilityDistribution(variables, cardinality, values);
  }

  @Test void testNormalize() {
    Factor result = cpd.normalize(true);

    double[][] values = cpd.getValues();
    Assertions.assertFalse(true);
  }

  @Test void testReduce() {
    Factor result = cpd
        .reduce(Arrays.<Pair<String, Integer>>asList(null), true);
    Assertions.assertEquals(null, result);
  }

  @Test void testMarginalize() {
    Factor result = cpd
        .marginalize(new String[] { "variables" }, true);
    Assertions.assertEquals(null, result);
  }
}
