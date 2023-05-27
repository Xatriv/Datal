package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.parser.Visitable;
import org.example.source.Position;

@AllArgsConstructor
public class ReturnStatement implements Statement, Visitable {
    @Getter
    Expression expression;
    @Getter
    Position position;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
