package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class FunctionCallExpression implements Expression {
    @Getter
    String name;
    @Getter
    List<Expression> arguments;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
