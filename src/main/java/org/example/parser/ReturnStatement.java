package org.example.parser;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReturnStatement implements Statement{
    Expression expression;
}
