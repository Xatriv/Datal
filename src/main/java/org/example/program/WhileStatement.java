package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class WhileStatement implements Statement {
    @Getter
    Expression condition;
    @Getter
    Block loopBlock;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
