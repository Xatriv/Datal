package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;

@AllArgsConstructor
public class IfStatement implements Statement {
    @Getter
    Expression condition;
    @Getter
    Block ifBlock;
    @Getter
    Block elseBlock;
    @Getter
    Position position;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
