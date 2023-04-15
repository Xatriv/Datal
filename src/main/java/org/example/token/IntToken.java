package org.example.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class IntToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private int value;
    @Getter
    private final TokenType type = TokenType.INT;
    public IntToken(int value){
        this.value=value;
    }
}
