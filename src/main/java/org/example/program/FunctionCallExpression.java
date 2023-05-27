package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;

import java.util.List;

@AllArgsConstructor
public class FunctionCallExpression implements Expression {
    @Getter
    String name;
    @Getter
    List<Expression> arguments;
    @Getter
    Position position;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
