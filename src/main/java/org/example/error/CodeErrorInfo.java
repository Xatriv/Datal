package org.example.error;

import org.example.source.Position;

public interface CodeErrorInfo {
    String getErrorStagePrefix();
    Severity getSeverity();
    String getMessage();
    Position getPosition();
}
