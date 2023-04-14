package org.example.lexer;

import java.io.IOException;

public interface Lexer {

    SimpleToken getToken();

    SimpleToken next() throws IOException;

}
