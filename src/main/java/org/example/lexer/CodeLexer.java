package org.example.lexer;

import org.example.source.ISource;
import org.example.token.*;

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
                TokenType.PARENTHESIS_L, TokenType.PARENTHESIS_R,
                TokenType.STRING_DELIMITER_L, TokenType.STRING_DELIMITER_R);
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
        if (character.equals("=")){
            if ((character = source.nextCharacter()).equals("=")){
                currentToken = new SimpleToken(TokenType.EQUALS);
                character = source.nextCharacter();
                return true;
            }
            currentToken = new SimpleToken(TokenType.ASSIGN);
            return true;
        }
        if (character.equals("!")){
            if ((character = source.nextCharacter()).equals("=")){
                currentToken = new SimpleToken(TokenType.NOT_EQUAL);
                character = source.nextCharacter();
                return true;
            }
            // TODO lexer error
            return false;
        }
        if (character.equals("<")){
            if ((character = source.nextCharacter()).equals("=")){
                currentToken = new SimpleToken(TokenType.LESS_OR_EQUAL_THAN);
                character = source.nextCharacter();
                return true;
            }
            currentToken = new SimpleToken(TokenType.LESS_THAN);
            return true;
        }
        if (character.equals(">")){
            if ((character = source.nextCharacter()).equals("=")){
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
        if (isEndOfKeywordOrIdent(character)){
            return false;
        }
        StringBuilder sB = new StringBuilder(character);
        character = source.nextCharacter();
        List<TokenType> keywordsTokenTypes = Arrays.asList(
                TokenType.AND, TokenType.OR, TokenType.NOT,
                TokenType.IF, TokenType.ELSE, TokenType.WHILE);
        while (!isEndOfKeywordOrIdent(character)){
            sB.append(character);
            character = source.nextCharacter();
        }
        for (TokenType keywordTokenType: keywordsTokenTypes){
            if (sB.toString().equals(keywordTokenType.getKeyword())){
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

    private boolean tryBuildString() {
        return true;
    }

    private void verifyEOL() throws IOException {
        while ((character).isBlank()) {
            if (newlineCharacter != null){
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
                ||tryBuildRelationToken()
//            || tryBuildNumber()
                || tryBuildIdentOrKeyword()
//            || tryBuildComment
//            || tryBuildString()
        ) {
            return currentToken;
        }
        character = source.nextCharacter();
        return new SimpleToken(TokenType.PLUS); //TODO change to lexer unknown token error
    }

}
