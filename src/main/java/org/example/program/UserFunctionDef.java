package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;

import java.util.List;

@AllArgsConstructor
public class UserFunctionDef implements FunctionDef {
    @Getter
    String name;
    @Getter
    List<String> parameters;
    @Getter
    Block body;
    @Getter
    Position position;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }

    public boolean areArgumentsAcceptable(List<String> arguments) {
        return parameters.size() == arguments.size();
    }
}
