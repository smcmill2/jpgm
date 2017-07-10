package primitives;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.List;

/**
 * Primitive used to describe an event or observation.
 *
 * i.e. X or X=1 or X=x1 or X=blue
 *
 * Created by smcmillan on 7/10/17.
 */
public class Event {
  String variable;
  String outcome;

  public Event(String event) {
    List<String> variableOutcomePair = Splitter.on("=").omitEmptyStrings()
        .trimResults().splitToList(event);

    variable = variableOutcomePair.get(0);
    outcome = variableOutcomePair.size() > 1 ? variableOutcomePair.get(1) : null;
  }

  public Event copy() {
    return new Event(this.toString());
  }

  @Override
  public String toString() {
    return outcome == null ? variable : Joiner.on("=")
        .skipNulls().join(variable, outcome);
  }

  public boolean equals(Event other) {
    return this.toString().equals(other.toString());
  }
}
