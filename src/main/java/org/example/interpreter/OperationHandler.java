package org.example.interpreter;

public class OperationHandler {
    public static Object add(Object left, Object right){
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() + ((Number) right).doubleValue();
        }
        return null;
    }

    public static Object subtract(Object left, Object right){
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() - ((Number) right).doubleValue();
        }
        return null;
    }

    public static Object multiply(Object left, Object right) {
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() * ((Number) right).doubleValue();
        }
        return null;
    }

    public static Object divide(Object left, Object right) throws ArithmeticException{
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() / ((Number) right).doubleValue();
        }
        return null;
    }
}
