package org.example.source;

import java.io.IOException;

public interface Source {

    Position getPosition();
    String getNewlineCharacter();
    public String nextCharacter() throws IOException;
}
