package org.example.parser;

import org.example.interpreter.ProgramVisitor;

import java.util.Hashtable;

public class Program implements Visitable {
    Hashtable<String, FunctionDef> functions;

    public Program(Hashtable<String, FunctionDef> functions){
        this.functions = functions;
    }

    public void accept(ProgramVisitor visitor) {
        visitor.visit();
    }
}
