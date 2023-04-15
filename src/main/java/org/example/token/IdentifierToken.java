package org.example.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class IdentifierToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private String name;
}
