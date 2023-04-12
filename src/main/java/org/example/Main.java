package org.example;

import org.example.lexer.Lexer;
import org.example.source.Source;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class Main {
    public static void main(String @NotNull [] args) {
        String fileName = args[0];
        try {
            runPipeline(fileName);
        } catch (IOException  e){
            System.out.println("Could not find input file.");
        }
    }

    private static void runPipeline(String fileName) throws IOException {
        Source source = new Source(fileName);
        Lexer lexer = new Lexer(source);
    }
}