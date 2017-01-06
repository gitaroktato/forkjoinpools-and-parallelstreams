package com.example;

import java.io.IOException;
import java.nio.file.*;

/**
 * Example showing how to perform grep in parallel using streams.
 * Note: Files.lines has some performance problems and will be fixed in Java 9.
 * http://bytefish.de/blog/jdk8_files_lines_parallel_stream/
 */
public class ParallelGrepExample {

    public static void main(String[] args) throws Exception {
        // Using parallel streams
        Path start = Paths.get("").toAbsolutePath();
        System.out.println("Search path is: " + start);
        final String needle = "RootTaskAsync";
        Files.walk(start, 100).parallel().filter(path -> path.toString().endsWith(".java")).forEach(path -> {
            try {
                Files.lines(path).parallel().filter(line -> line.contains(needle)).forEach(line ->
                        System.out.println("Match in " + path.toString() + " found: \n" + line + "\n ... "));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
