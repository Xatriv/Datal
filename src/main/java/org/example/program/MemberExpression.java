package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;

@AllArgsConstructor
public class MemberExpression implements Expression {
    @Getter
    Expression object;
    @Getter
    Expression member;
    @Getter
    Position position;
    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
