package org.example.source;

import java.io.IOException;

public interface Source {

    Position getPosition();
    public String nextCharacter() throws IOException;
}
