package com.example;

import java.util.List;
import java.util.concurrent.RecursiveTask;

/**
 * Created by popnam on 2017.01.07..
 */
public class PseudoCodeExample {

    class Task<T> extends RecursiveTask<T> {

        private final T data;

        Task(T data) {
            this.data = data;
        }

        @Override
        protected T compute() {
            if (isSmallEnough(data)) {
                return computeDirectly();
            } else {
                List<Task> subTasks = decompose(data);
                for (Task subTask : subTasks) {
                    subTask.fork();
                }
                for (Task subTask : subTasks) {
                    subTask.join();
                }
                return aggregateResult(subTasks);
            }
        }

        private T aggregateResult(List<Task> subTasks) {
            return null;
        }

        private List<Task> decompose(T data) {
            return null;
        }

        private T computeDirectly() {
            return null;
        }

        private boolean isSmallEnough(T data) {
            return false;
        }
    }
}
