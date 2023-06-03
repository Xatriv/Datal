package org.example.interpreter;

import lombok.Getter;
import org.example.source.Position;

import javax.naming.Context;
import java.util.ArrayList;
import java.util.List;

public class FunctionCallContext implements Scope {
    @Getter
    List<BlockContext> blockContexts = new ArrayList<>();
    @Getter
    List<Object> arguments;
    @Getter
    Position position;

    public FunctionCallContext(List<Object> arguments) {
        this.arguments = arguments;
    }
}
