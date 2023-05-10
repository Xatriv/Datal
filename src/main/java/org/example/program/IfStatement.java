package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class IfStatement implements Statement {
    @Getter
    Expression condition;
    @Getter
    Block ifBlock;
    @Getter
    Block elseBlock;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
