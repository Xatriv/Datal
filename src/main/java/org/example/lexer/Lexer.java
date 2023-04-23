package org.example.lexer;

import org.example.token.Token;

import java.io.IOException;

public interface Lexer {

    Token getToken();

    Token next() throws IOException;

}
