import org.example.lexer.CodeLexer;
import org.example.source.FileSource;
import org.example.source.StringSource;
import org.example.token.IdentifierToken;
import org.example.token.StringToken;
import org.example.token.Token;
import org.example.token.TokenType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTests {
    @Test
    void emptySourceTest() throws IOException {
        String code = "";
        List<Token> tokens = new ArrayList<>();
        StringSource source = new StringSource(code);
        CodeLexer codeLexer = new CodeLexer(source);
        Token t;
        while ((t = codeLexer.next()).getType() != TokenType.EOF)
            tokens.add(t);
        assertEquals(0, tokens.size());
    }

    @Test
    void buildSimpleTokenTest() throws IOException {
        String code = "*/+-(){};";
        List<Token> tokens = new ArrayList<>();
        StringSource source = new StringSource(code);
        CodeLexer codeLexer = new CodeLexer(source);
        Token t;
        while ((t = codeLexer.next()).getType() != TokenType.EOF)
            tokens.add(t);
        assertEquals(9, tokens.size());
        assertNotEquals(TokenType.PLUS,tokens.get(0).getType());
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
        String code = ">=>>=<= <=;<=< !=!======";
        List<Token> tokens = new ArrayList<>();
        StringSource source = new StringSource(code);
        CodeLexer codeLexer = new CodeLexer(source);
        Token t;
        while ((t = codeLexer.next()).getType() != TokenType.EOF)
            tokens.add(t);
        assertEquals(13, tokens.size());
        assertNotEquals(TokenType.MORE_THAN,tokens.get(0).getType());
        assertEquals(TokenType.MORE_OR_EQUAL_THAN, tokens.get(0).getType());
        assertEquals(TokenType.MORE_THAN, tokens.get(1).getType());
        assertEquals(TokenType.MORE_OR_EQUAL_THAN, tokens.get(2).getType());
        assertEquals(TokenType.LESS_OR_EQUAL_THAN, tokens.get(3).getType());
        assertEquals(TokenType.LESS_OR_EQUAL_THAN, tokens.get(4).getType());
        assertEquals(TokenType.SEMICOLON, tokens.get(5).getType());
        assertEquals(TokenType.LESS_OR_EQUAL_THAN, tokens.get(6).getType());
        assertEquals(TokenType.LESS_THAN, tokens.get(7).getType());
        assertEquals(TokenType.NOT_EQUAL, tokens.get(8).getType());
        assertEquals(TokenType.NOT_EQUAL, tokens.get(9).getType());
        assertEquals(TokenType.EQUALS, tokens.get(10).getType());
        assertEquals(TokenType.EQUALS, tokens.get(11).getType());
        assertEquals(TokenType.ASSIGN, tokens.get(12).getType());
    }

    @Test
    void buildKeywordOrIdentifierTest() throws IOException {
        String code = "and id or orornot not knot if IF else Else while return";
        List<Token> tokens = new ArrayList<>();
        StringSource source = new StringSource(code);
        CodeLexer codeLexer = new CodeLexer(source);
        Token t;
        while ((t = codeLexer.next()).getType() != TokenType.EOF)
            tokens.add(t);
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
        StringSource source = new StringSource(code);
        CodeLexer codeLexer = new CodeLexer(source);
        Token t;
        while ((t = codeLexer.next()).getType() != TokenType.EOF)
            tokens.add(t);
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
        String code = "[hello];[\n\r\\]\\\\A]";
        List<Token> tokens = new ArrayList<>();
        StringSource source = new StringSource(code);
        CodeLexer codeLexer = new CodeLexer(source);
        Token t;
        while ((t = codeLexer.next()).getType() != TokenType.EOF)
            tokens.add(t);
        assertEquals(3, tokens.size());
        assertEquals(TokenType.STRING, tokens.get(0).getType());
        assertEquals("hello", ((StringToken) tokens.get(0)).getValue());
        assertEquals(TokenType.SEMICOLON, tokens.get(1).getType());
        assertEquals(TokenType.STRING, tokens.get(2).getType());
        assertEquals("\n\r]\\A", ((StringToken) tokens.get(2)).getValue());
    }
}
