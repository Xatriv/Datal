import org.example.error.ErrorManager;
import org.example.interpreter.Interpreter;
import org.example.program.*;
import org.example.source.Position;
import org.example.types.Date;
import org.example.types.Period;
import org.junit.jupiter.api.Test;

import java.util.Hashtable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTests {

    @Test
    public void IntLiteralExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        IntLiteralExpression expression = new IntLiteralExpression(10, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Integer.class);
        assertEquals((Integer) interpreter.getLastResult(pos).getValue(), 10);
    }

    @Test
    public void DoubleLiteralExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        DoubleLiteralExpression expression = new DoubleLiteralExpression(10.5, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Double.class);
        assertEquals((Double) interpreter.getLastResult(pos).getValue(), 10.5);
    }

    @Test
    public void StringLiteralExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        StringLiteralExpression expression = new StringLiteralExpression("MyString", pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), String.class);
        assertEquals(interpreter.getLastResult(pos).getValue(), "MyString");
    }

    @Test
    public void DateLiteralExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Date date = new Date(true, 1, 1, 1, 0, 0, 0);
        DateLiteralExpression expression = new DateLiteralExpression(date, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Date.class);
        assertTrue(date.equals((Date) interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void PeriodLiteralExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Period period = new Period(1, 1, 1, 0, 0, 0, 0L);
        PeriodLiteralExpression expression = new PeriodLiteralExpression(period, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Period.class);
        assertEquals(period, interpreter.getLastResult(pos).getValue());
    }

    @Test
    public void MultiplicativeMultiplyIntsExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        IntLiteralExpression expression1 = new IntLiteralExpression(15, pos);
        IntLiteralExpression expression2 = new IntLiteralExpression(2, pos);
        MultiplicativeExpression expression = new MultiplicativeExpression(MultiplicativeOperator.MULTIPLY, expression1, expression2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Integer.class);
        assertEquals(30, interpreter.getLastResult(pos).getValue());
    }

    @Test
    public void MultiplicativeDivideIntsExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        IntLiteralExpression expression1 = new IntLiteralExpression(16, pos);
        IntLiteralExpression expression2 = new IntLiteralExpression(2, pos);
        MultiplicativeExpression expression = new MultiplicativeExpression(MultiplicativeOperator.DIVIDE, expression1, expression2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Integer.class);
        assertEquals(8, interpreter.getLastResult(pos).getValue());
    }

    @Test
    public void MultiplicativeMultiplyIntDoubleExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        IntLiteralExpression expression1 = new IntLiteralExpression(4, pos);
        DoubleLiteralExpression expression2 = new DoubleLiteralExpression(2.5, pos);
        MultiplicativeExpression expression = new MultiplicativeExpression(MultiplicativeOperator.MULTIPLY, expression1, expression2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Double.class);
        assertEquals(10.0, interpreter.getLastResult(pos).getValue());
    }

    @Test
    public void MultiplicativeDivideIntDoubleExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        IntLiteralExpression expression1 = new IntLiteralExpression(5, pos);
        DoubleLiteralExpression expression2 = new DoubleLiteralExpression(2.5, pos);
        MultiplicativeExpression expression = new MultiplicativeExpression(MultiplicativeOperator.DIVIDE, expression1, expression2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Double.class);
        assertEquals(2.0, interpreter.getLastResult(pos).getValue());
    }

    @Test
    public void MultiplicativeMultiplyPeriodIntExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Period period = new Period(1, 2, 3, 4, 5, 6, 10L);
        PeriodLiteralExpression expression1 = new PeriodLiteralExpression(period, pos);
        IntLiteralExpression expression2 = new IntLiteralExpression(2, pos);
        MultiplicativeExpression expression = new MultiplicativeExpression(MultiplicativeOperator.MULTIPLY, expression1, expression2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Period.class);
        assertEquals(2, ((Period) interpreter.getLastResult(pos).getValue()).getYear());
        assertEquals(4, ((Period) interpreter.getLastResult(pos).getValue()).getMonth());
        assertEquals(6, ((Period) interpreter.getLastResult(pos).getValue()).getDay());
        assertEquals(8, ((Period) interpreter.getLastResult(pos).getValue()).getHour());
        assertEquals(10, ((Period) interpreter.getLastResult(pos).getValue()).getMinute());
        assertEquals(12, ((Period) interpreter.getLastResult(pos).getValue()).getSecond());
        assertEquals(20L, ((Period) interpreter.getLastResult(pos).getValue()).getAbsolutePeriodDifference());
    }

    @Test
    public void MultiplicativeDividePeriodIntExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Period period = new Period(1, 2, 3, 4, 5, 6, 10L);
        PeriodLiteralExpression expression1 = new PeriodLiteralExpression(period, pos);
        IntLiteralExpression expression2 = new IntLiteralExpression(2, pos);
        MultiplicativeExpression expression = new MultiplicativeExpression(MultiplicativeOperator.DIVIDE, expression1, expression2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Period.class);
        assertEquals(0, ((Period) interpreter.getLastResult(pos).getValue()).getYear());
        assertEquals(1, ((Period) interpreter.getLastResult(pos).getValue()).getMonth());
        assertEquals(1, ((Period) interpreter.getLastResult(pos).getValue()).getDay());
        assertEquals(2, ((Period) interpreter.getLastResult(pos).getValue()).getHour());
        assertEquals(2, ((Period) interpreter.getLastResult(pos).getValue()).getMinute());
        assertEquals(3, ((Period) interpreter.getLastResult(pos).getValue()).getSecond());
        assertEquals(5L, ((Period) interpreter.getLastResult(pos).getValue()).getAbsolutePeriodDifference());
    }

    @Test
    public void AdditiveAddIntsExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        IntLiteralExpression expression1 = new IntLiteralExpression(15, pos);
        IntLiteralExpression expression2 = new IntLiteralExpression(2, pos);
        AdditiveExpression expression = new AdditiveExpression(AdditiveOperator.PLUS, expression1, expression2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Integer.class);
        assertEquals(17, interpreter.getLastResult(pos).getValue());
    }

    @Test
    public void AdditiveSubtractIntsExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        IntLiteralExpression expression1 = new IntLiteralExpression(16, pos);
        IntLiteralExpression expression2 = new IntLiteralExpression(2, pos);
        AdditiveExpression expression = new AdditiveExpression(AdditiveOperator.MINUS, expression1, expression2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Integer.class);
        assertEquals(14, interpreter.getLastResult(pos).getValue());
    }

    @Test
    public void AdditiveAddIntDoubleExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        IntLiteralExpression expression1 = new IntLiteralExpression(15, pos);
        DoubleLiteralExpression expression2 = new DoubleLiteralExpression(2.5, pos);
        AdditiveExpression expression = new AdditiveExpression(AdditiveOperator.PLUS, expression1, expression2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Double.class);
        assertEquals(17.5, interpreter.getLastResult(pos).getValue());
    }

    @Test
    public void AdditiveSubtractIntDoubleExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        IntLiteralExpression expression1 = new IntLiteralExpression(16, pos);
        DoubleLiteralExpression expression2 = new DoubleLiteralExpression(2.5, pos);
        AdditiveExpression expression = new AdditiveExpression(AdditiveOperator.MINUS, expression1, expression2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Double.class);
        assertEquals(13.5, interpreter.getLastResult(pos).getValue());
    }

    @Test
    public void AdditiveAddDatePeriodExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Date date = new Date(true, 1, 1, 1, 1, 1, 1);
        Period period = new Period(1, 2, 3, 4, 5, 6, 10L);
        DateLiteralExpression dateEx = new DateLiteralExpression(date, pos);
        PeriodLiteralExpression periodEx = new PeriodLiteralExpression(period, pos);
        AdditiveExpression expression = new AdditiveExpression(AdditiveOperator.PLUS, dateEx, periodEx, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Date.class);
        assertEquals(2, ((Date) interpreter.getLastResult(pos).getValue()).getYear());
        assertEquals(3, ((Date) interpreter.getLastResult(pos).getValue()).getMonth());
        assertEquals(4, ((Date) interpreter.getLastResult(pos).getValue()).getDay());
        assertEquals(5, ((Date) interpreter.getLastResult(pos).getValue()).getHour());
        assertEquals(6, ((Date) interpreter.getLastResult(pos).getValue()).getMinute());
        assertEquals(17, ((Date) interpreter.getLastResult(pos).getValue()).getSecond());
    }

    @Test
    public void AdditiveSubtractDatePeriodExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Date date = new Date(true, 10, 10, 10, 10, 10, 20);
        Period period = new Period(1, 2, 3, 4, 5, 6, 10L);
        DateLiteralExpression dateEx = new DateLiteralExpression(date, pos);
        PeriodLiteralExpression periodEx = new PeriodLiteralExpression(period, pos);
        AdditiveExpression expression = new AdditiveExpression(AdditiveOperator.MINUS, dateEx, periodEx, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Date.class);
        assertEquals(9, ((Date) interpreter.getLastResult(pos).getValue()).getYear());
        assertEquals(8, ((Date) interpreter.getLastResult(pos).getValue()).getMonth());
        assertEquals(7, ((Date) interpreter.getLastResult(pos).getValue()).getDay());
        assertEquals(6, ((Date) interpreter.getLastResult(pos).getValue()).getHour());
        assertEquals(5, ((Date) interpreter.getLastResult(pos).getValue()).getMinute());
        assertEquals(4, ((Date) interpreter.getLastResult(pos).getValue()).getSecond());
    }

    @Test
    public void AdditiveAddPeriodsExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Period period1 = new Period(1, 2, 3, 4, 5, 6, 10L);
        Period period2 = new Period(1, 2, 3, 4, 5, 6, 10L);
        PeriodLiteralExpression periodEx1 = new PeriodLiteralExpression(period1, pos);
        PeriodLiteralExpression periodEx2 = new PeriodLiteralExpression(period2, pos);
        AdditiveExpression expression = new AdditiveExpression(AdditiveOperator.PLUS, periodEx1, periodEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Period.class);
        assertEquals(2, ((Period) interpreter.getLastResult(pos).getValue()).getYear());
        assertEquals(4, ((Period) interpreter.getLastResult(pos).getValue()).getMonth());
        assertEquals(6, ((Period) interpreter.getLastResult(pos).getValue()).getDay());
        assertEquals(8, ((Period) interpreter.getLastResult(pos).getValue()).getHour());
        assertEquals(10, ((Period) interpreter.getLastResult(pos).getValue()).getMinute());
        assertEquals(12, ((Period) interpreter.getLastResult(pos).getValue()).getSecond());
        assertEquals(20, ((Period) interpreter.getLastResult(pos).getValue()).getAbsolutePeriodDifference());
    }

    @Test
    public void AdditiveSubtractPeriodsExpressionTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Period period1 = new Period(2, 3, 4, 5, 6, 7, 10L);
        Period period2 = new Period(1, 2, 3, 4, 5, 6, 10L);
        PeriodLiteralExpression periodEx1 = new PeriodLiteralExpression(period1, pos);
        PeriodLiteralExpression periodEx2 = new PeriodLiteralExpression(period2, pos);
        AdditiveExpression expression = new AdditiveExpression(AdditiveOperator.MINUS, periodEx1, periodEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Period.class);
        assertEquals(1, ((Period) interpreter.getLastResult(pos).getValue()).getYear());
        assertEquals(1, ((Period) interpreter.getLastResult(pos).getValue()).getMonth());
        assertEquals(1, ((Period) interpreter.getLastResult(pos).getValue()).getDay());
        assertEquals(1, ((Period) interpreter.getLastResult(pos).getValue()).getHour());
        assertEquals(1, ((Period) interpreter.getLastResult(pos).getValue()).getMinute());
        assertEquals(1, ((Period) interpreter.getLastResult(pos).getValue()).getSecond());
    }

    @Test
    public void comparativeEqualsIntsTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Integer int1 = 1;
        Integer int2 = 1;
        IntLiteralExpression intEx1 = new IntLiteralExpression(int1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.EQUALS, intEx1, intEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeEqualsDoublesTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Double double1 = 1.5;
        Double double2 = 1.5;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        DoubleLiteralExpression doubleEx2 = new DoubleLiteralExpression(double2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.EQUALS, doubleEx1, doubleEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeEqualsDoubleIntTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Double double1 = 1.0;
        Integer int2 = 1;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.EQUALS, doubleEx1, intEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeEqualsStringTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        String str1 = "MyString";
        String str2 = "MyString";
        StringLiteralExpression strEx1 = new StringLiteralExpression(str1, pos);
        StringLiteralExpression strEx2 = new StringLiteralExpression(str2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.EQUALS, strEx1, strEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeEqualsDateTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Date date1 = new Date(true, 1, 1, 1, 0, 0, 0);
        Date date2 = new Date(true, 1, 1, 1, 0, 0, 0);
        DateLiteralExpression strEx1 = new DateLiteralExpression(date1, pos);
        DateLiteralExpression strEx2 = new DateLiteralExpression(date2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.EQUALS, strEx1, strEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeEqualsPeriodTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Period date1 = new Period(1, 1, 1, 0, 0, 0, 0L);
        Period date2 = new Period(1, 1, 1, 0, 0, 0, 0L);
        PeriodLiteralExpression strEx1 = new PeriodLiteralExpression(date1, pos);
        PeriodLiteralExpression strEx2 = new PeriodLiteralExpression(date2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.EQUALS, strEx1, strEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeNotEqualsIntsTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Integer int1 = 2;
        Integer int2 = 1;
        IntLiteralExpression intEx1 = new IntLiteralExpression(int1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.NOT_EQUAL, intEx1, intEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeNotEqualsDoublesTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Double double1 = 2.5;
        Double double2 = 1.5;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        DoubleLiteralExpression doubleEx2 = new DoubleLiteralExpression(double2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.NOT_EQUAL, doubleEx1, doubleEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeNotEqualsDoubleIntTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Double double1 = 2.0;
        Integer int2 = 1;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.NOT_EQUAL, doubleEx1, intEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeNotEqualsStringTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        String str1 = "MyString";
        String str2 = "YourString";
        StringLiteralExpression strEx1 = new StringLiteralExpression(str1, pos);
        StringLiteralExpression strEx2 = new StringLiteralExpression(str2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.NOT_EQUAL, strEx1, strEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeNotEqualsDateTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Date date1 = new Date(true, 69, 1, 1, 0, 0, 0);
        Date date2 = new Date(true, 1, 1, 1, 0, 0, 0);
        DateLiteralExpression strEx1 = new DateLiteralExpression(date1, pos);
        DateLiteralExpression strEx2 = new DateLiteralExpression(date2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.NOT_EQUAL, strEx1, strEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeNotEqualsPeriodJustExplicitTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Period date1 = new Period(69, 1, 1, 0, 0, 0, 0L);
        Period date2 = new Period(1, 1, 1, 0, 0, 0, 0L);
        PeriodLiteralExpression strEx1 = new PeriodLiteralExpression(date1, pos);
        PeriodLiteralExpression strEx2 = new PeriodLiteralExpression(date2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.NOT_EQUAL, strEx1, strEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(false, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeNotEqualsPeriodImplicitTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Period date1 = new Period(1, 1, 1, 0, 0, 0, 10L);
        Period date2 = new Period(1, 1, 1, 0, 0, 0, 0L);
        PeriodLiteralExpression strEx1 = new PeriodLiteralExpression(date1, pos);
        PeriodLiteralExpression strEx2 = new PeriodLiteralExpression(date2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.NOT_EQUAL, strEx1, strEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeLessThanIntsTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Integer int1 = 1;
        Integer int2 = 2;
        IntLiteralExpression intEx1 = new IntLiteralExpression(int1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.LESS_THAN, intEx1, intEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeLessThanDoublesTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Double double1 = 1.5;
        Double double2 = 2.5;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        DoubleLiteralExpression doubleEx2 = new DoubleLiteralExpression(double2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.LESS_THAN, doubleEx1, doubleEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeLessThanDoubleIntTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Double double1 = 1.0;
        Integer int2 = 2;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.LESS_THAN, doubleEx1, intEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeLessThanDateTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Date date1 = new Date(true, 1, 1, 1, 0, 0, 0);
        Date date2 = new Date(true, 1, 2, 1, 0, 0, 0);
        DateLiteralExpression dateEx1 = new DateLiteralExpression(date1, pos);
        DateLiteralExpression dateEx2 = new DateLiteralExpression(date2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.LESS_THAN, dateEx1, dateEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeLessThanPeriodTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Period date1 = new Period(1, 1, 1, 0, 0, 0, 0L);
        Period date2 = new Period(1, 1, 1, 0, 0, 0, 10L);
        PeriodLiteralExpression periodEx1 = new PeriodLiteralExpression(date1, pos);
        PeriodLiteralExpression periodEx2 = new PeriodLiteralExpression(date2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.LESS_THAN, periodEx1, periodEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeLessOrEqualThanIntsTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Integer int1 = 2;
        Integer int2 = 3;
        Integer int3 = 2;
        IntLiteralExpression intEx1 = new IntLiteralExpression(int1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        IntLiteralExpression intEx3 = new IntLiteralExpression(int3, pos);
        ComparativeExpression expression1 = new ComparativeExpression(ComparisonOperator.LESS_OR_EQUAL_THAN, intEx1, intEx2, pos);
        ComparativeExpression expression2 = new ComparativeExpression(ComparisonOperator.LESS_OR_EQUAL_THAN, intEx1, intEx3, pos);
        interpreter.visit(expression1);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));

        interpreter.visit(expression2);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeLessOrEqualThanDoublesTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Double double1 = 3.5;
        Double double2 = 4.5;
        Double double3 = 3.5;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        DoubleLiteralExpression doubleEx2 = new DoubleLiteralExpression(double2, pos);
        DoubleLiteralExpression doubleEx3 = new DoubleLiteralExpression(double3, pos);
        ComparativeExpression expression1 = new ComparativeExpression(ComparisonOperator.LESS_OR_EQUAL_THAN, doubleEx1, doubleEx2, pos);
        ComparativeExpression expression2 = new ComparativeExpression(ComparisonOperator.LESS_OR_EQUAL_THAN, doubleEx1, doubleEx3, pos);
        interpreter.visit(expression1);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));

        interpreter.visit(expression2);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeLessOrEqualThanDoubleIntTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Double double1 = 3.0;
        Integer int2 = 4;
        Integer int3 = 3;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        IntLiteralExpression intEx3 = new IntLiteralExpression(int3, pos);
        ComparativeExpression expression1 = new ComparativeExpression(ComparisonOperator.LESS_OR_EQUAL_THAN, doubleEx1, intEx2, pos);
        ComparativeExpression expression2 = new ComparativeExpression(ComparisonOperator.LESS_OR_EQUAL_THAN, doubleEx1, intEx3, pos);
        interpreter.visit(expression1);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));

        interpreter.visit(expression2);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeLessOrEqualThanDateTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Date date1 = new Date(true, 1, 3, 1, 0, 0, 0);
        Date date2 = new Date(true, 1, 4, 1, 0, 0, 0);
        Date date3 = new Date(true, 1, 3, 1, 0, 0, 0);
        DateLiteralExpression dateEx1 = new DateLiteralExpression(date1, pos);
        DateLiteralExpression dateEx2 = new DateLiteralExpression(date2, pos);
        DateLiteralExpression dateEx3 = new DateLiteralExpression(date3, pos);
        ComparativeExpression expression1 = new ComparativeExpression(ComparisonOperator.LESS_OR_EQUAL_THAN, dateEx1, dateEx2, pos);
        ComparativeExpression expression2 = new ComparativeExpression(ComparisonOperator.LESS_OR_EQUAL_THAN, dateEx1, dateEx3, pos);
        interpreter.visit(expression1);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));

        interpreter.visit(expression2);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeLessOrEqualThanPeriodTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Period period1 = new Period(1, 1, 1, 0, 0, 0, 20L);
        Period period2 = new Period(1, 1, 1, 0, 0, 0, 30L);
        Period period3 = new Period(1, 1, 1, 0, 0, 0, 20L);
        PeriodLiteralExpression periodEx1 = new PeriodLiteralExpression(period1, pos);
        PeriodLiteralExpression periodEx2 = new PeriodLiteralExpression(period2, pos);
        PeriodLiteralExpression periodEx3 = new PeriodLiteralExpression(period3, pos);
        ComparativeExpression expression1 = new ComparativeExpression(ComparisonOperator.LESS_OR_EQUAL_THAN, periodEx1, periodEx2, pos);
        ComparativeExpression expression2 = new ComparativeExpression(ComparisonOperator.LESS_OR_EQUAL_THAN, periodEx1, periodEx3, pos);
        interpreter.visit(expression1);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));

        interpreter.visit(expression2);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeMoreThanIntsTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Integer int1 = 2;
        Integer int2 = 1;
        IntLiteralExpression intEx1 = new IntLiteralExpression(int1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.MORE_THAN, intEx1, intEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeMoreThanDoublesTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Double double1 = 3.5;
        Double double2 = 2.5;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        DoubleLiteralExpression doubleEx2 = new DoubleLiteralExpression(double2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.MORE_THAN, doubleEx1, doubleEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeMoreThanDoubleIntTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Double double1 = 3.0;
        Integer int2 = 2;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.MORE_THAN, doubleEx1, intEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeMoreThanDateTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Date date1 = new Date(true, 1, 3, 1, 0, 0, 0);
        Date date2 = new Date(true, 1, 2, 1, 0, 0, 0);
        DateLiteralExpression dateEx1 = new DateLiteralExpression(date1, pos);
        DateLiteralExpression dateEx2 = new DateLiteralExpression(date2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.MORE_THAN, dateEx1, dateEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeMoreThanPeriodTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Period date1 = new Period(1, 1, 1, 0, 0, 0, 20L);
        Period date2 = new Period(1, 1, 1, 0, 0, 0, 10L);
        PeriodLiteralExpression periodEx1 = new PeriodLiteralExpression(date1, pos);
        PeriodLiteralExpression periodEx2 = new PeriodLiteralExpression(date2, pos);
        ComparativeExpression expression = new ComparativeExpression(ComparisonOperator.MORE_THAN, periodEx1, periodEx2, pos);
        interpreter.visit(expression);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeMoreOrEqualThanIntsTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Integer int1 = 2;
        Integer int2 = 1;
        Integer int3 = 2;
        IntLiteralExpression intEx1 = new IntLiteralExpression(int1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        IntLiteralExpression intEx3 = new IntLiteralExpression(int3, pos);
        ComparativeExpression expression1 = new ComparativeExpression(ComparisonOperator.MORE_OR_EQUAL_THAN, intEx1, intEx2, pos);
        ComparativeExpression expression2 = new ComparativeExpression(ComparisonOperator.MORE_OR_EQUAL_THAN, intEx1, intEx3, pos);
        interpreter.visit(expression1);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));

        interpreter.visit(expression2);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeMoreOrEqualThanDoublesTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Double double1 = 3.5;
        Double double2 = 2.5;
        Double double3 = 3.5;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        DoubleLiteralExpression doubleEx2 = new DoubleLiteralExpression(double2, pos);
        DoubleLiteralExpression doubleEx3 = new DoubleLiteralExpression(double3, pos);
        ComparativeExpression expression1 = new ComparativeExpression(ComparisonOperator.MORE_OR_EQUAL_THAN, doubleEx1, doubleEx2, pos);
        ComparativeExpression expression2 = new ComparativeExpression(ComparisonOperator.MORE_OR_EQUAL_THAN, doubleEx1, doubleEx3, pos);
        interpreter.visit(expression1);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));

        interpreter.visit(expression2);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeMoreOrEqualThanDoubleIntTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Double double1 = 3.0;
        Integer int2 = 2;
        Integer int3 = 3;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        IntLiteralExpression intEx3 = new IntLiteralExpression(int3, pos);
        ComparativeExpression expression1 = new ComparativeExpression(ComparisonOperator.MORE_OR_EQUAL_THAN, doubleEx1, intEx2, pos);
        ComparativeExpression expression2 = new ComparativeExpression(ComparisonOperator.MORE_OR_EQUAL_THAN, doubleEx1, intEx3, pos);
        interpreter.visit(expression1);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));

        interpreter.visit(expression2);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeMoreOrEqualThanDateTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Date date1 = new Date(true, 1, 3, 1, 0, 0, 0);
        Date date2 = new Date(true, 1, 2, 1, 0, 0, 0);
        Date date3 = new Date(true, 1, 3, 1, 0, 0, 0);
        DateLiteralExpression dateEx1 = new DateLiteralExpression(date1, pos);
        DateLiteralExpression dateEx2 = new DateLiteralExpression(date2, pos);
        DateLiteralExpression dateEx3 = new DateLiteralExpression(date3, pos);
        ComparativeExpression expression1 = new ComparativeExpression(ComparisonOperator.MORE_OR_EQUAL_THAN, dateEx1, dateEx2, pos);
        ComparativeExpression expression2 = new ComparativeExpression(ComparisonOperator.MORE_OR_EQUAL_THAN, dateEx1, dateEx3, pos);
        interpreter.visit(expression1);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));

        interpreter.visit(expression2);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void comparativeMoreOrEqualThanPeriodTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Period period1 = new Period(1, 1, 1, 0, 0, 0, 20L);
        Period period2 = new Period(1, 1, 1, 0, 0, 0, 10L);
        Period period3 = new Period(1, 1, 1, 0, 0, 0, 20L);
        PeriodLiteralExpression periodEx1 = new PeriodLiteralExpression(period1, pos);
        PeriodLiteralExpression periodEx2 = new PeriodLiteralExpression(period2, pos);
        PeriodLiteralExpression periodEx3 = new PeriodLiteralExpression(period3, pos);
        ComparativeExpression expression1 = new ComparativeExpression(ComparisonOperator.MORE_OR_EQUAL_THAN, periodEx1, periodEx2, pos);
        ComparativeExpression expression2 = new ComparativeExpression(ComparisonOperator.MORE_OR_EQUAL_THAN, periodEx1, periodEx3, pos);
        interpreter.visit(expression1);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));

        interpreter.visit(expression2);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void andTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Integer int1 = 3;
        Integer int2 = 3;
        IntLiteralExpression intEx1 = new IntLiteralExpression(int1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        ComparativeExpression compEx1 = new ComparativeExpression(ComparisonOperator.EQUALS, intEx1, intEx2, pos);
        Double double1 = 3.0;
        Double double2 = 3.0;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        DoubleLiteralExpression doubleEx2 = new DoubleLiteralExpression(double2, pos);
        ComparativeExpression compEx2 = new ComparativeExpression(ComparisonOperator.EQUALS, doubleEx1, doubleEx2, pos);
        AndExpression andEx = new AndExpression(compEx1, compEx2, pos);
        interpreter.visit(andEx);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void orTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Integer int1 = 3;
        Integer int2 = 3;
        IntLiteralExpression intEx1 = new IntLiteralExpression(int1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        ComparativeExpression compEx1 = new ComparativeExpression(ComparisonOperator.EQUALS, intEx1, intEx2, pos);
        Double double1 = 3.0;
        Double double2 = 300.0;
        DoubleLiteralExpression doubleEx1 = new DoubleLiteralExpression(double1, pos);
        DoubleLiteralExpression doubleEx2 = new DoubleLiteralExpression(double2, pos);
        ComparativeExpression compEx2 = new ComparativeExpression(ComparisonOperator.EQUALS, doubleEx1, doubleEx2, pos);
        OrExpression andEx = new OrExpression(compEx1, compEx2, pos);
        interpreter.visit(andEx);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(true, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void notLogicalTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Integer int1 = 3;
        Integer int2 = 3;
        IntLiteralExpression intEx1 = new IntLiteralExpression(int1, pos);
        IntLiteralExpression intEx2 = new IntLiteralExpression(int2, pos);
        ComparativeExpression compEx1 = new ComparativeExpression(ComparisonOperator.EQUALS, intEx1, intEx2, pos);
        NegationExpression negEx = new NegationExpression(NegationOperator.NOT, compEx1, pos);
        interpreter.visit(negEx);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Boolean.class);
        assertEquals(false, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void notArithmeticalTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Integer int1 = 3;
        IntLiteralExpression intEx1 = new IntLiteralExpression(int1, pos);
        NegationExpression negEx = new NegationExpression(NegationOperator.MINUS, intEx1, pos);
        interpreter.visit(negEx);
        assertEquals(interpreter.getLastResult(pos).getValue().getClass(), Integer.class);
        assertEquals(-3, (interpreter.getLastResult(pos).getValue()));
    }

    @Test
    public void assignmentTest() {
        ErrorManager eM = new ErrorManager();
        Interpreter interpreter = new Interpreter(eM);
        Position pos = new Position(0, 0);
        Integer int1 = 3;
        IntLiteralExpression intEx1 = new IntLiteralExpression(int1, pos);
        IdentifierExpression ident = new IdentifierExpression("variable", pos);
        AssignmentExpression assEx = new AssignmentExpression(ident, intEx1, pos);
        ExpressionStatement exStmt = new ExpressionStatement(assEx, pos);
        FunctionCallExpression callEx = new FunctionCallExpression("func", List.of(), pos);
        ExpressionStatement callStmt = new ExpressionStatement(callEx, pos);
        Block block = new Block(List.of(exStmt) , pos);
        Block mainBlock = new Block(List.of(callStmt) , pos);
        UserFunctionDef func = new UserFunctionDef("func", List.of(), block, pos);
        UserFunctionDef main = new UserFunctionDef("main", List.of(), mainBlock, pos);
        Hashtable<String, FunctionDef> funcs = new Hashtable<>() {{ put("main", main); put("func", func); }};
        Program program = new Program(funcs);
        interpreter.visit(program);
    }


}
