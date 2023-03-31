package org.example.lexer;

public enum Token {
    WHILE("WHILE"),
    IF("IF"),
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
    BLOCK_DELIMITER_L("{"),
    BLOCK_DELIMITER_R("}"),
    RETURN("return"),

    DOUBLE("dpf"),
    INTEGER("int"),
    DATETIME("dat"),

    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),

    STRING_LITERAL_DELIMITER_L("["),
    STRING_LITERAL_DELIMITER_R("]"),

    // Multiple conventions. First occurrence determines newline char sequence
    EOL,
    EOF;

    Token() {}
    Token(String keyword) {
        this.keyword = keyword;
    }
    private String keyword;
    private TokenValue value;

}
