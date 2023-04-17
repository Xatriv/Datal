package org.example.lexer;

import org.example.source.ISource;
import org.example.token.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.lang.Math;

public class CodeLexer implements Lexer {

    private String character; //maybe unnecessary
    private Token currentToken;
    private String newlineCharacter;
    private final int identifierMaxLength;
    private final int stringLiteralMaxLength;

    private final ISource source;


    public CodeLexer(ISource source) throws IOException {
        this.source = source;
        character = source.nextCharacter();
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

    private boolean tryBuildSingleCharToken() throws IOException {
        List<TokenType> singleCharTokenTypes = Arrays.asList(
                TokenType.PLUS, TokenType.MINUS, TokenType.DIVIDE, TokenType.MULTIPLY,
                TokenType.MEMBER, TokenType.SEPARATOR, TokenType.SEMICOLON,
                TokenType.BLOCK_DELIMITER_L, TokenType.BLOCK_DELIMITER_R,
                TokenType.PARENTHESIS_L, TokenType.PARENTHESIS_R);
        for (TokenType type : singleCharTokenTypes) {
            if (character.equals(type.getKeyword())) {
                character = source.nextCharacter();
                currentToken = new SimpleToken(type);
                return true;
            }
        }
        return false;
    }

    private boolean tryBuildRelationToken() throws IOException {
        if (character.equals("=")) {
            if ((character = source.nextCharacter()).equals("=")) {
                currentToken = new SimpleToken(TokenType.EQUALS);
                character = source.nextCharacter();
                return true;
            }
            currentToken = new SimpleToken(TokenType.ASSIGN);
            return true;
        }
        if (character.equals("!")) {
            if ((character = source.nextCharacter()).equals("=")) {
                currentToken = new SimpleToken(TokenType.NOT_EQUAL);
                character = source.nextCharacter();
                return true;
            }
            // TODO lexer error
            return false;
        }
        if (character.equals("<")) {
            if ((character = source.nextCharacter()).equals("=")) {
                currentToken = new SimpleToken(TokenType.LESS_OR_EQUAL_THAN);
                character = source.nextCharacter();
                return true;
            }
            currentToken = new SimpleToken(TokenType.LESS_THAN);
            return true;
        }
        if (character.equals(">")) {
            if ((character = source.nextCharacter()).equals("=")) {
                currentToken = new SimpleToken(TokenType.MORE_OR_EQUAL_THAN);
                character = source.nextCharacter();
                return true;
            }
            currentToken = new SimpleToken(TokenType.MORE_THAN);
            return true;
        }
        return false;
    }

    private boolean tryBuildNumber() throws IOException {
        if (!charIsDigit(character)) return false;

        int wholePart = character.charAt(0) - '0';
        character = source.nextCharacter();
        while (charIsDigit(character)) {
            int digit = character.charAt(0) - '0';
            if ((Integer.MAX_VALUE - digit) / 10 - wholePart < 0) {
                wholePart = wholePart * 10 + digit;
                character = source.nextCharacter();
            } else {
                //TODO report lexer error - overflow
                return false;
            }
        }
        if (character.equals(".")) { //TODO maybe extract somewhere?
            //TODO check for overflow
            int fractionPart = 0;
            int decimalDigits = 0;
            character = source.nextCharacter(); //TODO handle parsing errors (everything other than int.int)
            while (charIsDigit(character)) {
                fractionPart = fractionPart * 10 + character.charAt(0) - '0';
                decimalDigits++;
                character = source.nextCharacter();
            }
            double result = wholePart + (double) fractionPart / Math.pow(10, decimalDigits);
            currentToken = new DoubleToken(result);
            return true;
        }
        // TODO return tryBuildDateOrPeriod(wholePart);
        else {
            currentToken = new IntToken(wholePart);
            return true;
        }
    }

    private boolean charIsDigit(String ch) {
        return ch.charAt(0) >= '0' && ch.charAt(0) <= '9';
    }

    private boolean tryBuildDateOrPeriod(int beginning) {

        if (character.equals("\"")) {
//            currentToken = ;
            return true; //TODO finish
        }
        return false; //TODO finish
    }

    private boolean tryBuildIdentOrKeyword() throws IOException {
        if (isEndOfKeywordOrIdent(character)) {
            return false;
        }
        StringBuilder sB = new StringBuilder(character);
        character = source.nextCharacter();
        List<TokenType> keywordsTokenTypes = Arrays.asList(
                TokenType.AND, TokenType.OR, TokenType.NOT,
                TokenType.IF, TokenType.ELSE, TokenType.WHILE,
                TokenType.RETURN);
        while (!isEndOfKeywordOrIdent(character)) {
            sB.append(character);
            character = source.nextCharacter();
        }
        for (TokenType keywordTokenType : keywordsTokenTypes) {
            if (sB.toString().equals(keywordTokenType.getKeyword())) {
                currentToken = new SimpleToken(keywordTokenType);
                return true;
            }
        }
        currentToken = new IdentifierToken(sB.toString());
        return true;
    }

    private boolean isEndOfKeywordOrIdent(String ch) {
        //TODO include limit from config
        return ch.isBlank() || ch.matches("[+\\-*/.\\\\!:;'\"()\\[\\]{}<>=#]");
    }

    private boolean tryBuildString() throws IOException {
        if (!character.equals("[")) return false;
        StringBuilder sB = new StringBuilder();
        character = source.nextCharacter();
        while (!character.equals("]")) {
            if (false /* TODO out of config range*/) return false;
            if (character.equals("\\")) {
                character = source.nextCharacter();
                switch (character) {
                    case "\\":
                        sB.append("\\");
                        break;
                    case "b":
                        sB.append("\b");
                        break;
                    case "f":
                        sB.append("\f");
                        break;
                    case "n":
                        sB.append("\n");
                        break;
                    case "r":
                        sB.append("\r");
                        break;
                    case "t":
                        sB.append("\t");
                        break;
                    default:
                        // includes string literal delimiter
                        sB.append(character);
                }
                character = source.nextCharacter();
            } else {
                sB.append(character);
                character = source.nextCharacter();
            }
        }
        currentToken = new StringToken(sB.toString());
        return true; // TODO lexer error
    }

    private void verifyEOL() throws IOException {
        while ((character).isBlank()) {
            if (newlineCharacter != null) {
                // TODO increment line (maybe not here but in source)
                character = source.nextCharacter();
                continue;
            }
            if (character.equals("\n")) {
                character = source.nextCharacter();
                newlineCharacter = character.equals("\r") ? "\n\r" : "\n";
            } else if (character.equals("\r")) {
                character = source.nextCharacter();
                if (character.equals("\n")) {
                    newlineCharacter = "\r\n";
                }
            } else {
                character = source.nextCharacter();
            }
        }
    }

    @Override
    public Token next() throws IOException {
        verifyEOL();
        if (character.equals(TokenType.EOF.getKeyword())) {
            return new SimpleToken(TokenType.EOF);
        }
        if (tryBuildSingleCharToken()
                || tryBuildRelationToken()
                || tryBuildNumber()
                || tryBuildIdentOrKeyword()
//            || tryBuildComment
                || tryBuildString()
        ) {
            return currentToken;
        }
        character = source.nextCharacter();
        return new SimpleToken(TokenType.PLUS); //TODO change to lexer unknown token error
    }

}
