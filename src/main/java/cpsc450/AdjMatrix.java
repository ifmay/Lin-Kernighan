/**
 * CPSC 450, Final Project
 * 
 * NAME: Isabelle May
 * DATE: Fall 2024
 */

package cpsc450;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adjacency Matrix implementation of the Graph interface.
 */
public class AdjMatrix implements Graph {

    private int vertexCount; // total number of vertices
    private int edgeCount;   // running count of edges
    private double[] matrix; // flattened 2D array for edge weights

    /**
     * Create an adjacency matrix (graph) given a specific (fixed) number of vertices.
     * 
     * @param vertices The number of vertices in the graph.
     */
    public AdjMatrix(int vertices) throws GraphException {
        if (vertices <= 0) {
            throw new GraphException("Number of vertices must be positive.");
        }
        this.vertexCount = vertices;
        this.matrix = new double[vertexCount * vertexCount];
        this.edgeCount = 0;

        // Initialize all weights to Double.POSITIVE_INFINITY (no edge)
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = Double.POSITIVE_INFINITY;
        }
    }

    /**
     * Add an edge from vertex x to vertex y with a default weight of 1.0.
     */
    @Override
    public void addEdge(int x, int y) {
        addEdge(x, y, 1.0);
    }

    /**
     * Add an edge from vertex x to vertex y with a specific weight.
     * 
     * @param x      The source vertex.
     * @param y      The destination vertex.
     * @param weight The weight of the edge.
     */
    public void addEdge(int x, int y, double weight) {
        if (isValidVertex(x) && isValidVertex(y) && weight > 0) {
            int index = x * vertexCount + y;
    
            // If the weight is already positive infinity, increment edgeCount (it's a new edge)
            if (matrix[index] == Double.POSITIVE_INFINITY) {
                edgeCount++;
            }
    
            // Set the edge weight
            matrix[index] = weight;
        }
    }

    /**
     * Remove an edge from vertex x to vertex y in the matrix.
     */
    @Override
    public void removeEdge(int x, int y) {
        if (isValidVertex(x) && isValidVertex(y)) {
            int index = x * vertexCount + y;
            if (matrix[index] != Double.POSITIVE_INFINITY) {
                matrix[index] = Double.POSITIVE_INFINITY;
                edgeCount--;
            }
        }
    }

    /**
     * Get the set of outgoing edges from a given vertex.
     */
    @Override
    public Set<Integer> out(int x) {
        Set<Integer> outgoing = new HashSet<>();
        if (isValidVertex(x)) {
            int rowStartIndex = x * vertexCount;
            for (int y = 0; y < vertexCount; y++) {
                if (matrix[rowStartIndex + y] != Double.POSITIVE_INFINITY) {
                    outgoing.add(y);
                }
            }
        }
        return outgoing;
    }

    /**
     * Get the set of incoming edges to a given vertex.
     */
    @Override
    public Set<Integer> in(int x) {
        Set<Integer> incoming = new HashSet<>();
        if (isValidVertex(x)) {
            for (int y = 0; y < vertexCount; y++) {
                if (matrix[y * vertexCount + x] != Double.POSITIVE_INFINITY) {
                    incoming.add(y);
                }
            }
        }
        return incoming;
    }

    /**
     * Get the set of adjacent vertices to a given vertex.
     */
    @Override
    public Set<Integer> adj(int x) {
        Set<Integer> adjacent = new HashSet<>();
        if (isValidVertex(x)) {
            adjacent.addAll(out(x));
            adjacent.addAll(in(x));
        }
        return adjacent;
    }

    /**
     * Check if there is an edge from vertex x to vertex y.
     */
    @Override
    public boolean hasEdge(int x, int y) {
        return isValidVertex(x) && isValidVertex(y) && matrix[x * vertexCount + y] != Double.POSITIVE_INFINITY;
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
     * Get the weight of the edge from x to y.
     */
    @Override
    public double weight(int x, int y) {
        if (isValidVertex(x) && isValidVertex(y)) {
            return matrix[x * vertexCount + y];
        }
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Returns a list of all edges in the graph.
     */
    @Override
    public List<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<>();
        for (int x = 0; x < vertexCount; x++) {
            for (int y = 0; y < vertexCount; y++) {
                if (matrix[x * vertexCount + y] != Double.POSITIVE_INFINITY) {
                    edges.add(new Edge(x, y, y));
                }
            }
        }
        return edges;
    }

    /**
     * Helper method to validate if a vertex index is within the valid range.
     */
    private boolean isValidVertex(int v) {
        return v >= 0 && v < vertexCount;
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
     * Neighbors are all the vertices that have an edge to the given vertex.
     */
    @Override
    public int[] getNeighbors(int currentNode) {
        if (currentNode < 0 || currentNode >= vertexCount) {
            throw new IllegalArgumentException("Invalid node index");
        }
        
        List<Integer> neighborsList = new ArrayList<>();
        
        // Look for edges in the row corresponding to the current node
        for (int i = 0; i < vertexCount; i++) {
            if (matrix[currentNode * vertexCount + i] != Double.POSITIVE_INFINITY) {
                neighborsList.add(i);
            }
        }
        
        // Convert the list to an array and return
        return neighborsList.stream().mapToInt(i -> i).toArray();
    }
}
