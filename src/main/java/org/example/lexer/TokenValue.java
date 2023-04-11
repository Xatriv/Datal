package org.example.lexer;

public class TokenValue<T> {

    public TokenValue(T value) {
        this.value = value;
    }

    private final T value;

    public T getValue() {
        return this.value;
    }
}
