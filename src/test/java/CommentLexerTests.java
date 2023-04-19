import org.example.error.ErrorManager;
import org.example.lexer.CodeLexer;
import org.example.lexer.CommentLexer;
import org.example.source.StringSource;
import org.example.token.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentLexerTests {
    @Test
    void emptySourceTest() throws IOException {
        String code = "";
        List<Token> tokens = new ArrayList<>();
        StringSource source = new StringSource(code);
        ErrorManager eM = new ErrorManager();
        CodeLexer codeLexer = new CodeLexer(source, eM);
        CommentLexer commentLexer = new CommentLexer(codeLexer);
        Token t;
        while ((t = commentLexer.next()).getType() != TokenType.EOF)
            tokens.add(t);
        assertEquals(0, tokens.size());
    }

    @Test
    void sameLineCommentTest() throws IOException {
        String code = "hello#beautiful\nworld";
        List<Token> tokens = new ArrayList<>();
        StringSource source = new StringSource(code);
        ErrorManager eM = new ErrorManager();
        CodeLexer codeLexer = new CodeLexer(source, eM);
        CommentLexer commentLexer = new CommentLexer(codeLexer);
        Token t;
        while ((t = commentLexer.next()).getType() != TokenType.EOF)
            tokens.add(t);
        assertEquals(2, tokens.size());
        assertEquals(TokenType.IDENTIFIER, tokens.get(0).getType());
        assertEquals("hello", ((IdentifierToken) tokens.get(0)).getName());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).getType());
        assertEquals("world", ((IdentifierToken) tokens.get(1)).getName());
    }

    @Test
    void newLineCommentTest() throws IOException {
        String code = "hello\n#beautiful\nworld";
        List<Token> tokens = new ArrayList<>();
        StringSource source = new StringSource(code);
        ErrorManager eM = new ErrorManager();
        CodeLexer codeLexer = new CodeLexer(source, eM);
        CommentLexer commentLexer = new CommentLexer(codeLexer);
        Token t;
        while ((t = commentLexer.next()).getType() != TokenType.EOF)
            tokens.add(t);
        assertEquals(2, tokens.size());
        assertEquals(TokenType.IDENTIFIER, tokens.get(0).getType());
        assertEquals("hello", ((IdentifierToken) tokens.get(0)).getName());
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).getType());
        assertEquals("world", ((IdentifierToken) tokens.get(1)).getName());
    }
}