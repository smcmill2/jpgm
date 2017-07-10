package primitives;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Created by smcmillan on 7/10/17.
 */
class EventTest {
  String event1;
  String event2;
  String event3;

  @BeforeEach void setUp() {
    event1 = "X=1";
    event2 = "X=x1";
    event3 = "X";
  }

  @Test void testCopy() {
    Event e1 = new Event(event1);
    Event e2 = e1.copy();

    Assertions.assertTrue(e1.toString().equals(e2.toString()));

    e1.variable = "Y";
    Assertions.assertFalse(e1.toString().equals(e2.toString()));
  }

  @Test void testToString() {
    Assertions.assertTrue(event1.equals(new Event(event1).toString()));
    Assertions.assertTrue(event2.equals(new Event(event2).toString()));
    Assertions.assertTrue(event3.equals(new Event(event3).toString()));
  }

  @Test void testEquality() {
    Assertions.assertTrue(new Event(event1).equals(new Event("X=1")));
    Assertions.assertFalse(new Event(event1).equals(new Event(event2)));
  }
}
