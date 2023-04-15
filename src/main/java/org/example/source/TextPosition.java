package org.example.source;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class TextPosition {
    private int line;
    private int column;
    @Override
    public String toString() {
        return String.format("Line: %d, Column: %d", line, column);
    }
}
