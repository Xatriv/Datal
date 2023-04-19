package org.example.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.example.source.Position;
import org.example.types.Date;

public class DateToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private Date value;
    @Getter
    private final TokenType type = TokenType.DATE;
    @Getter
    private final Position position;
    public DateToken(Date value, Position position){
        this.value=value;
        this.position = position;
    }
}
