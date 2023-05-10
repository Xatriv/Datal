package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class IdentifierExpression implements Expression {
    @Getter
    String name;
}
