package org.example.interpreter;

import org.example.types.Date;
import org.example.types.Period;

public class OperationHandler {
    public static Object add(Object left, Object right){
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left + (Integer) right;
        }
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() + ((Number) right).doubleValue();
        }
        if (left instanceof Date && right instanceof Period) {
            return ((Date) left).add((Period) right);
        }
        return null;
    }

    public static Object subtract(Object left, Object right){
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left - (Integer) right;
        }
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() - ((Number) right).doubleValue();
        }
        if (left instanceof Date && right instanceof Date) {
            return ((Date) left).subtract((Date) right);
        }
        return null;
    }

    public static Object multiply(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left * (Integer) right;
        }
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() * ((Number) right).doubleValue();
        }
        return null;
    }

    public static Object divide(Object left, Object right) throws ArithmeticException{
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left / (Integer) right;
        }
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() / ((Number) right).doubleValue();
        }
        return null;
    }

    public static Boolean equals(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return left.equals(right);
        }
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() == ((Number) right).doubleValue();
        }
        if (left instanceof Period && right instanceof Period) {
            return Period.compare((Period) left, (Period) right) == 0;
        }
        if (left instanceof Date && right instanceof Date) {
            return Date.compare((Date) left, (Date) right) == 0;
        }
        return null;
    }

    public static Boolean notEqual(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return ! left.equals(right);
        }
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() != ((Number) right).doubleValue();
        }
        if (left instanceof Period && right instanceof Period) {
            return Period.compare((Period) left, (Period) right) == 0;
        }
        if (left instanceof Date && right instanceof Date) {
            return Date.compare((Date) left, (Date) right) == 0;
        }
        return null;
    }

    public static Boolean lessThan(Object left, Object right) {
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() < ((Number) right).doubleValue();
        }
        if (left instanceof Date && right instanceof Date){
            return ((Date) left).secondsSinceNewEra() < ((Date) right).secondsSinceNewEra();
        }
        return null;
    }

    public static Boolean moreThan(Object left, Object right) {
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() > ((Number) right).doubleValue();
        }
        if (left instanceof Date && right instanceof Date){
            return ((Date) left).secondsSinceNewEra() > ((Date) right).secondsSinceNewEra();
        }
        return null;
    }

    public static Boolean lessOrEqualThan(Object left, Object right) {
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() <= ((Number) right).doubleValue();
        }
        if (left instanceof Date && right instanceof Date){
            return ((Date) left).secondsSinceNewEra() <= ((Date) right).secondsSinceNewEra();
        }
        return null;
    }

    public static Boolean moreOrEqualThan(Object left, Object right) {
        if (left instanceof Number && right instanceof Number) {
            return ((Number) left).doubleValue() >= ((Number) right).doubleValue();
        }
        if (left instanceof Date && right instanceof Date){
            return ((Date) left).secondsSinceNewEra() >= ((Date) right).secondsSinceNewEra();
        }
        return null;
    }
}
