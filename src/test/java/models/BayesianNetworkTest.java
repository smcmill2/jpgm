package models;

import com.google.common.graph.MutableGraph;
import factors.discrete.ConditionalProbabilityDistribution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Map;

import static org.mockito.Mockito.*;


class BayesianNetworkTest {
  @Mock ConditionalProbabilityDistribution g;
  @Mock ConditionalProbabilityDistribution i;
  @Mock ConditionalProbabilityDistribution d;

  @InjectMocks BayesianNetwork bayesianNetwork;

  @BeforeEach void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test void testAddEdge() {
    when(i.getVariable()).thenReturn("I");
    when(g.getVariable()).thenReturn("G");
    when(d.getVariable()).thenReturn("D");

    bayesianNetwork.addEdge(i, g);
    bayesianNetwork.addEdge(g, d);

    Assertions.assertTrue(true);
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> bayesianNetwork.addEdge(d, i));
  }

  @Test void testAddEdge2() {
    Assertions.assertThrows(NullPointerException.class,
        () -> bayesianNetwork.addEdge("u", "v"));
    when(i.getVariable()).thenReturn("u");
    when(d.getVariable()).thenReturn("v");

    bayesianNetwork.addEdge(i, d);
    Assertions.assertTrue(true);
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> bayesianNetwork.addEdge("v", "u"));
  }
}
