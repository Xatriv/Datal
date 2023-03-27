package org.example.lexer;

public enum Token {
    WHILE("WHILE"),
    IF("IF"),
    ELSE_IF("ELIF"),
    ELSE("ELSE"),
    IDENTIFIER,

    PARENTHESIS_L("("),
    PARENTHESIS_R(")"),

    EQUALS("=="),
    NOT_EQUAL("=="),
    LESS_THAN("<"),
    LESS_OR_EQUAL_THAN("<="),
    MORE_OR_EQUAL_THAN(">="),

    SEMICOLON(";"),
    THEN("THEN"),
    END("END"),
    RETURN("RETURN"),

    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),

    STRING_LITERAL_DELIMITER("\""),

    // Multiple conventions. First occurrence determines newline char sequence
    EOL,
    EOF,

    INT,
    DOUBLE,
    STRING;

    Token() {}
    Token(String keyword) {
        this.keyword = keyword;
    }
    private String keyword;
    private TokenValue value;

}
