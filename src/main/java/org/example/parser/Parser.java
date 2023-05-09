package org.example.parser;

import org.example.error.ErrorManager;
import org.example.error.ParserErrorInfo;
import org.example.error.Severity;
import org.example.lexer.Lexer;
import org.example.program.*;
import org.example.token.*;
import org.example.types.Period;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Parser {

    private static Lexer lexer;
    private final ErrorManager errorManager;
    private final int unknownTokensInARowLimit;

    @SuppressWarnings("unused")
    private static final List<TokenType> comparativeTokens = Arrays.asList(
            TokenType.EQUALS, TokenType.NOT_EQUAL, TokenType.LESS_THAN, TokenType.MORE_THAN,
            TokenType.LESS_OR_EQUAL_THAN, TokenType.MORE_OR_EQUAL_THAN);

    private static final List<TokenType> additiveTokens = Arrays.asList(TokenType.PLUS, TokenType.MINUS);
    private static final List<TokenType> multiplicativeTokens = Arrays.asList(TokenType.MULTIPLY, TokenType.DIVIDE);
    private static final List<TokenType> negationTokens = Arrays.asList(TokenType.MINUS, TokenType.NOT);


    public Parser(Lexer lexer, ErrorManager errorManager) {
        Parser.lexer = lexer;
        this.errorManager = errorManager;

        Properties props = new Properties();
        int unknownTokensInARowLimitProp = -1;
        try {
            InputStream input = new FileInputStream("src/main/java/org/example/parser/parser.properties");
            props.load(input);
            unknownTokensInARowLimitProp = readProperty(props, "UNKNOWN_TOKENS_IN_A_ROW_LIMIT", -1);
        } catch (IOException ignored) {
        } finally {
            this.unknownTokensInARowLimit = unknownTokensInARowLimitProp;
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
        //noinspection StatementWithEmptyBody
        while (parseFunctionDef(functions))
            ;
        assert lexer.getToken().getType() == TokenType.EOF;
        return new Program(functions);
    }

    private Token nextToken() throws IOException {
        lexer.next();
        int streak = 0;
        while (lexer.getToken().getType() == TokenType.UNKNOWN || lexer.getToken().getType() == TokenType.COMMENT) {
            streak++;
            if (unknownTokensInARowLimit < 0) {
                lexer.next();
                continue;
            } else if (unknownTokensInARowLimit == streak) {
                errorManager.reportError(new ParserErrorInfo(
                        Severity.ERROR,
                        lexer.getToken().getPosition(),
                        String.format("Too many unknown tokens in a row (>%s)", unknownTokensInARowLimit)
                ));
            } else if (lexer.getToken().getType() == TokenType.COMMENT) {
                streak = 0;
            } else {
                streak++;
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
    private boolean consumeIfExists(TokenType tokenType, String errorMessage) throws IOException {
        if (lexer.getToken().getType() != tokenType) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.WARN,
                    lexer.getToken().getPosition(),
                    errorMessage));
            return false;
        }
        nextToken();
        return true;
    }

    private boolean parseFunctionDef(Hashtable<String, FunctionDef> functions) throws IOException {
        if (lexer.getToken().getType() != TokenType.IDENTIFIER) return false;
        String identifier = ((IdentifierToken) lexer.getToken()).getName();
        nextToken();
        consumeIfExists(TokenType.PARENTHESIS_L, "Missing opening parenthesis in function definition");

        List<Parameter> parameters = parseParameters();
        consumeIfExists(TokenType.PARENTHESIS_R, "Missing closing parenthesis in function definition");
        Block bodyBlock = parseBlock();
        if (bodyBlock == null) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    lexer.getToken().getPosition(),
                    "Statement block missing in function definition"));
        }
        if (functions.putIfAbsent(identifier, new FunctionDef(identifier, parameters, bodyBlock)) != null) {
            errorManager.reportError(
                    new ParserErrorInfo(
                            Severity.ERROR,
                            lexer.getToken().getPosition(),
                            "Non-unique function identifier"));
            return false;
        }
        return true;
    }

    private List<Parameter> parseParameters() throws IOException {
        List<Parameter> parameters = new ArrayList<>();
        Parameter parameter = parseSingleParameter();
        if (parameter == null) {
            return null;
        }
        while (consumeIfExists(TokenType.SEPARATOR)) {
            parameter = parseSingleParameter();
            if (parameter == null) {
                errorManager.reportError(
                        new ParserErrorInfo(
                                Severity.WARN,
                                lexer.getToken().getPosition(),
                                "Parameter expected after a separator"));
            } else {
                String parameterName = parameter.getName();
                if (parameters.stream().anyMatch(p -> p.getName().equals(parameterName))) {
                    errorManager.reportError(
                            new ParserErrorInfo(
                                    Severity.ERROR,
                                    lexer.getToken().getPosition(),
                                    String.format("Parameter name already exists (%s)", parameter.getName())));
                } else {
                    parameters.add(parameter);
                }
            }
        }
        return parameters;
    }

    private Parameter parseSingleParameter() throws IOException {
        if (lexer.getToken().getType() != TokenType.IDENTIFIER) {
            return null;
        }
        String identifier = ((IdentifierToken) lexer.getToken()).getName();
        nextToken();
        return new Parameter(identifier);
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
                            Severity.ERROR,
                            lexer.getToken().getPosition(),
                            "Closing brace missing"));
        }
        return new Block(statements);
    }


    private Statement parseStatement() throws IOException {
        Expression expression = parseExpression();
        if (expression != null) {
            consumeIfExists(TokenType.SEMICOLON, "Semicolon missing at the end of expression statement");
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
        consumeIfExists(TokenType.PARENTHESIS_L, "Opening parenthesis expected.");
        Expression condition = parseExpression();
        if (condition == null) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    lexer.getToken().getPosition(),
                    "Exit condition expected in if statement."
            ));
        }
        consumeIfExists(TokenType.PARENTHESIS_L, "Closing parenthesis expected.");
        Block ifBlock = parseBlock();
        Block elseBlock;
        if (lexer.getToken().getType() == TokenType.ELSE){
            nextToken();
            elseBlock = parseBlock();
        } else {
            elseBlock = null;
        }
        return new IfStatement(condition, ifBlock, elseBlock);
    }

    private WhileStatement parseWhileStatement() throws IOException {
        if (!consumeIfExists(TokenType.WHILE)) return null;
        Expression condition = parseExpression();
        if (condition == null) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.ERROR,
                    lexer.getToken().getPosition(),
                    "Exit condition missing in while statement."
            ));
        }
        consumeIfExists(TokenType.PARENTHESIS_L, "Closing parenthesis expected.");
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
        while (additiveTokens.contains(lexer.getToken().getType())) {
            AdditiveOperator operator = lexer.getToken().getType() == TokenType.PLUS
                    ? AdditiveOperator.PLUS
                    : AdditiveOperator.MINUS;
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

    private Expression parseMultiplicativeExpression() throws IOException {
        Expression left = parseNegationExpression();
        if (left == null) return null;

        while (multiplicativeTokens.contains(lexer.getToken().getType())) {
            MultiplicativeOperator operator = lexer.getToken().getType() == TokenType.MULTIPLY
                    ? MultiplicativeOperator.MULTIPLY
                    : MultiplicativeOperator.DIVIDE;
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

    private Expression parseNegationExpression() throws IOException {
        NegationOperator operator;
        if (negationTokens.contains(lexer.getToken().getType())) {
            operator = lexer.getToken().getType() == TokenType.MINUS
                    ? NegationOperator.MINUS
                    : NegationOperator.NOT;
            nextToken();
        } else {
            operator = null;
        }
        Expression expression = parseMemberExpression();
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

    private Expression parseMemberExpression() throws IOException {
        Expression left = parseObjectValue();
        if (left == null) {
            left = parseIdentifierOrFunctionCall();
            if (left == null) return null;
        }
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
        consumeIfExists(TokenType.PARENTHESIS_R, "Missing closing parenthesis in parenthesized expression");
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
        consumeIfExists(TokenType.PARENTHESIS_R, "Missing closing parenthesis in function call");
        return new FunctionCallExpression(name, arguments);
    }

    private List<Expression> parseArguments() throws IOException {
        List<Expression> arguments = new ArrayList<>();
        Expression expression = parseExpression();
        while (expression != null) {
            nextToken();
            arguments.add(expression);
            expression = parseExpression();
        }
        return arguments;
    }
}
