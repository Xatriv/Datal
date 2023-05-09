package org.example.program;

import lombok.AllArgsConstructor;
@AllArgsConstructor
public class ComparativeExpression implements Expression{
    ComparisonOperator operator;
    Expression leftExpression;
    Expression rightExpression;
}
