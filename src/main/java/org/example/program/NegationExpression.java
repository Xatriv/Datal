package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class NegationExpression implements Expression{
    @Getter
    NegationOperator operator;
    @Getter
    Expression expression;
}
