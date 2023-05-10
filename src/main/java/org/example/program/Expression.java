package org.example.program;

public interface Expression {
    void accept(ProgramVisitor programVisitor);
}
