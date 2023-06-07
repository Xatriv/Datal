package org.example;

import org.example.error.*;
import org.example.interpreter.Interpreter;
import org.example.interpreter.PrinterVisitor;
import org.example.lexer.CodeLexer;
import org.example.lexer.CommentLexer;
import org.example.parser.Parser;
import org.example.program.Program;
import org.example.source.CodeSource;
import org.example.source.Position;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String @NotNull [] args) {
        String fileName = args[0];
        try {
            runPipeline(fileName);
        } catch (IOException e) {
            System.out.println("Could not find input file.");
        }
    }

    private static void runPipeline(String fileName) throws IOException {
        Program program;
        ErrorManager eM = new ErrorManager();
        try {
            try (Reader fileReader = new FileReader(fileName)) {
                CodeSource source = new CodeSource(fileReader, eM);
                CodeLexer codeLexer = new CodeLexer(source, eM);
                CommentLexer commentLexer = new CommentLexer(codeLexer);
                Parser parser = new Parser(commentLexer, eM);
                program = parser.parse();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            }
            Interpreter interpreter = new Interpreter(eM, program);
            //            PrinterVisitor printer = new PrinterVisitor(fileName);
            //            program.accept(printer);
            System.out.println("\nErrors: ");
            eM.printErrors(Severity.INFO);
        } catch (CodeError e) {
            eM.printErrors(Severity.INFO);
        } catch (MaxErrorsExceededError e) {
            eM.printErrors(Severity.INFO);
            System.out.println(e.getMessage());
        }
    }
}