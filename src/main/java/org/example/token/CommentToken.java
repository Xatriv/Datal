package org.example.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class CommentToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private String value;
    @Getter
    private TokenType type = TokenType.COMMENT;
    public CommentToken(String value){
        this.value=value;
    }

}
