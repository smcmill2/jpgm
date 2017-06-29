package util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class MiscTest {
  @Test void testJoinPairStrings() {
    String result = Misc.joinPair("Hello", "World", " ");
    Assertions.assertTrue("Hello World".equals(result));
  }

  @Test void testJoinPairStringInteger() {
    String result = Misc.joinPair("x", 2, "=");
    Assertions.assertTrue("x=2".equals(result));
  }

  @Test void testJoinPair() {
    String result = Misc.joinPair(Pair.of("x", -2), "=");
    Assertions.assertTrue("x=-2".equals(result));
  }

  @Test void testHasCycles() {
    MutableGraph<String> g = GraphBuilder.directed().allowsSelfLoops(false).build();

    g.putEdge("a", "b");
    g.putEdge("b", "c");
    g.putEdge("d", "c");

    Assertions.assertTrue(!Misc.hasCycles(g));

    g.putEdge("c", "a");
    Assertions.assertTrue(Misc.hasCycles(g));
  }

  @Test void testEliminationOrder() {
    MutableGraph<String> g = GraphBuilder.directed().allowsSelfLoops(false).build();

    g.putEdge("c", "d");
    g.putEdge("d", "g");
    g.putEdge("g", "l");
    g.putEdge("g", "h");
    g.putEdge("i", "s");
    g.putEdge("i", "g");
    g.putEdge("j", "h");
    g.putEdge("l", "j");
    g.putEdge("s", "j");

    List<String> expectedOrder = Lists.newArrayList("i", "s", "c", "d", "g", "l", "j", "h");

    Assertions.assertTrue(Iterables.elementsEqual(expectedOrder, Misc.eliminationOrder(g)));
  }
}
