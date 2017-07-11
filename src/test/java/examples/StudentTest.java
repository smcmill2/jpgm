package examples;

import com.google.common.collect.Lists;
import factors.Factor;
import factors.discrete.DiscreteFactor;
import inference.Inference;
import inference.exact.VariableElimination;
import models.BayesianNetwork;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import primitives.EventStream;

import java.util.List;

import static util.TestUtils.JPTEqualsVE;

/**
 * Tests for the Toy Student Network Example
 */
class StudentTest {
  double threshold = 10e-4;
  /**
   * Test the Student Bayesian Network with the queries that will be run
   * on it.
   *
   * http://www.cedar.buffalo.edu/~srihari/CSE674/Chap3/3.4-Reasoning&D-Separation.pdf
   */
  @Test void testBasicStudentBN() {
    BayesianNetwork model = Student.basicStudentBN();
    Inference inference = new VariableElimination(model);

    Assertions.assertEquals(0.502, inference.query("L=1"), threshold);
    Assertions.assertEquals(0.389, inference.query("L=1|I=0"), threshold);
    Assertions.assertEquals(0.513, inference.query("L=1|I=0,D=0"), threshold);

    Assertions.assertEquals(0.300, inference.query("I=1"), threshold);
    Assertions.assertEquals(0.079, inference.query("I=1|G=2"), threshold);
    Assertions.assertEquals(0.400, inference.query("D=1"), threshold);
    Assertions.assertEquals(0.629, inference.query("D=1|G=2"), threshold);
    Assertions.assertEquals(0.14, inference.query("I=1|L=0"), threshold);
    Assertions.assertEquals(0.079, inference.query("I=1|G=2,L=0"), threshold);

    Assertions.assertEquals(0.578, inference.query("I=1|G=2,S=1"), threshold);
    Assertions.assertEquals(0.629, inference.query("D=1|G=2"), threshold);
    Assertions.assertEquals(0.76, inference.query("D=1|G=2,S=1"), threshold);

    Assertions.assertEquals(0.11, inference.query("I=1|G=2,D=1"), threshold);
    Assertions.assertEquals(0.175, inference.query("I=1|G=1"), threshold);
    Assertions.assertEquals(0.34, inference.query("I=1|G=1,D=1"), threshold);
  }

  /**
   * Test whether main runs without error.
   */
  @Test void testMain() {
    Student.main(new String[] { "args" });
    Assertions.assertTrue(true);
  }
}
