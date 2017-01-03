package com.example;

/**
 * Shortest path calculation in parallel from single source.
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

    public int[][] sequentialFloyd(Graph gr) {
        int size = gr.size();
        int[][] currentIteration = new int[size][size];
        int[][] nextIteration = new int[size][size];
        for (int i : gr.size())
    }

    public static void main(String[] args) {
        Graph gr = new Graph(4);
        gr.addEdge(0,1);
        gr.addEdge(1,2);
        gr.addEdge(1,3);
        gr.addEdge(3,2);
        gr.addEdge(3,0);
    }

}
