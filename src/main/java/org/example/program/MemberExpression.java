package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class MemberExpression implements Expression {
    @Getter
    Expression object;
    @Getter
    Expression member;
    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
