package com.example;

import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

/**
 * Shortest path calculation in parallel from single source.
 * We'll use Floyd's algorithm to calculate shortest path for each node.
 * http://www.mcs.anl.gov/~itf/dbpp/text/node35.html
 */
public class ParallelShortestPathExample {

    static class Graph {

        private int [][] neighbouringMatrix;

        Graph(int size) {
            neighbouringMatrix = new int[size][size];
        }

        void addEdge(int i, int j) {
            neighbouringMatrix[i][j] = 1;
        }

        void removeEdge(int i, int j) {
            neighbouringMatrix[i][j] = 0;
        }

        boolean hasEdge(int i, int j) {
            return neighbouringMatrix[i][j] == 1;
        }

        int size() {
            return neighbouringMatrix.length;
        }
    }

    public static int[][] sequentialFloyd(Graph gr) {
        int size = gr.size();
        int[][] currentIteration = new int[size][size];
        int[][] nextIteration = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j)
                    currentIteration[i][j] = 0;
                else if (gr.hasEdge(i, j))
                    currentIteration[i][j] = 1;
                else
                    currentIteration[i][j] = Integer.MAX_VALUE;
            }
        }
        // We have to iterate N times to get proper size for all the paths.
        // No path can be longer than N or it would contain a node twice.
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    // We have to be careful not to overflow
                    int sumOfTwoPaths;
                    if (currentIteration[i][k] == Integer.MAX_VALUE
                        || currentIteration[k][j] == Integer.MAX_VALUE) {
                        sumOfTwoPaths = Integer.MAX_VALUE;
                    } else {
                        sumOfTwoPaths = currentIteration[i][k] + currentIteration[k][j];
                    }
                    // Calculating next iteration
                    nextIteration[i][j] = Math.min(currentIteration[i][j], sumOfTwoPaths);
                }
            }
            currentIteration = nextIteration;
        }
        return nextIteration;
    }

    /**
     * We split up the matrix into four sub-matrices.
     * Each iteration is computed in parallel and the aggregations are made afterwards.
     */
    static class ParallelFloyd extends RecursiveTask<int[][]> {

        private int[][] computeDirectly(int fromIndex, int toIndex, int[][] array) {
            for (int i = fromIndex; i < toIndex; i++) {
                for (int j = fromIndex; j < toIndex; j++) {
                    // We have to be careful not to overflow
                    int sumOfTwoPaths;
                    if (array[i][k] == Integer.MAX_VALUE
                            || array[k][j] == Integer.MAX_VALUE) {
                        sumOfTwoPaths = Integer.MAX_VALUE;
                    } else {
                        sumOfTwoPaths = array[i][k] + array[k][j];
                    }
                    // Calculating next iteration
                    result[i][j] = Math.min(array[i][j], sumOfTwoPaths);
                }
            }
            return result;
        }

        @Override
        protected int[][] compute() {
            return null;
        }
    }

    public static void main(String[] args) {
        // See example URL for the graph, that I represent.
        Graph gr = new Graph(4);
        gr.addEdge(0,1);
        gr.addEdge(1,2);
        gr.addEdge(1,3);
        gr.addEdge(3,2);
        gr.addEdge(3,0);
        // Make the computation and check the results.
        int[][] allShortestPaths = sequentialFloyd(gr);
        final int infinity = Integer.MAX_VALUE;
        int[][] expectedResult = new int[][]{
            {0,1,2,2},
            {2,0,1,1},
            {infinity,infinity,0,infinity},
            {1,2,1,0}
        };
        assertEquals(allShortestPaths, expectedResult);
    }

    private static void assertEquals(int[][] expected, int[][] actual) {
        if (expected.length != actual.length)
            throw new RuntimeException(String.format("Sizes of arrays are not equal. %d != %d",
                            expected, actual));

        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected.length; j++) {
                if (actual[i][j] != expected[i][j])
                    throw new RuntimeException(String.format("Got different result at [%d][%d]. %d != %d",
                                    i, j, expected[i][j], actual[i][j]));
            }
        }
    }

}
