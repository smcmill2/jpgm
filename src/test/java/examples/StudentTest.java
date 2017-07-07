package examples;

import com.google.common.collect.Lists;
import factors.Factor;
import factors.discrete.DiscreteFactor;
import inference.exact.VariableElimination;
import models.BayesianNetwork;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static util.TestUtils.JPTEqualsVE;

/**
 * Created by smcmillan on 7/7/17.
 */
class StudentTest {
  static double threshold = 10e-8;

  @Test void testBasicStudentBN() {
    BayesianNetwork model = Student.basicStudentBN();
    Factor jpt = model.getCPDs().stream()
        .map(cpd -> cpd.toDiscreteFactor())
        .reduce(new DiscreteFactor(), (a, b) -> a.product(b))
        .normalize(false);

    VariableElimination ve = new VariableElimination(model);

    // Intercausal Reasoning
    /**
     * Class is hard, student gets a C
     *
     */
    List<Pair<String, Integer>> qVar = Lists.newArrayList(Pair.of("I", 1));
    List<Pair<String, Integer>> evidence = Lists.newArrayList(
        Pair.of("D", 1),
        Pair.of("G", 1)
    );

    Assertions.assertTrue(JPTEqualsVE(jpt, ve, qVar, evidence),
        "P(I_1): 0.3 -> ~0.11, Actually getting 0.096");

    /**
     * Student Aces the SAT and gets a C
     */
    evidence = Lists.newArrayList(
        Pair.of("G", 2)
    );
    Assertions.assertTrue(JPTEqualsVE(jpt, ve, qVar, evidence),
        "Class is difficult, P(D_1): 0.4 -> ~0.63");

    evidence.add(Pair.of("S", 1));
    qVar = Lists.newArrayList(Pair.of("I", 1));
    Assertions.assertTrue(JPTEqualsVE(jpt, ve, qVar, evidence),
        "Student is intelligent, P(I_1): 0.3 -> ~0.58");

  }

  @Test void testMain() {
    Student.main(new String[] { "args" });
    Assertions.assertTrue(true,
        "Student Toy Examples ran without issue.\n");
  }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme