package org.example.error;

public class CodeError extends Error{
    public CodeError(CodeErrorInfo errorInfo){
        super(String.format("Fatal error: %s", errorInfo.getMessage()));
    }
}
