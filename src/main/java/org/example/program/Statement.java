package org.example.program;

import org.example.source.Position;

import java.awt.*;

public interface Statement {
    Position getPosition();
    void accept(ProgramVisitor programVisitor);
}
