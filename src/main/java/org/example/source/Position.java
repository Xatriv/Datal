package org.example.source;

import lombok.Getter;
import lombok.Setter;

public class Position {
    @Getter @Setter
    int line;
    @Getter @Setter
    int column;

    private final int startColumn;

    public void incrementColumn() {
        column++;
    }

    public void newLine() {
        line++;
        column = startColumn;
    }

    public Position(int line, int column){
        this.line = line;
        this.column = column;
        this.startColumn = column;
    }

    @Override
    public String toString() {
        return String.format("Line: %d; Column: %d", line, column);
    }

}
