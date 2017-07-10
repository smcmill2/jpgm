package factors.discrete;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import factors.Factor;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import primitives.EventStream;

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

  @Test void testCreation() {
    double[][] values = new double[][]{
        {0.90, 0.20},
        {0.10, 0.80}
    };

    // Not deep copying?
    //double[][] expected = new doubleArrays.copyOf(values, values.length);
    double[][] expected = new double[][]{
        {0.90, 0.20},
        {0.10, 0.80}
    };

    ConditionalProbabilityDistribution cpd = new ConditionalProbabilityDistribution(
        "x2", 2,
        Lists.newArrayList("x1"),
        Lists.newArrayList(2),
        values
    );

    Assertions.assertTrue(cpd.getVariable().equals("x2"));
    Assertions.assertEquals(cpd.getVariableCardinality(), 2);
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("x1", "x2"), cpd.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2, 2), cpd.getCardinality()));
    for(int r = 0;r < expected.length;++r) {
      Assertions.assertArrayEquals(expected[r],
          cpd.getValues()[r], threshold);
    }
  }

  @Test void testCopy() {
    ConditionalProbabilityDistribution copiedCPD = (ConditionalProbabilityDistribution) cpd.copy();

    Assertions.assertTrue(copiedCPD.getVariable().equals(cpd.getVariable()));
    Assertions.assertTrue(copiedCPD.getVariableCardinality() == cpd.getVariableCardinality());
    Assertions.assertTrue(Iterables.elementsEqual(copiedCPD.getScope(), cpd.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(copiedCPD.getCardinality(), cpd.getCardinality()));
    Assertions.assertTrue(Iterables.elementsEqual(copiedCPD.getEvidence(), cpd.getEvidence()));
    Assertions.assertTrue(Iterables.elementsEqual(copiedCPD.getEvidenceCardinality(), cpd.getEvidenceCardinality()));
    Assertions.assertArrayEquals(copiedCPD.values, cpd.values, threshold);
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
    Factor result = cpd.normalize(false);

    // Check new factor is normalized version
    for(int r = 0;r < expectedNormedTable.length;++r) {
      Assertions.assertArrayEquals(expectedNormedTable[r],
          ((ConditionalProbabilityDistribution)result).getValues()[r], threshold);
    }
    // Check existing factor has not changed
    for(int r = 0;r < expectedTable.length;++r) {
      Assertions.assertArrayEquals(expectedTable[r],
          cpd.getValues()[r], threshold);
    }

    cpd.normalize(true);
    for(int r = 0;r < expectedNormedTable.length;++r) {
      Assertions.assertArrayEquals(expectedNormedTable[r], cpd.getValues()[r], threshold);
    }
  }

  @Test void testReduce() {
    double[][] expected = new double[][]{
        {0.9},
        {0.08},
        {0.02}
    };

    EventStream reduction = new EventStream("D=0,I=1");

    Factor factor = cpd.reduce(reduction.getEvents(), false);
    // Check new factor is reduced version
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("G"), factor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(3),
        ((ConditionalProbabilityDistribution)factor).getCardinality()));
    for(int r = 0;r < expected.length;++r) {
      Assertions.assertArrayEquals(expected[r],
          ((ConditionalProbabilityDistribution)factor).getValues()[r], threshold);
    }
    // Check existing factor has not changed
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList( "I", "D", "G"), cpd.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2, 2, 3),
        cpd.getCardinality()));
    for(int r = 0;r < expected.length;++r) {
      Assertions.assertArrayEquals(expectedTable[r],
          cpd.getValues()[r], threshold);
    }

    cpd.reduce(reduction.getEvents(), true);

    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList( "G"), cpd.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList( 3), cpd.getCardinality()));
    for(int r = 0;r < expected.length;++r) {
      Assertions.assertArrayEquals(expected[r], cpd.getValues()[r], threshold);
    }
  }

  /**
   * TODO Decide what to do in this case
  @Test void testReduceCPDVariable() {
    double[][] expected = new double[][]{
        {1.0, 1.0, 1.0, 1.0},
    };

    List<Pair<String, Integer>> reduction = new ArrayList<>();
    reduction.add(Pair.of("G", 0));

    cpd.reduce(reduction, true);

    Assertions.assertTrue(cpd.getVariable().equals("G"));
    Assertions.assertTrue(cpd.getVariableCardinality() == 1);
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("I", "D", "G"), cpd.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2, 2, 1), cpd.getCardinality()));
    for(int r = 0;r < expected.length;++r) {
      Assertions.assertArrayEquals(expected[r], cpd.getValues()[r], threshold);
    }
  }
   */

  @Test void testMarginalize() {
    double[][] expected = new double[][]{
        {0.54, 0.23},
        {0.272, 0.27},
        {0.188, 0.5}
    };

    Factor factor = cpd
        .marginalize(Lists.newArrayList("I"), false);

    // Check new factor is marginalized version
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("D", "G"), factor.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2, 3),
        ((ConditionalProbabilityDistribution)factor).getCardinality()));
    for(int r = 0;r < expectedNormedTable.length;++r) {
      Assertions.assertArrayEquals(expected[r], ((ConditionalProbabilityDistribution)factor).getValues()[r], threshold);
    }
    // Check existing factor has not changed
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("I", "D", "G"), cpd.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2, 2, 3), cpd.getCardinality()));
    for(int r = 0;r < expectedTable.length;++r) {
      Assertions.assertArrayEquals(expectedTable[r], cpd.getValues()[r], threshold);
    }

    cpd.marginalize(Lists.newArrayList("I"), true);

    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("D", "G"), cpd.getScope()));
    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList(2, 3), cpd.getCardinality()));
    for(int r = 0;r < expectedNormedTable.length;++r) {
      Assertions.assertArrayEquals(expected[r], cpd.getValues()[r], threshold);
    }
  }
}
