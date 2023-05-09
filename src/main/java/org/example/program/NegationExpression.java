package org.example.program;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NegationExpression implements Expression{

    NegationOperator operator;
    Expression expression;
}
