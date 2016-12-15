package com.example;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

/**
 * ForkJoinPool and parallelStreams exception handling.
 */
public class ExceptionHandling {

    static class BadTask extends RecursiveAction {
        @Override
        protected void compute() {
            throw new RuntimeException("Bad things happen...");
        }
    }

    static class BadTaskUsingHandler extends RecursiveAction {
        @Override
        protected void compute() {
            Thread current = Thread.currentThread();
            current.getUncaughtExceptionHandler().uncaughtException(
                    current, new RuntimeException("Bad things happen..."));
        }
    }

    static class RootTask extends RecursiveAction {
        @Override protected void compute() {
            BadTask badTask = new BadTask();
            badTask.fork();
            badTask.join();
        }
    }

    static class RootTaskAsync extends RecursiveAction {
        @Override protected void compute() {
            BadTask badTask = new BadTask();
            badTask.fork();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
        // When joining BadTask, I got an exception.
        ForkJoinTask<Void> result = ForkJoinPool.commonPool().submit(new RootTask());
        try {
            result.get();
        } catch (Exception ex) {
            System.out.println("...");
        }
        printResults(result);
        // When not waiting for BadTask, I exit normally.
        result = ForkJoinPool.commonPool().submit(new RootTaskAsync());
        result.get();
        printResults(result);
        // Using UncaughtExceptionHandler
        ForkJoinPool myPool = new ForkJoinPool(1, ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                (t, e) -> System.out.println("Got exception from thread " + t.getName()), false);
        result = myPool.submit(new BadTaskUsingHandler());
        try {
            result.get();
        } catch(Exception ex) {
            //
        }
        printResults(result);
    }

    private static void printResults(ForkJoinTask<Void> result) {
        System.out.println("Is done? " + result.isDone());
        System.out.println("Is completed normally? " + result.isCompletedNormally());
        System.out.println("Is completed abnormally? " + result.isCompletedAbnormally());
        System.out.println("Got exception: " + result.getException() + "\n\n");
    }
}
