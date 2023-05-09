package org.example.program;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MultiplicativeExpression implements Expression{
    MultiplicativeOperator operator;
    Expression leftExpression;
    Expression rightExpression;
}
