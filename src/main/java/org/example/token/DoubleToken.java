package org.example.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.example.source.Position;

public class DoubleToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private double value;

    @Getter
    private final TokenType type = TokenType.DOUBLE;
    @Getter
    private final Position position;

    public DoubleToken(double value, Position position){
        this.value=value;
        this.position = position;
    }

}

