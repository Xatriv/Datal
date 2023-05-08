package org.example.parser;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class FunctionCallExpression implements Expression {
    String name;
    List<Expression> arguments;
}
