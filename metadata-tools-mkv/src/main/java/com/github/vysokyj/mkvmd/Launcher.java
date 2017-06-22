package com.github.vysokyj.mkvmd;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Launcher {

    public static String[] exec(List<String> args, boolean quiet) {
        return Launcher.exec(args.toArray(new String[args.size()]), quiet);
    }

    public static String[] exec(List<String> args) {
        return Launcher.exec(args.toArray(new String[args.size()]));
    }

    public static String[] exec(String[] args) {
        return Launcher.exec(args, false);
    }

    /**
     * Execute external program. Write output to console and return output to caller method.
     * @param args arguments
     * @return lines
     */
    public static String[] exec(String[] args, boolean quiet) {
        //print(args);
        List<String> lines = new ArrayList<>();
        Process p;
        try {
            p = Runtime.getRuntime().exec(args);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!quiet) System.out.println(line);
                lines.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines.toArray(new String[lines.size()]);
    }
//
//    static void print(String[] args) {
//        for (String arg : args) {
//            if (arg.contains(" ")) System.out.print("\"" + arg + "\"");
//            else System.out.print(arg);
//            System.out.print(" ");
//        }
//        System.out.println();
//    }
}

