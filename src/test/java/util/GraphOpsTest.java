package util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.graph.*;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static util.GraphOps.getUnwedParents;

class GraphOpsTest {
  MutableGraph<String> dag = GraphBuilder.directed().build();
  MutableGraph<String> undirected = GraphBuilder.undirected().build();

  @BeforeEach void setUp() {
    dag.putEdge("a", "b");
    dag.putEdge("a", "c");
    dag.putEdge("b", "d");
    dag.putEdge("c", "d");
    dag.putEdge("c", "f");
    dag.putEdge("d", "e");

    undirected.putEdge("a", "b");
    undirected.putEdge("a", "c");
    undirected.putEdge("b", "d");
    undirected.putEdge("c", "d");
    undirected.putEdge("c", "f");
    undirected.putEdge("d", "e");
  }

  @Test void testGetMoralGraph() {
    ImmutableGraph<String> result = GraphOps.getMoralGraph(dag);

    Assertions.assertTrue(result.adjacentNodes("a").containsAll(
        Lists.newArrayList("b", "c")));
    Assertions.assertTrue(result.adjacentNodes("b").containsAll(
        Lists.newArrayList("a", "c", "d")));
    Assertions.assertTrue(result.adjacentNodes("c").containsAll(
        Lists.newArrayList("a", "b", "d", "f")));
    Assertions.assertTrue(result.adjacentNodes("d").containsAll(
        Lists.newArrayList("b", "c", "e")));
    Assertions.assertTrue(result.adjacentNodes("e").containsAll(
        Lists.newArrayList("d")));
    Assertions.assertTrue(result.adjacentNodes("f").containsAll(
        Lists.newArrayList("c")));
  }

  @Test void testGetUnwedParentsDirected() {
    Assertions.assertTrue(
        Iterables.elementsEqual(
            getUnwedParents(dag, "d"),
            Lists.newArrayList(Pair.of("b", "c"))));
    Assertions.assertTrue(
        Iterables.elementsEqual(
            getUnwedParents(dag, "c"),
            Lists.newArrayList()));
  }

  @Test void testGetUnwedParentsUndirected() {
    Assertions.assertTrue(
        Iterables.elementsEqual(
            getUnwedParents(undirected, "b"),
            Lists.newArrayList(
                Pair.of("a", "d"))));
    Assertions.assertTrue(
            getUnwedParents(undirected, "c").containsAll(
                Lists.newArrayList(
                    Pair.of("a", "d"),
                    Pair.of("a", "f"),
                    Pair.of("d", "f"))));
  }
}
