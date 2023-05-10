package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class AdditiveExpression implements Expression {
    @Getter
    AdditiveOperator operator;
    @Getter
    Expression leftExpression;
    @Getter
    Expression rightExpression;
}
