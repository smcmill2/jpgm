package inference;

import factors.Factor;
import factors.discrete.DiscreteFactor;
import org.apache.commons.lang3.tuple.Pair;

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
  double query(List<Pair<String, Integer>> variables);
  double query(List<Pair<String, Integer>> variables, List<Pair<String, Integer>> evidence);
  DiscreteFactor queryFactor(List<String> variables);
  DiscreteFactor queryFactor(List<String> variables, List<Pair<String, Integer>> evidence);

  String mapQuery(List<String> variables);
  String mapQuery(List<String> variables, List<Pair<String, Integer>> evidence);
}
