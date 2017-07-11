package examples;

import com.google.common.collect.Lists;
import factors.discrete.ConditionalProbabilityDistribution;
import inference.Inference;
import inference.exact.VariableElimination;
import models.BayesianNetwork;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A toy class to demonstrate the basic capabilities of inference using
 * discrete factors and a Bayesian network, specifically diagnostic
 * and intercausal reasoning methods.
 *
 * Example taken from:
 * https://matrivian.wordpress.com/2014/02/21/inter-causal-reasoning/
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
public class Traffic {
  /**
   * A Bayesian Network constructed from the example listed above to
   * demonstrate diagnostic and intercausal reasoning.
   *
   * @return a BayesianNetwork
   */
  public static BayesianNetwork trafficBN() {
    ConditionalProbabilityDistribution president =
        new ConditionalProbabilityDistribution("P", 2,
            new double[][]{{0.99}, {0.01}});
    ConditionalProbabilityDistribution accident =
        new ConditionalProbabilityDistribution("A", 2,
            new double[][]{{0.9}, {0.1}});
    ConditionalProbabilityDistribution traffic =
        new ConditionalProbabilityDistribution("T", 2,
            Lists.newArrayList("P", "A"), Lists.newArrayList(2, 2),
            new double[][]{
        {0.90, 0.50, 0.40, 0.10},
        {0.10, 0.50, 0.60, 0.90}
    });

    BayesianNetwork network = new BayesianNetwork();
    network.addEdge(president, traffic);
    network.addEdge(accident, traffic);

    return network;
  }

  /**
   * A short program to display the results of several queries.
   *
   * @param args
   */
  public static void main(String[] args) {
    // @todo Flesh out the class
    System.out.println("A toy class for demonstrating inference");

    BayesianNetwork model = trafficBN();
    Inference ve = new VariableElimination(model);

    System.out.println("Diagnostic Reasoning:");
    System.out.println("Probability there was an accident"
        + "given there is traffic.");
    System.out.println("Our query would then look like P(A=1|T=1)");
    System.out.println(String.format("P(A=1|T=1) = %f\n",
        ve.query("A=1|T=1")));

    System.out.println("Intercausal Reasoning:");
    System.out.println("Probability there was an accident"
        + "given there is traffic and the president is in town.");
    System.out.println("Our query would then look like P(A=1|T=1,A=1)");
    System.out.println(String.format("P(A=1|T=1) = %f\n",
        ve.query("A=1|T=1,P=1")));
  }
}
