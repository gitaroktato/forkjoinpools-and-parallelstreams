package com.example;
import com.example.management.ForkJoinPoolManaged;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
/**
 * Created by Oresztesz_Margaritis on 12/15/2016.
 */
public class MonitoringForkJoinPools {

    public static void main(String[] args) throws Exception {
        ForkJoinPoolManaged managedPool = new ForkJoinPoolManaged(ForkJoinPool.commonPool());
        managedPool.registerMBean();

        System.in.read();
        ForkJoinPool.commonPool().submit(() -> {
           System.out.println("Hello World!!!");
        });
        ForkJoinPool.commonPool().submit(() -> {
            System.out.println("Hello World!!!");
        });
        ForkJoinPool.commonPool().submit(() -> {
            System.out.println("Hello World!!!");
        });
        ForkJoinPool.commonPool().submit(() -> {
            System.out.println("Hello World!!!");
        });
        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(1, TimeUnit.SECONDS);
        System.in.read();
    }
}
