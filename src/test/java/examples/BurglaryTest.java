package examples;

import inference.Inference;
import inference.exact.VariableElimination;
import models.BayesianNetwork;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by smcmillan on 7/10/17.
 */
class BurglaryTest {
  double threshold = 10e-8;

  @Test void testBurglaryExample() {
    BayesianNetwork result = Burglary.BurglaryExample();
    Inference inf = new VariableElimination(result);
    Assertions.assertTrue(true);
  }

  @Test void testMain() {
    Burglary.main(new String[] { "args" });
    Assertions.assertTrue(true);
  }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme