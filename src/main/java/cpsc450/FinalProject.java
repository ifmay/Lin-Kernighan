/**
 * CPSC 450, Fall 2024
 * 
 * NAME: Isabelle May
 * DATE: Fall 2024
 */

package cpsc450;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.awt.Color;
import java.awt.BasicStroke;
import static java.lang.System.out;

/**
 * Basic program for running performance tests and generating
 * corresponding graphs for FinalProject.
 */
public class FinalProject {

    /**
     * Adds labels to the graph edges for consistency with the test cases.
     * 
     * @param g The graph to label.
     */
    static void addEdgeLabels(Graph g) {
        EdgeLabeling<Integer> l = new EdgeLabeling<>(g);
        int weight = 10; // Start with a base weight
        for (int from = 0; from < g.vertices(); from++) {
            for (int to : g.out(from)) {
                if (from < to) { // Avoid labeling duplicate edges in undirected graphs
                    if (l.getLabel(from, to) == null) { // Only add a label if not already present
                        l.addLabel(from, to, weight);
                    }
                    weight += 5; // Increment weight
                }
            }
        }
    }

    /**
     * Measures the time for the Lin-Kernighan algorithm on a graph.
     * 
     * @param g       The graph to process.
     * @param digraph If true, treats the graph as directed.
     * @return The time in milliseconds.
     */
    static long timeLinKernighan(Graph g, boolean digraph) {
        try {
            // Ensure all edges are labeled
            addEdgeLabels(g);

            LinKernighan lk = new LinKernighan(g);
            long start = System.currentTimeMillis();
            lk.run();
            long end = System.currentTimeMillis();
            return end - start;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return -1 on error
        }
    }

    /**
     * Checks if a graph is connected using DFS.
     * 
     * @param g The graph to check.
     * @return True if the graph is connected, false otherwise.
     */
    static boolean isConnected(Graph g) {
        Set<Integer> visited = new HashSet<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(0); // Start DFS from the first vertex

        while (!stack.isEmpty()) {
            int current = stack.pop();
            if (!visited.contains(current)) {
                visited.add(current);
                for (int neighbor : g.out(current)) {
                    stack.push(neighbor);
                }
            }
        }

        // Check if all vertices are visited
        return visited.size() == g.vertices();
    }

    /**
     * Ensures the graph is connected by adding edges if necessary.
     * 
     * @param g The graph to connect.
     */
    static void ensureConnected(Graph g) {
        if (isConnected(g))
            return;

        Set<Integer> visited = new HashSet<>();
        Stack<Integer> stack = new Stack<>();
        stack.push(0); // Start DFS from the first vertex

        while (!stack.isEmpty()) {
            int current = stack.pop();
            if (!visited.contains(current)) {
                visited.add(current);
                for (int neighbor : g.out(current)) {
                    stack.push(neighbor);
                }
            }
        }

        // Add edges to connect the unvisited vertices
        for (int i = 0; i < g.vertices(); i++) {
            if (!visited.contains(i)) {
                g.addEdge(visited.iterator().next(), i); // Connect to a visited vertex
                visited.add(i);
            }
        }
    }

    /**
     * Create a sparse adjacency list consisting of vertices of
     * connected three cycles.
     * 
     * @param n The size of the graph in terms of the number of vertices.
     * @return The adjacency list.
     */
    static Graph createSparseAdjList(int n) throws Exception {
        Graph graph = new AdjList(n);
        for (int x = 0; x < n - 1; ++x)
            graph.addEdge(x, x + 1);
        for (int x = n - 1; x > 2; --x)
            graph.addEdge(x, x - 2);

        ensureConnected(graph); // Ensure the graph is connected
        return graph;
    }

    /**
     * Create a dense adjacency list with all vertices connected to
     * each other without self edges.
     * 
     * @param n The size of the graph in terms of the number of vertices.
     * @return The adjacency list.
     */
    static Graph createDenseAdjList(int n) {
        Graph graph = new AdjList(n);
        for (int x = 0; x < n; ++x)
            for (int y = 0; y < n; ++y)
                if (x != y)
                    graph.addEdge(x, y);

        ensureConnected(graph); // Ensure the graph is connected
        return graph;
    }

