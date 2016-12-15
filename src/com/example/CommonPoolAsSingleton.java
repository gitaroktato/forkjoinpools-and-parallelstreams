package com.example;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.ForkJoinPool;
/**
 * ForkJoinPool.commonPool is using programmatic singleton.
 */
public class CommonPoolAsSingleton {

    private void run() throws Exception {
        URLClassLoader currentClassLoader = (URLClassLoader) getClass().getClassLoader();
        System.out.println("Our current class-loader is " + currentClassLoader.toString());
        // NULL, means we're in boot class loader.
        System.out.println("Common pool's class-loader: " + ForkJoinPool.commonPool().getClass().getClassLoader() + ". This means boot class loader");
        // Same for this fellow.
        System.out.println("ForkJoinPool's class-loader: " + ForkJoinPool.class.getClassLoader() + ". This means boot class loader");
        // Trying to hack results in NPE
        try {
            URL[] urls = { ForkJoinPool.class.getProtectionDomain().getCodeSource().getLocation() };
            URLClassLoader aNewHope = new URLClassLoader(urls);
            Class<?> fJPClass = aNewHope.loadClass(ForkJoinPool.class.getName());
            ForkJoinPool newCommonPool = (ForkJoinPool) fJPClass.newInstance();
            System.out.println("New common pool's class-loader "
                    + newCommonPool.commonPool().getClass().getClassLoader());
        } catch(NullPointerException ex) {
            System.out.println("Getting ForkJoinPool's source code for reloading it didn't work");
        }
        createPoolFromNewClassLoader();
    }

    /**
     * Trying to use common pool from any other class-loader won't work.
     */
    private void createPoolFromNewClassLoader() throws Exception {
        File fileForMyLCass = new File( "./bin" );
        URLClassLoader newCL = new URLClassLoader(new URL[] {fileForMyLCass.toURL()}, null);
        Class<?> toBeLoadedFromDifferentClassLoadersClass = newCL.loadClass("ToBeLoadedFromDifferentClassLoader");
        Object toBeLoadedFromDifferentClassLoader = toBeLoadedFromDifferentClassLoadersClass.newInstance();
        Method run = toBeLoadedFromDifferentClassLoadersClass.getMethod("run");
        run.invoke(toBeLoadedFromDifferentClassLoader);
    }

    public static void main(String[] args) throws Exception {
        new CommonPoolAsSingleton().run();
    }
}
