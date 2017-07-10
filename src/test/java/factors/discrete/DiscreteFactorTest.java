package factors.discrete;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import factors.Factor;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for DiscreteFactor
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
class DiscreteFactorTest {
  private final double threshold = 10e-8;
  List<String> variables = Lists.newArrayList("I", "D", "G");
  List<Integer> cardinality = Lists.newArrayList(2, 2, 3);
  double[] values =  new double[]{
      0.126, 0.168, 0.126,
      0.009, 0.045, 0.126,
      0.252, 0.0224, 0.0056,
      0.06, 0.036, 0.024
  };

  DiscreteFactor discreteFactor = null;

  @BeforeEach void setUp() {
    discreteFactor = new DiscreteFactor(variables, cardinality, values);
  }

  @Test void testToString() {
    String expectedStr = Joiner.on("\n").join(
        Lists.newArrayList(
            " I  |  D  |  G  | \u03C6(" + Joiner.on(",").join(variables) +")",
            "I=0 | D=0 | G=0 | 0.1260",
            "I=0 | D=0 | G=1 | 0.1680",
            "I=0 | D=0 | G=2 | 0.1260",
            "I=0 | D=1 | G=0 | 0.0090",
            "I=0 | D=1 | G=1 | 0.0450",
            "I=0 | D=1 | G=2 | 0.1260",
            "I=1 | D=0 | G=0 | 0.2520",
            "I=1 | D=0 | G=1 | 0.0224",
            "I=1 | D=0 | G=2 | 0.0056",
            "I=1 | D=1 | G=0 | 0.0600",
            "I=1 | D=1 | G=1 | 0.0360",
            "I=1 | D=1 | G=2 | 0.0240"
        ));
    Assertions.assertTrue(expectedStr.equals(discreteFactor.toString()));
  }

  /**
   * TODO Add checks for marginalization/products/etc.
   */
  @Test void testFactorString() {
    String expectedStr = "\u03C6(I,D,G)";
    String expectedReducedFactorString = "\u03C6(D | I=1,G=2)";
    Factor reducedFactor = discreteFactor.reduce(
        Lists.newArrayList(Pair.of("I",1), Pair.of("G", 2)),
        false);

    Assertions.assertTrue(expectedStr.equals(discreteFactor.factorString()));
    Assertions.assertTrue(expectedReducedFactorString.equals(reducedFactor.factorString()));

  }

  @Test void testValidFactor() {
    List<String> tooFewVars = Lists.newArrayList("I", "D");
    List<Integer> tooManyCard = Lists.newArrayList(2, 2, 3, 4);
    double[] incorrectValueSize = new double[9];

    Assertions.assertThrows(IllegalArgumentException.class, () -> new DiscreteFactor(tooFewVars, cardinality, values));
    Assertions.assertThrows(IllegalArgumentException.class, () -> new DiscreteFactor(variables, tooManyCard, values));
    Assertions.assertThrows(IllegalArgumentException.class, () -> new DiscreteFactor(variables, cardinality, incorrectValueSize));
  }

  @Test void testEquals() {
    DiscreteFactor other = new DiscreteFactor(variables, cardinality, values);

    Assertions.assertTrue(discreteFactor.equals(other));

    DiscreteFactor emptyFactor = new DiscreteFactor();
    Assertions.assertTrue(emptyFactor.equals(new DiscreteFactor()));
  }

  @Test void testGetScope() {
    Assertions.assertTrue(Iterables.elementsEqual(variables, discreteFactor.getScope()));
  }

  @Test void testNormalize() {
    Factor factor = discreteFactor.normalize(false);

    double dfValueSum = Arrays.stream(((DiscreteFactor)factor).values).sum();

    Assertions.assertEquals(1.0, dfValueSum, threshold);
    Assertions.assertArrayEquals(values, ((DiscreteFactor)factor).values, threshold);

    discreteFactor.normalize(true);
    double actualValueSum = Arrays.stream(discreteFactor.values).sum();

    Assertions.assertEquals(1.0, actualValueSum, threshold);
    Assertions.assertArrayEquals(values, discreteFactor.values, threshold);
  }

