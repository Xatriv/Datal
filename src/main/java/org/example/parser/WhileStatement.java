package org.example.parser;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WhileStatement implements Statement {
    Expression condition;
    Block loopBlock;
}
