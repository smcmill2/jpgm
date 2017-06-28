package models;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.sun.org.apache.xpath.internal.operations.Mult;
import factors.discrete.ConditionalProbabilityDistribution;
import util.Misc;

import java.util.*;
import java.util.stream.Collectors;

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

  public BayesianNetwork() {
    this.varMap = new HashMap<>();
    this.predecessors = HashMultimap.create();
    this.successors = HashMultimap.create();
    this.g = GraphBuilder.directed().allowsSelfLoops(false).build();
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

  public void addEdge(String u, String v) {
    this.addEdge(this.varMap.get(u), this.varMap.get(v));
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
}
