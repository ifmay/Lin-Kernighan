/**
 * CPSC 450, Final Project
 * 
 * NAME: Isabelle May
 * DATE: Fall 2024
 */

package cpsc450;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BruteForcePathCalculator {

    /**
     * Calculates the optimal path weight for a given graph using a brute-force
     * approach.
     * 
     * @param g the graph
     * @return the optimal path weight
     */
    public static double calculateOptimalPathWeight(Graph g) {
        // Generate all permutations of the graph's vertices
        List<List<Integer>> allPermutations = generateAllPermutations(g.vertices());
        double optimalWeight = Double.MAX_VALUE;

        // Calculate the weight of each permutation and find the minimum
        for (List<Integer> perm : allPermutations) {
            double weight = 0;
            for (int i = 0; i < perm.size() - 1; i++) {
                weight += g.weight(perm.get(i), perm.get(i + 1));
            }
            weight += g.weight(perm.get(perm.size() - 1), perm.get(0)); // Return to the start node
            optimalWeight = Math.min(optimalWeight, weight);
        }
        return optimalWeight;
    }

    /**
     * Generates all permutations of integers from 0 to n-1.
     * 
     * @param n the number of vertices
     * @return a list of all permutations
     */
    private static List<List<Integer>> generateAllPermutations(int n) {
        List<List<Integer>> permutations = new ArrayList<>();
        List<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            nodes.add(i);
        }
        permute(nodes, 0, permutations);
        return permutations;
    }

    /**
     * Recursive method to generate permutations.
     * 
     * @param nodes  the current list of nodes
     * @param l      the current index
     * @param result the list to store permutations
     */
    private static void permute(List<Integer> nodes, int l, List<List<Integer>> result) {
        if (l == nodes.size()) {
            result.add(new ArrayList<>(nodes));
        } else {
            for (int i = l; i < nodes.size(); i++) {
                Collections.swap(nodes, i, l);
                permute(nodes, l + 1, result);
                Collections.swap(nodes, i, l); // backtrack
            }
        }
    }
}
