package util;

import com.google.common.math.DoubleMath;
import factors.Factor;
import factors.discrete.DiscreteFactor;
import inference.exact.VariableElimination;
import org.apache.commons.lang3.tuple.Pair;
import primitives.Event;

import java.util.List;

/**
 * Collection of Testing Utilities
 */
public class TestUtils {
  private static double threshold = 10e-8;

  public static boolean JPTEqualsVE(Factor jpt, VariableElimination ve,
    List<Event> queryVars, List<Event> evidence) {
    Factor table = ((DiscreteFactor)jpt).copy();

    System.out.println(table);
    if(evidence.size() > 0) {
      table.reduce(evidence, true);
    }
    table.normalize(true);

    double veProb = ve.query(queryVars, evidence);
    double jptProb = queryVars.stream()
        .mapToDouble(v -> ((DiscreteFactor)table).getValue(v))
        .reduce(1.0, (a, b) -> a * b);

    System.out.println(String.format("JPT Prob: %f VE Prob: %f", jptProb, veProb));

    return DoubleMath.fuzzyEquals(jptProb, veProb, threshold);
  }
}
