package org.example.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.source.Position;

@AllArgsConstructor
public class StringToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private String value;
    @Getter
    private TokenType type = TokenType.STRING;
    @Getter
    private final Position position;
    public StringToken(String value, Position position){
        this.value=value;
        this.position = position;
    }

}
