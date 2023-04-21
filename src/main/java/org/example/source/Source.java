package org.example.source;

import java.io.IOException;

public interface Source {

    int EOF = -1;
    char ETX = 3;
    Position getPosition();
    int nextCharacter() throws IOException;
}
