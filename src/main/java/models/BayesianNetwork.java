package models;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import factors.discrete.ConditionalProbabilityDistribution;
import org.apache.commons.lang3.tuple.Pair;
import util.Misc;

import java.util.*;

/**
 * Implementation of a Bayesian Network on which queries can be performed.
 *
 * @version 1.0.0
 *
 * @autho Sean McMillan
 */
public class BayesianNetwork {
  private Map<String, ConditionalProbabilityDistribution> varMap;
  private SetMultimap<String, String> predecessors;
  private SetMultimap<String, String> successors;
  private MutableGraph<String> g;
  private boolean dirty;  // TODO introduce caching of answer/queries

  public BayesianNetwork() {
    this.varMap = new HashMap<>();
    this.predecessors = HashMultimap.create();
    this.successors = HashMultimap.create();
    this.g = GraphBuilder.directed().allowsSelfLoops(false).build();
  }

  public List<ConditionalProbabilityDistribution> getCPDs() {
    return Lists.newArrayList(this.varMap.values());
  }

  public void addEdge(String u, String v) {
    this.addEdge(this.varMap.get(u), this.varMap.get(v));
  }

  public void addEdge(ConditionalProbabilityDistribution u,
      ConditionalProbabilityDistribution v) {
    Preconditions.checkNotNull(u);
    Preconditions.checkNotNull(v);
    Preconditions.checkArgument(!this.createsCycle(u.getVariable(), v.getVariable()),
        String.format("Adding edge %s -> %s would create a cycle.", u.getVariable(), v.getVariable()));

    this.varMap.put(u.getVariable(), u);
    this.varMap.put(v.getVariable(), v);
    this.g.putEdge(u.getVariable(), v.getVariable());
  }

  public List<String> getEliminationOrder(List<String> variables, List<String> evidence) {
    List<String> order = Misc.eliminationOrder(this.g);
    order.removeAll(variables);
    if(evidence != null) {
      order.removeAll(evidence);
    }
    return order;
  }

  private boolean createsCycle(String u, String v) {
    boolean cycle = false;

    g.putEdge(u, v);
    if(Misc.hasCycles(g)) {
      g.removeEdge(u, v);
      cycle = true;
    }

    return cycle;
  }

  public boolean isDSep(String X, String Y, Set<String> E) {
    return !this.activeTrails(X, E).contains(Y);
  }

  private Set<String> activeTrails(String source, Set<String> observations) {
    Stack<String> visitList = new Stack<>();
    visitList.addAll(observations);
    Set<String> ancestors = Sets.newHashSet();

    // Insert all ancestors of evidence Z into
    while(visitList.size() > 0) {
      String y = visitList.pop();
      if(!ancestors.contains(y)) {
        visitList.addAll(this.g.predecessors(y));
      }
      ancestors.add(y);
    }

    // Traverse active trails starting from source
    Stack<Pair<String, Boolean>> ndPairs = new Stack<>();
    ndPairs.add(Pair.of(source, true));
    Set<Pair<String, Boolean>> visited = Sets.newHashSet();
    Set<String> reachable = Sets.newHashSet();
    while(ndPairs.size() > 0) {
      Pair<String, Boolean> ndPair = ndPairs.pop();
      String node = ndPair.getLeft();
      boolean direction = ndPair.getRight().booleanValue();
      if(!visited.contains(ndPair)) {
        if(!observations.contains(node)) {
          reachable.add(node);
        }
        visited.add(ndPair);

        if(direction && !observations.contains(node)) {
          for(String parent : this.g.predecessors(node)) {
            ndPairs.push(Pair.of(parent, true));
          }
          for(String child : this.g.successors(node)) {
            ndPairs.push(Pair.of(child, false));
          }
        } else if(!direction) {
          if(!observations.contains(node)) {
            for(String child : this.g.successors(node)) {
              ndPairs.push(Pair.of(child, false));
            }
          }
          if(ancestors.contains(node)) {
            for(String parent : this.g.predecessors(node)) {
              ndPairs.push(Pair.of(parent, true));
            }
          }
        }
      }
    }

    return reachable;
  }


}
