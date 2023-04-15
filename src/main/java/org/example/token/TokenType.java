package org.example.token;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public enum TokenType {
    WHILE("WHILE"),
    IF("IF"),
    ELSE("ELSE"),

    PARENTHESIS_L("("),
    PARENTHESIS_R(")"),

    EQUALS("=="),
    NOT_EQUAL("=="),
    LESS_THAN("<"),
    LESS_OR_EQUAL_THAN("<="),
    MORE_OR_EQUAL_THAN(">="),

    SEMICOLON(";"),
    BLOCK_DELIMITER_L("{"),
    BLOCK_DELIMITER_R("}"),
    RETURN("return"),

    INT,
    DOUBLE,
    STRING,
    DATE,
    PERIOD,

    IDENTIFIER,


    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MEMBER("."),
    SEPARATOR("."),

    STRING_DELIMITER_L("["),
    STRING_DELIMITER_R("]"),

    // Multiple conventions. First occurrence determines newline char sequence
    EOL, // TODO check if keyword can be null
    EOF("\0");

    TokenType() {}
    TokenType(String keyword) {
        this.keyword = keyword;
    }
    @Getter @Setter(AccessLevel.PRIVATE)
    private String keyword;
}
