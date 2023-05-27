package org.example.program;

import org.example.source.Position;

public interface Statement {
    Position getPosition();
    void accept(ProgramVisitor programVisitor);
}
