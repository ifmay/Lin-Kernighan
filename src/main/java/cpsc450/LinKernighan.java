/**
 * CPSC 450, Final Project
 * 
 * NAME: Isabelle May
 * DATE: Fall 2024
 */

package cpsc450;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;

public class LinKernighan {

    private Graph graph;
    private List<Integer> tour; // Current TSP tour
    private static final int MAX_CANDIDATES = 10; // Limit for candidate edges
    private static final int MAX_RESTARTS = 5; // Number of global restarts
    private static final double GAIN_THRESHOLD = 1e-6; // Threshold for improvement
    private static final int INITIAL_MAX_DEPTH = 5; // Initial recursion depth
    private static final int MAX_RECURSION_DEPTH = 50; // Maximum recursion depth
    private static final double INVALID_COST = Double.MAX_VALUE / 2;

    private double[][] weightCache;

    /**
     * Constructor for the LinKernighan algorithm, initializing the graph, random
     * tour,
     * and caching edge weights for efficiency during optimization.
     * 
     * @param graph The graph representing the problem, containing vertices and edge
     *              weights.
     */
    public LinKernighan(Graph graph) {
        this.graph = graph;
        this.tour = initializeRandomTour();
        // Cache edge weights
        this.weightCache = new double[graph.vertices()][graph.vertices()];
        for (int i = 0; i < graph.vertices(); i++) {
            for (int j = 0; j < graph.vertices(); j++) {
                weightCache[i][j] = graph.weight(i, j);
            }
        }
    }

    /**
     * Executes the Lin-Kernighan optimization algorithm by performing multiple
     * restarts and attempting
     * to improve the current tour through dynamic k-opt moves. The algorithm runs
     * for a predefined
     * number of restarts, updating the best tour if a better one is found during
     * the process.
     * 
     * The algorithm starts with a randomized initial tour and continuously tries to
     * improve it by
     * applying k-opt moves until no further improvements are found. If the tour
     * improves during any
     * restart, it updates the best tour found so far.
     */
    public void run() {
        double bestCost = calculateTourCost(tour);
        List<Integer> bestTour = new ArrayList<>(tour);

        for (int restart = 0; restart < MAX_RESTARTS; restart++) {
            boolean improved = true;
            while (improved) {
                improved = false;

                for (int i = 0; i < tour.size(); i++) {
                    List<Edge> candidates = getCandidateEdges(i);
                    for (Edge candidate : candidates) {
                        if (attemptDynamicKOpt(i, candidate, INITIAL_MAX_DEPTH)) {
                            improved = true;
                            break;
                        }
                    }
                    if (improved)
                        break;
                }
            }

            // Update best tour if current tour is better
            double currentCost = calculateTourCost(tour);
            if (currentCost < bestCost) {
                bestCost = currentCost;
                bestTour = new ArrayList<>(tour);
            }

            // Restart with a new randomized tour
            this.tour = initializeRandomTour();
        }

        // Finalize the best tour
        this.tour = bestTour;
    }

    /**
     * Initializes a random tour by creating a list of vertices, shuffling them, and
     * returning the shuffled list.
     * This method generates a random starting tour by creating a list of all
     * vertices in the graph and then
     * shuffling the order of the vertices to create a randomized path.
     * 
     * @return A list of integers representing the vertices in a random order,
     *         forming the initial tour.
     */
    private List<Integer> initializeRandomTour() {
        List<Integer> initialTour = new ArrayList<>();
        for (int i = 0; i < graph.vertices(); i++) {
            initialTour.add(i);
        }
        Collections.shuffle(initialTour);
        return initialTour;
    }

