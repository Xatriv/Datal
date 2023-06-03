package org.example.interpreter;

import org.example.error.ErrorManager;
import org.example.error.InterpreterErrorInfo;
import org.example.error.Severity;
import org.example.program.*;
import org.example.source.Position;
import org.example.types.Date;
import org.example.types.Period;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Interpreter implements ProgramVisitor {

    private final ErrorManager errorManager;

    private final Stack<FunctionCallContext> callStack = new Stack<>();
    private InterpreterVisitationResult lastResult;
    private Hashtable<String, FunctionDef> functionDefs = new Hashtable<>();

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
                            String.format("Type error, expected any of type: %s; received: %s", types, object.getClass())));
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
        addFunctionDefinitionIfAbsent(new ReadStringFunctionDef()); // TODO readInt, print etc.
        addFunctionDefinitionIfAbsent(new PrintFunctionDef()); // TODO readInt, print etc.


        var mainCall = new FunctionCallExpression("main", List.of(), dummyPos);
        mainCall.accept(this);
//        mainFunction.accept(this);
//        for (var fun: program.getFunctions().values()) {
//            fun.accept(this);
//        }
    }

    @Override
    public void visit(UserFunctionDef functionDef) {
        if (functionDef.getParameters().size() != callStack.peek().getArguments().size()) {
            //TODO report error params != args
        }
        BlockContext context = new BlockContext();
        for (int i = 0; i < functionDef.getParameters().size(); i++) {
            context.addVariable(functionDef.getParameters().get(i), callStack.peek().getArguments().get(i));
        }
        callStack.peek().getBlockContexts().add(context);

        functionDef.getBody().accept(this);
        callStack.peek().getBlockContexts().clear();
    }

    @Override
    public void visit(Block block) {

        callStack.peek().getBlockContexts().add(0, new BlockContext());
        for (var stmt : block.getStatements()) {
            stmt.accept(this); //TODO handle return statements, if so - return
        }
        callStack.peek().getBlockContexts().remove(0);
    }

    @Override
    public void visit(ExpressionStatement statement) {
        statement.getExpression().accept(this);
    }

    @Override
    public void visit(IfStatement statement) {
        statement.getCondition().accept(this); //TODO wrap in if
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
            //TODO check if it doesn't need more condition isBool check
            statement.getLoopBlock().accept(this);
            statement.getCondition().accept(this);
            verifyInstance(lastResult.getValue(), List.of(Boolean.class), statement.getCondition().getPosition());
        }
    }

    @Override
    public void visit(ReturnStatement statement) {
        statement.getExpression().accept(this);

    }

    @Override
    public void visit(OrExpression expression) {
        expression.getLeftExpression().accept(this);
        verifyInstance(lastResult.getValue(), List.of(Boolean.class), expression.getPosition());
        if ((Boolean) lastResult.getValue()) {
            lastResult = new InterpreterVisitationResult(true);
            return;
        }
        expression.getRightExpression().accept(this);
        verifyInstance(lastResult.getValue(), List.of(Boolean.class), expression.getPosition());
        lastResult = new InterpreterVisitationResult(lastResult.getValue());
    }

    @Override
    public void visit(AndExpression expression) {
        expression.getLeftExpression().accept(this);
        verifyInstance(lastResult.getValue(), List.of(Boolean.class), expression.getPosition());
        if (!(Boolean) lastResult.getValue()) {
            lastResult = new InterpreterVisitationResult(false);
            return;
        }
        expression.getRightExpression().accept(this);
        verifyInstance(lastResult.getValue(), List.of(Boolean.class), expression.getPosition());
        lastResult = new InterpreterVisitationResult(lastResult.getValue());
    }

    @Override
    public void visit(ComparativeExpression expression) {
        //TODO
        expression.getLeftExpression().accept(this);
        var left = lastResult.getValue();
        expression.getRightExpression().accept(this);
        var right = lastResult.getValue();
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
        lastResult = new InterpreterVisitationResult(result);
    }

    @Override
    public void visit(AdditiveExpression expression) {
        //TODO
        expression.getLeftExpression().accept(this);
        var left = lastResult.getValue();
        expression.getRightExpression().accept(this);
        var right = lastResult.getValue();
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
                            String.format("Incompatible additive operands: %s + %s",
                                    left.getClass().getSimpleName(), right.getClass().getSimpleName())));
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
                            String.format("Incompatible operands in multiplicative expression: %s + %s",
                                    left.getClass(), right.getClass())));
        }
        lastResult = new InterpreterVisitationResult(result);
    }

    @Override
    public void visit(NegationExpression expression) {
        expression.getExpression().accept(this);
        //TODO
        if (lastResult.getValue() instanceof Boolean && expression.getOperator() == NegationOperator.NOT) {
            lastResult = new InterpreterVisitationResult(!(Boolean) lastResult.getValue());
        } else if (lastResult.getValue() instanceof Number && expression.getOperator() == NegationOperator.MINUS) {
            lastResult = new InterpreterVisitationResult(-1 * (Double) lastResult.getValue());
        } else {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            expression.getExpression().getPosition(),
                            String.format("Type error: Attempted negating %s with %s operator.",
                                    expression.getOperator().name(), expression.getExpression().getClass())));
        }
    }

    @Override
    public void visit(AssignmentExpression expression) {
        //TODO assign right to identifier from left
        var left = expression.getLeft();
        expression.getRight().accept(this);
        var right = lastResult.getValue();
        if (!(left instanceof IdentifierExpression) && !(left instanceof MemberExpression)) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            expression.getLeft().getPosition(),
                            String.format("Attempted to assign a value to: %s", left.getClass())));
        }
        if (left instanceof IdentifierExpression) {
            String identifierName = ((IdentifierExpression) left).getName();
            if (right != null) { //TODO maybe unnecessary - will do for now to handle void returns;
                var activeBlock = callStack.peek().getBlockContexts()//TODO check if reference or value
                        .stream()
                        .filter(b -> b.getLocalVariables().containsKey(identifierName))
                        .findFirst();
                if (activeBlock.isEmpty()) {
                    callStack.peek().getBlockContexts().get(0).addVariable(identifierName, right);
                } else {
                    activeBlock.get().getLocalVariables().put(identifierName, right);
                }
                lastResult = new InterpreterVisitationResult(right);
            }
        } else if (left instanceof MemberExpression) {
            IdentifierExpression objectIdentifier = (IdentifierExpression) ((MemberExpression) left).getObject();
            if (right != null) { //TODO maybe unnecessary - will do for now to handle void returns;
                var activeBlock = callStack.peek().getBlockContexts()//TODO check if reference or value
                        .stream()
                        .filter(b -> b.getLocalVariables().containsKey(objectIdentifier.getName()))
                        .findFirst();
                if (activeBlock.isEmpty()) {
//                    callStack.peek().getBlockContexts().get(0).addVariable(objectIdentifier.getName(), right);
                    //TODO throw error no such identifier
                    return;
                }
                Object object = activeBlock.get().getLocalVariables().get(objectIdentifier.getName()); //put(identifier.getName(), right);
                if (!(right instanceof Integer)) {
                    //TODO throw error incompatible types
                    return;
                }
                if (object instanceof Period) {
                    if (((MemberExpression) left).getMember() instanceof IdentifierExpression) {

                        Method method = null;
                        String memberName = ((IdentifierExpression) ((MemberExpression) left).getMember()).getName();
                        String firstLetter = memberName.substring(0, 1);
                        String restOfWord =  memberName.substring(1);
                        String methodName = String.format("set%s%s", firstLetter.toUpperCase(), restOfWord);
                        try {
                            method = object.getClass().getMethod(methodName, Integer.class);
                        } catch (NoSuchMethodException e) {
                            errorManager.reportError(
                                    new InterpreterErrorInfo(
                                            Severity.ERROR,
                                            ((MemberExpression) left).getPosition(),
                                            String.format("Period does not have any field named \"%s\"", memberName)));
                            return;
                        }
                        Object invocationResult;
                        try {
                            invocationResult = method.invoke(object, (Number) right);
                        } catch (IllegalAccessException | InvocationTargetException e){
                            errorManager.reportError(
                                    new InterpreterErrorInfo(
                                            Severity.ERROR,
                                            ((MemberExpression) left).getPosition(),
                                            String.format("Unable to access member \"%s\"", methodName)));
                            return;
                        }
                        lastResult = new InterpreterVisitationResult(invocationResult);

//                        switch (((IdentifierExpression) ((MemberExpression) left).getMember()).getName()) {
//                            case "year":
//                                ((Period) object).setYear((Integer) right); break;
//                            case "month":
//                                ((Period) object).setMonth((Integer) right); break;
//                            case "day":
//                                ((Period) object).setDay((Integer) right); break;
//                            case "hour":
//                                ((Period) object).setHour((Integer) right); break;
//                            case "minute":
//                                ((Period) object).setMinute((Integer) right); break;
//                            case "second":
//                                ((Period) object).setSecond((Integer) right); break;
//                            default:
//                                //TODO throw error no such member
//                                 return;
                    }
                    if ((((MemberExpression) left).getMember() instanceof FunctionCallExpression)){
                        Method method = null;
                        String methodName = ((FunctionCallExpression) ((MemberExpression) left).getMember()).getName();
                        try {
                            method = object.getClass().getMethod(methodName); //TODO maybe cast object to Period
                        } catch (NoSuchMethodException e) {
                            errorManager.reportError(
                                    new InterpreterErrorInfo(
                                            Severity.ERROR,
                                            ((MemberExpression) left).getPosition(),
                                            String.format("Period does not have any method named \"%s\"", methodName)));
                            return;
                        }
                        Object invocationResult;
                        try {
                            invocationResult = method.invoke(object);
                        } catch (IllegalAccessException | InvocationTargetException e){
                            errorManager.reportError(
                                    new InterpreterErrorInfo(
                                            Severity.ERROR,
                                            ((MemberExpression) left).getPosition(),
                                            String.format("Unable to access method \"%s\"", methodName)));
                            return;
                        }
                        lastResult = new InterpreterVisitationResult(invocationResult);
                    }
                }

            }
            lastResult = new InterpreterVisitationResult(right);
        }
    }


    @Override
    public void visit(MemberExpression expression) {
        //TODO
        expression.getObject().accept(this);
        if (lastResult.getValue() instanceof Period) {
            Period periodObj = (Period) lastResult.getValue();
            if (expression.getMember() instanceof IdentifierExpression) {
                switch (((IdentifierExpression) expression.getMember()).getName()) {
                    case "year": //TODO should it return only a value or reference to the value?
                        //TODO reflection
                        lastResult = new InterpreterVisitationResult(periodObj.getYear());
                        return;
                    case "month":
                        lastResult = new InterpreterVisitationResult(periodObj.getMonth());
                        return;
                    case "day":
                        lastResult = new InterpreterVisitationResult(periodObj.getDay());
                        return;
                    case "hour":
                        lastResult = new InterpreterVisitationResult(periodObj.getHour());
                        return;
                    case "minute":
                        lastResult = new InterpreterVisitationResult(periodObj.getMinute());
                        return;
                    case "second":
                        lastResult = new InterpreterVisitationResult(periodObj.getSecond());
                        return;
                    default:
                        errorManager.reportError(new InterpreterErrorInfo(Severity.ERROR, expression.getPosition(), "TODO")); //TODO
                        return;
                }
            }
        } else if (lastResult.getValue() instanceof Date) {
            Date dateObj = (Date) lastResult.getValue(); //TODO
        }
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
        try {
            expression.getValue().verify();
        } catch (IllegalArgumentException e) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            expression.getPosition(),
                            e.getMessage()));
        }
        lastResult = new InterpreterVisitationResult(expression.getValue());
    }

    @Override
    public void visit(PeriodLiteralExpression expression) {
        //TODO
//        Period p = new Period(
//          expression.getValue().stream().
//        );
        lastResult = new InterpreterVisitationResult(expression.getValue());
    }

    @Override
    public void visit(IdentifierExpression expression) {
        //search BlockContexts up to FunctionCallContext. Return value if exists, else - null
        for (BlockContext context : callStack.peek().getBlockContexts()) {
            var value = context.getLocalVariables().get(expression.getName());
            if (value != null) {
                lastResult = new InterpreterVisitationResult(value);
                return;
            }
        }
        errorManager.reportError(
                new InterpreterErrorInfo(
                        Severity.ERROR,
                        expression.getPosition(),
                        String.format("Undefined variable: %s", expression.getName())));
    }

    @Override
    public void visit(FunctionCallExpression expression) {
        FunctionDef function;
        if ((function = functionDefs.get(expression.getName())) == null) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            expression.getPosition(),
                            String.format("Reference to an undefined function: %s", expression.getName())));
            return;
        }
        List<Object> arguments = new ArrayList<>(List.of());
        for (var arg : expression.getArguments()) {
            arg.accept(this);
            arguments.add(lastResult.getValue());
        }
        callStack.push(new FunctionCallContext(arguments));
        function.accept(this);
        callStack.pop();
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
        Scanner myObj = new Scanner(System.in);
        String stringValue = myObj.nextLine();
        lastResult = new InterpreterVisitationResult(stringValue);
    }

    @Override
    public void visit(ReadIntFunctionDef functionDef) {
        reportWarnIfArgsInReadFunction();
        Scanner myObj = new Scanner(System.in);
        Integer intValue = null;
        try {
            intValue = Integer.parseInt(myObj.nextLine());
        } catch (NumberFormatException e) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            callStack.peek().getPosition(),
                            "Could not parse integer input."
                    )
            );
        }
        lastResult = new InterpreterVisitationResult(intValue);
    }

    @Override
    public void visit(ReadDoubleFunctionDef functionDef) {
        reportWarnIfArgsInReadFunction();
        Scanner myObj = new Scanner(System.in);
        Integer doubleValue = null;
        try {
            doubleValue = Integer.parseInt(myObj.nextLine());
        } catch (NumberFormatException e) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            callStack.peek().getPosition(),
                            "Could not parse double input."
                    )
            );
        }
        lastResult = new InterpreterVisitationResult(doubleValue);
    }

    @Override
    public void visit(ReadDateFunctionDef readDateFunctionDef) {
        reportWarnIfArgsInReadFunction();
        Scanner myObj = new Scanner(System.in);
        Date dateValue;
        if ((dateValue = Date.fromString(myObj.nextLine())) == null) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            callStack.peek().getPosition(),
                            "Could not parse double input."
                    )
            );
        }
        lastResult = new InterpreterVisitationResult(dateValue);
    }

    @Override
    public void visit(ReadPeriodFunctionDef readPeriodFunctionDef) {
        reportWarnIfArgsInReadFunction();
        Scanner myObj = new Scanner(System.in);
        Period periodValue;
        if ((periodValue = Period.fromString(myObj.nextLine())) == null) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.ERROR,
                            callStack.peek().getPosition(),
                            "Could not parse double input."
                    )
            );
        }
        lastResult = new InterpreterVisitationResult(periodValue);
    }

    @Override
    public void visit(PrintFunctionDef functionDef) {
        if (callStack.peek().getArguments().size() == 0) {
            errorManager.reportError(
                    new InterpreterErrorInfo(
                            Severity.WARN,
                            callStack.peek().getPosition(),
                            "Print function takes some arguments, but they were not provided." //TODO more descriptive
                    )
            );
        }
        for (Object argument : callStack.peek().getArguments()) {
            System.out.print(argument);
        }
    }
}
