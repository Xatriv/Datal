package org.example.program;

import org.example.source.Position;

public interface Expression {
    Position getPosition();
    void accept(ProgramVisitor programVisitor);
}
