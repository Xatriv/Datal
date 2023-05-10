package org.example.program;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class Block {
    @Getter
    List<Statement> statements;
}
