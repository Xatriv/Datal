package org.example.interpreter;

import lombok.AllArgsConstructor;
import org.example.error.ErrorManager;
import org.example.error.ParserErrorInfo;
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

    @Override
    public void visit(Program program) {
        Position dummyPos = new Position(0, 0);
        Block dummyBlock = new Block(List.of(), dummyPos);
        FunctionDef printFunction = new FunctionDef("print", List.of(), dummyBlock, dummyPos);
        FunctionDef readFunction = new FunctionDef("read", List.of(), dummyBlock, dummyPos);
        FunctionDef duplicatedFunctionDef;
        if ((duplicatedFunctionDef = program.addFunctionIfAbsent(printFunction.getName(), printFunction)) != null){
            errorManager.reportError(
                    new ParserErrorInfo(
                            Severity.ERROR,
                            duplicatedFunctionDef.getPosition(),
                            String.format("Non-unique function identifier (%s)", printFunction.getName())));
        }
        if ((duplicatedFunctionDef = program.addFunctionIfAbsent(readFunction.getName(), readFunction)) != null){
            errorManager.reportError(
                    new ParserErrorInfo(
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
        if (!(lastResult.getValue() instanceof Boolean)){
            errorManager.reportError(
                    new ParserErrorInfo(
                            Severity.ERROR,
                            statement.getPosition(),
                            "Expression condition does not evaluate to boolean"));
        }
        while ( (Boolean) lastResult.getValue()){
            //TODO check if it doesn't need more condition isBool check
            statement.getLoopBlock().accept(this);
            statement.getCondition().accept(this);
        }
    }

    @Override
    public void visit(ReturnStatement statement) {

    }

    @Override
    public void visit(OrExpression expression) {
        //TODO
        expression.accept(this);
    }

    @Override
    public void visit(AndExpression expression) {
        //TODO
        expression.accept(this);
    }

    @Override
    public void visit(ComparativeExpression expression) {
        //TODO
        expression.accept(this);
    }

    @Override
    public void visit(AdditiveExpression expression) {
        //TODO
        expression.accept(this);
//        expression.getLeftExpression().accept(this);
//        var xd = expression.getOperator().name();
//        expression.getRightExpression().accept(this);
    }

    @Override
    public void visit(MultiplicativeExpression expression) {
        //TODO
        expression.accept(this);
    }

    @Override
    public void visit(NegationExpression expression) {
        //TODO
        expression.accept(this);
    }

    @Override
    public void visit(AssignmentExpression expression) {
        //TODO
        expression.accept(this);
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
            //TODO read should accept some type as a parameter o it knows what type the input is

        } else {
            //TODO look up in context
        }
    }
}
