package org.example.parser;

import org.example.error.ErrorManager;
import org.example.error.ParserErrorInfo;
import org.example.error.Severity;
import org.example.lexer.Lexer;
import org.example.program.*;
import org.example.source.Position;
import org.example.token.*;
import org.example.types.Period;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Parser {

    private static Lexer lexer;
    private final ErrorManager errorManager;
    private final int unknownTokensInARowLimit;
    private final int maxParameters;

    @SuppressWarnings("FieldCanBeLocal")
    private final List<String> configPaths = Arrays.asList(
            "src/main/java/org/example/parser/parser.properties",
            "src/main/java/org/example/parser.properties",
            "src/main/parser.properties");


    public Parser(Lexer lexer, ErrorManager errorManager) {
        Parser.lexer = lexer;
        this.errorManager = errorManager;

        Properties props = new Properties();
        int unknownTokensInARowLimitProp = -1;
        int maxParametersProp = -1;
        try {
            Optional<String> maybePath = configPaths.stream()
                    .filter(path -> Files.exists(Paths.get(path)))
                    .findFirst();
            if (maybePath.isEmpty()){
                throw new IOException("Missing parser config path");
            }
            InputStream input = new FileInputStream(maybePath.get());
            props.load(input);
            unknownTokensInARowLimitProp = readProperty(props, "UNKNOWN_TOKENS_IN_A_ROW_LIMIT", -1);
            maxParametersProp = readProperty(props, "MAX_PARAMETERS", -1);
        } catch (IOException ignored) {
        } finally {
            this.unknownTokensInARowLimit = unknownTokensInARowLimitProp;
            this.maxParameters = maxParametersProp;
        }
    }

    @SuppressWarnings("unused")
    public Parser(Lexer lexer, ErrorManager errorManager, String configPath) {
        Parser.lexer = lexer;
        this.errorManager = errorManager;

        Properties props = new Properties();
        int unknownTokensInARowLimitProp = -1;
        int maxParametersProp = -1;
        try {
            InputStream input = new FileInputStream(configPath);
            props.load(input);
            unknownTokensInARowLimitProp = readProperty(props, "UNKNOWN_TOKENS_IN_A_ROW_LIMIT", -1);
            maxParametersProp = readProperty(props, "MAX_PARAMETERS", -1);
        } catch (IOException ignored) {
        } finally {
            this.unknownTokensInARowLimit = unknownTokensInARowLimitProp;
            this.maxParameters = maxParametersProp;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static int readProperty(Properties props, String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Program parse() throws IOException {
        nextToken();
        Hashtable<String, FunctionDef> functions = new Hashtable<>();
        FunctionDef function;
        while ( (function = parseFunctionDef()) != null){
            Position pos = lexer.getToken().getPosition();
            if (functions.putIfAbsent(function.getName(), function) != null) { //TODO functionDef (or Expression) should store position (at line x redefined function from line y)
                errorManager.reportError(
                        new ParserErrorInfo(
                                Severity.ERROR,
                                pos,
                                String.format("Non-unique function identifier (%s)", function.getName())));
            }
        }
        if (lexer.getToken().getType() != TokenType.EOF){
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    lexer.getToken().getPosition(),
                    String.format("Unexpected token at the end of file (%s)", lexer.getToken().getType().toString())
            ));
        }
        return new Program(functions);
    }

    private Token nextToken() throws IOException {
        if (lexer.next() == null){
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    new Position(0, 0),
                    "No tokens provided by the lexer"
            ));
        }
        int streak = 0;
        while (lexer.getToken().getType() == TokenType.UNKNOWN || lexer.getToken().getType() == TokenType.COMMENT) {
            Position pos = lexer.getToken().getPosition();
            if (unknownTokensInARowLimit < 0) {
                lexer.next();
                continue;
            }
            if (lexer.getToken().getType() == TokenType.COMMENT) {
                streak = 0;
            } else {
                streak++;
            }
            if (unknownTokensInARowLimit < streak) {
                errorManager.reportError(new ParserErrorInfo(
                        Severity.ERROR,
                        pos,
                        String.format("Too many unknown tokens in a row (>%s)", unknownTokensInARowLimit)
                ));
            }
            lexer.next();
        }
        return lexer.getToken();
    }

    private boolean consumeIfExists(TokenType tokenType) throws IOException {
        if (lexer.getToken().getType() != tokenType) {
            return false;
        }
        nextToken();
        return true;
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean consumeIfExists(TokenType tokenType, Position position, String errorMessage) throws IOException {
        if (lexer.getToken().getType() != tokenType) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.WARN,
                    position,
                    errorMessage));
            return false;
        }
        nextToken();
        return true;
    }

    private FunctionDef parseFunctionDef() throws IOException {
        if (lexer.getToken().getType() != TokenType.IDENTIFIER) return null;
        String identifier = ((IdentifierToken) lexer.getToken()).getName();
        nextToken();
        consumeIfExists(TokenType.PARENTHESIS_L, lexer.getToken().getPosition(), "Missing opening parenthesis in function definition");

        List<String> parameters = parseParameters();
        if (!consumeIfExists(TokenType.PARENTHESIS_R)) {
            errorManager.reportError(
                    new ParserErrorInfo(
                            Severity.ERROR,
                            lexer.getToken().getPosition(),
                            "Missing closing parenthesis in function definition"));
        }
        Block bodyBlock = parseBlock();
        if (bodyBlock == null) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    lexer.getToken().getPosition(),
                    "Missing statement block in function definition"));
        }
        return new FunctionDef(identifier, parameters, bodyBlock);
    }

    private List<String> parseParameters() throws IOException {
        List<String> parameters = new ArrayList<>();
        String parameter = parseSingleParameter();
        if (parameter == null) {
            return parameters;
        }
        parameters.add(parameter);
        while (consumeIfExists(TokenType.SEPARATOR)) {
            Position pos = lexer.getToken().getPosition();
            parameter = parseSingleParameter();
            if (parameter == null) {
                errorManager.reportError(
                        new ParserErrorInfo(
                                Severity.WARN,
                                pos,
                                "Parameter expected after a separator"));
            } else {
                String parameterName = parameter;
                if (parameters.stream().anyMatch(p -> p.equals(parameterName))) {
                    errorManager.reportError(
                            new ParserErrorInfo(
                                    Severity.ERROR,
                                    pos,
                                    String.format("Parameter name already exists (%s)", parameter)));
                } else {
                    parameters.add(parameter);
                    if (parameters.size() > maxParameters){
                        errorManager.reportError(
                                new ParserErrorInfo(
                                        Severity.ERROR,
                                        pos,
                                        String.format("Too many parameters (%d; max: %d)", parameters.size(), maxParameters)));
                    }
                }
            }
        }
        return parameters;
    }

    private String parseSingleParameter() throws IOException {
        if (lexer.getToken().getType() != TokenType.IDENTIFIER) {
            return null;
        }
        String identifier = ((IdentifierToken) lexer.getToken()).getName();
        nextToken();
        return identifier;
    }

    private Block parseBlock() throws IOException {
        if (!consumeIfExists(TokenType.BLOCK_DELIMITER_L)) return null;
        List<Statement> statements = new ArrayList<>();
        Statement statement;
        while ((statement = parseStatement()) != null) {
            statements.add(statement);
        }
        if (lexer.getToken().getType() != TokenType.BLOCK_DELIMITER_R) {
            errorManager.reportError(
                    new ParserErrorInfo(
                            Severity.WARN,
                            lexer.getToken().getPosition(),
                            "Closing brace missing"));
        }
        nextToken();
        return new Block(statements);
    }


    private Statement parseStatement() throws IOException {
        Expression expression = parseExpression();
        if (expression != null) {
            consumeIfExists(TokenType.SEMICOLON, lexer.getToken().getPosition(), "Semicolon missing at the end of expression statement");
            return new ExpressionStatement(expression);
        }
        IfStatement ifStatement = parseIfStatement();
        if (ifStatement != null) return ifStatement;
        WhileStatement forStatement = parseWhileStatement();
        if (forStatement != null) return forStatement;
        return parseReturnStatement();
    }


    private IfStatement parseIfStatement() throws IOException {
        if (!consumeIfExists(TokenType.IF)) return null;
        consumeIfExists(TokenType.PARENTHESIS_L, lexer.getToken().getPosition(), "Opening parenthesis expected in if statement");
        Expression condition = parseExpression();
        if (condition == null) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    lexer.getToken().getPosition(),
                    "Exit condition expected in if statement."
            ));
        }
        consumeIfExists(TokenType.PARENTHESIS_R, lexer.getToken().getPosition(), "Closing parenthesis expected in if statement");
        Block ifBlock = parseBlock();
        if (ifBlock == null){
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    lexer.getToken().getPosition(),
                    "Block missing after IF"
            ));
        }
        Block elseBlock;
        if (lexer.getToken().getType() == TokenType.ELSE) {
            nextToken();
            elseBlock = parseBlock();
            if (elseBlock == null){
                errorManager.reportError(new ParserErrorInfo(
                        Severity.ERROR,
                        lexer.getToken().getPosition(),
                        "Block missing after ELSE"
                ));
            }
        } else {
            elseBlock = null;
        }
        return new IfStatement(condition, ifBlock, elseBlock);
    }

    private WhileStatement parseWhileStatement() throws IOException {
        if (!consumeIfExists(TokenType.WHILE)) return null;
        consumeIfExists(TokenType.PARENTHESIS_L, lexer.getToken().getPosition(), "Opening parenthesis expected in while statement");
        Expression condition = parseExpression();
        if (condition == null) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    lexer.getToken().getPosition(),
                    "Exit condition missing in while statement."
            ));
        }
        consumeIfExists(TokenType.PARENTHESIS_R, lexer.getToken().getPosition(), "Closing parenthesis expected in while statement");
        Block loopBlock = parseBlock();
        return new WhileStatement(condition, loopBlock);
    }

    private ReturnStatement parseReturnStatement() throws IOException {
        if (!consumeIfExists(TokenType.RETURN)) return null;
        Expression expression = parseExpression();
        if (expression == null) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    lexer.getToken().getPosition(),
                    "Expression missing in return statement."
            ));
        }
        consumeIfExists(TokenType.SEMICOLON);
        return new ReturnStatement(expression);
    }

    private Expression parseExpression() throws IOException {
        return parseOrExpression();
    }

    private Expression parseOrExpression() throws IOException {
        Expression left = parseAndExpression();
        if (left == null) return null;
        while (consumeIfExists(TokenType.OR)) {
            Expression right = parseAndExpression();
            if (right == null) {
                errorManager.reportError(new ParserErrorInfo(
                        Severity.ERROR,
                        lexer.getToken().getPosition(),
                        "OR expression is missing right operand"
                ));
            }
            left = new OrExpression(left, right);
        }
        return left;
    }

    private Expression parseAndExpression() throws IOException {
        Expression left = parseComparativeExpression();
        if (left == null) return null;
        while (consumeIfExists(TokenType.AND)) {
            Expression right = parseComparativeExpression();
            if (right == null) {
                errorManager.reportError(new ParserErrorInfo(
                        Severity.ERROR,
                        lexer.getToken().getPosition(),
                        "AND expression is missing right operand"
                ));
            }
            left = new AndExpression(left, right);
        }
        return left;
    }

    private Expression parseComparativeExpression() throws IOException {
        Expression left = parseAdditiveExpression();
        if (left == null) return null;
        ComparisonOperator operator;
        switch (lexer.getToken().getType()) {
            case EQUALS:
                operator = ComparisonOperator.EQUALS;
                break;
            case NOT_EQUAL:
                operator = ComparisonOperator.NOT_EQUAL;
                break;
            case LESS_THAN:
                operator = ComparisonOperator.LESS_THAN;
                break;
            case LESS_OR_EQUAL_THAN:
                operator = ComparisonOperator.LESS_OR_EQUAL_THAN;
                break;
            case MORE_THAN:
                operator = ComparisonOperator.MORE_THAN;
                break;
            case MORE_OR_EQUAL_THAN:
                operator = ComparisonOperator.MORE_OR_EQUAL_THAN;
                break;
            default:
                return left;
        }
        nextToken();
        Expression right = parseAdditiveExpression();
        if (right == null) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    lexer.getToken().getPosition(),
                    "Comparative expression is missing right operand"
            ));
        }
        return new ComparativeExpression(operator, left, right);
    }

    private Expression parseAdditiveExpression() throws IOException {
        Expression left = parseMultiplicativeExpression();
        if (left == null) return null;
        AdditiveOperator operator;
        while ((operator = getIfAdditiveOperator()) != null ) {
            nextToken();
            Expression right = parseMultiplicativeExpression();
            if (right == null) {
                errorManager.reportError(new ParserErrorInfo(
                        Severity.ERROR,
                        lexer.getToken().getPosition(),
                        "Additive expression is missing right operand"
                ));
            }
            left = new AdditiveExpression(operator, left, right);
        }
        return left;
    }

    private AdditiveOperator getIfAdditiveOperator(){
        switch (lexer.getToken().getType()){
            case PLUS:
                return AdditiveOperator.PLUS;
            case MINUS:
                return AdditiveOperator.MINUS;
        }
        return null;
    }

    private Expression parseMultiplicativeExpression() throws IOException {
        Expression left = parseNegationExpression();
        if (left == null) return null;
        MultiplicativeOperator operator;
        while ((operator = getIfMultiplicativeOperator()) != null) {
            nextToken();
            Expression right = parseNegationExpression();
            if (right == null) {
                errorManager.reportError(new ParserErrorInfo(
                        Severity.ERROR,
                        lexer.getToken().getPosition(),
                        "Multiplicative expression is missing right operand"
                ));
            }
            left = new MultiplicativeExpression(operator, left, right);
        }
        return left;
    }

    private MultiplicativeOperator getIfMultiplicativeOperator(){
        switch (lexer.getToken().getType()){
            case MULTIPLY:
                return MultiplicativeOperator.MULTIPLY;
            case DIVIDE:
                return MultiplicativeOperator.DIVIDE;
        }
        return null;
    }

    private Expression parseNegationExpression() throws IOException {
        NegationOperator operator;
        if ((operator = getIfNegationOperator()) != null) {
            nextToken();
        }
        Expression expression = parseAssignmentExpression();
        if (expression == null) {
            expression = parseSimpleValue();
        }
        if (operator != null && expression == null) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    lexer.getToken().getPosition(),
                    "Expression or value expected after negation"
            ));
        }
        if (operator != null) {
            return new NegationExpression(operator, expression);
        }
        return expression;
    }

    private NegationOperator getIfNegationOperator(){
        switch (lexer.getToken().getType()){
            case MINUS:
                return NegationOperator.MINUS;
            case NOT:
                return NegationOperator.NOT;
        }
        return null;
    }

    private Expression parseAssignmentExpression() throws IOException {
        Expression left = parseMemberExpression();
        if (left == null) return null;
        if (consumeIfExists(TokenType.ASSIGN)) {
            Expression right = parseExpression();
            if (right == null) {
                errorManager.reportError(new ParserErrorInfo(
                        Severity.ERROR,
                        lexer.getToken().getPosition(),
                        "Assignment expression is missing right operand"
                ));
            }
            return new AssignmentExpression(left, right);
        }
        return left;
    }

    private Expression parseMemberExpression() throws IOException {
        Expression left = parseObjectValue();
        if (left == null) return null;
        while (consumeIfExists(TokenType.MEMBER)) {
            Expression right = parseIdentifierOrFunctionCall();
            if (right == null) {
                errorManager.reportError(new ParserErrorInfo(
                        Severity.ERROR,
                        lexer.getToken().getPosition(),
                        "Member access expression is missing right operand"
                ));
            }
            left = new MemberExpression(left, right);
        }
        return left;
    }

    private Expression parseSimpleLiteral() throws IOException {
        switch (lexer.getToken().getType()) {
            case INT:
                Expression intLiteral = new IntLiteralExpression(((IntToken) lexer.getToken()).getValue());
                nextToken();
                return intLiteral;
            case STRING:
                Expression stringLiteral = new StringLiteralExpression(((StringToken) lexer.getToken()).getValue());
                nextToken();
                return stringLiteral;
            case DOUBLE:
                Expression doubleLiteral = new DoubleLiteralExpression(((DoubleToken) lexer.getToken()).getValue());
                nextToken();
                return doubleLiteral;
        }
        return null;
    }

    private Expression parseSimpleValue() throws IOException {
        Expression simpleLiteral = parseSimpleLiteral();
        if (simpleLiteral != null) return simpleLiteral;
        if (!consumeIfExists(TokenType.PARENTHESIS_L)) {
            return null;
        }
        Expression expression = parseExpression();
        if (expression == null) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    lexer.getToken().getPosition(),
                    "Expression expected inside parenthesized expression"
            ));
        }
        consumeIfExists(TokenType.PARENTHESIS_R, lexer.getToken().getPosition(), "Missing closing parenthesis in parenthesized expression");
        return expression;
    }

    private Expression parseObjectValue() throws IOException {
        Expression objectLiteral = parseObjectLiteral();
        if (objectLiteral != null) return objectLiteral;
        return parseIdentifierOrFunctionCall();
    }

    private Expression parseObjectLiteral() throws IOException {
        switch (lexer.getToken().getType()) {
            case DATE:
                Expression dateLiteral = new DateLiteralExpression(((DateToken) lexer.getToken()).getValue());
                nextToken();
                return dateLiteral;
            case PERIOD:
                List<Period> periods = new ArrayList<>(Collections.singletonList(
                        ((PeriodToken) lexer.getToken()).getValue()));
                while (nextToken().getType() == TokenType.PERIOD) {
                    periods.add(((PeriodToken) lexer.getToken()).getValue());
                    nextToken();
                }
                return new PeriodLiteralExpression(periods);
        }
        return null;
    }

    private Expression parseIdentifierOrFunctionCall() throws IOException {
        if (lexer.getToken().getType() != TokenType.IDENTIFIER) return null;
        String name = ((IdentifierToken) lexer.getToken()).getName();
        nextToken();

        Expression expression = parseFunctionCall(name);
        if (expression == null) {
            return new IdentifierExpression(name);
        }
        return expression;
    }

    private Expression parseFunctionCall(String name) throws IOException {
        if (!consumeIfExists(TokenType.PARENTHESIS_L)) return null;
        List<Expression> arguments = parseArguments();
        consumeIfExists(TokenType.PARENTHESIS_R, lexer.getToken().getPosition(), "Missing closing parenthesis in function call");
        return new FunctionCallExpression(name, arguments);
    }

    private List<Expression> parseArguments() throws IOException {
        List<Expression> arguments = new ArrayList<>();
        Expression expression = parseExpression();
        if (expression == null){
            return arguments;
        }
        while (consumeIfExists(TokenType.SEPARATOR)) {
            expression = parseExpression();
            if (expression == null){
                errorManager.reportError(
                        new ParserErrorInfo(
                                Severity.WARN,
                                lexer.getToken().getPosition(),
                                "Argument expected after a separator"));
            } else {
                arguments.add(expression);
            }
        }
        return arguments;
    }
}
