package examples;

import com.google.common.collect.Lists;
import factors.discrete.ConditionalProbabilityDistribution;
import factors.discrete.DiscreteFactor;
import inference.Inference;
import inference.exact.VariableElimination;
import models.BayesianNetwork;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A toy class to demonstrate the basic capabilities of inference using
 * discrete factors and a Bayesian network.
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
public class Student {
  public static BayesianNetwork basicStudentBN() {
    ConditionalProbabilityDistribution difficulty =
        new ConditionalProbabilityDistribution("D", 2,
            new double[][]{{0.6, 0.4}});
    ConditionalProbabilityDistribution intelligence =
        new ConditionalProbabilityDistribution("I", 2,
            new double[][]{{0.7, 0.3}});
    ConditionalProbabilityDistribution grade =
        new ConditionalProbabilityDistribution("G", 3,
            Lists.newArrayList("I", "D"), Lists.newArrayList(2, 2),
            new double[][]{
        {0.30, 0.05, 0.90, 0.50},
        {0.40, 0.25, 0.08, 0.30},
        {0.30, 0.70, 0.02, 0.20}
    });
    ConditionalProbabilityDistribution sat =
        new ConditionalProbabilityDistribution("S", 2,
            Lists.newArrayList("I"), Lists.newArrayList(2),
            new double[][]{{0.95, 0.20}, {0.05, 0.80}});
    ConditionalProbabilityDistribution letter =
        new ConditionalProbabilityDistribution("L", 2,
            Lists.newArrayList("G"), Lists.newArrayList(3),
            new double[][]{{0.10, 0.40, 0.99}, {0.90, 0.60, 0.01}});

    BayesianNetwork network = new BayesianNetwork();
    network.addEdge(difficulty, grade);
    network.addEdge(intelligence, grade);
    network.addEdge(intelligence, sat);
    network.addEdge(grade, letter);

    return network;
  }

  public static void main(String[] args) {
    System.out.println("A toy class for demonstrating inference");

    BayesianNetwork model = basicStudentBN();
    Inference ve = new VariableElimination(model);
    List<Pair<String, Integer>> qVars = Lists.newArrayList();

    System.out.println("Probability class is difficult:");
    qVars = Lists.newArrayList(Pair.of("D", 1));
    System.out.println(String.format("P(d=1): %f", ve.query(qVars)));
    System.out.println("Probability student is intelligent:");
    qVars = Lists.newArrayList(Pair.of("I", 1));
    System.out.println(String.format("P(i=1): %f", ve.query(qVars)));

    System.out.println("Student gets a C");
    List<Pair<String, Integer>> evidence = Lists.newArrayList(Pair.of("G", 2));

    System.out.println("Probability class is difficult:");
    qVars = Lists.newArrayList(Pair.of("D", 1));
    System.out.println(String.format("P(d=1|g=2): %f", ve.query(qVars, evidence)));
    System.out.println("Probability student is intelligent:");
    qVars = Lists.newArrayList(Pair.of("I", 1));
    System.out.println(String.format("P(i=1|g=2): %f", ve.query(qVars, evidence)));

    System.out.println("Student Aced the SAT");
    evidence.add(Pair.of("S", 1));

    System.out.println("Probability class is difficult:");
    qVars = Lists.newArrayList(Pair.of("D", 1));
    System.out.println(String.format("P(d=1|g=2,s=1): %f", ve.query(qVars, evidence)));
    System.out.println("Probability student is intelligent:");
    qVars = Lists.newArrayList(Pair.of("I", 1));
    System.out.println(String.format("P(i=1|g=2,s=1): %f", ve.query(qVars, evidence)));
  }
}
