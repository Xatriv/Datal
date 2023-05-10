package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AndExpression implements Expression{
    @Getter
    Expression rightExpression;
    @Getter
    Expression leftExpression;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
