package examples;

import inference.Inference;
import inference.exact.VariableElimination;
import models.BayesianNetwork;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static examples.Burglary.BurglaryExample;

/**
 * Tests for the Toy Burglary Example
 */
class BurglaryTest {
  /**
   * Tests taken from:
   * https://www.cs.utexas.edu/~mooney/cs343/slide-handouts/bayes-nets.pdf
   */
  @Test void testBurglaryExample() {
    BayesianNetwork result = BurglaryExample();
    Inference inference = new VariableElimination(result);

    Assertions.assertEquals(0.016, inference.query("B=1|J=1"), 10e-4);
    Assertions.assertEquals(0.29, inference.query("B=1|J=1,M=1"), 10e-3);
    Assertions.assertEquals(0.76, inference.query("A=1|J=1,M=1"), 10e-3);
    Assertions.assertEquals(0.18, inference.query("E=1|J=1,M=1"), 10e-3);

    // Slides say 0.86
    Assertions.assertEquals(0.85, inference.query("J=1|B=1"), 10e-3);
    // Slides say 0.67
    Assertions.assertEquals(0.66, inference.query("M=1|B=1"), 10e-3);

    // Slides say 0.376
    Assertions.assertEquals(0.373, inference.query("B=1|A=1"), 10e-4);
    Assertions.assertEquals(0.003, inference.query("B=1|A=1,E=1"), 10e-4);

    // Diagnostic and Causal
    Assertions.assertEquals(0.03, inference.query("A=1|J=1,E=0"), 10e-3);
    // Diagnostic and Intercausal
    Assertions.assertEquals(0.017, inference.query("B=1|J=1,E=0"), 10e-4);
  }

  /**
   * Test whether main runs without error.
   */
  @Test void testMain() {
    Burglary.main(new String[] { "args" });
    Assertions.assertTrue(true);
  }
}