    /**
     * Creates a chart from the test results.
     * 
     * @param series Array of XYSeries representing the data.
     * @param title  Title of the chart.
     * @param file   File path to save the chart as PNG.
     */
    static void chart(XYSeries[] series, String title, String file) throws Exception {
        XYSeriesCollection ds = new XYSeriesCollection();
        for (XYSeries s : series)
            ds.addSeries(s);

        JFreeChart chart = ChartFactory.createXYLineChart(title, "Vertices", "Time (ms)", ds);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(220, 220, 220));

        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        for (int i = 0; i < series.length; ++i) {
            renderer.setSeriesShapesVisible(i, true);
            renderer.setSeriesShapesFilled(i, true);
            renderer.setSeriesStroke(i, new BasicStroke(2.5f));
        }

        File lineChart = new File(file);
        ChartUtils.saveChartAsPNG(lineChart, chart, 640, 480);
    }

    /**
     * Executes the Held-Karp algorithm on a given graph and measures the execution
     * time.
     * 
     * @param g The graph on which the Held-Karp algorithm will be run.
     * @return The execution time in milliseconds, or -1 if an error occurs.
     */
    static long timeHeldKarp(Graph g) {
        try {
            HeldKarp hk = new HeldKarp(g);
            long start = System.currentTimeMillis();
            List<Integer> tour = hk.run();
            long end = System.currentTimeMillis();

            System.out.println("Held-Karp tour: " + tour); // Optional debugging output
            return end - start;
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return -1 on error

        }
    }

    /**
     * Example test for Lin-Kernighan and Exact TSP Algorithm performance.
     */
    static void runAlgorithmTests() throws Exception {
        int STEP = 1;
        int END = 20;

        // Series for performance
        XYSeries lkSeries = new XYSeries("Lin-Kernighan");
        XYSeries hkSeries = new XYSeries("Held-Karp");

        for (int n = STEP; n <= END; n += STEP) {
            // Create graphs
            Graph graph = createDenseAdjList(n);
            addEdgeLabels(graph); // Ensure edges are labeled

            // Measure performance
            long lkTime = timeLinKernighan(graph, false);
            long hkTime = timeHeldKarp(graph);

            // Add results to series
            lkSeries.add(n, lkTime);
            hkSeries.add(n, hkTime);

            // Print timings
            System.out.printf(
                    "Vertices: %d | Lin-Kernighan: %dms | Held-Karp: %dms%n",
                    n, lkTime, hkTime);
        }

        // Generate chart
        chart(new XYSeries[] { lkSeries, hkSeries },
                "Lin Kernighan Vs. Bellman Held Karp Performance",
                "algorithm_performance.png");
    }

    /**
 * Runs performance tests for the Lin-Kernighan algorithm on sparse and dense graphs.
 * 
 * This method generates two series (one for sparse and one for dense graphs) and records 
 * the execution time of the Lin-Kernighan algorithm for graphs with vertices ranging from 
 * 200 to 2000. Results are printed to the console and saved as a chart.
 * 
 * @throws Exception If an error occurs during the execution of the tests.
 */
    static void runLinKernighanTests() throws Exception {
        int STEP = 200;
        int END = 2000;

        // Series for Lin-Kernighan
        XYSeries lkSparseSeries = new XYSeries("Lin-Kernighan Sparse Graph");
        XYSeries lkDenseSeries = new XYSeries("Lin-Kernighan Dense Graph");

        for (int n = STEP; n <= END; n += STEP) {
            // Sparse Graphs
            Graph sparseGraph = createSparseAdjList(n);
            addEdgeLabels(sparseGraph); // Ensure all edges are labeled

            long lkSparseTime = timeLinKernighan(sparseGraph, false);

            lkSparseSeries.add(n, lkSparseTime);

            // Dense Graphs
            Graph denseGraph = createDenseAdjList(n);
            addEdgeLabels(denseGraph); // Ensure all edges are labeled

            long lkDenseTime = timeLinKernighan(denseGraph, false);

            lkDenseSeries.add(n, lkDenseTime);

            // Print timings to console
            out.println(
                    "Vertices: " + n + " | Lin-Kernighan Sparse: " + lkSparseTime + "ms, Dense: " + lkDenseTime + "ms");
        }

        // Generate separate charts
        chart(new XYSeries[] { lkSparseSeries, lkDenseSeries }, "Lin-Kernighan Performance",
                "lin_kernighan_performance.png");
    }
    
/**
 * Main method to execute the algorithm performance tests.
 * 
 * This method invokes the runAlgorithmTests and runLinKernighanTests methods to execute 
 * performance tests and display results for both Held-Karp and Lin-Kernighan algorithms.
 * 
 * @param args Command-line arguments (not used in this implementation).
 */
    public static void main(String[] args) {
        try {
            runAlgorithmTests();
            runLinKernighanTests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
