package com.example;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

public class PowerOfWorkerThreads {

    static long basetime = System.currentTimeMillis();

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

    public static void main(String[] args) throws InterruptedException, IOException {
        ForkJoinPool myPool = new ForkJoinPool(2);
        myPool.submit(new RootTask());
        myPool.submit(new RootTask());
        myPool.submit(new RootTask());
        myPool.awaitTermination(125, TimeUnit.SECONDS);
    }
}