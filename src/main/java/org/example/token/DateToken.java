package org.example.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.types.Date;

@AllArgsConstructor
public class DateToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private Date value;
}
