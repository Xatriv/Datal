package org.example.token;

import org.example.source.Position;

public interface Token {
    TokenType getType();
    Position getPosition();
}
