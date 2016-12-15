package com.example;

import javax.print.attribute.IntegerSyntax;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

/**
 * Example shows differences in ForkJoinPool's async modes.
 */
public class FIFOOrLIFO {

    static abstract class AbstractTask extends RecursiveAction {

        protected final void intensiveComputation() {
            IntStream.of(1, 10_000).forEach(d -> { float dummy = (float) (3.14F / 8.4392 * Math.random() / 5.123415569);});
        }
    }

    static class LeafTask extends AbstractTask {

        @Override
        protected void compute() {
            intensiveComputation();
        }
    }

    static class IntermediateTask extends AbstractTask {

        @Override
        protected void compute() {
            intensiveComputation();
            IntStream.range(1,30).forEach(l -> new LeafTask().fork());
        }
    }


    static class RootTask extends AbstractTask {

        @Override
        protected void compute() {
            intensiveComputation();
            IntStream.range(1,30).forEach(l -> new IntermediateTask().fork());
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        // Create an uncaughtExceptionHandler that prints faults.
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> System.out.println(e);
        // Using pool in async mode will result in using WorkQueues as FIFO instead of LIFO.
        // Good, if existing tasks create new tasks casually, but they're waiting them to finish.
        ForkJoinPool asyncPool = new ForkJoinPool(4,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                uncaughtExceptionHandler, true);
        // We create the following task pipeline in the example
        // RootTask -> IntermediateTask -> LeafTask
        long start = System.currentTimeMillis();
        IntStream.range(1, 10_000).forEach(l -> asyncPool.submit(new RootTask()));
        asyncPool.shutdown();
        asyncPool.awaitTermination(30, TimeUnit.SECONDS);
        System.out.format("Async pool finished at %d\n", System.currentTimeMillis() - start);
        // Now let's try with LIFO
        ForkJoinPool notAsyncPool = new ForkJoinPool(4,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                uncaughtExceptionHandler, false);
        start = System.currentTimeMillis();
        IntStream.range(1, 10_000).forEach(l -> notAsyncPool.submit(new RootTask()));
        notAsyncPool.shutdown();
        notAsyncPool.awaitTermination(30, TimeUnit.SECONDS);
        System.out.format("Not async pool finished at %d\n", System.currentTimeMillis() - start);
    }
}
