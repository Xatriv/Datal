package org.example;

import org.example.lexer.CodeLexer;
import org.example.lexer.SimpleToken;
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
        CodeLexer codeLexer = new CodeLexer(source);
        SimpleToken xd;
        while ( (xd = codeLexer.next()) != SimpleToken.EOF) {
            System.out.println(xd.name());
        }
    }
}