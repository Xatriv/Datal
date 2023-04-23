package org.example.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.example.source.Position;

public class IntToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private int value;
    @Getter
    private final TokenType type = TokenType.INT;
    @Getter
    private final Position position;
    public IntToken(int value, Position position){
        this.value=value;
        this.position = position;
    }
}
