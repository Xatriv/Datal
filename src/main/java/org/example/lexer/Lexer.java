package org.example.lexer;

import org.example.token.TokenType;

import java.io.IOException;

public interface Lexer {

    TokenType getToken();

    TokenType next() throws IOException;

}
