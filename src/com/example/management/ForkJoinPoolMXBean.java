package com.example.management;
/**
 * Created by Oresztesz_Margaritis on 12/15/2016.
 */
public interface ForkJoinPoolMXBean {

    int getActiveThreadCount();
    int getRunningThreadCount();
    int getQueuedSubmissionCount();
    int getPoolSize();
    long getStealCount();

}
