package org.example.parser;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MultiplicativeExpression implements Expression{
    MultiplicativeOperator operator;
    Expression leftExpression;
    Expression rightExpression;
}
