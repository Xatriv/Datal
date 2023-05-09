package org.example.program;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WhileStatement implements Statement {
    Expression condition;
    Block loopBlock;
}
