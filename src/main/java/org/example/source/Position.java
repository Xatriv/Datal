package org.example.source;

import lombok.Getter;

public class Position {
    @Getter
    int line;
    @Getter
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

}
