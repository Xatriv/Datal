package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class StringLiteralExpression implements Expression {
    @Getter
    String value;
}
