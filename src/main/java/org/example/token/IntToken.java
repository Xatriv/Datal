package org.example.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.types.Period;

@AllArgsConstructor
public class IntToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private int value;
}
