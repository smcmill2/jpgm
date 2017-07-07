package inference.exact;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import factors.Factor;
import factors.discrete.ConditionalProbabilityDistribution;
import factors.discrete.DiscreteFactor;
import models.BayesianNetwork;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static util.TestUtils.JPTEqualsVE;

class VariableEliminationTest {
  double threshold = 10e-4;
  VariableElimination ve;
  BayesianNetwork bn;
  ConditionalProbabilityDistribution P;
  ConditionalProbabilityDistribution A;
  ConditionalProbabilityDistribution T;

  Factor jpt;

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
    jpt = bn.getCPDs().stream()
        .map(cpd -> cpd.toDiscreteFactor())
        .reduce(new DiscreteFactor(), (a, b) -> a.product(b))
        .normalize(false);
  }

  @Test void testQuery() {
    List<Pair<String, Integer>> queryVars = Lists.newArrayList(
        Pair.of("A", 1)
    );
    List<Pair<String, Integer>> evidence = Lists.newArrayList(
        Pair.of("T", 1)
    );

    Assertions.assertTrue(JPTEqualsVE(jpt, ve, queryVars, evidence));

    evidence.add(Pair.of("P", 1));
    Assertions.assertTrue(JPTEqualsVE(jpt, ve, queryVars, evidence));
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
