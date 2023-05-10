package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.parser.Visitable;

@AllArgsConstructor
public class AdditiveExpression implements Expression, Visitable {
    @Getter
    AdditiveOperator operator;
    @Getter
    Expression leftExpression;
    @Getter
    Expression rightExpression;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
