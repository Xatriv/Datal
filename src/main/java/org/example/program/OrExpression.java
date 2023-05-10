package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class OrExpression implements Expression{
    @Getter
    Expression leftExpression;
    @Getter
    Expression rightExpression;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
