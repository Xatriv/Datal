import org.example.error.CodeError;
import org.example.error.ErrorManager;
import org.example.lexer.Lexer;
import org.example.lexer.MockLexer;
import org.example.parser.Parser;
import org.example.program.*;
import org.example.source.Position;
import org.example.token.*;
import org.example.types.Date;
import org.example.types.Period;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTests {
    @Test
    void noTokensTest() {
        List<Token> tokens = List.of();
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, parser::parse);
        assertEquals(1, eM.getErrors().size());
        assertEquals("No tokens provided by the lexer", eM.getErrors().get(0).getMessage());
    }

    @Test
    void justEOFTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = List.of(
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertEquals(0, parser.parse().getFunctions().size());
    }

    @Test
    void invalidTokenAtTheEndTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = List.of(
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("Unexpected token at the end of file (BLOCK_DELIMITER_R)", eM.getErrors().get(0).getMessage());
    }

    @Test
    void singleEmptyFunctionTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(0, program.getFunctions().get(fun.getName()).getBody().getStatements().size());
        assertEquals(0, program.getFunctions().get(fun.getName()).getParameters().size());
    }

    @Test
    void functionWithOneParameterTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new IdentifierToken("param1", pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        String param = "param1";
//        Hashtable<String, FunctionDef> functions = new Hashtable<>() {{ put(fun.getName(), fun); }};
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(0, program.getFunctions().get(fun.getName()).getBody().getStatements().size());
        assertEquals(1, program.getFunctions().get(fun.getName()).getParameters().size());
        assertEquals(param, program.getFunctions().get(fun.getName()).getParameters().get(0));
    }

    @Test
    void functionWithMultipleParameterTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new IdentifierToken("param1", pos),
                new SimpleToken(TokenType.SEPARATOR, pos),
                new IdentifierToken("param2", pos),
                new SimpleToken(TokenType.SEPARATOR, pos),
                new IdentifierToken("param3", pos),
                new SimpleToken(TokenType.SEPARATOR, pos),
                new IdentifierToken("param4", pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        String param1 = "param1";
        String param2 = "param2";
        String param3 = "param3";
        String param4 = "param4";
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(0, program.getFunctions().get(fun.getName()).getBody().getStatements().size());
        assertEquals(4, program.getFunctions().get(fun.getName()).getParameters().size());
        assertEquals(param1, program.getFunctions().get(fun.getName()).getParameters().get(0));
        assertEquals(param2, program.getFunctions().get(fun.getName()).getParameters().get(1));
        assertEquals(param3, program.getFunctions().get(fun.getName()).getParameters().get(2));
        assertEquals(param4, program.getFunctions().get(fun.getName()).getParameters().get(3));
    }

    @Test
    void noParameterBeforeCommaTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.SEPARATOR, pos),
                new IdentifierToken("param2", pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("Missing closing parenthesis in function definition", eM.getErrors().get(0).getMessage());
    }

    @Test
    void noParameterAfterCommaTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new IdentifierToken("param1", pos),
                new SimpleToken(TokenType.SEPARATOR, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        String param1 = "param1";
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(0, program.getFunctions().get(fun.getName()).getBody().getStatements().size());
        assertEquals(1, program.getFunctions().get(fun.getName()).getParameters().size());
        assertEquals(param1, program.getFunctions().get(fun.getName()).getParameters().get(0));
    }


    @Test
    void missingBlockTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("Missing statement block in function definition", eM.getErrors().get(0).getMessage());
    }

    @Test
    void missingOpeningParenthesisInFunctionDefinitionTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(1, eM.getErrors().size());
        assertEquals("Missing opening parenthesis in function definition", eM.getErrors().get(0).getMessage());
    }

    @Test
    void missingClosingParenthesisInFunctionDefinitionTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("Missing closing parenthesis in function definition", eM.getErrors().get(0).getMessage());
    }

    @Test
    void duplicatePFunctionNameTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("Non-unique function identifier (fun1)", eM.getErrors().get(0).getMessage());
    }

    @Test
    void duplicateParameterNameTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new IdentifierToken("param1", pos),
                new SimpleToken(TokenType.SEPARATOR, pos),
                new IdentifierToken("param1", pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("String name already exists (param1)", eM.getErrors().get(0).getMessage());
    }

    @Test
    void singleExpressionStatement() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(1, (
                (IntLiteralExpression) (
                        (ExpressionStatement) program
                                .getFunctions().get(fun.getName())
                                .getBody()
                                .getStatements()
                                .get(0)
                ).getExpression()
        ).getValue());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    void missingSemicolonAfterExpressionStatement() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(1, (
                (IntLiteralExpression) (
                        (ExpressionStatement) program
                                .getFunctions().get(fun.getName())
                                .getBody()
                                .getStatements()
                                .get(0)
                ).getExpression()
        ).getValue());
        assertEquals(1, eM.getErrors().size());
        assertEquals("Semicolon missing at the end of expression statement", eM.getErrors().get(0).getMessage());
    }

    @Test
    void simpleLiteralsTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new DoubleToken(2.1, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new StringToken("This is string literal", pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(1, (
                (IntLiteralExpression) (
                        (ExpressionStatement) program
                                .getFunctions().get(fun.getName())
                                .getBody()
                                .getStatements()
                                .get(0)
                ).getExpression()
        ).getValue());
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(1, (
                (IntLiteralExpression) (
                        (ExpressionStatement) program
                                .getFunctions().get(fun.getName())
                                .getBody()
                                .getStatements()
                                .get(0)
                ).getExpression()
        ).getValue());
        assertEquals(2.1, (
                (DoubleLiteralExpression) (
                        (ExpressionStatement) program
                                .getFunctions().get(fun.getName())
                                .getBody()
                                .getStatements()
                                .get(1)
                ).getExpression()
        ).getValue());
        assertEquals("This is string literal", (
                (StringLiteralExpression) (
                        (ExpressionStatement) program
                                .getFunctions().get(fun.getName())
                                .getBody()
                                .getStatements()
                                .get(2)
                ).getExpression()
        ).getValue());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    void objectLiteralsTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new PeriodToken(new Period(0, 0, 10, 0, 0, 0), pos),
                new PeriodToken(new Period(0, 0, 0, 0, 0, 7), pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new DateToken(new Date(true, 2022, 1, 2, 3, 4, 5), pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        List<Period> periods = (
                (PeriodLiteralExpression) (
                        (ExpressionStatement) program
                                .getFunctions().get(fun.getName())
                                .getBody()
                                .getStatements()
                                .get(0)
                ).getExpression()
        ).getValue();
        Period periodDay = periods.get(0);
        Period periodMinute = periods.get(1);
        assertEquals(10, periodDay.getDay());
        assertEquals(7, periodMinute.getSecond());
        Date date = ((DateLiteralExpression) (
                (ExpressionStatement) program
                        .getFunctions().get(fun.getName())
                        .getBody()
                        .getStatements()
                        .get(1)
        ).getExpression()).getValue();
        assertTrue(date.isAD());
        assertEquals(2022, date.getYear());
        assertEquals(1, date.getMonth());
        assertEquals(2, date.getDay());
        assertEquals(3, date.getHour());
        assertEquals(4, date.getMinute());
        assertEquals(5, date.getSecond());
    }

    @Test
    void identifierExpressionTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IdentifierToken("name", pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals("name", (
                (IdentifierExpression) (
                        (ExpressionStatement) program
                                .getFunctions().get(fun.getName())
                                .getBody()
                                .getStatements()
                                .get(0)
                ).getExpression()
        ).getName());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    void functionCallExpressionTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals("fun1", (
                (FunctionCallExpression) (
                        (ExpressionStatement) program
                                .getFunctions().get(fun.getName())
                                .getBody()
                                .getStatements()
                                .get(0)
                ).getExpression()
        ).getName());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    void functionCallExpressionMissingClosingParenthesisTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals("fun1", (
                (FunctionCallExpression) (
                        (ExpressionStatement) program
                                .getFunctions().get(fun.getName())
                                .getBody()
                                .getStatements()
                                .get(0)
                ).getExpression()
        ).getName());
        assertEquals(1, eM.getErrors().size());
        assertEquals("Missing closing parenthesis in function call", eM.getErrors().get(0).getMessage());
    }

    @Test
    void memberExpressionTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.MEMBER, pos),
                new IdentifierToken("member1", pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new IdentifierToken("obj", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.MEMBER, pos),
                new IdentifierToken("member2", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals("member1", (
                (IdentifierExpression) (
                        (MemberExpression) (
                                (ExpressionStatement) program
                                        .getFunctions().get(fun.getName())
                                        .getBody()
                                        .getStatements()
                                        .get(0)
                        ).getExpression()
                ).getMember()).getName());
        assertEquals("member2", (
                (FunctionCallExpression) (
                        (MemberExpression) (
                                (ExpressionStatement) program
                                        .getFunctions().get(fun.getName())
                                        .getBody()
                                        .getStatements()
                                        .get(1)
                        ).getExpression()
                ).getMember()).getName());
        assertEquals(0, eM.getErrors().size());

    }

    @Test
    void memberExpressionMissingRightOperandTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IdentifierToken("obj", pos),
                new SimpleToken(TokenType.MEMBER, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("Member access expression is missing right operand", eM.getErrors().get(0).getMessage());
    }

    @Test
    void assignmentTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.MEMBER, pos),
                new IdentifierToken("member1", pos),
                new SimpleToken(TokenType.ASSIGN, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        AssignmentExpression assignmentExpression = (AssignmentExpression) (
                (ExpressionStatement) program
                        .getFunctions().get(fun.getName())
                        .getBody()
                        .getStatements()
                        .get(0)
        ).getExpression();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals("member1", (
                (IdentifierExpression) (
                        (MemberExpression) assignmentExpression.getLeft()
                ).getMember()
        ).getName());
        assertEquals(1, (
                (IntLiteralExpression) assignmentExpression.getRight()
        ).getValue());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    void assignmentMissingRightOperandTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.MEMBER, pos),
                new IdentifierToken("member1", pos),
                new SimpleToken(TokenType.ASSIGN, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("Assignment expression is missing right operand", eM.getErrors().get(0).getMessage());
    }

    @Test
    public void notNegateAssignmentTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.NOT, pos),
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.MEMBER, pos),
                new IdentifierToken("member1", pos),
                new SimpleToken(TokenType.ASSIGN, pos),
                new IntToken(0, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        NegationExpression negationExpression = (NegationExpression) (
                (ExpressionStatement) program
                        .getFunctions().get(fun.getName())
                        .getBody()
                        .getStatements()
                        .get(0)
        ).getExpression();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(AssignmentExpression.class.getName(), negationExpression.getExpression().getClass().getName());
        assertEquals(NegationOperator.NOT, negationExpression.getOperator());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    public void notNegateAssignmentMissingExpressionTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.NOT, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("Expression or value expected after negation", eM.getErrors().get(0).getMessage());
    }

    @Test
    public void multiplicativeExpressionTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.MULTIPLY, pos),
                new SimpleToken(TokenType.NOT, pos),
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.MEMBER, pos),
                new IdentifierToken("member1", pos),
                new SimpleToken(TokenType.ASSIGN, pos),
                new IntToken(0, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        MultiplicativeExpression multiplicativeExpression = (MultiplicativeExpression) (
                (ExpressionStatement) program
                        .getFunctions().get(fun.getName())
                        .getBody()
                        .getStatements()
                        .get(0)
        ).getExpression();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(IntLiteralExpression.class.getName(), multiplicativeExpression.getLeftExpression().getClass().getName());
        assertEquals(NegationExpression.class.getName(), multiplicativeExpression.getRightExpression().getClass().getName());
        assertEquals(MultiplicativeOperator.MULTIPLY, multiplicativeExpression.getOperator());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    public void multiplicativeExpressionChainedTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.MULTIPLY, pos),
                new IntToken(2, pos),
                new SimpleToken(TokenType.DIVIDE, pos),
                new IntToken(3, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        MultiplicativeExpression multiplicativeExpression = (MultiplicativeExpression) (
                (ExpressionStatement) program
                        .getFunctions().get(fun.getName())
                        .getBody()
                        .getStatements()
                        .get(0)
        ).getExpression();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(MultiplicativeExpression.class.getName(), multiplicativeExpression.getLeftExpression().getClass().getName());
        assertEquals(1, ((IntLiteralExpression)
                ((MultiplicativeExpression) multiplicativeExpression.getLeftExpression()).getLeftExpression()).getValue());
        assertEquals(2, ((IntLiteralExpression)
                ((MultiplicativeExpression) multiplicativeExpression.getLeftExpression()).getRightExpression()).getValue());
        assertEquals(3, ((IntLiteralExpression) multiplicativeExpression.getRightExpression()).getValue());
        assertEquals(MultiplicativeOperator.DIVIDE, multiplicativeExpression.getOperator());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    public void multiplicativeExpressionMissingRightOperandTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.MULTIPLY, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("Multiplicative expression is missing right operand", eM.getErrors().get(0).getMessage());
    }

    @Test
    public void additiveExpressionTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.PLUS, pos),
                new IntToken(2, pos),
                new SimpleToken(TokenType.MULTIPLY, pos),
                new IntToken(3, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        AdditiveExpression additiveExpression = (AdditiveExpression) (
                (ExpressionStatement) program
                        .getFunctions().get(fun.getName())
                        .getBody()
                        .getStatements()
                        .get(0)
        ).getExpression();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(IntLiteralExpression.class.getName(), additiveExpression.getLeftExpression().getClass().getName());
        assertEquals(MultiplicativeExpression.class.getName(), additiveExpression.getRightExpression().getClass().getName());
        assertEquals(AdditiveOperator.PLUS, additiveExpression.getOperator());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    public void additiveExpressionChainedTest() throws IOException { //TODO
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.PLUS, pos),
                new IntToken(2, pos),
                new SimpleToken(TokenType.MINUS, pos),
                new IntToken(3, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Program program = parser.parse();
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        AdditiveExpression additiveExpression = (AdditiveExpression) (
                (ExpressionStatement) program
                        .getFunctions().get(fun.getName())
                        .getBody()
                        .getStatements()
                        .get(0)
        ).getExpression();
        assertEquals(AdditiveExpression.class.getName(), additiveExpression.getLeftExpression().getClass().getName());
        assertEquals(1, ((IntLiteralExpression)
                ((AdditiveExpression) additiveExpression.getLeftExpression()).getLeftExpression()).getValue());
        assertEquals(2, ((IntLiteralExpression)
                ((AdditiveExpression) additiveExpression.getLeftExpression()).getRightExpression()).getValue());
        assertEquals(3, ((IntLiteralExpression) additiveExpression.getRightExpression()).getValue());
        assertEquals(AdditiveOperator.MINUS, additiveExpression.getOperator());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    public void additiveExpressionMissingRightOperandTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.PLUS, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("Additive expression is missing right operand", eM.getErrors().get(0).getMessage());
    }

    @Test
    public void comparativeExpressionTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.LESS_THAN, pos),
                new IdentifierToken("b", pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        ComparativeExpression comparativeExpression = (ComparativeExpression) (
                (ExpressionStatement) program
                        .getFunctions().get(fun.getName())
                        .getBody()
                        .getStatements()
                        .get(0)
        ).getExpression();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(IntLiteralExpression.class.getName(), comparativeExpression.getLeftExpression().getClass().getName());
        assertEquals(IdentifierExpression.class.getName(), comparativeExpression.getRightExpression().getClass().getName());
        assertEquals(ComparisonOperator.LESS_THAN, comparativeExpression.getOperator());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    public void comparativeExpressionMissingRightOperandTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.LESS_THAN, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("Comparative expression is missing right operand", eM.getErrors().get(0).getMessage());
    }

    @Test
    public void andExpressionTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.AND, pos),
                new IdentifierToken("b", pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        AndExpression andExpression = (AndExpression) (
                (ExpressionStatement) program
                        .getFunctions().get(fun.getName())
                        .getBody()
                        .getStatements()
                        .get(0)
        ).getExpression();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(IntLiteralExpression.class.getName(), andExpression.getLeftExpression().getClass().getName());
        assertEquals(IdentifierExpression.class.getName(), andExpression.getRightExpression().getClass().getName());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    public void andExpressionMissingRightOperandTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.AND, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("AND expression is missing right operand", eM.getErrors().get(0).getMessage());
    }

    @Test
    public void orExpressionTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.OR, pos),
                new IdentifierToken("b", pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        OrExpression orExpression = (OrExpression) (
                (ExpressionStatement) program
                        .getFunctions().get(fun.getName())
                        .getBody()
                        .getStatements()
                        .get(0)
        ).getExpression();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(IntLiteralExpression.class.getName(), orExpression.getLeftExpression().getClass().getName());
        assertEquals(IdentifierExpression.class.getName(), orExpression.getRightExpression().getClass().getName());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    public void orExpressionMissingRightOperandTest() {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.OR, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        assertThrows(CodeError.class, () -> {
            //noinspection Convert2MethodRef
            parser.parse();
        });
        assertEquals(1, eM.getErrors().size());
        assertEquals("OR expression is missing right operand", eM.getErrors().get(0).getMessage());
    }

    @Test
    public void returnStatementTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.RETURN, pos),
                new IdentifierToken("a", pos),
                new SimpleToken(TokenType.AND, pos),
                new IdentifierToken("b", pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        AndExpression andExpression = (AndExpression) (
                (ReturnStatement) program
                        .getFunctions().get(fun.getName())
                        .getBody()
                        .getStatements()
                        .get(0)
        ).getExpression();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(AndExpression.class.getName(), andExpression.getClass().getName());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    public void ifElseStatementTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.IF, pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IdentifierToken("a", pos),
                new SimpleToken(TokenType.ASSIGN, pos),
                new IdentifierToken("b", pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.ELSE, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IdentifierToken("print", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new IdentifierToken("c", pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        IfStatement statement = (IfStatement) program
                .getFunctions().get(fun.getName())
                .getBody()
                .getStatements()
                .get(0);
        Expression condition = statement.getCondition();
        Expression ifBlockExpression = ((ExpressionStatement) statement.getIfBlock().getStatements().get(0)).getExpression();
        Expression elseBlockExpression = ((ExpressionStatement) statement.getElseBlock().getStatements().get(0)).getExpression();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(AssignmentExpression.class.getName(), ifBlockExpression.getClass().getName());
        assertEquals(FunctionCallExpression.class.getName(), elseBlockExpression.getClass().getName());
        assertEquals(IntLiteralExpression.class.getName(), condition.getClass().getName());
        assertEquals(0, eM.getErrors().size());
    }

    @Test
    public void WhileStatementTest() throws IOException {
        Position pos = new Position(0, 0);
        List<Token> tokens = Arrays.asList(
                new IdentifierToken("fun1", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new SimpleToken(TokenType.WHILE, pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new IntToken(1, pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_L, pos),
                new IdentifierToken("print", pos),
                new SimpleToken(TokenType.PARENTHESIS_L, pos),
                new StringToken("Hello", pos),
                new SimpleToken(TokenType.PARENTHESIS_R, pos),
                new SimpleToken(TokenType.SEMICOLON, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.BLOCK_DELIMITER_R, pos),
                new SimpleToken(TokenType.EOF, pos));
        ErrorManager eM = new ErrorManager();
        Lexer lexer = new MockLexer(tokens);
        Parser parser = new Parser(lexer, eM);
        Block block = new Block(List.of());
        FunctionDef fun = new FunctionDef("fun1", List.of(), block);
        Program program = parser.parse();
        WhileStatement statement = (WhileStatement) program
                .getFunctions().get(fun.getName())
                .getBody()
                .getStatements()
                .get(0);
        Expression condition = statement.getCondition();
        Expression expression = ((ExpressionStatement) statement.getLoopBlock().getStatements().get(0)).getExpression();
        assertEquals(1, program.getFunctions().size());
        assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
        assertEquals(FunctionCallExpression.class.getName(), expression.getClass().getName());
        assertEquals(IntLiteralExpression.class.getName(), condition.getClass().getName());
        assertEquals(0, eM.getErrors().size());
    }
}
