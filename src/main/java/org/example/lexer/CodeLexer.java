package org.example.lexer;

import org.example.source.ISource;
import org.example.token.DoubleToken;
import org.example.token.SimpleToken;
import org.example.token.Token;
import org.example.token.TokenType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CodeLexer implements Lexer {

    private String character; //maybe unnecessary
    private Token currentToken;
    private String newlineCharacter;
    private final int identifierMaxLength;
    private final int stringLiteralMaxLength;

    private final ISource source;


    public CodeLexer(ISource source) throws IOException {
        this.source = source;
        Properties props = new Properties();
        int identifierMaxLengthProp = -1;
        int stringLiteralMaxLengthProp = -1;
        try {
            InputStream input = new FileInputStream("src/main/java/org/example/lexer/lexer.properties");
            props.load(input);
            identifierMaxLengthProp = readProperty(props, "IDENTIFIER_MAX_LENGTH", -1);
            stringLiteralMaxLengthProp = readProperty(props, "STRING_LITERAL_MAX_LENGTH", -1);
        } catch (IOException ignored) {
        } finally {
            this.identifierMaxLength = identifierMaxLengthProp;
            this.stringLiteralMaxLength = stringLiteralMaxLengthProp;
        }
    }

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


    private void printConfig() {
        System.out.println(this.identifierMaxLength);
        System.out.println(this.stringLiteralMaxLength);
    }

    @Override
    public Token getToken() {
        return null;
    }

    private void setToken(Token token) {
        this.currentToken = token;
    }

    private boolean tryBuildOperator() {
        List<TokenType> singleCharTokenTypes = Arrays.asList(
                TokenType.PLUS, TokenType.MINUS, TokenType.DIVIDE, TokenType.MULTIPLY,
                TokenType.MEMBER, TokenType.SEPARATOR, TokenType.SEMICOLON,
                TokenType.BLOCK_DELIMITER_L, TokenType.BLOCK_DELIMITER_R,
                TokenType.PARENTHESIS_L, TokenType.PARENTHESIS_R,
                TokenType.STRING_DELIMITER_L, TokenType.STRING_DELIMITER_R);
        for (TokenType type : singleCharTokenTypes) {
            if (character.equals(type.getKeyword())) {
                currentToken = new SimpleToken(type);
                return true;
            }
        }
        return false;
    }

    private boolean tryBuildNumber() throws IOException {
        if (!Character.isDigit(character.charAt(0))) return false;

        int wholePart = character.charAt(0) - '0';
        int decimalDigits = 0;
        while (Character.isDigit(character.charAt(0))) {
            character = source.nextCharacter();
            int digit = character.charAt(0) - '0';
            if ((Integer.MAX_VALUE - digit) / 10 - wholePart > 0) {
                wholePart = wholePart * 10 + digit;
            } else {
                //report error
                return false;
            }
        }
        if (character.equals(".")) { //TODO maybe extract somewhere?
            //TODO check for overflow
            int fractionPart = 0;
            character = source.nextCharacter(); //TODO handle parsing errors (everything other than int.int)
            while (Character.isDigit(character.charAt(0))) {
                fractionPart += Integer.parseInt(character);
                decimalDigits += 1;
            }
            double result = wholePart + (double) fractionPart / decimalDigits;
            currentToken = new DoubleToken(result);
            return true;
        } else {
            return tryBuildDateOrPeriod(wholePart);
        }
    }

    private boolean tryBuildDateOrPeriod(int beginning) {

        if (character.equals("\"")) {
//            currentToken = ;
            return true; //TODO finish
        }
        return false; //TODO finish
    }

    private boolean tryBuildIdentOrKeyword() throws IOException {
        character = source.nextCharacter();
        if (character.isBlank() || source.nextCharacter().matches("[+\\-*/.\\\\!:;'\"()\\[\\]{}<>=]")) {
            return false; // TODO return identifier
        }
        return true;
    }

    private boolean tryBuildString() {
        return true;
    }

    private void verifyEOL() throws IOException {
        while ((character = source.nextCharacter()).isBlank()) {
            if (newlineCharacter != null)
                continue;
            if (character.equals("\n")) {
                character = source.nextCharacter();
                newlineCharacter = character.equals("\r") ? "\n\r" : "\n";
            } else if (character.equals("\r")) {
                character = source.nextCharacter();
                if (character.equals("\n")) {
                    newlineCharacter = "\r\n";
                }
            }
        }
    }

    @Override
    public Token next() throws IOException {
        verifyEOL();
        if (character.equals(TokenType.EOF.getKeyword())) {
            return new SimpleToken(TokenType.EOF);
        }
        System.out.println(character);
        if (tryBuildOperator()) {
            return currentToken;
        }
        return new SimpleToken(TokenType.PLUS);
    }
}