  @Test void testReduce() {
    double[] iReduction = new double[]{0.126, 0.168, 0.126,
      0.009, 0.045, 0.126};
    List<Pair<String, Integer>> reduceList = new ArrayList<>();
    reduceList.add(Pair.of("I", 0));

    Factor factor = discreteFactor.reduce(reduceList, false);

    // Check new factor is reduced version
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("D", "G"), factor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2, 3), ((DiscreteFactor)factor).getCardinality()));
    Assertions.assertArrayEquals(iReduction, ((DiscreteFactor)factor).values, threshold);
    // Check existing factor has not changed
    Assertions.assertTrue(Iterables.elementsEqual(variables, discreteFactor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(cardinality, discreteFactor.getCardinality()));
    Assertions.assertArrayEquals(values, discreteFactor.values, threshold);

    discreteFactor.reduce(reduceList, true);

    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("D", "G"), discreteFactor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2, 3), discreteFactor.getCardinality()));
    Assertions.assertArrayEquals(iReduction, discreteFactor.values, threshold);
  }

  @Test void testReduceSingleVariable() {
    double[] expected = new double[]{1.0};

    DiscreteFactor singleVar = new DiscreteFactor(Lists.newArrayList("I"),
        Lists.newArrayList(2), new double[]{0.7, 0.3});

    singleVar.reduce(Lists.newArrayList(Pair.of("I", 1)), true);

    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(), singleVar.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(), singleVar.getCardinality()));
    Assertions.assertArrayEquals(new double[]{0.3}, singleVar.values);
  }

  @Test void testMultipleReduction() {
    double[] dgReduction = new double[]{0.126, 0.024};
    List<Pair<String, Integer>> reduceList = new ArrayList<>();
    reduceList.add(Pair.of("G", 2));
    reduceList.add(Pair.of("D", 1));

    Factor factor = discreteFactor.reduce(reduceList, false);

    // Check new factor is reduced version
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("I"), factor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2), ((DiscreteFactor)factor).getCardinality()));
    Assertions.assertArrayEquals(dgReduction, ((DiscreteFactor)factor).values, threshold);
    // Check existing factor has not changed
    Assertions.assertTrue(Iterables.elementsEqual(variables, discreteFactor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(cardinality, discreteFactor.getCardinality()));
    Assertions.assertArrayEquals(values, discreteFactor.values, threshold);

    discreteFactor.reduce(reduceList, true);

    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("I"), discreteFactor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2), discreteFactor.getCardinality()));
    Assertions.assertArrayEquals(dgReduction, discreteFactor.values, threshold);
  }

  @Test void testMarginalize() {
    double[] g_marginalized = new double[]{0.42, 0.18, 0.28, 0.12};

    Factor factor = discreteFactor.marginalize(Lists.newArrayList("G"), false);

    // Check new factor is marginalized version
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("I", "D"), factor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2, 2), ((DiscreteFactor)factor).getCardinality()));
    Assertions.assertArrayEquals(g_marginalized, ((DiscreteFactor)factor).values, threshold);
    // Check existing factor has not changed
    Assertions.assertTrue(Iterables.elementsEqual(variables, discreteFactor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(cardinality, discreteFactor.getCardinality()));
    Assertions.assertArrayEquals(values, discreteFactor.values, threshold);

    discreteFactor.marginalize(Lists.newArrayList("G"), true);

    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("I", "D"), discreteFactor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2, 2), discreteFactor.getCardinality()));
    Assertions.assertArrayEquals(g_marginalized, discreteFactor.values, threshold);
  }

  /**
   * TODO parameterize test
   */
  @Test void testMultipleMarginalized() {
    double[] gd_marginalized = new double[]{0.60, 0.40};

    Factor factor = discreteFactor.marginalize(Lists.newArrayList("D", "G"), false);

    // Check new factor is marginalized version
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("I"), factor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2), ((DiscreteFactor)factor).getCardinality()));
    Assertions.assertArrayEquals(gd_marginalized, ((DiscreteFactor)factor).values, threshold);
    // Check existing factor has not changed
    Assertions.assertTrue(Iterables.elementsEqual(variables, discreteFactor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(cardinality, discreteFactor.getCardinality()));
    Assertions.assertArrayEquals(values, discreteFactor.values, threshold);

    discreteFactor.marginalize(Lists.newArrayList("D", "G"), true);

    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("I"), discreteFactor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2), discreteFactor.getCardinality()));
    Assertions.assertArrayEquals(gd_marginalized, discreteFactor.values, threshold);
  }

  @Test void testProductOverlap() {
    DiscreteFactor xy = new DiscreteFactor(
        Lists.newArrayList("X", "Y"),
        Lists.newArrayList(3, 2),
        new double[]{0.5, 0.8, 0.1, 0, 0.3, 0.9}
    );
    DiscreteFactor yz = new DiscreteFactor(
        Lists.newArrayList("Y", "Z"),
        Lists.newArrayList(2, 2),
        new double[]{0.5, 0.7, 0.1, 0.2}
    );
    DiscreteFactor expected = new DiscreteFactor(
        Lists.newArrayList("X", "Y", "Z"),
        Lists.newArrayList(3, 2, 2),
        new double[]{
            0.25, 0.35, 0.08, 0.16,
            0.05, 0.07, 0, 0,
            0.15, 0.21, 0.09, 0.18
        }
    );

    DiscreteFactor xyz = xy.product(yz);

    Assertions.assertTrue(Iterables.elementsEqual(expected.getScope(), xyz.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(expected.getCardinality(), xyz.getCardinality()));
    Assertions.assertArrayEquals(expected.values, xyz.values, threshold);
  }

  @Test void testProductNoOverlap() {
    DiscreteFactor x = new DiscreteFactor(
        Lists.newArrayList("X"),
        Lists.newArrayList(3),
        new double[]{0.7, 0.2, 0.1}
    );
    DiscreteFactor y = new DiscreteFactor(
        Lists.newArrayList("Y"),
        Lists.newArrayList(2),
        new double[]{0.5, 0.4}
    );
    DiscreteFactor expected = new DiscreteFactor(
        Lists.newArrayList("X", "Y"),
        Lists.newArrayList(3, 2),
        new double[]{
            0.35, 0.28,
            0.10, 0.08,
            0.05, 0.04
        }
    );

    DiscreteFactor xy = x.product(y);

    Assertions.assertTrue(Iterables.elementsEqual(expected.getScope(), xy.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(expected.getCardinality(), xy.getCardinality()));
    Assertions.assertArrayEquals(expected.values, xy.values, threshold);
  }
}
