package org.example.program;

import lombok.Getter;
import org.example.source.Position;

import java.util.List;

public class ReadStringFunctionDef implements FunctionDef {
    @Getter
    String name = "readString";
    @Getter
    Position position = new Position(0,0);

    public ReadStringFunctionDef(){

    }
    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
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
