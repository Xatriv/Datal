package org.example.lexer;

public enum SimpleToken implements Token {
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

    DOUBLE(new TokenValue<Double>(0.0)),
    INTEGER(new TokenValue<Integer>(0)),
    DATETIME(new TokenValue<String>("")), //TODO replace with DateTime type
    PERIOD(new TokenValue<String>("")), //TODO replace with DateTime type

    PLUS("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),

    STRING_LITERAL_DELIMITER_L("["),
    STRING_LITERAL_DELIMITER_R("]"),

    // Multiple conventions. First occurrence determines newline char sequence
    EOL,
    EOF;

    SimpleToken() {}
    SimpleToken(String keyword) {
        this.keyword = keyword;
    }
    SimpleToken(TokenValue<?> value) { this.value = value; }
    private String keyword;
    private TokenValue<?> value;

}
