package org.example.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class DoubleToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private double value;

    @Getter
    private final TokenType type = TokenType.DOUBLE;

    public DoubleToken(double value){
        this.value=value;
    }

}

