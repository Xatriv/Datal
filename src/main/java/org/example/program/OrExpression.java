package org.example.program;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrExpression implements Expression{
    Expression leftExpression;
    Expression rightExpression;
}
