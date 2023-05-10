import org.example.error.CodeError;
import org.example.error.ErrorManager;
import org.example.error.Severity;
import org.example.lexer.CodeLexer;
import org.example.source.CodeSource;
import org.example.token.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTests {
    @Test
    void emptySourceTest() throws IOException {
        String code = "";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
        }
        assertEquals(0, tokens.size());
    }

    @Test
    void buildSimpleTokenTest() throws IOException {
        String code = "*/+-(){};";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
        }
        assertEquals(9, tokens.size());
        assertNotEquals(TokenType.PLUS, tokens.get(0).getType());
        assertEquals(TokenType.MULTIPLY, tokens.get(0).getType());
        assertEquals(TokenType.DIVIDE, tokens.get(1).getType());
        assertEquals(TokenType.PLUS, tokens.get(2).getType());
        assertEquals(TokenType.MINUS, tokens.get(3).getType());
        assertEquals(TokenType.PARENTHESIS_L, tokens.get(4).getType());
        assertEquals(TokenType.PARENTHESIS_R, tokens.get(5).getType());
        assertEquals(TokenType.BLOCK_DELIMITER_L, tokens.get(6).getType());
        assertEquals(TokenType.BLOCK_DELIMITER_R, tokens.get(7).getType());
        assertEquals(TokenType.SEMICOLON, tokens.get(8).getType());
    }

    @Test
    void buildRelationTokenTest() throws IOException {
        String code = ">=>>=<= <=;<=< !======";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
        }
        assertEquals(12, tokens.size());
        assertNotEquals(TokenType.MORE_THAN, tokens.get(0).getType());
        assertEquals(TokenType.MORE_OR_EQUAL_THAN, tokens.get(0).getType());
        assertEquals(TokenType.MORE_THAN, tokens.get(1).getType());
        assertEquals(TokenType.MORE_OR_EQUAL_THAN, tokens.get(2).getType());
        assertEquals(TokenType.LESS_OR_EQUAL_THAN, tokens.get(3).getType());
        assertEquals(TokenType.LESS_OR_EQUAL_THAN, tokens.get(4).getType());
        assertEquals(TokenType.SEMICOLON, tokens.get(5).getType());
        assertEquals(TokenType.LESS_OR_EQUAL_THAN, tokens.get(6).getType());
        assertEquals(TokenType.LESS_THAN, tokens.get(7).getType());
        assertEquals(TokenType.NOT_EQUAL, tokens.get(8).getType());
        assertEquals(TokenType.EQUALS, tokens.get(9).getType());
        assertEquals(TokenType.EQUALS, tokens.get(10).getType());
        assertEquals(TokenType.ASSIGN, tokens.get(11).getType());
    }

    @Test
    void buildKeywordOrIdentifierTest() throws IOException {
        String code = "and id or orornot not knot if IF else Else while return";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
        }
        assertEquals(12, tokens.size());
        assertEquals(TokenType.AND, tokens.get(0).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).getType());
        assertEquals("id", ((IdentifierToken) tokens.get(1)).getName());
        assertEquals(TokenType.OR, tokens.get(2).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(3).getType());
        assertEquals("orornot", ((IdentifierToken) tokens.get(3)).getName());
        assertEquals(TokenType.NOT, tokens.get(4).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(5).getType());
        assertEquals("knot", ((IdentifierToken) tokens.get(5)).getName());
        assertEquals(TokenType.IF, tokens.get(6).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(7).getType());
        assertEquals("IF", ((IdentifierToken) tokens.get(7)).getName());
        assertEquals(TokenType.ELSE, tokens.get(8).getType());
        assertEquals(TokenType.IDENTIFIER, tokens.get(9).getType());
        assertEquals("Else", ((IdentifierToken) tokens.get(9)).getName());
        assertEquals(TokenType.WHILE, tokens.get(10).getType());
        assertEquals(TokenType.RETURN, tokens.get(11).getType());
    }

    @Test
    void buildNumber() throws IOException {
        String code = "0 00 001 0.12345;1.2345;12d; 10d 2023Y:1M:1D:0H:0':0\"";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
        }
        assertEquals(11, tokens.size());
        assertEquals(TokenType.INT, tokens.get(0).getType());
        assertEquals(TokenType.INT, tokens.get(1).getType());
        assertEquals(TokenType.INT, tokens.get(2).getType());
        assertEquals(TokenType.DOUBLE, tokens.get(3).getType());
        assertEquals(TokenType.SEMICOLON, tokens.get(4).getType());
        assertEquals(TokenType.DOUBLE, tokens.get(5).getType());
        assertEquals(TokenType.SEMICOLON, tokens.get(6).getType());
        assertEquals(TokenType.PERIOD, tokens.get(7).getType());
        assertEquals(TokenType.SEMICOLON, tokens.get(8).getType());
        assertEquals(TokenType.PERIOD, tokens.get(9).getType());
        assertEquals(TokenType.DATE, tokens.get(10).getType());
    }

    @Test
    void buildString() throws IOException {
        String code = "[hello];[\\t\\n\\r\\]ABC\\\\D]";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
        }
        assertEquals(3, tokens.size());
        assertEquals(TokenType.STRING, tokens.get(0).getType());
        assertEquals("hello", ((StringToken) tokens.get(0)).getValue());
        assertEquals(TokenType.SEMICOLON, tokens.get(1).getType());
        assertEquals(TokenType.STRING, tokens.get(2).getType());
        assertEquals("\t\n\r]ABC\\D", ((StringToken) tokens.get(2)).getValue());
    }

    @Test
    void buildStringWithUTF32() throws IOException {
        String code = "[\uD83D\uDE0B葛葛葛葛葛葛葛葛\uD83D\uDC7E]";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
        }
        assertEquals(1, tokens.size());
        assertEquals(TokenType.STRING, tokens.get(0).getType());
        assertEquals("\uD83D\uDE0B葛葛葛葛葛葛葛葛\uD83D\uDC7E", ((StringToken) tokens.get(0)).getValue());
    }

    @Test
    void errorDateMissingSecondUnitSymbol() throws IOException {
        String code = "2023Y:1M:1D:0H:0':0"; //note lack of \"
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
            assertEquals(1, tokens.size());
            assertEquals(TokenType.DATE, tokens.get(0).getType());
            assertEquals(eM.getErrors().size(), 1);
            assertEquals(eM.getErrors().get(0).getSeverity(), Severity.WARN);
            assertEquals(eM.getErrors().get(0).getMessage(), "Unexpected character while building date. Seconds must be followed by \" ");
        }
    }


    @Test
    void buildComment() throws IOException {
        String code = "hello#thisIsComment */return\nreturn";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
        }
        assertEquals(3, tokens.size());
        assertEquals(TokenType.IDENTIFIER, tokens.get(0).getType());
        assertEquals("hello", ((IdentifierToken) tokens.get(0)).getName());
        assertEquals(TokenType.COMMENT, tokens.get(1).getType());
        assertEquals("thisIsComment */return", ((CommentToken) tokens.get(1)).getValue());
        assertEquals(TokenType.RETURN, tokens.get(2).getType());
    }

    @Test
    public void errorIntOverflowTest() throws IOException {
        String code = "123456789101112";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            assertThrows(CodeError.class, () -> {
                //noinspection Convert2MethodRef
                codeLexer.next();
            });
            assertEquals(1, eM.getErrors().size());
        }
    }

    @Test
    public void errorDoubleOverflowTest() throws IOException {
        String code = "0.123456789101112";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            assertThrows(CodeError.class, codeLexer::next);
            assertEquals(1, eM.getErrors().size());
        }
    }

    @Test
    void errorMismatchedString() throws IOException {
        String code = "[hello";
        try (Reader sR = new StringReader(code)) {
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            assertThrows(CodeError.class, codeLexer::next);
            assertEquals(1, eM.getErrors().size());
            assertEquals(eM.getErrors().get(0).getMessage(), "String unmatched");
            assertEquals(eM.getErrors().get(0).getSeverity(), Severity.ERROR);
        }
    }

    @Test
    public void positionEmptyFile() throws IOException {
        String code = "";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)){
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
            assertEquals(0, tokens.size());
            assertEquals(TokenType.EOF, t.getType());
            assertEquals(1, t.getPosition().getColumn());
            assertEquals(1, t.getPosition().getLine());
        }
    }

    @Test
    public void positionSingleToken() throws IOException {
        String code = "return";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)){
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
            tokens.add(t); //EOF
            assertEquals(2, tokens.size());
            assertEquals(1, tokens.get(0).getPosition().getColumn());
            assertEquals(1, tokens.get(0).getPosition().getLine());
            assertEquals(7, tokens.get(1).getPosition().getColumn());
            assertEquals(1, tokens.get(1).getPosition().getLine());
        }
    }

    @Test
    public void positionJustNewline() throws IOException {
        String code = "\n";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)){
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
            tokens.add(t); //EOF
            assertEquals(1, tokens.size());
            assertEquals(1, tokens.get(0).getPosition().getColumn());
            assertEquals(2, tokens.get(0).getPosition().getLine());
        }
    }

    @Test
    public void positionValidNewlineNTest() throws IOException {
        String code = "return\nvariable\nelse";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)){
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
            tokens.add(t); //EOF
            assertEquals(4, tokens.size());
            assertEquals(1, tokens.get(0).getPosition().getColumn());
            assertEquals(1, tokens.get(0).getPosition().getLine());
            assertEquals(1, tokens.get(1).getPosition().getColumn());
            assertEquals(2, tokens.get(1).getPosition().getLine());
            assertEquals(1, tokens.get(2).getPosition().getColumn());
            assertEquals(3, tokens.get(2).getPosition().getLine());
            assertEquals(5, tokens.get(3).getPosition().getColumn());
            assertEquals(3, tokens.get(3).getPosition().getLine());
        }
    }

    @Test
    public void positionValidNewlineNRTest() throws IOException {
        String code = "return\n\rvariable\n\relse";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)){
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
            tokens.add(t); //EOF
            assertEquals(4, tokens.size());
            assertEquals(1, tokens.get(0).getPosition().getColumn());
            assertEquals(1, tokens.get(0).getPosition().getLine());
            assertEquals(1, tokens.get(1).getPosition().getColumn());
            assertEquals(2, tokens.get(1).getPosition().getLine());
            assertEquals(1, tokens.get(2).getPosition().getColumn());
            assertEquals(3, tokens.get(2).getPosition().getLine());
            assertEquals(5, tokens.get(3).getPosition().getColumn());
            assertEquals(3, tokens.get(3).getPosition().getLine());
        }
    }

    @Test
    public void positionValidNewlineRNTest() throws IOException {
        String code = "return\r\nvariable\r\nelse";
        List<Token> tokens = new ArrayList<>();
        try (Reader sR = new StringReader(code)){
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            Token t;
            while ((t = codeLexer.next()).getType() != TokenType.EOF)
                tokens.add(t);
            tokens.add(t); //EOF
            assertEquals(4, tokens.size());
            assertEquals(1, tokens.get(0).getPosition().getColumn());
            assertEquals(1, tokens.get(0).getPosition().getLine());
            assertEquals(1, tokens.get(1).getPosition().getColumn());
            assertEquals(2, tokens.get(1).getPosition().getLine());
            assertEquals(1, tokens.get(2).getPosition().getColumn());
            assertEquals(3, tokens.get(2).getPosition().getLine());
            assertEquals(5, tokens.get(3).getPosition().getColumn());
            assertEquals(3, tokens.get(3).getPosition().getLine());
        }
    }



    @Test
    public void invalidNewlineAfterNTest() throws IOException {
        String codeN_NR = "something\nxd;;\n\r;";
        try (Reader sR = new StringReader(codeN_NR)){
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            //noinspection StatementWithEmptyBody
            while ( (codeLexer.next()).getType() != TokenType.SEMICOLON);
            assertThrows(CodeError.class, codeLexer::next);
        }

        String code = "something\nxd;;\r\n;";
        try (Reader sR = new StringReader(code)){
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            //noinspection StatementWithEmptyBody
            while ( (codeLexer.next()).getType() != TokenType.SEMICOLON);
            assertThrows(CodeError.class, codeLexer::next);
        }
    }

    @Test
    public void invalidNewlineAfterNRTest() throws IOException {
        String codeN_NR = "something\n\rxd;;\n;";
        try (Reader sR = new StringReader(codeN_NR)){
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            //noinspection StatementWithEmptyBody
            while ( (codeLexer.next()).getType() != TokenType.SEMICOLON);
            assertThrows(CodeError.class, codeLexer::next);
        }

        String code = "something\n\rxd;;\r\n;";
        try (Reader sR = new StringReader(code)){
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            //noinspection StatementWithEmptyBody
            while ( (codeLexer.next()).getType() != TokenType.SEMICOLON);
            assertThrows(CodeError.class, codeLexer::next);
        }
    }

    @Test
    public void invalidNewlineAfterRNTest() throws IOException {
        String codeN_NR = "something\r\nxd;;\n;";
        try (Reader sR = new StringReader(codeN_NR)){
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            //noinspection StatementWithEmptyBody
            while ( (codeLexer.next()).getType() != TokenType.SEMICOLON);
            assertThrows(CodeError.class, codeLexer::next);
        }

        String code = "something\r\nxd;;\n\r;";
        try (Reader sR = new StringReader(code)){
            ErrorManager eM = new ErrorManager();
            CodeSource source = new CodeSource(sR, eM);
            CodeLexer codeLexer = new CodeLexer(source, eM);
            //noinspection StatementWithEmptyBody
            while ( (codeLexer.next()).getType() != TokenType.SEMICOLON);
            assertThrows(CodeError.class, codeLexer::next);
        }
    }
}
