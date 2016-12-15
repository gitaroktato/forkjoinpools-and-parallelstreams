package com.example;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * Notice, that workers don't block on join() or on invokeAll() functions.
 * This only means, that additional tasks are pushed into their WorkQueue.
 */
public class PowerOfWorkerThreads {

    private static long basetime = System.currentTimeMillis();

    static long currentTime() {
        return System.currentTimeMillis() - basetime;
    }

    static abstract class AbstractTask extends RecursiveAction {

        private final AbstractTask parent;

        protected AbstractTask(AbstractTask parent) {
            this.parent = parent;
        }

        protected final void intensiveComputation() {
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            String parentAsString = parent == null ? null : parent.getClassNameAndHashCode();
            return getClassNameAndHashCode() + " -> " + parentAsString + " at " + Thread.currentThread().getName();
        }

        private String getClassNameAndHashCode() {
            return getClass().getSimpleName() + "@" + hashCode();
        }

        @Override
        protected final void compute() {
            System.out.println(this + " started at " + currentTime());
            doCompute();
            System.out.println(this + " finished at " + currentTime());
        }

        protected abstract void doCompute();
    }

    static class LeafTask extends AbstractTask {

        protected LeafTask(AbstractTask parent) {
            super(parent);
        }

        @Override
        protected void doCompute() {
            intensiveComputation();
        }
    }

    static class IntermediateTask extends AbstractTask {

        protected IntermediateTask(AbstractTask parent) {
            super(parent);
        }

        @Override
        protected void doCompute() {
            intensiveComputation();
            invokeAll(new LeafTask(this),
                    new LeafTask(this),
                    new LeafTask(this));
        }
    }


    static class RootTask extends AbstractTask {

        protected RootTask() {
            super(null);
        }

        @Override
        protected void doCompute() {
            invokeAll(new IntermediateTask(this),
                    new IntermediateTask(this));
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
        // First argument controls maximum degree of parallelism. There will be fewer workers created in the beginning,
        // but number of workers can't extend this number.
        ForkJoinPool myPool = new ForkJoinPool(4);
        // We create the following task tree in the example:
        // RootTask -> 3 IntermediateTasks -> 3 LeafTasks
        // IntermediateTask and LeafTask takes 3 second to finish.
        ForkJoinTask<Void> task1 = myPool.submit(new RootTask());
        ForkJoinTask<Void> task2 = myPool.submit(new RootTask());
        ForkJoinTask<Void> task3 = myPool.submit(new RootTask());
        // Waiting to finish
        task1.get(); task2.get(); task3.get();
        // Sequentially it would take 81 seconds.
        System.out.format("Computation finished after %ds" + currentTime() / 1000);
        // As ForkJoinPool uses daemon threads, we don't have to gracefully shut them down as we do with a ThreadPoolExecutor
        // ThreadPooExecutor.shutdown()
        // ThreadPoolExecutor.awaitTermination() etc...
    }
}
