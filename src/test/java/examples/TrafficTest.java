package examples;

import inference.Inference;
import inference.exact.VariableElimination;
import models.BayesianNetwork;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Traffic Toy Example
 */
class TrafficTest {
  /**
   * Test the Traffic Bayesian Network with the queries that will be run
   * on it.
   */
  @Test void testTrafficBN() {
    BayesianNetwork result = Traffic.trafficBN();
    Inference inference = new VariableElimination(result);

    Assertions.assertEquals(0.348, inference.query("A=1|T=1"), 10e-4);
    Assertions.assertEquals(0.143, inference.query("A=1|T=1,P=1"), 10e-4);
  }

  /**
   * Test whether main runs without error.
   */
  @Test void testMain() {
    Traffic.main(new String[] { "args" });
    Assertions.assertTrue(true);
  }
}
