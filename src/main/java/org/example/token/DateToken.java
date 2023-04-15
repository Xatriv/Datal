package org.example.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.example.types.Date;

public class DateToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private Date value;
    @Getter
    private final TokenType type = TokenType.PERIOD;
    DateToken(Date value){
        this.value=value;
    }
}
