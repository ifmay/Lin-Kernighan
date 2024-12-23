/**
 * CPSC 450, Fall 2024
 *
 * NAME: Shawn Bowers
 * DATE: Fall 2024
 *
 */ 

package cpsc450;

import java.util.List;
import java.util.Set;

/** 
 * A generic digraph interface. Can simulate undirected graphs via
 * adjacent calls.
 */
public interface Graph {

    /**
     * Add an edge from x to y. If x or y are not valid vertices, no
     * edge is added.
     * @param x The start (from) vertex of the edge.
     * @param y The end (to) vertex of the edge.
     */
    void addEdge(int x, int y);

    /**
     * Remove an edge from x to y. If x and y are invalid vertices, or
     * if no edge exists from x to y, then nothing is removed.
     * @param x The start (from) vertex of the edge.
     * @param y The end (to) vertex of the edge.
     */
    void removeEdge(int x, int y);

    /**
     * Returns the vertices y connected by an edge from x to y. If x is
     * an invalid vertex, the empty set is returned. 
     * @param x The vertex to find out vertices of. 
     * @return A set of out vertices of x.
     */
    Set<Integer> out(int x);

    /**
     * Returns the vertices y connected by an edge from y to x. If x is
     * an invalid vertex, the empty set is returned.
     * @param x The vertex to find in vertices of. 
     * @return A set of in vertices of x.
     */
    Set<Integer> in(int x);

    /**
     * Returns the vertices y connected by an edge to or from x. If x is
     * an invalid vertex, the empty set is returned.
     * @param x The vertex to find adjacent vertices of. 
     * @return A set of adjacent vertices of x.
     */
    Set<Integer> adj(int x) throws GraphException;

    /**
     * Returns true if the graph contains an edge from x to y.
     * @param x The start (from) vertex of the edge.
     * @param y The end (to) vertex of the edge.
     */
    boolean hasEdge(int x, int y);

    /**
     * Returns true if the graph contains the vertex x. Returns false if
     * x is not valid, where valid vertices range from 0 to the number
     * of vertices - 1. 
     * @param x The vertex to check. 
     * @return True if the vertex is valid.
     */
    boolean hasVertex(int x); 
  
    /**
     * Returns the number of vertices in the graph.
     * @return The number of vertices.
     */
    int vertices();

    /**
     * Returns the number of edges in the graph. 
     * @return The number of edges.
     */
    int edges();
  
    /**
     * Returns a list of all edges in the graph.
     * @return A list of all edges.
     */
    List<Edge> getAllEdges();

    /**
     * Returns the weight of the edge from x to y.
     * If no edge exists, returns Double.POSITIVE_INFINITY.
     * @param x The start (from) vertex of the edge.
     * @param y The end (to) vertex of the edge.
     * @return The weight of the edge.
     */
    double weight(int x, int y);

    int size();

    int[] getNeighbors(int currentNode);
}

