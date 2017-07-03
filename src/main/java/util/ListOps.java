package util;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implement a variety of useful List operations.
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
public class ListOps {
  public static <L, R> List<Pair<L, R>> zip(List<L> left, List<R> right) {
    Preconditions.checkArgument(left.size() == right.size(),
        "Lists are of unequal size");
    return IntStream.range(0, left.size())
        .mapToObj(i -> Pair.of(left.get(i), right.get(i)))
        .collect(Collectors.toList());
  }

  public static <E> List<Pair<Integer, E>>enumerate(List<E> list) {
    return IntStream.range(0, list.size())
        .mapToObj(i -> Pair.of(i, list.get(i)))
        .collect(Collectors.toList());
  }

  public static double[] normalize(double[] values) {
    double sum = Arrays.stream(values).sum();
    return Doubles.asList(values).stream()
        .mapToDouble(v -> v / sum)
        .toArray();
  }

  /*
  TODO refactor component into creation of discreteFactor?
  List<Set<String>> mappings = new ArrayList<>();
  for(Pair<String, Integer> pair : zip(newScope, newCardinality)) {
    mappings.add(IntStream.range(0, pair.getRight())
        .mapToObj(c -> this.joinPair(pair.getLeft(), c))
        .collect(Collectors.toSet()));
  }
  */
}
