package inference.exact;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.graph.*;
import it.unimi.dsi.fastutil.PriorityQueues;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static util.GraphOps.getUnwedParents;

/**
 * Created by smcmillan on 7/5/17.
 */
public class EliminationOrdering {
  public enum NodeCost {
    MIN_NEIGHBORS,  // Total Number of Neighbors of Node
    MIN_WEIGHT,  // Total Cardinality of Factor of Node
    MIN_FILL,  // Number of Fill Edges Added by Removing Node
    WEIGHTED_MIN_FILL  // Total Weight of New Fill Edges
  }

  public static List<String> getOrdering(ImmutableGraph<String> g) {
    return getOrdering(g, NodeCost.MIN_NEIGHBORS);
  }

  public static List<String> getOrdering(ImmutableGraph<String> g, NodeCost heuristic) {
    List<String> order = new ArrayList<>();
    MutableGraph<String> graph = Graphs.copyOf(g);

    while(graph.nodes().size() > 0) {
      String node = getMinCostNode(graph, heuristic);

      Set<String> neighbors = graph.adjacentNodes(node);
      for(List<String> pairs : Sets.cartesianProduct(neighbors, neighbors)) {
        String n1 = pairs.get(0);
        String n2 = pairs.get(1);
        if(!n1.equals(n2)) {
          graph.putEdge(n1, n2);
        }
      }

      graph.removeNode(node);
      order.add(node);
    }

    return order;
  }

  private static String getMinCostNode(MutableGraph<String> g, NodeCost heuristic) {
    String node = "";
    switch (heuristic) {
      case MIN_NEIGHBORS:
        node = g.nodes().stream()
            .sorted(Comparator.comparingInt(g::degree)).findFirst().get();
        break;
      case MIN_WEIGHT:
        break;
      case MIN_FILL:
        node = g.nodes().stream()
            .sorted(Comparator.comparingInt(n -> getUnwedParents(g, n).size()))
            .findFirst().get();
        break;
      case WEIGHTED_MIN_FILL:
        break;

      default:
        node = g.nodes().stream()
            .sorted(Comparator.comparingInt(g::degree)).findFirst().get();
        break;
    }

    return node;
  }

}
