package models;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.graph.ImmutableGraph;
import factors.discrete.ConditionalProbabilityDistribution;
import factors.discrete.DiscreteFactor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.mockito.Mockito.*;

class FactorGraphTest {
  @Mock ConditionalProbabilityDistribution cpd;
  @Mock DiscreteFactor discreteFactor;
  @Mock BayesianNetwork bayesianNetwork;
  @Mock ImmutableGraph immutableGraph;

  FactorGraph factorGraph;

  @BeforeEach void setUp() {
    MockitoAnnotations.initMocks(this);

    when(cpd.getScope()).thenReturn(Lists.newArrayList("x1", "x2", "x3"));
    when(cpd.factorString()).thenReturn("\u03C6(x1,x2,x3)");
    when(cpd.toDiscreteFactor()).thenReturn(discreteFactor);

    //when(immutableGraph.nodes()).thenReturn(Sets.toImmutableEnumSet(cpd));
    //when(bayesianNetwork.getStructure()).thenReturn(immutableGraph);
    //when(bayesianNetwork.getNodeCPD(cpd.factorString())).thenReturn(cpd);

    factorGraph = new FactorGraph();
  }

  @Test void testSetupFromBN() {
    //FactorGraph factorGraphFromBN = new FactorGraph(bayesianNetwork);

    Assertions.assertTrue(true);
  }

  @Test void testAddCPD() {
    /*
    factorGraph.addCPD(cpd);

    ImmutableGraph structure = factorGraph.getStructure();

    Assertions.assertTrue(structure.nodes().containsAll(cpd.getScope()));
    Assertions.assertTrue(structure.nodes().contains(cpd.factorString()));

    Assertions.assertTrue(
        structure.adjacentNodes(cpd.factorString()).containsAll(cpd.getScope()));
    for(String node : cpd.getScope()) {
      Assertions.assertTrue(
          structure.adjacentNodes(node).contains(cpd.factorString()));
    }
    */
  }
}