    /**
     * Retrieves the top candidate edges for a given vertex in the current tour.
     * The candidate edges are the edges with the smallest weights that connect the
     * current vertex to any other vertex in the graph.
     * The method uses a priority queue to keep track of the top edges with the
     * smallest weights.
     * 
     * @param index The index of the vertex in the current tour for which the
     *              candidate edges are to be found.
     * @return A list of the top candidate edges, sorted by their weights in
     *         ascending order.
     */
    private List<Edge> getCandidateEdges(int index) {
        List<Edge> candidates = new ArrayList<>();
        int current = tour.get(index);

        // PriorityQueue to store the top MAX_CANDIDATES edges with the smallest weight.
        PriorityQueue<Edge> queue = new PriorityQueue<>(MAX_CANDIDATES, Comparator.comparingDouble(e -> e.weight));

        // Iterate through all vertices to find the candidate edges
        for (int i = 0; i < graph.vertices(); i++) {
            if (i != current) {
                double weight = graph.weight(current, i);
                Edge edge = new Edge(current, i, weight);

                // Add the edge to the priority queue
                if (queue.size() < MAX_CANDIDATES) {
                    queue.offer(edge); // Add the edge if there is have space
                } else if (queue.peek().weight > weight) {
                    queue.poll(); // Remove the largest edge if the new one is smaller
                    queue.offer(edge); // Add the new edge
                }
            }
        }

        // Transfer the edges from the priority queue to a list
        while (!queue.isEmpty()) {
            candidates.add(queue.poll());
        }

        // Return the top candidate edges
        return candidates;
    }

    /**
     * Attempts to improve the current tour using a dynamic k-opt approach.
     * This method tries to improve the tour by performing recursive k-opt
     * operations
     * based on a candidate edge and depth limit. It also explores optimizing the
     * tour
     * by breaking edges and adding new ones.
     * 
     * @param i         The index of the vertex in the current tour where the
     *                  candidate edge should be considered.
     * @param candidate The edge being considered for the k-opt operation.
     * @param maxDepth  The maximum recursion depth for the k-opt operation.
     * @return A boolean indicating whether the tour was improved (either by k-opt
     *         or edge breaking).
     */
    private boolean attemptDynamicKOpt(int i, Edge candidate, int maxDepth) {
        List<Integer> currentTour = new ArrayList<>(tour);
        double initialCost = calculateTourCost(tour);
        double cumulativeGain = 0;

        boolean improved = recursiveKOpt(i, candidate, currentTour, initialCost, cumulativeGain, 0, maxDepth);

        if (improved) {
            this.tour = currentTour; // Update the tour if improved
        }

        // Now try to optimize the tour by breaking edges if necessary
        Set<Edge> brokenEdges = new HashSet<>();
        Set<Edge> addedEdges = new HashSet<>();
        double bestGain = Double.NEGATIVE_INFINITY;

        if (optimizeWithBreakingEdge(brokenEdges, addedEdges, bestGain, 0)) {
            this.tour = currentTour; // Update the tour if improvement was found
            return true; // Improvement found through breaking edges
        }

        return improved; // Return whether the dynamic k-opt improved the tour
    }

