package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;

@AllArgsConstructor
public class ComparativeExpression implements Expression{
    @Getter
    ComparisonOperator operator;
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
