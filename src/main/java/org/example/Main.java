package org.example;

import org.example.lexer.CodeLexer;
import org.example.lexer.CommentLexer;
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
        CommentLexer commentLexer = new CommentLexer(codeLexer);
        Token tok;
        while ((tok = commentLexer.next()).getType() != TokenType.EOF) {
            System.out.println(tok.getType().name());
            if (tok.getType() == TokenType.STRING){
                StringToken tok1 = (StringToken) tok;
                System.out.println(tok1.getValue());
            } else if (tok.getType() == TokenType.DOUBLE){
                DoubleToken tok1 = (DoubleToken) tok;
                System.out.println(tok1.getValue());
            } else if (tok.getType() == TokenType.INT){
                IntToken tok1 = (IntToken) tok;
                System.out.println(tok1.getValue());
            } else if (tok.getType() == TokenType.IDENTIFIER){
                IdentifierToken tok1 = (IdentifierToken) tok;
                System.out.println(tok1.getName());
            } else if (tok.getType() == TokenType.COMMENT){
                CommentToken tok1 = (CommentToken) tok;
                System.out.println(tok1.getValue());
            }
        }
    }
}