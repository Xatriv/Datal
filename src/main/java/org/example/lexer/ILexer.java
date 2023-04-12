package org.example.lexer;

import java.io.IOException;

public interface ILexer {

    Token getToken();

    Token next() throws IOException;

}
