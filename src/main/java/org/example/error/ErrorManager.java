package org.example.error;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ErrorManager {
    @Getter
    private final List<CodeErrorInfo> errors;

    private final int maxErrors;

    @SuppressWarnings("FieldCanBeLocal")
    private final List<String> configPaths = Arrays.asList(
            "src/main/java/org/example/error/error.properties",
            "src/main/java/org/example/error.properties",
            "src/main/error.properties");


    public void reportError(CodeErrorInfo err){
        errors.add(err);
        if (err.getSeverity() == Severity.ERROR){
            printErrors(Severity.INFO);
            throw new CodeError(err);
        }
        if (0 <= maxErrors && maxErrors < errors.size()){
            printErrors(Severity.INFO);
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
            Optional<String> maybePath = configPaths.stream()
                    .filter(path -> Files.exists(Paths.get(path)))
                    .findFirst();
            if (maybePath.isEmpty()){
                throw new IOException("Missing error manager config path");
            }
            InputStream input = new FileInputStream(maybePath.get());
            props.load(input);
            maxErrorsProp = readProperty(props, "MAX_ERRORS", -1);
        } catch (IOException ignored) {
        } finally {
            this.maxErrors = maxErrorsProp;
        }

        errors = new ArrayList<>();
    }

    public void printErrors(Severity minimumSeverity){
        System.out.println("\n");
        for (var err : getErrors().stream().filter(
                e-> e.getSeverity().ordinal() >= minimumSeverity.ordinal()).collect(Collectors.toList())) {
            System.out.printf("%s %s %s %s\n",
                    err.getErrorStagePrefix(),
                    err.getPosition().toString(),
                    err.getSeverity().toString(),
                    err.getMessage());
        }
    }

}