    /**
     * Recursively attempts k-opt optimization on the tour by performing swaps and
     * evaluating the gain.
     * It explores the possibility of improving the current tour by performing swaps
     * of edges based on
     * candidate edges and recursively exploring further k-opt moves, but within a
     * specified maximum depth.
     * 
     * @param i              The index of the vertex in the current tour where the
     *                       swap should be performed.
     * @param candidate      The edge being considered for the k-opt swap.
     * @param currentTour    The current list of vertices representing the tour.
     * @param initialCost    The initial cost of the tour before any optimizations
     *                       are applied.
     * @param cumulativeGain The cumulative gain from previous optimizations.
     * @param depth          The current depth of the recursive search.
     * @param maxDepth       The maximum depth to limit the recursion for k-opt
     *                       moves.
     * @return A boolean indicating whether an improvement in the tour was found
     *         (true) or not (false).
     */
    private boolean recursiveKOpt(int i, Edge candidate, List<Integer> currentTour, double initialCost,
            double cumulativeGain, int depth, int maxDepth) {
        if (depth >= maxDepth || cumulativeGain < GAIN_THRESHOLD) {
            return false; // Terminate recursion if maximum depth is reached or cumulative gain is too
                          // small
        }

        // Perform the swap and calculate the gain
        int a = currentTour.get(i);
        int b = currentTour.get((i + 1) % currentTour.size());
        int c = candidate.u;
        int d = candidate.v;

        double gain = graph.weight(a, b) + graph.weight(c, d) - graph.weight(a, c) - graph.weight(b, d);

        // Avoid adding gains that are too small
        if (gain < GAIN_THRESHOLD) {
            return false;
        }

        cumulativeGain += gain;

        // Swap the edges
        performSwap(currentTour, i, c, d);

        double newCost = calculateTourCost(currentTour);
        if (newCost + cumulativeGain < initialCost) {
            return true; // Improvement found
        }

        // Explore further k-opt moves, but limit exploration depth
        for (int next = 0; next < currentTour.size(); next++) {
            if (next != i) {
                List<Edge> nextCandidates = getCandidateEdges(next);
                for (Edge nextCandidate : nextCandidates) {
                    // Stop if the next candidate is not promising enough
                    if (recursiveKOpt(next, nextCandidate, currentTour, initialCost, cumulativeGain, depth + 1,
                            maxDepth)) {
                        return true;
                    }
                }
            }
        }

        reverseSwap(currentTour, i, c, d); // Undo the swap if no improvement
        return false; // No improvement found
    }

    /**
     * Attempts to optimize the tour by breaking edges and evaluating the gain.
     * It integrates the `breakingEdge` method to identify and apply beneficial edge
     * breaks.
     * 
     * @param broken A set of edges that have been broken during the optimization
     *               process.
     * @param added  A set of edges that have been added during the optimization
     *               process.
     * @param gBest  The best gain found so far in the optimization process.
     * @param k      A parameter used in the optimization process, potentially
     *               controlling the number of iterations or depth.
     * @return A boolean indicating whether the optimization with breaking edges was
     *         successful.
     */
    private boolean optimizeWithBreakingEdge(Set<Edge> broken, Set<Edge> added, double gBest, int k) {
        return breakingEdge(broken, added, gBest, k); // Integrate breakingEdge to optimize tour
    }

    /**
     * Reverses a segment of the tour between two specified vertices, effectively
     * performing a swap of edges.
     * This method finds the positions of the specified vertices in the tour and
     * reverses the segment between them.
     * 
     * @param tour The current tour (list of vertices).
     * @param i    The index of the first vertex in the swap.
     * @param c    The first vertex in the swap.
     * @param d    The second vertex in the swap.
     */
    private void reverseSwap(List<Integer> tour, int i, int c, int d) {
        int start = i + 1;
        int end = tour.indexOf(d);
        reverseSegment(tour, start, end); // This method actually reverses the segment
    }

    /**
     * Performs a swap on the tour by reversing a segment of the tour between two
     * vertices.
     * This method identifies the segment to be swapped and calls `reverseSegment`
     * to perform the actual swap.
     * 
     * @param tour The current tour (list of vertices).
     * @param i    The index of the first vertex in the swap.
     * @param c    The first vertex in the swap.
     * @param d    The second vertex in the swap.
     */
    private void performSwap(List<Integer> tour, int i, int c, int d) {
        int start = i + 1;
        int end = tour.indexOf(d);
        reverseSegment(tour, start, end);
    }

    /**
     * Reverses a segment of the tour between the specified start and end indices.
     * This method swaps the elements in the tour between the start and end
     * positions, effectively reversing the order.
     * 
     * @param tour  The current tour (list of vertices).
     * @param start The starting index of the segment to be reversed.
     * @param end   The ending index of the segment to be reversed.
     */
    private void reverseSegment(List<Integer> tour, int start, int end) {
        while (start < end) {
            int temp = tour.get(start);
            tour.set(start, tour.get(end));
            tour.set(end, temp);
            start++;
            end--;
        }
    }

