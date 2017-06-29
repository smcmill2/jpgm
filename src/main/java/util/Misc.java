package util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.graph.Graph;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implement miscellaneous helper functions.
 *
 * @version 1.0.0
 *
 * @author Sean McMillan
 */
public class Misc {
  public static <L, R> String joinPair(L l, R r, String joinVal) {
    return Joiner.on(joinVal).join(l.toString(), r.toString());
  }

  public static <L, R> String joinPair(Pair<L, R> pair, String joinVal) {
    return joinPair(pair.getLeft(), pair.getRight(), joinVal);
  }

  public static List<String> eliminationOrder(Graph<String> g) {
    Tarjan t = new Tarjan(g);

    return Lists.reverse(t.SCC).stream()
        .flatMap(n -> n.stream())
        .collect(Collectors.toList());
  }

  public static boolean hasCycles(Graph<String> g) {
    Tarjan t = new Tarjan(g);
    return t.hasCycles();
  }

  private static class Tarjan {
    private Map<String, Integer> index;
    private Map<String, Integer> lowlink;
    private Set<String> onStack;
    private Stack<String> S;
    private List<List<String>> SCC;
    private int idx;

    public Tarjan(Graph<String> g) {
      this.index = new HashMap<>();
      this.lowlink = new HashMap<>();
      this.onStack  = new HashSet<>();
      this.S = new Stack<>();

      this.idx = 0;

      this.SCC = new ArrayList<>();

      for(String v : g.nodes()) {
        if(!this.lowlink.keySet().contains(v)) {
          this.dfs(g, v);
        }
      }
    }

    private void dfs(Graph<String> g, String v) {
      this.index.put(v, this.idx);
      this.lowlink.put(v, this.idx);
      this.idx++;
      this.S.push(v);

      this.onStack.add(v);

      for(String w : g.successors(v)) {
        if(!this.lowlink.keySet().contains(w)) {
          this.dfs(g, w);
          this.lowlink.put(v, Math.min(this.lowlink.get(v), this.lowlink.get(w)));
        } else if(this.onStack.contains(w)) {
          this.lowlink.put(v, Math.min(this.lowlink.get(v), this.index.get(w)));
        }
      }

      if(this.lowlink.get(v).intValue() == this.index.get(v).intValue()) {
        List<String> component = new ArrayList<>();

        String w;
        do {
          w = this.S.pop();
          this.onStack.remove(w);
          component.add(w);
        } while (!w.equals(v));
        this.SCC.add(component);
      }
    }

    public boolean hasCycles() {
      boolean cycle = false;

      for(List<String> component : this.SCC) {
        if(component.size() > 1) {
          cycle = true;
          break;
        }
      }

      return cycle;
    }
  }
}
