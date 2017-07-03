package util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ListOpsTest {
  double threshold = 10e-8;
  List<Integer> l1;
  List<Integer> l2;

  @BeforeEach void setUp() {
    l1 = IntStream.range(0, 100)
        .boxed()
        .collect(Collectors.toList());
    l2 = IntStream.range(50, 150)
        .boxed()
        .collect(Collectors.toList());
  }

  @Test void testZip() {
    List<Pair<Integer, Integer>> result = ListOps
        .zip(l1, l2);
    List<Pair<Integer, Integer>> expected = IntStream.range(0, l1.size())
        .mapToObj(i -> Pair.of(l1.get(i), l2.get(i)))
        .collect(Collectors.toList());
    Assertions.assertEquals(expected, result);
  }

  @Test void testEnumerate() {
    List<Pair<Integer, String>> result = ListOps
        .enumerate(
            l2.stream()
                .map(Object::toString)
                .collect(Collectors.toList()));
    List<Pair<Integer, String>> expected = IntStream.range(0, l2.size())
        .mapToObj(i -> Pair.of(i, l2.get(i).toString()))
        .collect(Collectors.toList());
    Assertions.assertEquals(expected, result);
  }

  @Test void testNormalize() {
    double[] array = new double[]{1.0, 1.0, 1.0, 1.0};

    double[] normalizedArray = ListOps.normalize(array);

    Assertions.assertArrayEquals(new double[]{0.25, 0.25, 0.25, 0.25},
        normalizedArray, threshold);
    Assertions.assertEquals(1.0, Arrays.stream(normalizedArray).sum(), threshold);
  }
}
