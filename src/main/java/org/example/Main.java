package org.example;

import org.example.lexer.CodeLexer;
import org.example.token.StringToken;
import org.example.token.Token;
import org.example.token.TokenType;
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
        Token xd;
        while ((xd = codeLexer.next()).getType() != TokenType.EOF) {
            System.out.println(xd.getType().name());
            if (xd.getType() == TokenType.STRING){
                StringToken xd1 = (StringToken) xd;
                System.out.println(xd1.getValue());
            }
        }
    }
}