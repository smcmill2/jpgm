package inference.exact;

import com.google.common.collect.Lists;
import factors.discrete.ConditionalProbabilityDistribution;
import factors.discrete.DiscreteFactor;
import models.BayesianNetwork;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VariableEliminationTest {
  double threshold = 10e-4;
  VariableElimination ve;
  BayesianNetwork bn;
  ConditionalProbabilityDistribution P;
  ConditionalProbabilityDistribution A;
  ConditionalProbabilityDistribution T;

  @BeforeEach void setUp() {
    P = new ConditionalProbabilityDistribution("P", 2,
        new double[][]{{0.99}, {0.01}});
    A = new ConditionalProbabilityDistribution("A", 2,
        new double[][]{{0.9}, {0.1}});
    T = new ConditionalProbabilityDistribution("T", 2,
        Lists.newArrayList("P", "A"),
        Lists.newArrayList(2, 2),
        new double[][]{
            {0.9, 0.5, 0.4, 0.1},
            {0.1, 0.5, 0.6, 0.9}
        });

    bn = new BayesianNetwork();
    bn.addEdge(P, T);
    bn.addEdge(A, T);
    ve = new VariableElimination(bn);
  }

  @Test void testQuery() {
    double a1Prob = ve.query(Lists.newArrayList(Pair.of("A", 1)),
        Lists.newArrayList(Pair.of("T", 1)));
    Assertions.assertEquals(0.348, a1Prob, threshold);

    a1Prob = ve.query(Lists.newArrayList(Pair.of("A", 1)),
    Lists.newArrayList(Pair.of("T", 1),
        Pair.of("P", 1)));
    Assertions.assertEquals(0.143, a1Prob, threshold);
  }

  @Test void testQuery2() {
    Assertions.assertTrue(true);
  }

  @Test void testMapQuery() {
    Assertions.assertTrue(true);
  }

  @Test void testMapQuery2() {
    Assertions.assertTrue(true);
  }
}
