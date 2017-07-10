package inference;

import factors.Factor;
import factors.discrete.DiscreteFactor;
import org.apache.commons.lang3.tuple.Pair;
import primitives.Event;

import java.util.List;

/**
 * Interface for Inference
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
public interface Inference {
  double threshold = 10e-8;
  double query(String queryString);
  double query(List<Event> variables);
  double query(List<Event> variables, List<Event> evidence);
  DiscreteFactor queryFactor(List<Event> variables);
  DiscreteFactor queryFactor(List<Event> variables, List<Event> evidence);

  String mapQuery(List<Event> variables);
  String mapQuery(List<Event> variables, List<Event> evidence);
}
