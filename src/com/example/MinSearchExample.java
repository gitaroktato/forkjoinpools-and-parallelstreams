package com.example;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Minimum search example with both fork-join and streams.
 */
public class MinSearchExample {

    static class MinFinder extends RecursiveTask<Integer> {

        final int[] data;
        final int from;
        final int to;
        final int threshold;

        MinFinder(int[] data, int threshold) {
            this.data = data;
            this.threshold = threshold;
            this.from = 0;
            this.to = data.length - 1;
        }

        MinFinder(int[] data, int threshold, int from, int to) {
            this.data = data;
            this.threshold = threshold;
            this.from = from;
            this.to = to;
        }


        @Override
        protected Integer compute() {
            if (to - from < threshold) {
                return sequentialMinSearch();
            } else {
                int middle = from + ((to - from) / 2);
                MinFinder subtask1 = new MinFinder(data, threshold, from, middle);
                MinFinder subtask2 = new MinFinder(data, threshold, middle, to);
                subtask1.fork();
                subtask2.fork();
                return aggregateResults(subtask1.join(), subtask2.join());
            }
        }

        private int aggregateResults(int first, int second) {
            return Math.min(first, second);
        }

        private int sequentialMinSearch() {
            int min = data[from];
            for (int i = from; i < to; i++) {
                if (data[i] < min) {
                    min = data[i];
                }
            }
            return min;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int[] randomSeed = IntStream.generate(() -> ThreadLocalRandom.current()
                .nextInt(0, 10000))
                .limit(100).toArray();
        // Min search in parallel stream
        System.out.println("Minimum found as stream " +
                Arrays.stream(randomSeed).parallel().min().getAsInt());
        // Minimum search from custom fork-join pool
        ForkJoinPool myPool = new ForkJoinPool(2);
        myPool.submit(() -> System.out.println(
                Arrays.stream(randomSeed).parallel().min().getAsInt())
        ).get();
        // Min search in fork-join pool
        Integer forkJoinResult = ForkJoinPool.commonPool().invoke(
                new MinFinder(randomSeed, 10));
        System.out.println("Minimum found in fork-join " +
                forkJoinResult);

    }
}
