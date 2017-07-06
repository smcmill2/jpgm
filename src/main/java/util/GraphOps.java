package util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by smcmillan on 7/5/17.
 */
public class GraphOps {
  /**
   * Return the moralized graph where all pairs of parents of each node in the
   * directed graph are connected in the new undirected graph.
   *
   * TODO template this
   * @param g
   * @return
   */
  public static ImmutableGraph<String> getMoralGraph(MutableGraph<String> g) {
    MutableGraph<String> moralGraph = GraphBuilder.undirected().allowsSelfLoops(false).build();

    for (String node : g.nodes()) {
      for(Pair<String, String> couple : getUnwedParents(g, node)) {
        moralGraph.putEdge(couple.getLeft(), couple.getRight());
      }

      for (String child : g.successors(node)) {
        moralGraph.putEdge(node, child);
      }
    }

    return ImmutableGraph.copyOf(moralGraph);
  }

  /**
   * Given a graph and node return all the pairs of unwed parents for that node.
   * In a directed graph these are predecessors. In an undirected graph they
   * are simply adjacent nodes.
   *
   * @param g the graph
   * @param node the node in question
   * @return a list of pairs of unwed parents to node node
   */
  public static List<Pair<String, String>> getUnwedParents(Graph<String> g, String node) {
    Set<String> parents;
    Set<Pair<String, String>> unwedParents = new HashSet<>();

    if(g.isDirected()) {
      parents = g.predecessors(node);
    } else {
      parents = g.adjacentNodes(node);
    }

    for(List<String> couple : Sets.cartesianProduct(parents, parents)) {
      String c1 = couple.get(0);
      String c2 = couple.get(1);

      if(!c1.equals(c2)) {
        if(c1.compareTo(c2) > 0) {
          unwedParents.add(Pair.of(c2, c1));
        } else {
          unwedParents.add(Pair.of(c1, c2));
        }
      }
    }

    return Lists.newArrayList(unwedParents);
  }
}
