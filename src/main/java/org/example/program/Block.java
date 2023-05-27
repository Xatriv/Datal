package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.parser.Visitable;
import org.example.source.Position;

import java.util.List;

@AllArgsConstructor
public class Block implements Visitable {
    @Getter
    List<Statement> statements;
    @Getter
    Position position;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
