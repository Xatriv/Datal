package org.example.lexer;

public class TokenValue {

    private final Object value;
    TokenValue(int value){
        this.value = value;
    }
    TokenValue(float value){
        this.value = value;
    }
    TokenValue(String value){
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }
}
