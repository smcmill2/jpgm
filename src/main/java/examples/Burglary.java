package examples;

import com.google.common.collect.Lists;
import factors.discrete.ConditionalProbabilityDistribution;
import inference.Inference;
import inference.exact.VariableElimination;
import models.BayesianNetwork;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * https://www.cs.utexas.edu/~mooney/cs343/slide-handouts/bayes-nets.pdf
 * Created by smcmillan on 7/7/17.
 */
public class Burglary {
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

    System.out.println(burglary.toString());
    System.out.println(earthquake.toString());
    System.out.println(alarm.toString());
    System.out.println(johnCalls.toString());
    System.out.println(maryCalls.toString());

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
    Inference ve = new VariableElimination(model);
    List<Pair<String, Integer>> qVars = Lists.newArrayList();
    List<Pair<String, Integer>> evidence = Lists.newArrayList();

    System.out.println("Diagnostic Inference");
    qVars = Lists.newArrayList(Pair.of("B", 1));
    evidence = Lists.newArrayList(Pair.of("J", 1));
    System.out.println(String.format("P(B=1|J=1): %f", ve.query(qVars, evidence)));
    evidence.add(Pair.of("M", 1));
    System.out.println(String.format("P(B=1|J=1,M=1): %f", ve.query(qVars, evidence)));

  }
}
