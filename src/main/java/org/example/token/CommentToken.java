package org.example.token;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.source.Position;

@AllArgsConstructor
public class CommentToken implements Token{
    @Getter @Setter(AccessLevel.PRIVATE)
    private String value;
    @Getter
    private TokenType type = TokenType.COMMENT;
    @Getter
    private final Position position;
    public CommentToken(String value, Position position){
        this.value = value;
        this.position = position;
    }

}
