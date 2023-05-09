package org.example.program;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class FunctionCallExpression implements Expression {
    String name;
    List<Expression> arguments;
}
