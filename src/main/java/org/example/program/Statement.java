package org.example.program;

public interface Statement {
    void accept(ProgramVisitor programVisitor);
}
