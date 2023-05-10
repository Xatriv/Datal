package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class MultiplicativeExpression implements Expression{
    @Getter
    MultiplicativeOperator operator;
    @Getter
    Expression leftExpression;
    @Getter
    Expression rightExpression;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
