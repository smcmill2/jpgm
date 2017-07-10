package primitives;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Primitive used to describe a set of events.
 *
 * P(X=1): The stream X=1 is a singular event
 * P(X=1|Y=1,Z=1): The stream consists of 3 events
 *    X=1 given Y=1 and Z=1
 *
 * Created by smcmillan on 7/10/17.
 */
public class EventStream {
  List<Event> events = null;
  List<Event> observations = null;

  public EventStream(String eventStream) {
    List<List<Event>> eventsAndObservation = Splitter.on("|")
        .omitEmptyStrings()
        .trimResults(CharMatcher.anyOf("()"))
        .splitToList(eventStream).stream()
            .map(e -> streamToEventList(e))
        .collect(Collectors.toList());

    events = eventsAndObservation.get(0);

    if (eventsAndObservation.size() == 2) {
      observations = eventsAndObservation.get(1);
    }
  }

  public static List<Event> streamToEventList(String stream) {
    return Splitter.on(",").omitEmptyStrings().trimResults()
        .splitToList(stream).stream()
        .map(e -> new Event(e)).collect(Collectors.toList());
  }

  @Override public String toString() {
    return Joiner.on("|")
        .skipNulls()
        .join(
            Joiner.on(",").join(events.stream()
                .map(Event::toString)
                .collect(Collectors.toList())),
            Joiner.on(",").join(observations.stream()
                .map(Event::toString)
                .collect(Collectors.toList()))
        );
  }
}
