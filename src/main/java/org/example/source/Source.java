package org.example.source;

import java.io.IOException;

public interface Source {

    public String nextCharacter() throws IOException;
}
