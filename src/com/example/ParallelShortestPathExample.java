package com.example;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
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
        initializeShortestPathMatrix(gr, size, currentIteration);
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

    private static void initializeShortestPathMatrix(Graph gr, int size, int[][] currentIteration) {
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
    }

    /**
     * We split up the matrix into four sub-matrices.
     * Each iteration is computed in parallel and the aggregations are made afterwards.
     */
    static class ParallelFloyd extends RecursiveTask<int[][]> {

        private final Graph graph;

        ParallelFloyd(Graph graph) {
            this.graph = graph;
        }

        @Override
        protected int[][] compute() {
            int size = graph.size();
            int[][] currentIteration = new int[size][size];
            initCurrentIteration(currentIteration);
            for (int k = 0; k < size; k++) {
                int middle = size / 2;
                // Split matrix into regions
                ParallelFloydSubComputation topLeft = new ParallelFloydSubComputation(currentIteration,
                        0, middle, 0, middle, k);
                ParallelFloydSubComputation topRight = new ParallelFloydSubComputation(currentIteration,
                        middle, size, 0, middle, k);
                ParallelFloydSubComputation bottomLeft = new ParallelFloydSubComputation(currentIteration,
                        0, middle, middle, size, k);
                ParallelFloydSubComputation bottomRight = new ParallelFloydSubComputation(currentIteration,
                        middle, size, middle, size, k);
                topLeft.fork();
                topRight.fork();
                bottomLeft.fork();
                bottomRight.fork();
                // Merge the result
                mergeRegion(currentIteration, topLeft);
                mergeRegion(currentIteration, topRight);
                mergeRegion(currentIteration, bottomLeft);
                mergeRegion(currentIteration, bottomRight);
            }
            return currentIteration;
        }

        private void mergeRegion(int[][] currentIteration, ParallelFloydSubComputation computation) {
            int[][] result = computation.join();
            for (int i = computation.fromX; i < computation.toX; i++) {
                for (int j = computation.fromY; j < computation.toY; j++) {
                    currentIteration[i][j] = result[i][j];
                }
            }
        }

        private void initCurrentIteration(int[][] currentIteration) {
            initializeShortestPathMatrix(graph, graph.size(), currentIteration);
        }
    }

    static class ParallelFloydSubComputation extends RecursiveTask<int[][]> {

        private final int[][] currentPathMatrix;
        private final int fromX;
        private final int toX;
        private final int fromY;
        private final int toY;
        private final int step;

        ParallelFloydSubComputation(int[][] currentPathMatrix,
                                    int fromX, int toX, int fromY, int toY, int step) {
            this.currentPathMatrix = currentPathMatrix;
            this.fromX = fromX;
            this.toX = toX;
            this.fromY = fromY;
            this.toY = toY;
            this.step = step;
        }

        @Override
        protected int[][] compute() {
            int[][] result = new int[currentPathMatrix.length][currentPathMatrix.length];
            for (int i = fromX; i < toX; i++) {
                for (int j = fromY; j < toY; j++) {
                    // We have to be careful not to overflow
                    int sumOfTwoPaths;
                    if (currentPathMatrix[i][step] == Integer.MAX_VALUE
                            || currentPathMatrix[step][j] == Integer.MAX_VALUE) {
                        sumOfTwoPaths = Integer.MAX_VALUE;
                    } else {
                        sumOfTwoPaths = currentPathMatrix[i][step] + currentPathMatrix[step][j];
                    }
                    // Calculating next iteration
                    result[i][j] = Math.min(currentPathMatrix[i][j], sumOfTwoPaths);
                }
            }
            return result;
        }
    }

    public static void main(String[] args) throws Exception {
        // See example URL for the graph, that I represent.
        Graph gr = new Graph(4);
        gr.addEdge(0,1);
        gr.addEdge(1,2);
        gr.addEdge(1,3);
        gr.addEdge(3,2);
        gr.addEdge(3,0);
        // Expected value
        final int infinity = Integer.MAX_VALUE;
        int[][] expectedResult = new int[][]{
                {0,1,2,2},
                {2,0,1,1},
                {infinity,infinity,0,infinity},
                {1,2,1,0}
        };
        // Make the computation and check the results.
        long fromTime = System.nanoTime();
        int[][] sequentialShortestPath = sequentialFloyd(gr);
        long sequentialDuration = System.nanoTime() - fromTime;
        assertEquals(expectedResult, sequentialShortestPath);
        // Parallel computation with results.
        fromTime = System.nanoTime();
        ForkJoinTask<int[][]> parallelShortestPathTask = ForkJoinPool.commonPool().submit(new ParallelFloyd(gr));
        int[][] parallelShortestPath = parallelShortestPathTask.get();
        long parallelDuration = System.nanoTime() - fromTime;
        assertEquals(expectedResult, parallelShortestPath);
        // Parallel computation again (Warm-up is done in previous step.
        fromTime = System.nanoTime();
        parallelShortestPathTask = ForkJoinPool.commonPool().submit(new ParallelFloyd(gr));
        parallelShortestPath = parallelShortestPathTask.get();
        long parallelDurationSecondTime = System.nanoTime() - fromTime;
        assertEquals(expectedResult, parallelShortestPath);
        // Computation time
        System.out.print("Computation time sequential: " + sequentialDuration
                + " parallel: " + parallelDuration
                + " parallel second time: " + parallelDurationSecondTime);
    }

    private static void assertEquals(int[][] expected, int[][] actual) {
        if (expected.length != actual.length)
            throw new RuntimeException(String.format("Sizes of arrays are not equal. %d != %d",
                            expected, actual));

        for (int i = 0; i < expected.length; i++) {
            for (int j = 0; j < expected.length; j++) {
                if (actual[i][j] != expected[i][j])
                    throw new RuntimeException(String.format("Got different result at [%d][%d]: %d != %d",
                                    i, j, expected[i][j], actual[i][j]));
            }
        }
    }

}
