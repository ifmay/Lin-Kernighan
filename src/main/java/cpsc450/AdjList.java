/**
 * CPSC 450, Final Project
 * 
 * NAME: Isabelle May
 * DATE: Fall 2024
 */

package cpsc450;

import java.util.*;

/**
 * Basic adjacency list implementation of the Graph interface.
 */
public class AdjList implements Graph {

    private final int vertexCount; // total number of vertices
    private int edgeCount;         // running count of number of edges
    private final Map<Integer, Set<Integer>> outEdges; // storage for the out edges
    private final Map<Integer, Set<Integer>> inEdges;  // storage for the in edges
    private final Map<Integer, Map<Integer, Double>> edgeWeights; // store edge weights


    /**
     * Create an adjacency list (graph) with a specific number of vertices.
     * 
     * @param vertices The number of vertices in the graph.
     * @throws GraphException if the number of vertices is less than or equal to 0.
     */
    public AdjList(int vertices) throws GraphException {
        if (vertices <= 0) {
            throw new GraphException("Number of vertices must be positive.");
        }
        this.vertexCount = vertices;
        this.edgeCount = 0;
        this.outEdges = new HashMap<>();
        this.inEdges = new HashMap<>();
        this.edgeWeights = new HashMap<>(); // Initialize edgeWeights here
        for (int i = 0; i < vertices; i++) {
            outEdges.put(i, new HashSet<>());
            inEdges.put(i, new HashSet<>());
            edgeWeights.put(i, new HashMap<>()); // Initialize the inner map for weights
        }
    }

    /**
     * Add an edge from vertex x to vertex y.
     */
    @Override
    public void addEdge(int x, int y) {
        if (isValidVertex(x) && isValidVertex(y)) {
            if (!outEdges.get(x).contains(y)) {
                outEdges.get(x).add(y);
                inEdges.get(y).add(x);
                edgeCount++;
            }
        }
    }

    /**
     * Remove an edge from vertex x to vertex y.
     */
    @Override
    public void removeEdge(int x, int y) {
        if (isValidVertex(x) && isValidVertex(y)) {
            if (outEdges.get(x).remove(y)) {
                inEdges.get(y).remove(x);
                edgeCount--;
            }
        }
    }

    /**
     * Get the set of outgoing edges from a given vertex.
     */
    @Override
    public Set<Integer> out(int x) {
        return isValidVertex(x) ? new HashSet<>(outEdges.get(x)) : Collections.emptySet();
    }

    /**
     * Get the set of incoming edges to a given vertex.
     */
    @Override
    public Set<Integer> in(int x) {
        return isValidVertex(x) ? new HashSet<>(inEdges.get(x)) : Collections.emptySet();
    }

    /**
     * Get the set of adjacent vertices to a given vertex.
     */
    @Override
    public Set<Integer> adj(int x) {
        if (!isValidVertex(x)) return Collections.emptySet();
        Set<Integer> adjVertices = new HashSet<>(outEdges.get(x));
        adjVertices.addAll(inEdges.get(x));
        return adjVertices;
    }

    /**
     * Check if there is an edge from vertex x to vertex y.
     */
    @Override
    public boolean hasEdge(int x, int y) {
        return isValidVertex(x) && isValidVertex(y) && outEdges.get(x).contains(y);
    }

    /**
     * Check if a vertex exists in the graph.
     */
    @Override
    public boolean hasVertex(int x) {
        return isValidVertex(x);
    }

    /**
     * Get the number of vertices in the graph.
     */
    @Override
    public int vertices() {
        return vertexCount;
    }

    /**
     * Get the number of edges in the graph.
     */
    @Override
    public int edges() {
        return edgeCount;
    }

    /**
     * Get all edges in the graph as a list of Edge objects.
     */
    @Override
    public List<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<>();
        for (Map.Entry<Integer, Set<Integer>> entry : outEdges.entrySet()) {
            int source = entry.getKey();
            for (int destination : entry.getValue()) {
                edges.add(new Edge(source, destination, destination));
            }
        }
        return edges;
    }

    /**
     * Helper method to check if a vertex index is valid.
     */
    private boolean isValidVertex(int v) {
        return v >= 0 && v < vertexCount;
    }

    /**
     * Get the weight of the edge from x to y.
    */
     @Override
     public double weight(int x, int y) {
         if (isValidVertex(x) && isValidVertex(y)) {
             // Check if the edge exists in the map and return its weight
             if (edgeWeights.get(x).containsKey(y)) {
                 return edgeWeights.get(x).get(y);
             }
         }
         return 2; // Return infinity if no such edge exists
     }

     /**
     * Returns the number of vertices in the graph.
     */
    @Override
    public int size() {
        return vertexCount;  // Returns the number of vertices
    }

     /**
     * Returns an array of neighbors for the given node.
     * Neighbors are all the vertices that are adjacent to the current node (both outgoing and incoming).
     */
    @Override
    public int[] getNeighbors(int currentNode) {
        if (currentNode < 0 || currentNode >= vertexCount) {
            throw new IllegalArgumentException("Invalid node index");
        }

        Set<Integer> neighborsSet = new HashSet<>();
        
        // Add outgoing neighbors
        neighborsSet.addAll(outEdges.get(currentNode));
        
        // Add incoming neighbors
        neighborsSet.addAll(inEdges.get(currentNode));
        
        // Convert the set to an array and return
        return neighborsSet.stream().mapToInt(i -> i).toArray();
    }
  }
