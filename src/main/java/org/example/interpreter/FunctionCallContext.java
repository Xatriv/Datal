package org.example.interpreter;

import lombok.Getter;
import lombok.Setter;
import org.example.source.Position;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallContext implements Scope {
    @Getter
    List<BlockContext> blockContexts = new ArrayList<>();
    @Getter
    List<Object> arguments;
    @Getter
    Position position;
    @Getter @Setter
    Boolean returned;

    public FunctionCallContext(List<Object> arguments, Position position) {
        this.arguments = arguments;
        this.position = position;
        this.returned = false;
    }
}
