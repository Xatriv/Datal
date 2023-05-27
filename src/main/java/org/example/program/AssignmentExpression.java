package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;

@AllArgsConstructor
public class AssignmentExpression implements Expression{
    @Getter
    Expression left;
    @Getter
    Expression right;
    @Getter
    Position position;
    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
