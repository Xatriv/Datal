package org.example.interpreter;

import org.example.error.ErrorManager;
import org.example.error.InterpreterErrorInfo;
import org.example.error.Severity;
import org.example.program.*;
import org.example.source.Position;
import org.example.types.Date;
import org.example.types.Period;

import java.util.*;

public class Interpreter implements ProgramVisitor {

    private final ErrorManager errorManager;

    private final Stack<FunctionCallContext> callStack = new Stack<>();
    private ValueReference lastResult;
    private final Hashtable<String, FunctionDef> functionDefs = new Hashtable<>();

    private ValueReference memberContext;

    private ValueReference moveLastResult(Position position) {
        ValueReference value = getLastResult(position);
        lastResult = null;
        return value;
    }

    public ValueReference getLastResult(Position position) {
        if (lastResult.getValue() == null) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            position,
                            "Attempted use of a null value."));
        }
        return lastResult;
    }

    public Interpreter(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }

    public Interpreter(ErrorManager errorManager, Program program) {
        this.errorManager = errorManager;
        program.accept(this);
    }

    private void verifyInstance(Object object, List<Class<?>> types, Position position) {
        if (types.stream().noneMatch(t -> t.isInstance(object))) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            position,
                            String.format("Type error, expected any of type: %s; received: %s", types, object.getClass().getSimpleName())));
        }
    }

    private void addFunctionDefinitionIfAbsent(FunctionDef functionDef) {
        FunctionDef duplicatedUserFunctionDef;
        if ((duplicatedUserFunctionDef = functionDefs.putIfAbsent(functionDef.getName(), functionDef)) != null) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            duplicatedUserFunctionDef.getPosition(),
                            String.format("Non-unique function identifier (%s)", functionDef.getName())));
        }
    }

    @Override
    public void visit(Program program) {
        Position dummyPos = new Position(0, 0);
        functionDefs.putAll(program.getFunctions());
        addFunctionDefinitionIfAbsent(new ReadStringFunctionDef());
        addFunctionDefinitionIfAbsent(new ReadIntFunctionDef());
        addFunctionDefinitionIfAbsent(new ReadDoubleFunctionDef());
        addFunctionDefinitionIfAbsent(new ReadDateFunctionDef());
        addFunctionDefinitionIfAbsent(new ReadPeriodFunctionDef());
        addFunctionDefinitionIfAbsent(new PrintFunctionDef());


        var mainCall = new FunctionCallExpression("main", List.of(), dummyPos);
        mainCall.accept(this);
    }

    private void prepareBlockContext() {
        BlockContext context = new BlockContext();
        callStack.peek().getBlockContexts().add(0, context);
    }

    @Override
    public void visit(UserFunctionDef functionDef) {
        if (functionDef.getParameters().size() != callStack.peek().getArguments().size()) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            functionDef.getPosition(),
                            String.format("Mismatched arguments. Function takes %d parameters, but %d were provided ",
                                    functionDef.getParameters().size(), callStack.peek().getArguments().size())));
        }
        prepareBlockContext();
        for (int i = 0; i < functionDef.getParameters().size(); i++) {
            addVariableToBlockContext(callStack.peek().getArguments().get(i), functionDef.getParameters().get(i));
        }

        functionDef.getBody().accept(this);
        deleteBlockContext();
    }

    private void deleteBlockContext() {
        callStack.peek().getBlockContexts().remove(0);
    }

    @Override
    public void visit(Block block) {
        prepareBlockContext();
        var statements = block.getStatements();
        for (int i = 0; i < statements.size() && !callStack.peek().getReturned(); i++) {
            statements.get(i).accept(this);
            if (statements.get(i) instanceof ReturnStatement) {
                callStack.peek().setReturned(true);
                break;
            } else if (i == statements.size() - 1) {
                lastResult = new ValueReference(null);
            }
        }
        deleteBlockContext();
    }

    @Override
    public void visit(ExpressionStatement statement) {
        statement.getExpression().accept(this);
    }

    @Override
    public void visit(IfStatement statement) {
        statement.getCondition().accept(this);
        if (!(lastResult.getValue() instanceof Boolean)) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            statement.getCondition().getPosition(),
                            "Boolean expression expected inside \"if\" condition "
                    )
            );
        }
        if ((Boolean) lastResult.getValue()) {
            statement.getIfBlock().accept(this);
        } else if (statement.getElseBlock() != null) {
            statement.getElseBlock().accept(this);
        }
    }

    @Override
    public void visit(WhileStatement statement) {
        statement.getCondition().accept(this);
        verifyInstance(lastResult.getValue(), List.of(Boolean.class), statement.getCondition().getPosition());
        while ((Boolean) lastResult.getValue()) {
            statement.getLoopBlock().accept(this);
            statement.getCondition().accept(this);
            verifyInstance(lastResult.getValue(), List.of(Boolean.class), statement.getCondition().getPosition());
        }
    }

    @Override
    public void visit(ReturnStatement statement) {
        if (statement.getExpression() != null) {
            statement.getExpression().accept(this);
        } else {
            lastResult = new ValueReference(null);
        }
    }

    @Override
    public void visit(OrExpression expression) {
        expression.getLeftExpression().accept(this);
        verifyInstance(lastResult.getValue(), List.of(Boolean.class), expression.getPosition());
        if ((Boolean) lastResult.getValue()) {
            lastResult = new ValueReference(true);
            return;
        }
        expression.getRightExpression().accept(this);
        verifyInstance(lastResult.getValue(), List.of(Boolean.class), expression.getPosition());
        lastResult = new ValueReference(lastResult.getValue());
    }

    @Override
    public void visit(AndExpression expression) {
        expression.getLeftExpression().accept(this);
        verifyInstance(lastResult.getValue(), List.of(Boolean.class), expression.getPosition());
        if (!(Boolean) lastResult.getValue()) {
            lastResult = new ValueReference(false);
            return;
        }
        expression.getRightExpression().accept(this);
        verifyInstance(lastResult.getValue(), List.of(Boolean.class), expression.getPosition());
        lastResult = new ValueReference(lastResult.getValue());
    }

    @Override
    public void visit(ComparativeExpression expression) {
        expression.getLeftExpression().accept(this);
        var left = getLastResult(expression.getLeftExpression().getPosition()).getValue();
        expression.getRightExpression().accept(this);
        var right = getLastResult(expression.getRightExpression().getPosition()).getValue();
        Boolean result = null;
        switch (expression.getOperator()) {
            case EQUALS:
                result = OperationHandler.equals(left, right);
                break;
            case NOT_EQUAL:
                result = OperationHandler.notEqual(left, right);
                break;
            case LESS_THAN:
                result = OperationHandler.lessThan(left, right);
                break;
            case MORE_THAN:
                result = OperationHandler.moreThan(left, right);
                break;
            case MORE_OR_EQUAL_THAN:
                result = OperationHandler.moreOrEqualThan(left, right);
                break;
            case LESS_OR_EQUAL_THAN:
                result = OperationHandler.lessOrEqualThan(left, right);
                break;
        }
        if (result == null) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            expression.getPosition(),
                            String.format("Comparison between incompatible types: %s, %s",
                                    left.getClass().getSimpleName(), right.getClass().getSimpleName())
                    )
            );
        }
        lastResult = new ValueReference(result);
    }

    @Override
    public void visit(AdditiveExpression expression) {
        expression.getLeftExpression().accept(this);
        var left = getLastResult(expression.getLeftExpression().getPosition()).getValue();
        expression.getRightExpression().accept(this);
        var right = getLastResult(expression.getRightExpression().getPosition()).getValue();
        Object result;
        if (expression.getOperator() == AdditiveOperator.PLUS) {
            result = OperationHandler.add(left, right);
        } else {
            result = OperationHandler.subtract(left, right);
        }
        if (result == null) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            expression.getPosition(),
                            String.format("Incompatible additive operands: %s +- %s",
                                    left.getClass().getSimpleName(), right.getClass().getSimpleName())));
        }
        lastResult = new ValueReference(result);
    }

    @Override
    public void visit(MultiplicativeExpression expression) {
        expression.getLeftExpression().accept(this);
        var left = getLastResult(expression.getLeftExpression().getPosition()).getValue();
        expression.getRightExpression().accept(this);
        var right = getLastResult(expression.getRightExpression().getPosition()).getValue();
        Object result;
        if (expression.getOperator() == MultiplicativeOperator.MULTIPLY) {
            result = OperationHandler.multiply(left, right);
        } else {
            try {
                result = OperationHandler.divide(left, right);
            } catch (ArithmeticException e) {
                result = null;
                errorManager.reportError(
                        new InterpreterErrorInfo(
                                Severity.ERROR,
                                expression.getPosition(),
                                "Division by zero detected"));
            }
        }
        if (result == null) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            expression.getPosition(),
                            String.format("Incompatible operands in multiplicative expression: %s */ %s",
                                    left.getClass(), right.getClass())));
        }
        lastResult = new ValueReference(result);
    }

    @Override
    public void visit(NegationExpression expression) {
        expression.getExpression().accept(this);
        Object operand = getLastResult(expression.getExpression().getPosition()).getValue();
        if (operand instanceof Boolean && expression.getOperator() == NegationOperator.NOT) {
            lastResult = new ValueReference(!(Boolean) lastResult.getValue());
        } else if (operand instanceof Number && expression.getOperator() == NegationOperator.MINUS) {
            lastResult = new ValueReference(OperationHandler.multiply(-1, lastResult.getValue()));
        } else {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            expression.getExpression().getPosition(),
                            String.format("Type error: Attempted negating %s with %s operator.",
                                    expression.getOperator().name(), expression.getExpression().getClass().getSimpleName())));
        }
    }


    private void invokePeriodGetter(Period period, String name, Position position) {
        switch (name) {
            case "year":
                lastResult = period.getYearReference(); break;
            case "month":
                lastResult = period.getMonthReference(); break;
            case "day":
                lastResult = period.getDayReference(); break;
            case "hour":
                lastResult = period.getHourReference(); break;
            case "minute":
                lastResult = period.getMinuteReference(); break;
            case "second":
                lastResult = period.getSecondReference(); break;
            default:
                errorManager.reportError(
                        new InterpreterErrorInfo(
                                Severity.ERROR,
                                position,
                                String.format("Period does not have any field named \"%s\"", name)));
        }
    }

    private void invokeDateGetter(Date date, String name, Position position) {
        switch (name) {
            case "year":
                lastResult = date.getYearReference(); break;
            case "month":
                lastResult = date.getMonthReference(); break;
            case "day":
                lastResult = date.getDayReference(); break;
            case "hour":
                lastResult = date.getHourReference(); break;
            case "minute":
                lastResult = date.getMinuteReference(); break;
            case "second":
                lastResult = date.getSecondReference(); break;
            default:
                errorManager.reportError(
                        new InterpreterErrorInfo(
                                Severity.ERROR,
                                position,
                                String.format("Date does not have any field named \"%s\"", name)));
        }
    }

    @SuppressWarnings("unused")
    private void invokePeriodMethod(Period period, String methodName, List<Object> arguments, Position position) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (methodName) {
            case "getDifference":
                lastResult = period.getAbsolutePeriodDifferenceReference();
                break;
            default:
                new InterpreterErrorInfo(
                        Severity.ERROR,
                        position,
                        String.format("Period does not have any method named \"%s\"", methodName));
        }
    }

    @SuppressWarnings("unused")
    private void invokeDateMethod(Date date, String methodName, List<Object> arguments, Position position) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (methodName) {
            case "calculateSecondsSinceNewEra":
                lastResult = new ValueReference(date.secondsSinceNewEra());
                break;
            default:
                new InterpreterErrorInfo(
                        Severity.ERROR,
                        position,
                        String.format("Date does not have any method named \"%s\"", methodName));
        }
    }

    @Override
    public void visit(AssignmentExpression expression) {
        var left = expression.getLeft();
        verifyInstance(left, List.of(IdentifierExpression.class, MemberExpression.class), left.getPosition());
        expression.getRight().accept(this);
        var rightResult = getLastResult(expression.getRight().getPosition()).getValue();
        if (left instanceof IdentifierExpression) {
            left.accept(this);
            String identifierName = ((IdentifierExpression) left).getName();
            if (lastResult.getValue() == null) {
                addVariableToBlockContext(rightResult, identifierName);
            } else {
                 lastResult.setValue(rightResult);
            }
            return;
        }
        if (left instanceof MemberExpression) {
            left.accept(this);
            if (lastResult.getValue() == null) {
                errorManager.reportError(
                        new InterpreterErrorInfo(
                                Severity.ERROR,
                                left.getPosition(),
                                "Undefined access to an object"));
            }
            lastResult.setValue(rightResult);
        }

    }

    private void addVariableToBlockContext(Object variable, String identifierName) {
        callStack.peek().getBlockContexts().get(0).addVariable(identifierName, new ValueReference(variable));
    }


    @Override
    public void visit(MemberExpression expression) {
        expression.getObject().accept(this);
//        verifyInstance(expression.getObject(), List.of(IdentifierExpression.class, FunctionCallExpression.class), expression.getPosition());
        verifyInstance(lastResult.getValue(), List.of(Period.class, Date.class), expression.getPosition());
        memberContext = moveLastResult(expression.getPosition());
        expression.getMember().accept(this);



    }


    @Override
    public void visit(IntLiteralExpression expression) {
        lastResult = new ValueReference(expression.getValue());
    }

    @Override
    public void visit(DoubleLiteralExpression expression) {
        lastResult = new ValueReference(expression.getValue());
    }

    @Override
    public void visit(StringLiteralExpression expression) {
        lastResult = new ValueReference(expression.getValue());
    }

    @Override
    public void visit(DateLiteralExpression expression) {
        try {
            expression.getValue().verify();
        } catch (IllegalArgumentException e) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            expression.getPosition(),
                            e.getMessage()));
        }
        lastResult = new ValueReference(expression.getValue());
    }

    @Override
    public void visit(PeriodLiteralExpression expression) {
        lastResult = new ValueReference(expression.getValue());
    }

    @Override
    public void visit(IdentifierExpression expression) {
        if (memberContext != null) {
            if (memberContext.getValue() instanceof Period) {
                invokePeriodGetter((Period) memberContext.getValue(), expression.getName(), expression.getPosition());
            }
            if (memberContext.getValue() instanceof Date) {
                invokeDateGetter((Date) memberContext.getValue(), expression.getName(), expression.getPosition());
            }
            memberContext = null;
            return;
        }

        for (BlockContext context : callStack.peek().getBlockContexts()) {
            ValueReference value = context.getLocalVariables().get(expression.getName());
            if (value != null) {
                lastResult = value;
                return;
            }
        }
        lastResult = new ValueReference(null);
    }

    @Override
    public void visit(FunctionCallExpression expression) {
        List<Object> arguments = new ArrayList<>(List.of());
        for (var arg : expression.getArguments()) {
            arg.accept(this);
            var argLastResult = moveLastResult(arg.getPosition()).getValue();
            if (argLastResult instanceof Period){
                arguments.add(new Period ( (Period) argLastResult));
            } else if (argLastResult instanceof Date){
                arguments.add(new Date ( (Date) argLastResult));
            } else {
                arguments.add(argLastResult);
            }
        }
        if (memberContext != null) {
            if (memberContext.getValue() instanceof Period) {
                invokePeriodMethod(((Period) memberContext.getValue()), expression.getName(), arguments, expression.getPosition());
            }
            if (memberContext.getValue() instanceof Date) {
                invokeDateMethod(((Date) memberContext.getValue()), expression.getName(), arguments, expression.getPosition());
            }
            memberContext = null;
        } else {
            FunctionDef function;
            if ((function = functionDefs.get(expression.getName())) == null) {
                errorManager.reportError(
                        new InterpreterErrorInfo(
                                Severity.ERROR,
                                expression.getPosition(),
                                String.format("Reference to an undefined function: %s", expression.getName())));
                return;
            }
            callStack.push(new FunctionCallContext(arguments, expression.getPosition()));
            function.accept(this);
            callStack.pop();
        }
    }

    private void reportWarnIfArgsInReadFunction() {
        if (callStack.peek().getArguments().size() != 0) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.WARN,
                            callStack.peek().getPosition(),
                            "Read function does not take any arguments, but they were provided."
                    )
            );
        }
    }

    @Override
    public void visit(ReadStringFunctionDef functionDef) {
        reportWarnIfArgsInReadFunction();
        Scanner scanner = new Scanner(System.in);
        String stringValue = scanner.nextLine();
        lastResult = new ValueReference(stringValue);
    }

    @Override
    public void visit(ReadIntFunctionDef functionDef) {
        reportWarnIfArgsInReadFunction();
        Scanner scanner = new Scanner(System.in);
        Integer intValue = null;
        try {
            intValue = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            callStack.peek().getPosition(),
                            "Could not parse integer input."
                    )
            );
        }
        lastResult = new ValueReference(intValue);
    }

    @Override
    public void visit(ReadDoubleFunctionDef functionDef) {
        reportWarnIfArgsInReadFunction();
        Scanner scanner = new Scanner(System.in);
        Double doubleValue = null;
        try {
            doubleValue = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            callStack.peek().getPosition(),
                            "Could not parse double input."
                    )
            );
        }
        lastResult = new ValueReference(doubleValue);
    }

    @Override
    public void visit(ReadDateFunctionDef readDateFunctionDef) {
        reportWarnIfArgsInReadFunction();
        Scanner scanner = new Scanner(System.in);
        Date dateValue;
        if ((dateValue = Date.fromString(scanner.nextLine())) == null) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            callStack.peek().getPosition(),
                            "Could not parse date input."
                    )
            );
        }
        lastResult = new ValueReference(dateValue);
    }

    @Override
    public void visit(ReadPeriodFunctionDef readPeriodFunctionDef) {
        reportWarnIfArgsInReadFunction();
        Scanner scanner = new Scanner(System.in);
        Period periodValue;
        if ((periodValue = Period.fromString(scanner.nextLine())) == null) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            callStack.peek().getPosition(),
                            "Could not parse period input."
                    )
            );
        }
        lastResult = new ValueReference(periodValue);
    }

    @Override
    public void visit(PrintFunctionDef functionDef) {
        if (callStack.peek().getArguments().size() == 0) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.WARN,
                            callStack.peek().getPosition(),
                            "Print function takes any number of arguments higher than 0, but they were not provided."
                    )
            );
        }
        for (Object argument : callStack.peek().getArguments()) {
            System.out.print(argument);
        }
    }
}
