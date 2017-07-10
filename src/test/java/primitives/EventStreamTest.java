package primitives;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by smcmillan on 7/10/17.
 */
class EventStreamTest {
  String eventStream = "W=1,X|y=2,z=z3";
  EventStream es1;

  @BeforeEach void setUp() {
    es1 = new EventStream(eventStream);
  }

  @Test void testStreamToEventList() {
    List<Event> expected = Lists.newArrayList(
        new Event("x1"),
        new Event("x2=3")
    );

    List<Event> result = EventStream.streamToEventList("x1,x2=3");

    for(int i = 0;i < result.size();++i) {
      Assertions.assertTrue(expected.get(i).equals(result.get(i)),
          String.format("%s != %s", expected.get(i), result.get(i)));
    }
  }

  @Test void testToString() {
    String result = eventStream.toString();

    System.out.println(result);
    Assertions.assertTrue(eventStream.equals(es1.toString()));
  }
}
