package org.example.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class DoubleToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private double value;
}

