package factors.discrete;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import factors.Factor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import primitives.EventStream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

class JointProbabilityDistributionTest {
  private final double threshold = 10e-8;
  List<String> variables = Lists.newArrayList("I", "D", "G");
  List<Integer> cardinality = Lists.newArrayList(2, 2, 3);
  double[] values =  new double[]{
      0.126, 0.168, 0.126,
      0.009, 0.045, 0.126,
      0.252, 0.0224, 0.0056,
      0.06, 0.036, 0.024
  };

  JointProbabilityDistribution jpd = null;

  @BeforeEach void setUp() {
    jpd = new JointProbabilityDistribution(variables, cardinality, values);
  }

  @Test void testThrowsWhenValuesDontSumToOne() {
    Assertions.assertNotEquals(null, jpd);
    Assertions.assertThrows(RuntimeException.class, () -> {
      new JointProbabilityDistribution(variables, cardinality, new double[12]);
    });
    Assertions.assertThrows(RuntimeException.class, () -> {
      new JointProbabilityDistribution(variables, cardinality,
          IntStream.range(0, 12).mapToDouble(Double::new).toArray());
    });
  }

  @Test void testCopy() {
    JointProbabilityDistribution result = (JointProbabilityDistribution) jpd.copy();

    Assertions.assertTrue(Iterables.elementsEqual(result.getScope(), jpd.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(result.getCardinality(), jpd.getCardinality()));
    Assertions.assertArrayEquals(result.getValues(), jpd.getValues(), threshold);
  }

  @Test void testNormalize() {
    jpd.normalize(true);

    double actualValueSum = Arrays.stream(jpd.getValues()).sum();

    Assertions.assertEquals(1.0, actualValueSum, threshold);
  }

  @Test void testReduce() {
    double[] iReduction = new double[]{0.252, 0.0224, 0.0056, 0.06, 0.036, 0.024};
    double iSum = Arrays.stream(iReduction).sum();
    double[] expectedValues = Arrays.stream(iReduction)
        .map(v -> v / iSum)
        .toArray();
    EventStream reudction = new EventStream("I=1");

    Factor factor = jpd.reduce(reudction.getEvents(), false);

    // Check new factor is reduced version
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList( "D", "G"), factor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList( 2, 3), ((JointProbabilityDistribution)factor).getCardinality()));
    Assertions.assertArrayEquals(expectedValues, ((JointProbabilityDistribution)factor).getValues(), threshold);
    Assertions.assertEquals(1.0, Arrays.stream(((JointProbabilityDistribution) factor).getValues()).sum(), threshold);
    // Check existing factor has not changed
    Assertions.assertTrue(Iterables.elementsEqual(variables, jpd.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(cardinality, jpd.getCardinality()));
    Assertions.assertArrayEquals(values, jpd.getValues(), threshold);

    jpd.reduce(reudction.getEvents(), true);

    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList( "D", "G"), jpd.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList( 2, 3), jpd.getCardinality()));
    Assertions.assertArrayEquals(expectedValues, jpd.getValues(), threshold);
    Assertions.assertEquals(1.0, Arrays.stream(jpd.getValues()).sum(), threshold);
  }
}
