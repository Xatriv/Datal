package org.example.interpreter;

import org.example.error.ErrorManager;
import org.example.error.InterpreterErrorInfo;
import org.example.error.Severity;
import org.example.program.*;
import org.example.source.Position;

import java.util.List;

public class Interpreter implements ProgramVisitor {

    private final ErrorManager errorManager;

    private InterpreterVisitationResult lastResult;

    public Interpreter(ErrorManager errorManager, Program program){
        this.errorManager = errorManager;
        program.accept(this);
    }

    private void verifyInstance(Object object, Class<?> type, Position position){ //TODO maybe Object instead of expression?
        if (!(type.isInstance(object))) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            position,
                            String.format("Type error, expected: %s; received: %s", type, object.getClass())));
        }
    }

    @Override
    public void visit(Program program) {
        Position dummyPos = new Position(0, 0);
        Block dummyBlock = new Block(List.of(), dummyPos);
        FunctionDef printFunction = new FunctionDef("print", List.of(), dummyBlock, dummyPos);
        FunctionDef readFunction = new FunctionDef("read", List.of(), dummyBlock, dummyPos);
        FunctionDef duplicatedFunctionDef;
        if ((duplicatedFunctionDef = program.addFunctionIfAbsent(printFunction.getName(), printFunction)) != null){
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            duplicatedFunctionDef.getPosition(),
                            String.format("Non-unique function identifier (%s)", printFunction.getName())));
        }
        if ((duplicatedFunctionDef = program.addFunctionIfAbsent(readFunction.getName(), readFunction)) != null){
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            duplicatedFunctionDef.getPosition(),
                            String.format("Non-unique function identifier (%s)", readFunction.getName())));
        }
        FunctionDef mainFunction = program.getFunctions().get("main");
        for (var stmt: mainFunction.getBody().getStatements()) {
            stmt.accept(this);
        }
