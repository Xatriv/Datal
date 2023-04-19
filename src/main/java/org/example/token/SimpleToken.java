package org.example.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;

@AllArgsConstructor
public class SimpleToken implements Token{
    @Getter
    public final TokenType type;
    @Getter
    private final Position position;
}