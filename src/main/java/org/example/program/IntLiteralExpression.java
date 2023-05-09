package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class IntLiteralExpression implements Expression{
    @Getter
    int value;
}
