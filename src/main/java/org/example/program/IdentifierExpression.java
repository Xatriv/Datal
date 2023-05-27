package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;

@AllArgsConstructor
public class IdentifierExpression implements Expression {
    @Getter
    String name;
    @Getter
    Position position;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
