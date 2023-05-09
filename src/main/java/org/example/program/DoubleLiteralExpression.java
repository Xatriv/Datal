package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DoubleLiteralExpression implements Expression{
    @Getter
    double value;
}
