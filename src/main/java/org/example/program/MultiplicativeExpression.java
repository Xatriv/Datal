package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;

@AllArgsConstructor
public class MultiplicativeExpression implements Expression{
    @Getter
    MultiplicativeOperator operator;
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
