package inference.exact;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.*;
import com.google.common.graph.Graphs;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;
import factors.Factor;
import factors.discrete.ConditionalProbabilityDistribution;
import factors.discrete.DiscreteFactor;
import inference.Inference;
import models.BayesianNetwork;
import org.apache.commons.lang3.tuple.Pair;
import util.Misc;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class VariableElimination implements Inference {
  private BayesianNetwork model;

  public VariableElimination(BayesianNetwork model) {
    this.model = model;
  }

  @Override public DiscreteFactor query(List<String> variables) {
    DiscreteFactor f = this.queryModel(this.model, variables, null);
    return f;
  }

  @Override public DiscreteFactor query(List<String> variables, List<Pair<String, Integer>> evidence) {
    DiscreteFactor f = this.queryModel(this.model, variables, evidence);

    return f;
  }

  /*
  public double query(List<Pair<String, Integer>> variables, List<Pair<String, Integer>> evidence) {
    DiscreteFactor f = this.queryModel(this.model, variables.stream()
        .map(pair -> pair.getLeft()).collect(Collectors.toList()), evidence);

    return variables.stream()
        .mapToDouble(v -> f.getValue(v))
        .reduce(1.0, (a, b) -> a * b);
  }
  */

  private DiscreteFactor queryModel(BayesianNetwork model, List<String> variables,
      List<Pair<String, Integer>> evidence) {
    MutableGraph<String> graph = Graphs.copyOf(model.getMoralStructure());
    List<String> eliminationOrder = EliminationOrdering.getOrdering(
        ImmutableGraph.copyOf(graph),
        EliminationOrdering.NodeCost.MIN_FILL);

    HashMultimap<String, String> factors = HashMultimap.create();
    Map<String, Factor> factorMap = new HashMap<>();
    // Construct a factor for each CPD
    for(ConditionalProbabilityDistribution cpd : model.getCPDs()) {
      Factor f = cpd.toDiscreteFactor();
      for(String v : cpd.getScope()) {
        factors.put(v, ((DiscreteFactor) f).factorString());
      }
      factorMap.put(((DiscreteFactor)f).factorString(), f);
    }

    Set<String> notX = Sets.union(Sets.newHashSet(variables),
        Sets.newHashSet(evidence.stream().map(e -> e.getLeft()).collect(
            Collectors.toList())));
    eliminationOrder.removeAll(notX);  // Factor into order creation

    // Instantiate Observed Evidence
    for(Pair<String, Integer> e : evidence) {
      Set<String> observed = factors.get(e.getLeft());

      for(String o : observed) {
        Factor f = factorMap.get(o);
        Factor reducedFactor = f.reduce(Lists.newArrayList(e), false);

        String factorString = o;
        List<String> fStrList = Lists.newArrayList(Splitter.on(")").splitToList(factorString));
        fStrList.set(fStrList.size()-1,
            String.format("%s)", Misc.joinPair(e, "=")));
        if(factorString.contains("|")) {
          factorString = Joiner.on(",").join(fStrList);
        } else {
          factorString = Joiner.on(" | ").join(fStrList);
        }

        factors = HashMultimap.create(Multimaps.filterEntries(factors,
          entry -> !(entry.getKey().equals(e.getLeft()) || entry.getValue().equals(o))));

        for(String v : reducedFactor.getScope()) {
          factors.put(v, factorString);
        }

        factorMap.remove(o);
        factorMap.put(factorString, reducedFactor);
      }
    }

    // Eliminate variables in Z
    for(String Z : eliminationOrder) {
      System.out.println(String.format("Sum-Out(%s)\n", Z));
      // Sum Out Z from all containing factors
      Set<String> containingFactors = factors.get(Z);

      System.out.println(String.format("Containing Factors:\n"));
      containingFactors.stream()
          .forEach(f -> System.out.println(factorMap.get(f).toString()));
      System.out.println("\n");
      Factor newFactor = containingFactors.stream()
          .map(factorMap::get)
          .reduce(new DiscreteFactor(),
              (a, b) -> ((DiscreteFactor)a).product((DiscreteFactor)b));

      // Remove the old entries
      factors = HashMultimap.create(Multimaps.filterEntries(factors, entry ->
          !(entry.getKey().equals(Z) || containingFactors.contains(entry.getValue()))
      ));
      containingFactors.stream()
          .forEach(factorMap::remove);

      // Add the new factor
      for(String v : newFactor.getScope()) {
        factors.put(v, ((DiscreteFactor)newFactor).factorString());
      }
      factorMap.put(((DiscreteFactor)newFactor).factorString(), newFactor);

      System.out.println(String.format("Adding New Factor: %s\n", newFactor.toString()));
    }

    // Return product of remaining factors and normalize
    return (DiscreteFactor)Sets.newHashSet(factorMap.values()).stream()
        .reduce(new DiscreteFactor(),
            (a, b) -> ((DiscreteFactor)a).product((DiscreteFactor)b))
        .normalize(false);
  }

  @Override public String mapQuery(List<String> variables) {
    return null;
  }

  @Override public String mapQuery(List<String> variables,
      List<Pair<String, Integer>> evidence) {
    return null;
  }
}
