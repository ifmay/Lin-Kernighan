/**
 * CPSC 450, Final Project
 * 
 * NAME: Isabelle May
 * DATE: Fall 2024
 */

 package cpsc450;

 import java.util.ArrayList;
 import java.util.List;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Objects;
 
 /**
 * HeldKarp class implements the exact solution to the Travelling Salesman Problem (TSP).
 */
 public class HeldKarp {
 
     private Graph graph;
     private double[][] weightCache;
     private Map<State, Double> memo;
     private Map<State, Integer> parent;
 
    /**
     * Constructor for the HeldKarp class
     * @param graph The graph representing the TSP problem
     */
     public HeldKarp(Graph graph) {
         this.graph = graph;
         this.weightCache = new double[graph.vertices()][graph.vertices()];
         for (int i = 0; i < graph.vertices(); i++) {
             for (int j = 0; j < graph.vertices(); j++) {
                 weightCache[i][j] = graph.weight(i, j);
             }
         }
         this.memo = new HashMap<>();
         this.parent = new HashMap<>();
     }
 
     /**
     * Main function to run the Held-Karp algorithm
     * @return A List representing the optimal tour
     */
     public List<Integer> run() {
         int n = graph.vertices();
         List<Integer> tour = new ArrayList<>();
         double minCost = tsp(1, 0, n);
         reconstructTour(tour, n);
         return tour;
     }
 
      /**
     * Recursive function to solve TSP using dynamic programming
     * @param mask A bitmask representing the set of visited vertices
     * @param pos The current position (vertex)
     * @param n The total number of vertices
     * @return The minimum cost to visit all vertices from the current position
     */
     private double tsp(int mask, int pos, int n) {
         State state = new State(mask, pos);
         if (memo.containsKey(state)) {
             return memo.get(state);
         }
 
         if (mask == (1 << n) - 1) {
             return weightCache[pos][0];
         }
 
         double minCost = Double.MAX_VALUE;
         for (int next = 0; next < n; next++) {
             if ((mask & (1 << next)) == 0) {
                 double newCost = weightCache[pos][next] + tsp(mask | (1 << next), next, n);
                 if (newCost < minCost) {
                     minCost = newCost;
                     parent.put(state, next);
                 }
             }
         }
 
         memo.put(state, minCost);
         return minCost;
     }
 
     /**
     * Reconstructs the optimal tour from the parent map
     * @param tour The list to store the optimal tour
     * @param n The total number of vertices
     */
     private void reconstructTour(List<Integer> tour, int n) {
         int mask = 1;
         int pos = 0;
         tour.add(pos);
 
         for (int i = 1; i < n; i++) {
             State state = new State(mask, pos);
             pos = parent.get(state);
             tour.add(pos);
             mask |= (1 << pos);
         }
     }
 

    private static class State {
    int mask, pos;

     /**
     * State class represents the state of the TSP problem during recursion
     */
    State(int mask, int pos) {
        this.mask = mask;
        this.pos = pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return mask == state.mask && pos == state.pos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mask, pos);
    }
    }
}
