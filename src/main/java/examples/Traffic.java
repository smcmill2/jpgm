package examples;

import com.google.common.collect.Lists;
import factors.discrete.ConditionalProbabilityDistribution;
import inference.Inference;
import inference.exact.VariableElimination;
import models.BayesianNetwork;
import org.apache.commons.lang3.tuple.Pair;

/**
 * A toy class to demonstrate the basic capabilities of inference using
 * discrete factors and a Bayesian network.
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
public class Traffic {
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

  public static void main(String[] args) {
    // @todo Flesh out the class
    System.out.println("A toy class for demonstrating inference");

    BayesianNetwork model = trafficBN();
    Inference ve = new VariableElimination(model);
    //System.out.println(ve.queryFactor(Lists.newArrayList("L")));
    /*
    System.out.println(ve.queryFactor(Lists.newArrayList("L"),
        Lists.newArrayList(Pair.of("G", 1)))
    );
    System.out.println(ve.queryFactor(Lists.newArrayList("A"),
        Lists.newArrayList(Pair.of("T", 1))));
        */
  }
}
