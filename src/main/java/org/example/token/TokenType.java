package org.example.token;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public enum TokenType {
    WHILE("while"),
    IF("if"),
    ELSE("else"),
    AND("and"),
    OR("or"),
    NOT("not"),
    RETURN("return"),

    PARENTHESIS_L("("),
    PARENTHESIS_R(")"),
    BLOCK_DELIMITER_L("{"),
    BLOCK_DELIMITER_R("}"),

    ASSIGN("="),
    EQUALS("=="),
    NOT_EQUAL("!="),
    LESS_THAN("<"),
    MORE_THAN(">"),
    LESS_OR_EQUAL_THAN("<="),
    MORE_OR_EQUAL_THAN(">="),

    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MEMBER("."),
    SEPARATOR(","),
    SEMICOLON(";"),

    INT,
    DOUBLE,
    STRING,
    DATE,
    PERIOD,

    IDENTIFIER,

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
