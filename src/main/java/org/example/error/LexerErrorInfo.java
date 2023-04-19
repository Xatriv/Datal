package org.example.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.source.Position;

@AllArgsConstructor
public class LexerErrorInfo implements CodeErrorInfo {
    @Getter
    private final Severity severity;
    @Getter
    private final String message;
    @Getter
    private final Position position;
}
