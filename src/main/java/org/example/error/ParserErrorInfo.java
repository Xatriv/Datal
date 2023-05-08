package org.example.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;

@AllArgsConstructor
public class ParserErrorInfo implements CodeErrorInfo {
    @Override
    public String getErrorStagePrefix() {
        return "SYNTAX ERROR";
    }
    @Getter
    private final Severity severity;
    @Getter
    private final Position position;
    @Getter
    private final String message;
}
