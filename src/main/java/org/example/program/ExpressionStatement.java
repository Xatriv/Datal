package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ExpressionStatement implements Statement{
    @Getter
    Expression expression;
}
