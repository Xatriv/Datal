package org.example.parser;

import org.example.error.ErrorManager;
import org.example.error.ParserErrorInfo;
import org.example.error.Severity;
import org.example.lexer.Lexer;
import org.example.token.IdentifierToken;
import org.example.token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Parser {

    private static Lexer lexer;
    private final ErrorManager errorManager;


    public Parser(Lexer lexer, ErrorManager errorManager) {
        Parser.lexer = lexer;
        this.errorManager = errorManager;
    }

    public Program parse() throws IOException {
        lexer.next();
        Hashtable<String, FunctionDef> functions = new Hashtable<>();
        while (parseFunctionDef(functions))
            ;
        assert lexer.getToken().getType() == TokenType.EOF;
        return new Program(functions);
    }

    private boolean consumeIfExists(TokenType tokenType) throws IOException {
        if (lexer.getToken().getType() != tokenType) {
            return false;
        }
        lexer.next();
        return true;
    }

    private boolean consumeIfExists(TokenType tokenType, String errorMessage) throws IOException {
        if (lexer.getToken().getType() != tokenType) {
            errorManager.reportError(new ParserErrorInfo(
                    Severity.WARN,
                    lexer.getToken().getPosition(),
                    errorMessage));
            return false;
        }
        lexer.next();
        return true;
    }

    private boolean parseFunctionDef(Hashtable<String, FunctionDef> functions) throws IOException {
        if (lexer.getToken().getType() != TokenType.IDENTIFIER) {
            return false;
        }
        String identifier = ((IdentifierToken) lexer.getToken()).getName();
        lexer.next();
        if (!consumeIfExists(TokenType.PARENTHESIS_L,
                "Missing opening parenthesis in function definition")) {
            lexer.next();
        }

        // TODO params
        List<Parameter> parameters =  parseParameters();
        if (!consumeIfExists(TokenType.PARENTHESIS_R,
                "Missing closing parenthesis in function definition")) {
            lexer.next();
        }
        // TODO block
        Block bodyBlock = parseBlock();
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
            errorManager.reportError(
                    new ParserErrorInfo(
                            Severity.ERROR,
                            lexer.getToken().getPosition(),
                            "Identifier expected"));
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
        if (lexer.getToken().getType() != TokenType.IDENTIFIER){
            return null;
        }
        String identifier = ((IdentifierToken) lexer.getToken()).getName();
        lexer.next();
        return new Parameter(identifier);
    }

    private Block parseBlock() throws IOException {
        if (!consumeIfExists(TokenType.BLOCK_DELIMITER_L)) {
            return null;
        }
        List<Statement> statements = new ArrayList<>();
        Statement statement = parseStatement();
        statement = parseStatement();
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
        //parse each statement
        // could be done like return addStmt or multStmt or andStmt...
        // TODO
        if (lexer.getToken().getType() == TokenType.BLOCK_DELIMITER_R){ // TODO this is very wrong
            return  null;
        }
        lexer.next();
        return new Statement();
    }
}
