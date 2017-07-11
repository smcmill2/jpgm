package examples;

import com.google.common.collect.Lists;
import factors.discrete.ConditionalProbabilityDistribution;
import inference.Inference;
import inference.exact.VariableElimination;
import models.BayesianNetwork;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Burglary Toy Example for demonstrating capabilities and inference.
 *
 * Examples taken from:
 * https://www.cs.utexas.edu/~mooney/cs343/slide-handouts/bayes-nets.pdf
 */
public class Burglary {
  /**
   * Create and return a Bayesian Network representing whether
   * John or Mary calls given an alarm went off. Either a burglar or earthquake
   * could possibly set off the alarm.
   *
   * @return a BayesianNetwork
   */
  public static BayesianNetwork BurglaryExample() {
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

    BayesianNetwork network = new BayesianNetwork();
    network.addEdge(burglary, alarm);
    network.addEdge(earthquake, alarm);
    network.addEdge(alarm, johnCalls);
    network.addEdge(alarm, maryCalls);

    return network;
  }

  public static void main(String[] args) {
    System.out.println("A toy class for demonstrating inference");

    BayesianNetwork model = BurglaryExample();
    Inference inference = new VariableElimination(model);

    System.out.println("\nDiagnostic Inference");
    inference.printQuery("B=1|J=1");
    inference.printQuery("B=1|J=1,M=1");
    inference.printQuery("A=1|J=1,M=1");
    inference.printQuery("E=1|J=1,M=1");

    System.out.println("\nCausal Inference");
    // Slides say 0.86
    inference.printQuery("J=1|B=1");
    // Slides say 0.67
    inference.printQuery("M=1|B=1");

    System.out.println("\nIntercausal Inference");
    // Slides say 0.376
    inference.printQuery("B=1|A=1");
    inference.printQuery("B=1|A=1,E=1");

    // Diagnostic and Causal
    System.out.println("\nMixed Inference");
    inference.printQuery("A=1|J=1,E=0", "Diagnostic and Causal");
    inference.printQuery("B=1|J=1,E=0", "Diagnostic and Intercausal");
  }
}
