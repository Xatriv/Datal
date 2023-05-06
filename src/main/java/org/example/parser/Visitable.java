package org.example.parser;

import org.example.interpreter.ProgramVisitor;

public interface Visitable {
    void accept(ProgramVisitor programVisitor);
}
