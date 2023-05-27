import org.example.error.CodeError;
import org.example.error.ErrorManager;
import org.example.lexer.CodeLexer;
import org.example.lexer.Lexer;
import org.example.parser.Parser;
import org.example.program.*;
import org.example.source.CodeSource;
import org.example.source.Position;
import org.example.types.Date;
import org.example.types.Period;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParserLexerIntegrationTests {
    @Test
    public void justEOFTest() throws IOException {
        String code = "";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Parser parser = new Parser(codeLexer, eM);
            assertEquals(0, parser.parse().getFunctions().size());
        }
    }

    @Test
    void invalidTokenAtTheEndTest() throws IOException {
        String code = "}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("Unexpected token at the end of file (BLOCK_DELIMITER_R)", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    void singleEmptyFunctionTest() throws IOException {
        String code = "fun1() {}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
            assertEquals(1, program.getFunctions().size());
            assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
            assertEquals(0, program.getFunctions().get(fun.getName()).getBody().getStatements().size());
            assertEquals(0, program.getFunctions().get(fun.getName()).getParameters().size());
        }
    }

    @Test
    void functionWithOneParameterTest() throws IOException {
        String code = "fun1(param1) {}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
            String param = "param1";
            assertEquals(1, program.getFunctions().size());
            assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
            assertEquals(0, program.getFunctions().get(fun.getName()).getBody().getStatements().size());
            assertEquals(1, program.getFunctions().get(fun.getName()).getParameters().size());
            assertEquals(param, program.getFunctions().get(fun.getName()).getParameters().get(0));
        }
    }

    @Test
    void functionWithMultipleParameterTest() throws IOException {
        String code = "fun1(param1, param2, param3, param4) {}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
            String param1 = "param1";
            String param2 = "param2";
            String param3 = "param3";
            String param4 = "param4";
            assertEquals(1, program.getFunctions().size());
            assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
            assertEquals(0, program.getFunctions().get(fun.getName()).getBody().getStatements().size());
            assertEquals(4, program.getFunctions().get(fun.getName()).getParameters().size());
            assertEquals(param1, program.getFunctions().get(fun.getName()).getParameters().get(0));
            assertEquals(param2, program.getFunctions().get(fun.getName()).getParameters().get(1));
            assertEquals(param3, program.getFunctions().get(fun.getName()).getParameters().get(2));
            assertEquals(param4, program.getFunctions().get(fun.getName()).getParameters().get(3));
        }
    }

    @Test
    void noParameterBeforeCommaTest() throws IOException {
        String code = "fun1(,param2) {}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("Missing closing parenthesis in function definition", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    void noParameterAfterCommaTest() throws IOException {
        String code = "fun1(param1, ) {}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
            String param1 = "param1";
            assertEquals(1, program.getFunctions().size());
            assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
            assertEquals(0, program.getFunctions().get(fun.getName()).getBody().getStatements().size());
            assertEquals(1, program.getFunctions().get(fun.getName()).getParameters().size());
            assertEquals(param1, program.getFunctions().get(fun.getName()).getParameters().get(0));
        }
    }


    @Test
    void missingBlockTest() throws IOException {
        String code = "fun1()";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("Missing statement block in function definition", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    void missingOpeningParenthesisInFunctionDefinitionTest() throws IOException {
        String code = "fun1) {}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
            assertEquals(1, program.getFunctions().size());
            assertEquals(fun.getName(), program.getFunctions().get(fun.getName()).getName());
            assertEquals(1, eM.getErrors().size());
            assertEquals("Missing opening parenthesis in function definition", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    void missingClosingParenthesisInFunctionDefinitionTest() throws IOException {
        String code = "fun1( {}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("Missing closing parenthesis in function definition", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    void duplicateFunctionNameTest() throws IOException {
        String code = "fun1(){} fun1(){}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("Non-unique function identifier (fun1)", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    void duplicateParameterNameTest() throws IOException {
        String code = "fun1(param1, param1) {}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("Parameter name already exists (param1)", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    void singleExpressionStatement() throws IOException {
        String code = "fun1() {1;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Expression expression = new IntLiteralExpression(1, pos);
            Statement statement = new ExpressionStatement(expression, pos);
            Block block = new Block(List.of(statement), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    void missingSemicolonAfterExpressionStatement() throws IOException {
        String code = "fun1() {1}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Expression expression = new IntLiteralExpression(1, pos);
            Statement statement = new ExpressionStatement(expression, pos);
            Block block = new Block(List.of(statement), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    void simpleLiteralsTest() throws IOException {
        String code = "fun1() {1;2.1;[This is string literal];}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Expression expression = new IntLiteralExpression(1, pos);
            Statement statement = new ExpressionStatement(expression, pos);
            Block block = new Block(List.of(statement), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    void objectLiteralsTest() throws IOException {
        String code = "fun1() {10d 7';2022y:1m:2d:3h:4':5\";}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Expression expression = new IntLiteralExpression(1, pos);
            Statement statement = new ExpressionStatement(expression, pos);
            Block block = new Block(List.of(statement), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void identifierExpressionTest() throws IOException {
        String code = "fun1() {name;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Expression expression = new IntLiteralExpression(1, pos);
            Statement statement = new ExpressionStatement(expression, pos);
            Block block = new Block(List.of(statement), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void functionCallExpressionTest() throws IOException {
        String code = "fun1() {fun1();}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Expression expression = new IntLiteralExpression(1, pos);
            Statement statement = new ExpressionStatement(expression, pos);
            Block block = new Block(List.of(statement), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void functionCallExpressionMissingClosingParenthesisTest() throws IOException {
        String code = "fun1() {fun1(;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Expression expression = new IntLiteralExpression(1, pos);
            Statement statement = new ExpressionStatement(expression, pos);
            Block block = new Block(List.of(statement), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void memberExpressionTest() throws IOException {
        String code = "fun1() {fun1().member1;obj.member2();}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Expression expression = new IntLiteralExpression(1, pos);
            Statement statement = new ExpressionStatement(expression, pos);
            Block block = new Block(List.of(statement), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void memberExpressionMissingRightOperandTest() throws IOException {
        String code = "fun1() {obj.;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("Member access expression is missing right operand", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    public void assignmentTest() throws IOException {
        String code = "fun1() {fun1().member1 = 1;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Expression expression = new IntLiteralExpression(1, pos);
            Statement statement = new ExpressionStatement(expression, pos);
            Block block = new Block(List.of(statement), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void assignmentMissingRightOperandTest() throws IOException {
        String code = "fun1() {fun1().member1 = ;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("Assignment expression is missing right operand", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    public void notNegateAssignmentTest() throws IOException {
        String code = "fun1() {not fun1().member1 = 0;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Expression expression = new IntLiteralExpression(1, pos);
            Statement statement = new ExpressionStatement(expression, pos);
            Block block = new Block(List.of(statement), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void notNegateAssignmentMissingExpressionTest() throws IOException {
        String code = "fun1() {not ;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("Expression or value expected after negation", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    public void multiplicativeExpressionTest() throws IOException {
        String code = "fun1() {1 * not fun1().member1 = 0;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void multiplicativeExpressionChainedTest() throws IOException {
        String code = "fun1() {1 * 2 / 3;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void multiplicativeExpressionMissingRightOperandTest() throws IOException {
        String code = "fun1() {1 * ;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("Multiplicative expression is missing right operand", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    public void additiveExpressionTest() throws IOException {
        String code = "fun1() {1 + 2 * 3;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void additiveExpressionChainedTest() throws IOException {
        String code = "fun1() {1 + 2 - 3 ;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void additiveExpressionMissingRightOperandTest() throws IOException {
        String code = "fun1() {1 +;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("Additive expression is missing right operand", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    public void comparativeExpressionTest() throws IOException {
        String code = "fun1() {1 < b ;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void comparativeExpressionMissingRightOperandTest() throws IOException {
        String code = "fun1() {1 <;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("Comparative expression is missing right operand", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    public void andExpressionTest() throws IOException {
        String code = "fun1() {1 and b ;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void andExpressionMissingRightOperandTest() throws IOException {
        String code = "fun1() {1 and;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("AND expression is missing right operand", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    public void orExpressionTest() throws IOException {
        String code = "fun1() {1 or b;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void orExpressionMissingRightOperandTest() throws IOException {
        String code = "fun1() {1 or;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                parser.parse();
            });
            assertEquals(1, eM.getErrors().size());
            assertEquals("OR expression is missing right operand", eM.getErrors().get(0).getMessage());
        }
    }

    @Test
    public void returnStatementTest() throws IOException {
        String code = "fun1() {return a and b;}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void ifElseStatementTest() throws IOException {
        String code = "fun1() {if (1) {a = b;} else { print(c);}}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
    }

    @Test
    public void WhileStatementTest() throws IOException {
        String code = "fun1() {while (1) {print([Hello]);}}";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            Lexer lexer = new CodeLexer(source, eM);
            Parser parser = new Parser(lexer, eM);
            Program program = parser.parse();
            Position pos = new Position(0, 0);
            Block block = new Block(List.of(), pos);
            FunctionDef fun = new FunctionDef("fun1", List.of(), block, pos);
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
}
