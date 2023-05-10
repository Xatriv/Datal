package org.example.error;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ErrorManager {
    @Getter
    private final List<CodeErrorInfo> errors;

    private final int maxErrors;


    public void reportError(CodeErrorInfo err){
        errors.add(err);
        if (err.getSeverity() == Severity.ERROR){
            throw new CodeError(err);
        }
        if (0 <= maxErrors && maxErrors < errors.size()){
            throw new MaxErrorsExceededError(maxErrors);
        }
    }
    @SuppressWarnings("SameParameterValue")
    private static int readProperty(Properties props, String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }



    public ErrorManager(){
        Properties props = new Properties();
        int maxErrorsProp = -1;
        try {
            InputStream input = new FileInputStream("src/main/java/org/example/error/error.properties");
            props.load(input);
            maxErrorsProp = readProperty(props, "MAX_ERRORS", -1);
        } catch (IOException ignored) {
        } finally {
            this.maxErrors = maxErrorsProp;
        }

        errors = new ArrayList<>();
    }

    public void printErrors(){
        for (var err : getErrors()) {
            System.out.printf("%s %s %s %s\n",
                    err.getErrorStagePrefix(),
                    err.getPosition().toString(),
                    err.getSeverity().toString(),
                    err.getMessage());
        }
    }

}
