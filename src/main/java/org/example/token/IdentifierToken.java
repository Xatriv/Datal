package org.example.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.example.source.Position;

public class IdentifierToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private String name;

    @Getter
    private final TokenType type = TokenType.IDENTIFIER;
    @Getter
    private final Position position;
    public IdentifierToken(String name, Position position){
        this.name=name;
        this.position = position;
    }
}
