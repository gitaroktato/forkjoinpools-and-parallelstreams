package com.example;

import java.io.IOException;
import java.nio.file.*;

/**
 * Created by Oresztesz_Margaritis on 1/6/2017.
 */
public class ParallelGrepExample {

    public static void main(String[] args) throws Exception {
        // Using parallel streams
        Path start = Paths.get("").toAbsolutePath();
        System.out.println("Search path is: " + start);
        final String needle = "RootTaskAsync";
        Files.walk(start, 100).parallel().filter(path -> path.toString().endsWith(".java")).forEach(path -> {
            try {
                Files.lines(path).parallel().filter(line -> line.contains(needle)).forEach(line -> {
                    System.out.println("Match in " + path.toString() + " found: \n" + line + "\n ... ");
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
