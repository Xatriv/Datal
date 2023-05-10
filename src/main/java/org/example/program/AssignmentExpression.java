package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AssignmentExpression implements Expression{
    @Getter
    Expression left;
    @Getter
    Expression right;
    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
