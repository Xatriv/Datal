package org.example.lexer;

import org.example.source.Position;
import org.example.token.SimpleToken;
import org.example.token.Token;
import org.example.token.TokenType;

import java.util.List;


public class MockLexer  implements Lexer {
    List<Token> tokens;
    private int index;
    public MockLexer(List<Token> tokens){
        this.tokens = tokens;
        index = -1;
    }
    @Override
    public Token getToken() {
        if (index == -1 || tokens.size() == 0){
            return null;
        } else if (index >= tokens.size()) {
            return tokens.get(tokens.size()-1);
        }
        return tokens.get(index);
    }

    @Override
    public Token next() {
        index++;
        return getToken();
    }
}
