package com.example;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.stream.IntStream;

/**
 * ForkJoinPool and parallelStreams exception handling.
 */
public class ExceptionHandlingInStreams {


    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
        // Using UncaughtExceptionHandler
        ForkJoinPool myPool = new ForkJoinPool(1, ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                (t, e) -> System.out.println("Got exception from thread " + t.getName()), false);
        // Using parallel stream from a different ForkJoinPool
        ForkJoinTask<?> result = myPool.submit(() -> {
                    IntStream.of(1).parallel().forEach(l -> {
                        Thread current = Thread.currentThread();
                        current.getUncaughtExceptionHandler().uncaughtException(
                                current, new RuntimeException("Hate")
                        );
                    });
                });
        result.get();
        // If we're not using uncaughtException method, Exception will propagate after calling get().
        result = myPool.submit(() ->
                IntStream.of(1).parallel().forEach(l -> {
                    throw new RuntimeException("Hate");
                })
            );
        System.out.println("Get exception after calling get()");
        result.get();
    }
}
