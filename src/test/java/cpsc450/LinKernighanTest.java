/**
 * CPSC 450, Final Project
 * 
 * NAME: Isabelle May
 * DATE: Fall 2024
 */

package cpsc450;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class LinKernighanTest {
    @Test
    void testSingleNode() {
        Graph g = new AdjList(1);
        LinKernighan lk = new LinKernighan(g);
        lk.run();
        assertEquals(1, g.vertices());
        assertEquals(0, g.edges());
    }

    @Test
    void testTwoNodes() {
        Graph g = new AdjList(2);
        g.addEdge(0, 1);

        LinKernighan lk = new LinKernighan(g);
        lk.run();

        assertEquals(2, g.vertices());
        assertEquals(1, g.edges());
    }

    @Test
    void testSmallCompleteGraph() {
        Graph g = new AdjList(4);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 0);
        g.addEdge(0, 2);
        g.addEdge(1, 3);

        EdgeLabeling<Integer> l = new EdgeLabeling<>(g);
        l.addLabel(0, 1, 10);
        l.addLabel(1, 2, 15);
        l.addLabel(2, 3, 20);
        l.addLabel(3, 0, 25);
        l.addLabel(0, 2, 30);
        l.addLabel(1, 3, 35);

        LinKernighan lk = new LinKernighan(g);
        lk.run();

        assertEquals(4, g.vertices());
        assertEquals(6, g.edges());
    }

    @Test
    void testDisconnectedGraph() {
        Graph g = new AdjList(5);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        // Nodes 3 and 4 disconnected

        LinKernighan lk = new LinKernighan(g);
        lk.run();

        assertEquals(2, g.edges());
        assertTrue(g.out(3).isEmpty());
        assertTrue(g.out(4).isEmpty());
    }

    @Test
    void testGraphWithIdenticalWeights() {
        Graph g = new AdjList(3);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 0);

        EdgeLabeling<Integer> l = new EdgeLabeling<>(g);
        l.addLabel(0, 1, 10);
        l.addLabel(1, 2, 10);
        l.addLabel(2, 0, 10);

        LinKernighan lk = new LinKernighan(g);
        lk.run();

        assertEquals(3, g.vertices());
        assertEquals(3, g.edges());
    }

    @Test
    void testGraphWithNegativeWeights() {
        Graph g = new AdjList(3);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 0);

        EdgeLabeling<Integer> l = new EdgeLabeling<>(g);
        l.addLabel(0, 1, -10);
        l.addLabel(1, 2, -15);
        l.addLabel(2, 0, -20);

        LinKernighan lk = new LinKernighan(g);
        lk.run();

        assertEquals(3, g.vertices());
        assertEquals(3, g.edges());
    }

    @Test
    void testGraphWithZeroWeights() {
        Graph g = new AdjList(3);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 0);

        EdgeLabeling<Integer> l = new EdgeLabeling<>(g);
        l.addLabel(0, 1, 0);
        l.addLabel(1, 2, 0);
        l.addLabel(2, 0, 0);

        LinKernighan lk = new LinKernighan(g);
        lk.run();

        assertEquals(3, g.vertices());
        assertEquals(3, g.edges());
    }

    @Test
    void testModerateSizeGraph() {
        Graph g = new AdjList(20);
        for (int i = 0; i < 19; i++) {
            g.addEdge(i, i + 1);
        }
        g.addEdge(19, 0);

        EdgeLabeling<Integer> l = new EdgeLabeling<>(g);
        for (int i = 0; i < 20; i++) {
            l.addLabel(i, (i + 1) % 20, (i + 1) * 10);
        }

        LinKernighan lk = new LinKernighan(g);
        long startTime = System.currentTimeMillis();
        lk.run();
        long endTime = System.currentTimeMillis();

        assertEquals(20, g.vertices());
        assertEquals(20, g.edges());
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }

    @Test
    void testLargeGraph() {
        Graph g = new AdjList(50);
        for (int i = 0; i < 49; i++) {
            g.addEdge(i, i + 1);
        }
        g.addEdge(49, 0);

        EdgeLabeling<Integer> l = new EdgeLabeling<>(g);
        for (int i = 0; i < 50; i++) {
            l.addLabel(i, (i + 1) % 50, (i + 1) * 10);
        }

        LinKernighan lk = new LinKernighan(g);
        long startTime = System.currentTimeMillis();
        lk.run();
        long endTime = System.currentTimeMillis();

        assertEquals(50, g.vertices());
        assertEquals(50, g.edges());
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }

    @Test
    void testLargeDenseGraphs() {
        Graph g = new AdjList(50);
        for (int i = 0; i < 49; i++) {
            for (int j = i + 1; j < 10; j++) {
                g.addEdge(i, j);
            }
        }

        EdgeLabeling<Integer> l = new EdgeLabeling<>(g);
        for (int i = 0; i < 49; i++) {
            for (int j = i + 1; j < 10; j++) {
                l.addLabel(i, j, (int) (Math.random() * 100));
            }
        }

        LinKernighan lk = new LinKernighan(g);
        long startTime = System.currentTimeMillis();
        lk.run();
        long endTime = System.currentTimeMillis();

        assertEquals(50, g.vertices());
        assertEquals(45, g.edges());
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }

    @Test
    void testFindsShortestPath() {
        Graph g = new AdjList(10);
        for (int i = 0; i < 49; i++) {
            for (int j = i + 1; j < 10; j++) {
                g.addEdge(i, j);
            }
        }
        LinKernighan lk = new LinKernighan(g);
        lk.run();
        double lkPathWeight = lk.calculateTourCost(lk.getTour());
        double optimalPathWeight = BruteForcePathCalculator.calculateOptimalPathWeight(g);
        double tolerance = 0.02 * optimalPathWeight;
        System.out.println("Lin-Kernighan Path Weight: " + lkPathWeight);
        System.out.println("Optimal Path Weight: " + optimalPathWeight);
        assertTrue(Math.abs(lkPathWeight - optimalPathWeight) <= tolerance,
                "Lin-Kernighan did not find the shortest path. Found weight: " + lkPathWeight + ", Optimal weight: "
                        + optimalPathWeight);
    }

    @Test
    void testMultipleEdgesWithVaryingWeights() {
        Graph g = new AdjList(4);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 0);
        g.addEdge(0, 2);
        g.addEdge(1, 3);

        EdgeLabeling<Integer> l = new EdgeLabeling<>(g);
        l.addLabel(0, 1, 10);
        l.addLabel(1, 2, 15);
        l.addLabel(2, 3, 10);
        l.addLabel(3, 0, 25);
        l.addLabel(0, 2, 5);
        l.addLabel(1, 3, 20);

        LinKernighan lk = new LinKernighan(g);
        lk.run();

        assertEquals(4, g.vertices());
        assertEquals(6, g.edges());
    }

    @Test
    void testTimeEfficiencyForLargeGraph() {
        Graph g = new AdjList(100);
        for (int i = 0; i < 99; i++) {
            g.addEdge(i, i + 1);
        }
        g.addEdge(99, 0);

        EdgeLabeling<Integer> l = new EdgeLabeling<>(g);
        for (int i = 0; i < 100; i++) {
            l.addLabel(i, (i + 1) % 100, (i + 1) * 10);
        }

        LinKernighan lk = new LinKernighan(g);
        long startTime = System.currentTimeMillis();
        lk.run();
        long endTime = System.currentTimeMillis();

        System.out.println("Time taken for large graph: " + (endTime - startTime) + " ms");
        assertTrue((endTime - startTime) < 200);
    }

    @Test
    void testCorrectnessOnGraph() {
        Graph g = new AdjList(10);
        for (int i = 0; i < 9; i++) {
            g.addEdge(i, i + 1);
        }
        g.addEdge(9, 0);

        EdgeLabeling<Integer> l = new EdgeLabeling<>(g);
        for (int i = 0; i < 10; i++) {
            l.addLabel(i, (i + 1) % 10, (i + 1) * 10);
        }

        LinKernighan lk = new LinKernighan(g);
        lk.run();

        double lkPathWeight = lk.calculateTourCost(lk.getTour());
        double optimalPathWeight = BruteForcePathCalculator.calculateOptimalPathWeight(g);
        double tolerance = 0.01 * optimalPathWeight;

        assertTrue(Math.abs(lkPathWeight - optimalPathWeight) <= tolerance);
    }

    
}