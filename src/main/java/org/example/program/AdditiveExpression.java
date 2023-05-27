package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.parser.Visitable;
import org.example.source.Position;

@AllArgsConstructor
public class AdditiveExpression implements Expression, Visitable {
    @Getter
    AdditiveOperator operator;
    @Getter
    Expression leftExpression;
    @Getter
    Expression rightExpression;
    @Getter
    Position position;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
