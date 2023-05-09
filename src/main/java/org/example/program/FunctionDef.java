package org.example.program;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class FunctionDef {
    String name;
    List<Parameter> parameters;
    Block body;
}
