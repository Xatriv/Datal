package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ReturnStatement implements Statement{
    @Getter
    Expression expression;
}