//        mainFunction.accept(this);
//        for (var fun: program.getFunctions().values()) {
//            fun.accept(this);
//        }
    }

    @Override
    public void visit(FunctionDef functionDef) {
        functionDef.getBody().accept(this);
    }

    @Override
    public void visit(Block block) {
        for (var stmt: block.getStatements()) {
            stmt.accept(this);
        }
    }

    @Override
    public void visit(ExpressionStatement statement) {
        statement.getExpression().accept(this);
    }

    @Override
    public void visit(IfStatement statement) {
//        statement.getCondition().accept(this); //TODO wrap in if
//        statement.getIfBlock().accept(this);
//        statement.getElseBlock().accept(this);
    }

    @Override
    public void visit(WhileStatement statement) {
        statement.getCondition().accept(this);
        verifyInstance(lastResult.getValue(), Boolean.class, statement.getCondition().getPosition());
        while ( (Boolean) lastResult.getValue()){
            //TODO check if it doesn't need more condition isBool check
            statement.getLoopBlock().accept(this);
            statement.getCondition().accept(this);
            verifyInstance(lastResult.getValue(), Boolean.class, statement.getCondition().getPosition());
        }
    }

    @Override
    public void visit(ReturnStatement statement) {

    }

    @Override
    public void visit(OrExpression expression) {
        expression.getLeftExpression().accept(this);
        verifyInstance(lastResult, Boolean.class, expression.getPosition());
        if ((Boolean) lastResult.getValue()){
            lastResult = new InterpreterVisitationResult(true);
            return;
        }
        expression.getRightExpression().accept(this);
        verifyInstance(lastResult.getValue(), Boolean.class, expression.getPosition());
        lastResult = new InterpreterVisitationResult(lastResult.getValue());
    }

    @Override
    public void visit(AndExpression expression) {
        expression.getLeftExpression().accept(this);
        verifyInstance(lastResult, Boolean.class, expression.getPosition());
        if (! (Boolean) lastResult.getValue()){
            lastResult = new InterpreterVisitationResult(false);
            return;
        }
        expression.getRightExpression().accept(this);
        verifyInstance(lastResult.getValue(), Boolean.class, expression.getPosition());
        lastResult = new InterpreterVisitationResult(lastResult.getValue());
    }

    @Override
    public void visit(ComparativeExpression expression) {
        expression.getLeftExpression().accept(this);
        var left = lastResult;
        expression.getRightExpression().accept(this);
        var right = lastResult;
        switch (expression.getOperator()){
            case EQUALS:

            case NOT_EQUAL:
                System.out.println("TODO equals"); //TODO
            case LESS_THAN:
            case MORE_THAN:
            case MORE_OR_EQUAL_THAN:
            case LESS_OR_EQUAL_THAN:

        }

                //TODO
        expression.accept(this);
    }

    @Override
    public void visit(AdditiveExpression expression) {
        //TODO
        expression.getLeftExpression().accept(this);
        var left = lastResult.getValue();
        expression.getRightExpression().accept(this);
        var right = lastResult.getValue();
        Object result;
        if (expression.getOperator() == AdditiveOperator.PLUS){
            result = OperationHandler.add(left, right);
        } else {
            result = OperationHandler.subtract(left, right);
        }
        if (result == null){
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            expression.getPosition(),
                            String.format("Incompatible addition operands: %s + %s",
                                    left.getClass(), right.getClass())));
        }
        lastResult = new InterpreterVisitationResult(result);
    }

    @Override
    public void visit(MultiplicativeExpression expression) {
        //TODO
        expression.getLeftExpression().accept(this);
        var left = lastResult.getValue();
        expression.getRightExpression().accept(this);
        var right = lastResult.getValue();
        Object result;
        if (expression.getOperator() == MultiplicativeOperator.MULTIPLY){
            result = OperationHandler.multiply(left, right);
        } else {
            try{
                result = OperationHandler.divide(left, right);
            } catch (ArithmeticException e){
                result = null;
                new InterpreterErrorInfo(
                        Severity.ERROR,
                        expression.getPosition(),
                        String.format("Incompatible operands in additive expression: %s + %s",
                                left.getClass(), right.getClass()));
            }
        }
        if (result == null){
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            expression.getPosition(),
                            String.format("Incompatible operands in multiplicative expression: %s + %s",
                                    left.getClass(), right.getClass())));
        }
        lastResult = new InterpreterVisitationResult(result);
    }

    @Override
    public void visit(NegationExpression expression) {
        //TODO
        expression.accept(this);
    }

    @Override
    public void visit(AssignmentExpression expression) {
        //TODO assign right to identifier from left
        expression.getLeft().accept(this);
        var left = lastResult;
        expression.getRight().accept(this);
        var right = lastResult;
            if (! (left.getValue() instanceof IdentifierExpression)) {
                errorManager.reportError(
                        new InterpreterErrorInfo(
                                Severity.ERROR,
                                expression.getLeft().getPosition(),
                                String.format("Attempted to assign a value to: %s", left.getClass())));
            }
    }

    @Override
    public void visit(MemberExpression expression) {
        //TODO
        expression.accept(this);
    }

    @Override
    public void visit(IntLiteralExpression expression) {
        lastResult = new InterpreterVisitationResult(expression.getValue());
    }

    @Override
    public void visit(DoubleLiteralExpression expression) {
        lastResult = new InterpreterVisitationResult(expression.getValue());
    }

    @Override
    public void visit(StringLiteralExpression expression) {
        lastResult = new InterpreterVisitationResult(expression.getValue());
    }

    @Override
    public void visit(DateLiteralExpression expression) {
        lastResult = new InterpreterVisitationResult(expression.getValue());
    }

    @Override
    public void visit(PeriodLiteralExpression expression) {
        lastResult = new InterpreterVisitationResult(expression.getValue());
    }

    @Override
    public void visit(IdentifierExpression expression) {

    }

    @Override
    public void visit(FunctionCallExpression expression) {
        if (expression.getName().equals("print")) {
            for (var arg : expression.getArguments()) {
                //TODO do something to check if it can be printed and then do it
                arg.accept(this);
                System.out.println(lastResult.getValue());
//                if (value.getClass() ==  )
            }
        } else if (expression.getName().equals("read")) {
            //TODO read should accept some type as a parameter so it knows what type the input is

        } else {
            //TODO look up in context
        }
    }
}
