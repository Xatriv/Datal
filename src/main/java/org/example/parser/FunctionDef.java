package org.example.parser;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class FunctionDef {
    String name;
    List<Parameter> parameters;
    Block body;
}
