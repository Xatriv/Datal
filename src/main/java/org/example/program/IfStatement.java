package org.example.program;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IfStatement implements Statement {
    Expression condition;
    Block ifBlock;
    Block elseBlock;
}
