package org.example.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.example.types.Period;

public class PeriodToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private Period value;
    @Getter
    private final TokenType type = TokenType.PERIOD;
    public PeriodToken(Period value){
        this.value=value;
    }
}
