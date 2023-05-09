package org.example.program;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AdditiveExpression implements Expression {
    AdditiveOperator operator;
    Expression leftExpression;
    Expression rightExpression;
}
