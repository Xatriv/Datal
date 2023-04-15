package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SimpleToken implements Token{
    @Getter
    public final TokenType type;
}