package inference.exact;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.GraphOps;

import java.util.Arrays;
import java.util.List;

class EliminationOrderingTest {
  MutableGraph<String> g = GraphBuilder.undirected().build();

  @BeforeEach void setUp() {
    g.putEdge("d", "i");
    g.putEdge("d", "g");
    g.putEdge("i", "g");
    g.putEdge("i", "s");
    g.putEdge("g", "s");
    g.putEdge("g", "l");
    g.putEdge("g", "h");
    g.putEdge("g", "j");
    g.putEdge("l", "j");
    g.putEdge("s", "l");
    g.putEdge("s", "j");
    g.putEdge("j", "h");
  }

  @Test void testGetOrderingMinNeighbors() {
    List<String> result = EliminationOrdering.getOrdering(ImmutableGraph.copyOf(g), EliminationOrdering.NodeCost.MIN_NEIGHBORS);

    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("d", "i", "h", "g", "s", "l", "j"), result));
  }


  // TODO Add More complete test
  @Test void testGetOrderingMinWeight() {
    List<String> result = EliminationOrdering.getOrdering(ImmutableGraph.copyOf(g), EliminationOrdering.NodeCost.MIN_FILL);

    Assertions.assertTrue(Iterables.elementsEqual(Lists.newArrayList("d", "i", "h", "g", "s", "l", "j"), result));
  }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme