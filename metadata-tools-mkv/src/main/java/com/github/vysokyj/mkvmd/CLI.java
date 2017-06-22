package com.github.vysokyj.mkvmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Parameters(separators=" =")
public class CLI {

    @Parameter(description = "Files")
    private List<String> paths = new ArrayList<>();

    @Parameter(names = { "-v", "--verbose" }, description = "More verbose output")
    private boolean verbose = false;

    @Parameter(names = { "-s", "--search" }, description = "TMDB search hint")
    private String searchHint = null;

    private List<File> getFiles() {
        List<File> files = new ArrayList<>(paths.size());
        if (paths.isEmpty()) {
            System.out.println("Missing MKV file path parameter!");
            System.exit(1);
        }
        for (String path : paths) {
            if (verbose) System.out.println("Checking file path: " + path);
            String fileExtension = Files.getFileExtension(path);

            if (!fileExtension.toLowerCase().equals("mkv")) {
                System.out.println("Invalid argument: " + path);
                System.out.println("Only MKV files ares supported!");
                System.exit(1);
            }
            File file = new File(path);
            if (!file.exists() || !file.isFile()) {
                System.out.println("Invalid argument: " + path);
                System.out.println("File not found!");
                System.exit(1);
            }
            files.add(file);
        }
        return files;
    }

    private void processFile(File file) {
        System.out.println("File: " + file.getName());
        String movieName = searchHint;
        if (movieName == null) movieName = Files.getNameWithoutExtension(file.getName());
        Engine engine = new Engine(file);
        Integer index = null;
        while (index == null) {
            Map<Integer, String> results = engine.search(movieName);
            index = getMovieIndex(movieName, results);
            if (index == null) movieName = getSearchString();
        }
        engine.save(index);
    }

    private String getSearchString() {
        String result = null;
        System.out.print("Enter new search string: ");
        while (result == null) {
            result = getLine();
        }
        return result;
    }

    private Integer getMovieIndex(String movieName, Map<Integer, String> results) {
        Integer index = null;
        int count = results.size();
        System.out.println("Search results for: \"" + movieName + "\"");
        if (count == 0) {
            System.out.println("Nothing found.");
            return null;
        }

        for (Integer key : results.keySet()) System.out.println(results.get(key));
        System.out.print("Enter 1 to " + count + " for select, n for new search or x for exit: ");
        while (index == null) {
            try {
                String line = getLine();
                if (line == null) continue;
                if (line.equals("n")) return null;
                if (line.equals("x")) System.exit(0);
                int i = Integer.parseInt(line);
                if (i < 1 || i > count) throw new IllegalArgumentException("Invalid index.");
                index = i;
            } catch (Exception ignored){
                System.out.print("Please enter valid result number: ");
            }
        }
        return index;
    }

    public void execute() {
        List<File> files = getFiles();
        System.out.printf("Processing %d files\n", files.size());
        for (File file : files) processFile(file);
    }

    private String getLine() {
        Scanner sc = new Scanner(System.in, "UTF-8");
        if (sc.hasNext()) {
            return sc.nextLine();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        CLI cli = new CLI();
        JCommander jCommander = new JCommander(cli, args);
        if (args.length == 0) {
            System.out.println("mkvmd - MKV Metadata Engine");
            System.out.println("Add metadata and cover art from themoviedb.com to MKV files.");
            System.out.println();
            System.out.println("Copyright (C) 2015 Jiri Vysoky");
            System.out.println();
            jCommander.usage();
        }
        else cli.execute();
    }
}
