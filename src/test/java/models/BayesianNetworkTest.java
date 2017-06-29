package models;

import com.google.common.collect.Sets;
import factors.discrete.ConditionalProbabilityDistribution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;


class BayesianNetworkTest {
  @Mock ConditionalProbabilityDistribution g;
  @Mock ConditionalProbabilityDistribution i;
  @Mock ConditionalProbabilityDistribution d;
  @Mock ConditionalProbabilityDistribution c;
  @Mock ConditionalProbabilityDistribution s;
  @Mock ConditionalProbabilityDistribution l;
  @Mock ConditionalProbabilityDistribution j;
  @Mock ConditionalProbabilityDistribution h;

  @InjectMocks BayesianNetwork bayesianNetwork;

  @BeforeEach void setUp() {
    MockitoAnnotations.initMocks(this);

    when(i.getVariable()).thenReturn("I");
    when(g.getVariable()).thenReturn("G");
    when(d.getVariable()).thenReturn("D");
    when(c.getVariable()).thenReturn("C");
    when(s.getVariable()).thenReturn("S");
    when(l.getVariable()).thenReturn("L");
    when(j.getVariable()).thenReturn("J");
    when(h.getVariable()).thenReturn("H");
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

  @Test void testIsDSep() {
    bayesianNetwork.addEdge(c, d);
    bayesianNetwork.addEdge(d, g);
    bayesianNetwork.addEdge(i, g);
    bayesianNetwork.addEdge(i, s);
    bayesianNetwork.addEdge(g, l);
    bayesianNetwork.addEdge(s, j);
    bayesianNetwork.addEdge(g, h);
    bayesianNetwork.addEdge(j, h);

    Assertions.assertTrue(!bayesianNetwork.isDSep("D", "I", Sets.newHashSet("L")));
    Assertions.assertTrue(!bayesianNetwork.isDSep("D", "J", Sets.newHashSet("L")));
    Assertions.assertTrue(bayesianNetwork.isDSep("D", "J", Sets.newHashSet("L", "I")));
    Assertions.assertTrue(!bayesianNetwork.isDSep("D", "J", Sets.newHashSet("L", "I", "H")));
    Assertions.assertTrue(!bayesianNetwork.isDSep("D", "H", Sets.newHashSet("G")));
    Assertions.assertTrue(bayesianNetwork.isDSep("D", "H", Sets.newHashSet("G", "J")));
    Assertions.assertTrue(bayesianNetwork.isDSep("L", "S", Sets.newHashSet("G")));
  }
}
