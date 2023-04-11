package org.example.lexer;

public interface ILexer {

    Token getToken(); //why?
    void setToken(Token token); //why?

    Token Next();

}
