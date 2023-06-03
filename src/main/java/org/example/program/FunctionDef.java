package org.example.program;

import org.example.parser.Visitable;
import org.example.source.Position;

import java.util.Hashtable;
import java.util.List;

public interface FunctionDef extends Visitable {
    String getName();

    Position getPosition();
    Block getBody();
    List<String> getParameters();

}
