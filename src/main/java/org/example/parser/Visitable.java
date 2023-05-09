package org.example.parser;

import org.example.program.ProgramVisitor;

public interface Visitable {
    void accept(ProgramVisitor programVisitor);
}
