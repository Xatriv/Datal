package org.example.error;

public class MaxErrorsExceededError extends Error {
    public MaxErrorsExceededError(int limit) {
        super(String.format("Maximum number of interpreter error exceeded (limit is %d)", limit));
    }
}
