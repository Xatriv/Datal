package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.parser.Visitable;

@AllArgsConstructor
public class ExpressionStatement implements Statement, Visitable {
    @Getter
    Expression expression;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
