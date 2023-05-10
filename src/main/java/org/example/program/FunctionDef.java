package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class FunctionDef {
    @Getter
    String name;
    @Getter
    List<Parameter> parameters;
    @Getter
    Block body;
}
