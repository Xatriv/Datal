package org.example.program;

public interface ProgramVisitor {
    void visit(Program program);
    void visit(UserFunctionDef functionDef);
    void visit(Block block);
    void visit(ExpressionStatement statement);
    void visit(IfStatement statement);
    void visit(WhileStatement statement);
    void visit(ReturnStatement statement);
    void visit(OrExpression expression);
    void visit(AndExpression expression);
    void visit(ComparativeExpression expression);
    void visit(AdditiveExpression expression);
    void visit(MultiplicativeExpression expression);
    void visit(NegationExpression expression);
    void visit(AssignmentExpression expression);
    void visit(MemberExpression expression);
    void visit(IntLiteralExpression expression);
    void visit(DoubleLiteralExpression expression);
    void visit(StringLiteralExpression expression);
    void visit(DateLiteralExpression expression);
    void visit(PeriodLiteralExpression expression);
    void visit(IdentifierExpression expression);
    void visit(FunctionCallExpression expression);
    void visit(ReadStringFunctionDef readStringFunctionDef);
    void visit(PrintFunctionDef printFunctionDef);
    void visit(ReadIntFunctionDef readIntFunctionDef);
    void visit(ReadDoubleFunctionDef readDoubleFunctionDef);
    void visit(ReadDateFunctionDef readDateFunctionDef);
    void visit(ReadPeriodFunctionDef readPeriodFunctionDef);
}
