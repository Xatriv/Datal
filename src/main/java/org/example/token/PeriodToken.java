package org.example.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.types.Period;

public class PeriodToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private Period value;
    public static final TokenType type = TokenType.PERIOD;
    PeriodToken(Period value){

    }
}
