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
import primitives.Event;
import primitives.EventStream;
import util.Misc;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class VariableElimination implements Inference {
  private BayesianNetwork model;

  public VariableElimination(BayesianNetwork model) {
    this.model = model;
  }

  public void printQuery(String queryString) {
    EventStream es = new EventStream(queryString);

    System.out.println(String.format("P(%s): %f",
        es.toString(), this.query(queryString)));
  }

  public void printQuery(String queryString, String annotation) {
    System.out.println(annotation);
    this.printQuery(queryString);
  }

  public double query(List<Event> variables) {
    return query(variables, Lists.newArrayList());
  }

  @Override public double query(String queryString) {
    EventStream es = new EventStream(queryString);
    return query(es.getEvents(), es.getObservations());
  }

  @Override public double query(List<Event> variables,
      List<Event> evidence) {
    DiscreteFactor f = queryFactor(variables, evidence);

    return variables.stream()
        .mapToDouble(v -> f.getValue(v))
        .reduce(1.0, (a, b) -> a * b);
  }

  public DiscreteFactor queryFactor(List<Event> variables) {
    DiscreteFactor f = this.queryModel(this.model, variables, Lists.newArrayList());
    return f;
  }

  public DiscreteFactor queryFactor(List<Event> variables, List<Event> evidence) {
    DiscreteFactor f = this.queryModel(this.model, variables, evidence);

    return f;
  }

  private DiscreteFactor queryModel(BayesianNetwork model, List<Event> variables,
      List<Event> evidence) {
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
        factors.put(v, f.factorString());
      }
      factorMap.put(f.factorString(), f);
    }

    Set<String> notX = Sets.newHashSet(variables.stream()
      .map(Event::getVariable)
    .collect(Collectors.toList()));
    if(evidence != null) {
      notX = Sets.union(notX, Sets.newHashSet(evidence.stream()
          .map(Event::getVariable)
          .collect(Collectors.toList())
      ));
    }

    eliminationOrder.removeAll(notX);  // Factor into order creation

    // Instantiate Observed Evidence
    for(Event e: evidence) {
      Set<String> observed = factors.get(e.getVariable());

      for(String o : observed) {
        Factor f = factorMap.get(o);
        Factor reducedFactor = f.reduce(Lists.newArrayList(e), false);

        factors = HashMultimap.create(Multimaps.filterEntries(factors,
          entry -> !(entry.getKey().equals(e.getVariable()) || entry.getValue().equals(o))));

        for(String v : reducedFactor.getScope()) {
          factors.put(v, reducedFactor.factorString());
        }

        factorMap.remove(o);
        factorMap.put(reducedFactor.factorString(), reducedFactor);
      }
    }

    // Eliminate variables in Z
    for(String Z : eliminationOrder) {
      //System.out.println(String.format("Sum-Out(%s)\n", Z));
      // Sum Out Z from all containing factors
      Set<String> containingFactors = factors.get(Z);

      //System.out.println(String.format("Containing Factors:\n"));
      /*
      containingFactors.stream()
          .forEach(f -> System.out.println(factorMap.get(f).toString()));
      */
      //System.out.println("\n");
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
        factors.put(v, newFactor.factorString());
      }
      factorMap.put(newFactor.factorString(), newFactor);

      //System.out.println(String.format("Adding New Factor:\n %s", newFactor.toString()));
    }

    // Return product of remaining factors and normalize
    return (DiscreteFactor)Sets.newHashSet(factorMap.values()).stream()
        .reduce(new DiscreteFactor(),
            (a, b) -> ((DiscreteFactor)a).product((DiscreteFactor)b))
        .normalize(false);
  }

  @Override public String mapQuery(List<Event> variables) {
    return null;
  }

  @Override public String mapQuery(List<Event> variables,
      List<Event> evidence) {
    return null;
  }
}
