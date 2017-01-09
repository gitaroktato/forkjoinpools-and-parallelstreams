package com.example;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

/**
 * How to use other fork-join pool with CompletableFuture?
 */
public class AvoidCommonPoolWithCompletableFutureExample {

    public static void main(String[] args) throws Exception {
        // This can produce some very strange results, like
        // pool-1-thread-1
        // ForkJoinPool.commonPool-worker-3
        // ForkJoinPool.commonPool-worker-1
        CompletableFuture<Optional<String>> future = CompletableFuture.supplyAsync(() ->
                Stream.of("one", "two", "three")
                        .parallel()
                        .peek(s -> System.out.println(Thread.currentThread().getName()))
                        .filter(s -> s.length() < 4)
                        .reduce(String::concat), Executors.newSingleThreadExecutor());
        System.out.println(future.get());
        // Just works
        future = CompletableFuture.supplyAsync(() ->
                Stream.of("one", "two", "three")
                        .parallel()
                        .peek(s -> System.out.println(Thread.currentThread().getName()))
                        .filter(s -> s.length() < 4)
                        .reduce(String::concat), new ForkJoinPool(2));
        System.out.println(future.get());
    }
}
