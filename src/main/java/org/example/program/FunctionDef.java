package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.parser.Visitable;

import java.util.List;

@AllArgsConstructor
public class FunctionDef implements Visitable {
    @Getter
    String name;
    @Getter
    List<String> parameters;
    @Getter
    Block body;

    @Override
    public void accept(ProgramVisitor programVisitor) {
        programVisitor.visit(this);
    }
}
