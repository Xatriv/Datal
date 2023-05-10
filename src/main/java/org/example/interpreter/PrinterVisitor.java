package org.example.interpreter;

import org.example.program.*;

public class PrinterVisitor implements ProgramVisitor {
    int spaces = 0;
    @Override
    public void visit(Program program) {
        spaces = 0;
        for (var fun: program.getFunctions().values()) {
            fun.accept(this);
        }
    }

    @Override
    public void visit(FunctionDef functionDef) {
        print(String.format("FunctionDef: %s", functionDef.getName()));
        functionDef.getBody().accept(this);
    }

    @Override
    public void visit(Block block) {
        spaces++;
        for (var stmt: block.getStatements()) {
            stmt.accept(this);
        }
        spaces--;
    }

    @Override
    public void visit(ExpressionStatement statement) {
        spaces++;
        print("ExpressionStatement");
        statement.getExpression().accept(this);
        spaces--;
    }

    @Override
    public void visit(IfStatement statement) {
        spaces++;
        print("IF");
        statement.getCondition().accept(this);
        print("THEN");
        statement.getIfBlock().accept(this);
        print("ELSE");
        statement.getElseBlock().accept(this);
        spaces--;
    }

    @Override
    public void visit(WhileStatement statement) {
        spaces++;
        print("WHILE");
        statement.getCondition().accept(this);
        print("DO");
        statement.getLoopBlock().accept(this);
        spaces--;
    }

    @Override
    public void visit(ReturnStatement statement) {
        spaces++;
        print("ReturnStatement");
        statement.getExpression().accept(this);
        spaces--;
    }

    @Override
    public void visit(OrExpression expression) {
        spaces++;
        print(expression.getClass().getSimpleName());
        expression.getLeftExpression().accept(this);
        print("OR");
        expression.getRightExpression().accept(this);
        spaces--;
    }

    @Override
    public void visit(AndExpression expression) {
        spaces++;
        print(expression.getClass().getSimpleName());
        expression.getLeftExpression().accept(this);
        print("AND");
        expression.getRightExpression().accept(this);
        spaces--;
    }

    @Override
    public void visit(ComparativeExpression expression) {
        spaces++;
        print(expression.getClass().getSimpleName());
        expression.getLeftExpression().accept(this);
        printIndented(expression.getOperator().name());
        expression.getRightExpression().accept(this);
        spaces--;
    }

    @Override
    public void visit(AdditiveExpression expression) {
        spaces++;
        print(expression.getClass().getSimpleName());
        expression.getLeftExpression().accept(this);
        printIndented(expression.getOperator().name());
        expression.getRightExpression().accept(this);
        spaces--;
    }

    @Override
    public void visit(MultiplicativeExpression expression) {
        spaces++;
        print(expression.getClass().getSimpleName());
        expression.getLeftExpression().accept(this);
        printIndented(expression.getOperator().name());
        expression.getRightExpression().accept(this);
        spaces--;
    }

    @Override
    public void visit(NegationExpression expression) {
        spaces++;
        print(expression.getClass().getSimpleName());
        printIndented(expression.getOperator().name());
        expression.getExpression().accept(this);
        spaces--;
    }

    @Override
    public void visit(AssignmentExpression expression) {
        spaces++;
        print(expression.getClass().getSimpleName());
        expression.getLeft().accept(this);
        printIndented("ASSIGN");
        expression.getRight().accept(this);
        spaces--;
    }

    @Override
    public void visit(MemberExpression expression) {
        spaces++;
        print(expression.getClass().getSimpleName());
        expression.getObject().accept(this);
        printIndented("MEMBER");
        expression.getMember().accept(this);
        spaces--;
    }

    @Override
    public void visit(IntLiteralExpression expression) {
        spaces++;
        print("Int: " + expression.getValue());
        spaces--;
    }

    @Override
    public void visit(DoubleLiteralExpression expression) {
        spaces++;
        print("Double: " + expression.getValue());
        spaces--;
    }

    @Override
    public void visit(StringLiteralExpression expression) {
        spaces++;
        print("String" + expression.getValue());
        spaces--;
    }

    @Override
    public void visit(DateLiteralExpression expression) {
        spaces++;
        print("Date: " + expression.getValue().toString());
        spaces--;
    }

    @Override
    public void visit(PeriodLiteralExpression expression) {
        spaces++;
        print(String.valueOf(expression.getValue()));
        spaces--;
    }

    @Override
    public void visit(IdentifierExpression expression) {
        spaces++;
        print( "Identifier: " + expression.getName());
        spaces--;
    }

    @Override
    public void visit(FunctionCallExpression expression) {
        spaces++;
        print(expression.getClass().getSimpleName());
        print(String.valueOf(expression.getName()));
        spaces--;
    }

    void print(String string){
        System.out.print("  ".repeat(spaces));
        System.out.println(string);
    }

    void printIndented(String string){
        print("  " + string);
    }
}
