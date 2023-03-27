package org.example;

import org.example.lexer.Lexer;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class Main {
    public static void main(String @NotNull [] args) {
        String fileName = args[0];
//        String fileName = "/first.txt";
        try {
            runPipeline(fileName);
        } catch (IOException  e){
            System.out.println("Could not find input file.");
        }

        System.out.println("Hello world!");
    }

    private static void runPipeline(String fileName) throws IOException {
        BufferedReader br = initiateSource(fileName);
        Lexer lexer = new Lexer(br);

    }
    private static BufferedReader initiateSource(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        return new BufferedReader(new FileReader(fileName));
    }
}