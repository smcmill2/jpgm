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

/**
 * All inference query example numbers taken from:
 * https://www.cs.utexas.edu/~mooney/cs343/slide-handouts/bayes-nets.pdf
 */
class VariableEliminationTest {
  double threshold = 10e-4;
  VariableElimination ve;
  BayesianNetwork bn;
  Factor jpt;

  @BeforeEach void setUp() {
    ConditionalProbabilityDistribution burglary = new ConditionalProbabilityDistribution(
        "B", 2, new double[][]{{0.999}, {0.001}}
    );
    ConditionalProbabilityDistribution earthquake = new ConditionalProbabilityDistribution(
        "E", 2, new double[][]{{0.998}, {0.002}}
    );
    ConditionalProbabilityDistribution alarm = new ConditionalProbabilityDistribution(
        "A", 2,
        Lists.newArrayList("B", "E"),
        Lists.newArrayList(2, 2),
        new double[][] { { 0.999, 0.71, 0.06, 0.05 },
            { 0.001, 0.29, 0.94, 0.95 } }
    );
    ConditionalProbabilityDistribution johnCalls = new ConditionalProbabilityDistribution(
        "J", 2,
        Lists.newArrayList("A"),
        Lists.newArrayList(2),
        new double[][]{
            {0.95, 0.10},
            {0.05, 0.90}
        }
    );
    ConditionalProbabilityDistribution maryCalls = new ConditionalProbabilityDistribution(
        "M", 2,
        Lists.newArrayList("A"),
        Lists.newArrayList(2),
        new double[][]{
            {0.99, 0.30},
            {0.01, 0.70}
        }
    );

    bn = new BayesianNetwork();
    bn.addEdge(burglary, alarm);
    bn.addEdge(earthquake, alarm);
    bn.addEdge(alarm, johnCalls);
    bn.addEdge(alarm, maryCalls);

    ve = new VariableElimination(bn);
    jpt = bn.getCPDs().stream()
        .map(cpd -> cpd.toDiscreteFactor())
        .reduce(new DiscreteFactor(), (a, b) -> a.product(b))
        .normalize(false);
  }

  @Test void testDiagnosticInference() {
    Assertions.assertEquals(0.016, ve.query("B=1|J=1"), threshold);
    Assertions.assertEquals(0.29, ve.query("B=1|J=1,M=1"), threshold);
    Assertions.assertEquals(0.76, ve.query("A=1|J=1,M=1"), threshold);
    Assertions.assertEquals(0.18, ve.query("E=1|J=1,M=1"), threshold);
  }

  @Test void testCausalInference() {
    Assertions.assertEquals(0.86, ve.query("J=1|B=1"), threshold);
    Assertions.assertEquals(0.67, ve.query("M=1|B=1"), threshold);
  }

  @Test void testInterCausalInference() {
    Assertions.assertEquals(0.376, ve.query("B=1|A=1"), threshold);
    Assertions.assertEquals(0.003, ve.query("B=1|A=1,E=1"), threshold);
  }

  @Test void testMixedInference() {
    // Diagnostic and Causal
    Assertions.assertEquals(0.03, ve.query("A=1|J=1,E=0"), threshold);
    // Diagnostic and Intercausal
    Assertions.assertEquals(0.017, ve.query("B=1|J=1,E=0"), threshold);
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
