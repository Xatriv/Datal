package org.example.program;

import lombok.Getter;
import org.example.parser.Visitable;

import java.util.Hashtable;

public class Program implements Visitable {
    @Getter
    Hashtable<String, FunctionDef> functions;

    public Program(Hashtable<String, FunctionDef> functions){
        this.functions = functions;
    }

    public void accept(ProgramVisitor visitor) {
        visitor.visit(this);
    }
}
