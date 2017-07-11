package examples;

import com.google.common.collect.Lists;
import factors.discrete.ConditionalProbabilityDistribution;
import inference.Inference;
import inference.exact.VariableElimination;
import models.BayesianNetwork;

/**
 * A Toy class representing the Student Network from Probabilistic Graphical
 * Models by Daphne Koller. The purpose is to provide a basic Bayesian Network
 * on which to learn the inference capabilities of the library.
 *
 * All examples taken from:
 * http://www.cedar.buffalo.edu/~srihari/CSE674/Chap3/3.4-Reasoning&D-Separation.pdf
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
    Inference inference = new VariableElimination(model);

    System.out.println("\nExamples of Causal Reasoning");
    inference.printQuery("L=1", "Probability of getting a Letter");
    inference.printQuery("L=1|I=0", "but our student isn't intelligent.");
    inference.printQuery("L=1,|I=0,D=0", "and the class wasn't difficult.");

    System.out.println("\nExamples of Evidential Reasoning");
    inference.printQuery("I=1", "A priori our student is intelligent.");
    inference.printQuery("I=1|G=2", "but he received a C.");
    inference.printQuery("D=1", "A priori the class is difficult.");
    inference.printQuery("D=1|G=2", "Probability class is difficult increases.");
    inference.printQuery("I=1|L=0", "Not sure of students grade, but know he received a letter.");
    inference.printQuery("I=1|L=0,G=2", "After finding out the grade letter no longer matters.");

    System.out.println("\nExamples of Intercausal Reasoning");
    inference.printQuery("I=1|G=2,S=1", "A high SAT score outweighs a bad grade");
    inference.printQuery("I=1|G=2", "Recall the effect of a bad grade on intelligence.");
    inference.printQuery("D=1|G=2,S=1", "It also means the class was probably difficult.");
    inference.printQuery("D=1|G=2", "Recall the effect of a bad grade on course difficulty.");

    System.out.println("\nExplaining Away");
    inference.printQuery("I=1|G=2", "Recall the effect of a bad grade on intelligence.");
    inference.printQuery("I=1|G=2,D=1", "Poor performance is partially explained by course difficulty.");
    inference.printQuery("I=1|G=1", "Another example.");
    inference.printQuery("I=1|G=1,D=1", "Again performance is partially explained by course difficult.");
  }
}
