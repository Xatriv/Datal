package org.example.program;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AndExpression implements Expression{
    Expression rightExpression;
    Expression leftExpression;
}
