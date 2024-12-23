/**
 * CPSC 450, Final Project
 * 
 * NAME: Isabelle May
 * DATE: Fall 2024
 */

package cpsc450;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EdgeLabeling<T> {
    private final Map<Integer, Map<Integer, T>> labels;

    public EdgeLabeling(Graph g) {
        labels = new HashMap<>();
        for (int i = 0; i < g.vertices(); i++) {
            labels.put(i, new HashMap<>());
        }
    }

    public void addLabel(int u, int v, T label) {
        labels.computeIfAbsent(u, k -> new HashMap<>()).put(v, label);
    }

    public Optional<T> getLabel(int u, int v) {
        return Optional.ofNullable(labels.getOrDefault(u, new HashMap<>()).get(v));
    }
}
