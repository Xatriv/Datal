package org.example.program;

import lombok.AllArgsConstructor;
import org.example.source.Position;

import java.util.List;

public class PrintFunctionDef implements FunctionDef{

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }

    @Override
    public String getName() {
        return "print";
    }

    @Override
    public Position getPosition() {
        return new Position(0, 0);
    }

    @Override
    public Block getBody() {
        return null;
    }

    @Override
    public List<String> getParameters() {
        return null;
    }
}