    /**
     * Calculates the total cost of a given tour by summing the edge weights between
     * consecutive vertices.
     * The cost is computed based on the weights of edges in the graph. Invalid edge
     * weights or potential
     * overflow situations are handled gracefully by returning a predefined constant
     * for invalid costs.
     * 
     * @param tour The list of vertices representing the tour.
     * @return The total cost of the tour, or a predefined constant (INVALID_COST)
     *         if invalid edge weights or overflow are encountered.
     */
    double calculateTourCost(List<Integer> tour) {
        double totalCost = 0.0;

        for (int i = 0; i < tour.size(); i++) {
            int u = tour.get(i);
            int v = tour.get((i + 1) % tour.size());
            double edgeWeight = graph.weight(u, v);

            // Gracefully handle invalid edge weight (NaN, Infinity) by skipping the edge or
            // applying a penalty
            if (Double.isNaN(edgeWeight) || Double.isInfinite(edgeWeight)) {
                System.err.println("Invalid edge weight detected between " + u + " and " + v + ": " + edgeWeight);
                return INVALID_COST; // Instead of returning Double.MAX_VALUE, use a defined constant for invalid
                                     // costs
            }

            // Check if adding this edge weight causes overflow
            if (Double.isInfinite(totalCost + edgeWeight)) {
                System.err.println("Overflow detected in total cost calculation");
                return INVALID_COST; // Handle overflow gracefully
            }

            totalCost += edgeWeight;
        }
        return totalCost;
    }

    /**
     * Attempts to break edges in the current tour to find improvements. The method
     * recursively explores
     * potential edges to break, aiming to improve the tour's cost by finding edges
     * that, when broken,
     * lead to a better solution.
     * 
     * The recursion halts when the maximum depth is exceeded or when no further
     * improvement is found.
     * The method employs backtracking to undo choices that do not lead to a better
     * solution.
     * 
     * @param broken The set of edges that have been broken during the process.
     * @param added  The set of edges that have been added as part of the tour
     *               optimization.
     * @param gBest  The current best gain found so far.
     * @param k      The current recursion depth.
     * @return True if an improvement was found, false otherwise.
     */
    private boolean breakingEdge(Set<Edge> broken, Set<Edge> added, double gBest, int k) {
        if (k > MAX_RECURSION_DEPTH) {
            return false; // Stop if recursion depth is exceeded
        }

        int t2i = getLastVertex(broken);

        // Iterate over possible edges to break
        for (int t2iPlus1 : graph.out(t2i)) {
            if (wasAlreadyChosen(t2iPlus1, broken) || isNeighbor(t2i, t2iPlus1)) {
                continue; // Skip already chosen edges or neighbors
            }

            Edge newEdge = new Edge(t2i, t2iPlus1, graph.weight(t2i, t2iPlus1));
            broken.add(newEdge);

            // Calculate gain after breaking the edge
            double gCurrent = calculateGain(broken, added);
            if (gCurrent > gBest) {
                relinkTour(broken, added); // If improvement is found, update tour
                return true;
            }

            // Recurse only if further improvement is possible
            if (gCurrent + GAIN_THRESHOLD > gBest && breakingEdge(broken, added, gBest, k + 1)) {
                return true; // Continue searching for better edges if gain is still promising
            }

            broken.remove(newEdge); // Backtrack if no improvement
        }

        return false; // No improvement found
    }

