package org.example;

import org.example.lexer.CodeLexer;
import org.example.token.*;
import org.example.source.FileSource;
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
        FileSource source = new FileSource(fileName);
        CodeLexer codeLexer = new CodeLexer(source);
        Token xd;
        while ((xd = codeLexer.next()).getType() != TokenType.EOF) {
            System.out.println(xd.getType().name());
            if (xd.getType() == TokenType.STRING){
                StringToken xd1 = (StringToken) xd;
                System.out.println(xd1.getValue());
            } else if (xd.getType() == TokenType.DOUBLE){
                DoubleToken xd1 = (DoubleToken) xd;
                System.out.println(xd1.getValue());
            } else if (xd.getType() == TokenType.INT){
                IntToken xd1 = (IntToken) xd;
                System.out.println(xd1.getValue());
            } else if (xd.getType() == TokenType.IDENTIFIER){
                IdentifierToken xd1 = (IdentifierToken) xd;
                System.out.println(xd1.getName());
            } else if (xd.getType() == TokenType.COMMENT){
                CommentToken xd1 = (CommentToken) xd;
                System.out.println(xd1.getValue());
            }
        }
    }
}