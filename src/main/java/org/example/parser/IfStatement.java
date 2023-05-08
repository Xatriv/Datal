package org.example.parser;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IfStatement implements Statement {
    Expression condition;
    Block ifBlock;
    Block elseBlock;
}
