package org.example.lexer;

import lombok.Getter;
import org.example.token.Token;
import org.example.token.TokenType;

import java.io.IOException;

public class CommentLexer implements Lexer{
    private final Lexer lexer;
    @Getter
    private Token token;

    @Override
    public Token next() throws IOException {
        token = lexer.next();
        while (token.getType() == TokenType.COMMENT){
            token = lexer.next();
        }
        return token;
    }

    public CommentLexer(Lexer lexer){
        this.lexer = lexer;
    }
}