    /**
     * Attempts to break edges in the current tour to find improvements. The method
     * recursively explores
     * potential edges to break, aiming to improve the tour's cost by finding edges
     * that, when broken,
     * lead to a better solution.
     * 
     * The recursion halts when the maximum depth is exceeded or when no further
     * improvement is found.
     * The method employs backtracking to undo choices that do not lead to a better
     * solution.
     * 
     * @param broken The set of edges that have been broken during the process.
     * @param added  The set of edges that have been added as part of the tour
     *               optimization.
     * @param gBest  The current best gain found so far.
     * @param k      The current recursion depth.
     * @return True if an improvement was found, false otherwise.
     */
    private double calculateGain(Set<Edge> broken, Set<Edge> added) {
        double brokenCost = 0.0;
        double addedCost = 0.0;

        // Calculate the total cost of the broken edges
        for (Edge edge : broken) {
            double weight = edge.weight;
            // Handle large edge weights gracefully by checking for overflow or invalid
            // weights
            if (Double.isInfinite(brokenCost + weight) || Double.isNaN(weight)) {
                System.err.println("Invalid edge weight detected in broken edges");
                return INVALID_COST; // Use a constant to indicate invalid or problematic weight
            }
            brokenCost += weight;
        }

        // Calculate the total cost of the added edges
        for (Edge edge : added) {
            double weight = edge.weight;
            if (Double.isInfinite(addedCost + weight) || Double.isNaN(weight)) {
                System.err.println("Invalid edge weight detected in added edges");
                return INVALID_COST; // Handle invalid added edge weights
            }
            addedCost += weight;
        }

        // The gain is the difference between the cost of the broken edges and the added edges
        double gain = brokenCost - addedCost;

        // Check if the gain itself is invalid (NaN or Infinity)
        if (Double.isNaN(gain) || Double.isInfinite(gain)) {
            System.err.println("Invalid gain calculation");
            return INVALID_COST; // Return a defined constant for invalid gains
        }

        return gain;
    }

    /**
     * Updates the current tour by incorporating the edges from the added set.
     * The method creates a new tour by adding the vertices from each edge in the
     * added set.
     * 
     * @param broken The set of edges that were broken (not used in this method, but
     *               passed for consistency).
     * @param added  The set of edges that are added to the new tour.
     */
    private void relinkTour(Set<Edge> broken, Set<Edge> added) {
        List<Integer> newTour = new ArrayList<>();

        // Add edges from the added set to the new tour
        for (Edge edge : added) {
            newTour.add(edge.u); // Add starting vertex of the edge
            newTour.add(edge.v); // Add ending vertex of the edge
        }

        // Update the tour with the new path
        this.tour = newTour;
    }

    /**
     * Checks if there is an edge between the two vertices.
     * 
     * @param vertex1 The first vertex.
     * @param vertex2 The second vertex.
     * @return true if there is an edge between the two vertices; false otherwise.
     */
    private boolean isNeighbor(int vertex1, int vertex2) {
        // Check if there is an edge between vertex1 and vertex2
        return graph.hasEdge(vertex1, vertex2);
    }

    /**
     * Checks if the given vertex is part of any edge in the specified set of edges.
     * 
     * @param vertex The vertex to check.
     * @param edges  The set of edges to check against.
     * @return true if the vertex is part of any edge in the set; false otherwise.
     */
    private boolean wasAlreadyChosen(int vertex, Set<Edge> edges) {
        for (Edge edge : edges) {
            if (edge.u == vertex || edge.v == vertex) {
                return true; // Vertex is part of an already chosen edge
            }
        }
        return false; // Vertex has not been chosen in any of the edges
    }

    /**
     * Checks if the given vertex is part of any edge in the specified set of edges.
     * 
     * @param vertex The vertex to check.
     * @param edges  The set of edges to check against.
     * @return true if the vertex is part of any edge in the set; false otherwise.
     */
    private int getLastVertex(Set<Edge> edges) {
        Edge lastEdge = null;
        for (Edge edge : edges) {
            lastEdge = edge; // Get the last edge (this will be the last edge in the iteration order)
        }
        return lastEdge != null ? lastEdge.v : -1; // Return the last vertex, or -1 if no edges exist
    }

    /**
     * Prints the current tour along with its total cost.
     */
    public void printTour() {
        System.out.println("Current Tour: " + tour);
        System.out.println("Total Cost: " + calculateTourCost(tour));
    }

    /**
     * Retrieves the current tour.
     * 
     * @return The current tour as a list of integers (vertices).
     */
    public List<Integer> getTour() {
        return this.tour;
    }
}
