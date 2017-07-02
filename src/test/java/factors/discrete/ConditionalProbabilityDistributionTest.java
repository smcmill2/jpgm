package factors.discrete;

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

class ConditionalProbabilityDistributionTest {
  private final double threshold = 10e-8;
  List<String> evidence = Lists.newArrayList("I", "D");
  List<Integer> eCardinality = Lists.newArrayList(2, 2);

  double[][] expectedTable = new double[][]{
        {0.126, 0.009, 0.252, 0.060},
        {0.168, 0.045, 0.0224, 0.036},
        {0.126, 0.126, 0.0056, 0.024}
    };

  double[][] expectedNormedTable = new double[][]{
      {0.3, 0.05, 0.9, 0.5},
      {0.4, 0.25, 0.08, 0.3},
      {0.3, 0.70, 0.02, 0.2}
  };
  ConditionalProbabilityDistribution cpd = null;

  @BeforeEach void setUp() {
    cpd = new ConditionalProbabilityDistribution("G", 3, evidence, eCardinality, expectedTable);
  }

  @Test void testEquals() {
    ConditionalProbabilityDistribution cpd2 = new ConditionalProbabilityDistribution("G", 3, evidence, eCardinality, expectedTable);
    ConditionalProbabilityDistribution cpd3 = new ConditionalProbabilityDistribution("g", 3, evidence, eCardinality, expectedTable);
    ConditionalProbabilityDistribution cpd4 = new ConditionalProbabilityDistribution("G", 2, new double[][]{{0.2},{0.8}});

    expectedTable[0][0] = 1.0;
    ConditionalProbabilityDistribution cpd5 = new ConditionalProbabilityDistribution("G", 3, evidence, eCardinality, expectedTable);

    Assertions.assertTrue(cpd.equals(cpd2));
    Assertions.assertTrue(!cpd.equals(cpd3));
    Assertions.assertTrue(!cpd.equals(cpd4));
    Assertions.assertTrue(!cpd.equals(cpd5));
  }

  @Test void testNormalize() {
    Factor result = cpd.normalize(true);

    double[][] actual = cpd.getValues();
    for(int r = 0;r < expectedNormedTable.length;++r) {
      Assertions.assertArrayEquals(expectedNormedTable[r], actual[r], threshold);
    }
  }

  @Test void testReduce() {
    double[][] expected = new double[][]{
        {0.9},
        {0.08},
        {0.02}
    };

    List<Pair<String, Integer>> reduction = new ArrayList<>();
    reduction.add(Pair.of("D", 0));
    reduction.add(Pair.of("I", 1));

    Factor result = cpd.reduce(reduction, true);
    double[][] actual = cpd.getValues();

    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList( "I", "D", "G"), cpd.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(1, 1, 3), cpd.getCardinality()));
    for(int r = 0;r < expected.length;++r) {
      Assertions.assertArrayEquals(expected[r], actual[r], threshold);
    }
  }

  @Test void testMarginalize() {
    double[][] expected = new double[][]{
        {0.54, 0.23},
        {0.272, 0.27},
        {0.188, 0.5}
    };

    Factor result = cpd
        .marginalize(Lists.newArrayList("I"), true);

    double[][] actual = cpd.getValues();

    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("D", "G"), cpd.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2, 3), cpd.getCardinality()));
    for(int r = 0;r < expectedNormedTable.length;++r) {
      Assertions.assertArrayEquals(expected[r], actual[r], threshold);
    }
  }
}
