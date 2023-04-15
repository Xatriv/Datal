package org.example.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class IdentifierToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private String name;

    @Getter
    private final TokenType type = TokenType.IDENTIFIER;
    public IdentifierToken(String name){
        this.name=name;
    }
}
