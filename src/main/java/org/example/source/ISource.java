package org.example.source;

import java.io.IOException;

public interface ISource {

    public String nextCharacter() throws IOException;
}
