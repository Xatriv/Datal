package org.example.lexer;

import org.example.error.ErrorManager;
import org.example.error.LexerErrorInfo;
import org.example.error.Severity;
import org.example.source.Position;
import org.example.source.Source;
import org.example.token.*;
import org.example.types.Date;
import org.example.types.Period;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.lang.Math;

public class CodeLexer implements Lexer {

    private int character;
    private Token currentToken;
    private final int identifierMaxLength;
    private final int stringLiteralMaxLength;
    private final int commentMaxLength;

    private Position position;

    private final Source source;
    private final ErrorManager errorManager;

    private static final List<TokenType> keywordsTokenTypes = Arrays.asList(
            TokenType.AND, TokenType.OR, TokenType.NOT,
            TokenType.IF, TokenType.ELSE, TokenType.WHILE,
            TokenType.RETURN);

    private static final List<TokenType> singleCharTokenTypes = Arrays.asList(
            TokenType.PLUS, TokenType.MINUS, TokenType.DIVIDE, TokenType.MULTIPLY,
            TokenType.MEMBER, TokenType.SEPARATOR, TokenType.SEMICOLON,
            TokenType.BLOCK_DELIMITER_L, TokenType.BLOCK_DELIMITER_R,
            TokenType.PARENTHESIS_L, TokenType.PARENTHESIS_R);


    public CodeLexer(Source source, ErrorManager errorManager) throws IOException {
        this.source = source;
        this.errorManager = errorManager;
        character = source.nextCharacter();
        Properties props = new Properties();
        int identifierMaxLengthProp = -1;
        int stringLiteralMaxLengthProp = -1;
        int commentMaxLengthProp = -1;
        try {
            InputStream input = new FileInputStream("src/main/java/org/example/lexer/lexer.properties");
            props.load(input);
            identifierMaxLengthProp = readProperty(props, "IDENTIFIER_MAX_LENGTH", -1);
            stringLiteralMaxLengthProp = readProperty(props, "STRING_LITERAL_MAX_LENGTH", -1);
            commentMaxLengthProp = readProperty(props, "COMMENT_MAX_LENGTH", -1);
        } catch (IOException ignored) {
        } finally {
            this.identifierMaxLength = identifierMaxLengthProp;
            this.stringLiteralMaxLength = stringLiteralMaxLengthProp;
            this.commentMaxLength = commentMaxLengthProp;
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


    @SuppressWarnings("unused")
    private void printConfig() {
        System.out.println(this.identifierMaxLength);
        System.out.println(this.stringLiteralMaxLength);
    }

    @Override
    public Token getToken() {
        return currentToken;
    }

    @SuppressWarnings("unused")
    private void setToken(Token token) {
        this.currentToken = token;
    }

    private boolean tryBuildSingleCharToken() throws IOException {
        for (TokenType type : singleCharTokenTypes) {
            if (character == type.getKeyword().charAt(0)) {
                character = source.nextCharacter();
                currentToken = new SimpleToken(type, position);
                return true;
            }
        }
        return false;
    }

    private boolean tryBuildRelationToken() throws IOException {
        if (character == '=') {
            buildExtendableRelationToken(TokenType.EQUALS, TokenType.ASSIGN);
            return true;
        }
        if (character == '!') {
            if ((character = source.nextCharacter()) == '=') {
                currentToken = new SimpleToken(TokenType.NOT_EQUAL, position);
                character = source.nextCharacter();
            } else {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.WARN, position, "Could not build relation operator."));
            }
            return true;
        }
        if (character == '<') {
            buildExtendableRelationToken(TokenType.LESS_OR_EQUAL_THAN, TokenType.LESS_THAN);
            return true;
        }
        if (character == '>') {
            buildExtendableRelationToken(TokenType.MORE_OR_EQUAL_THAN, TokenType.MORE_THAN);
            return true;
        }
        return false;
    }

    private void buildExtendableRelationToken(TokenType extendedToken, TokenType regularToken) throws IOException {
        if ((character = source.nextCharacter()) == '=') {
            currentToken = new SimpleToken(extendedToken, position);
            character = source.nextCharacter();
        } else {
            currentToken = new SimpleToken(regularToken, position);
        }
    }

    private boolean tryBuildNumber() throws IOException {
        if (!characterIsDigit(character)) return false;

        int wholePart = character - '0';
        while (characterIsDigit(character = source.nextCharacter())) {
            int digit = character - '0';
            if ((Integer.MAX_VALUE - digit) / 10 < wholePart) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "Overflow while building integer"));
                return false;
            }
            wholePart = wholePart * 10 + digit;
        }
        if (character == '.') {
            return tryBuildFloat(wholePart);
        }
        if ("yYAB".indexOf(character) != -1) {
            return tryBuildYearPeriodOrDate(wholePart);
        }

        int upperCasedCharacter = Character.toUpperCase(character);
        Period newPeriod = null;
        switch (upperCasedCharacter) {
            case 'M':
                newPeriod = new Period(0, wholePart, 0, 0, 0, 0);
                break;
            case 'D':
                newPeriod = new Period(0, 0, wholePart, 0, 0, 0);
                break;
            case 'H':
                newPeriod = new Period(0, 0, 0, wholePart, 0, 0);
                break;
            case '"':
                newPeriod = new Period(0, 0, 0, 0, wholePart, 0);
                break;
            case '\'':
                newPeriod = new Period(0, 0, 0, 0, 0, wholePart);
                break;
        }
        if (newPeriod != null) {
            character = source.nextCharacter();
            currentToken = new PeriodToken(newPeriod, position);
            return true;
        }
        currentToken = new IntToken(wholePart, position);
        return true;
    }

    private boolean characterIsDigit(int character) {
        return '9' >= character && character >= '0';
    }

    private boolean tryBuildYearPeriodOrDate(int wholePart) throws IOException {
        boolean isEraAC = "yYA".indexOf(character) != -1;
        if ("AB".indexOf(character) != -1 && !((character = source.nextCharacter()) == 'C')) {
            errorManager.reportError(
                    new LexerErrorInfo(Severity.INFO, position, "Unexpected character while building date. Era must be either y, Y, AC or BC "));
            currentToken = new IntToken(wholePart, position);
            return true;
        }
        if (!((character = source.nextCharacter()) == ':')) {
            currentToken = new PeriodToken(new Period(wholePart, 0, 0, 0, 0, 0), position);
            return true;
        }
        character = source.nextCharacter();

        int monthValue;
        if ((monthValue = getDateNumber("month")) < 0) return false;
        if (isTimeUnitSymbolInvalid("mM")) return false;

        int dayValue;
        if ((dayValue = getDateNumber("day")) < 0) return false;
        if (isTimeUnitSymbolInvalid("dD")) return false;

        int hourValue;
        if ((hourValue = getDateNumber("hour")) < 0) return false;
        if (isTimeUnitSymbolInvalid("hH")) return false;

        int minuteValue;
        if ((minuteValue = getDateNumber("minute")) < 0) return false;
        if (isTimeUnitSymbolInvalid("'")) return false;


        int secondValue;
        if ((secondValue = getDateNumber("second")) < 0) return false;
        if (character != '\"') {
            errorManager.reportError(
                    new LexerErrorInfo(Severity.INFO, position, "Unexpected character while building date. Seconds must be followed by \" "));
            currentToken = new DateToken(new Date(isEraAC, wholePart, monthValue,
                    dayValue, hourValue, minuteValue, secondValue), position);
            return true;
        }
        character = source.nextCharacter();
        currentToken = new DateToken(new Date(isEraAC, wholePart, monthValue,
                dayValue, hourValue, minuteValue, secondValue), position);
        return true;
    }

    private boolean tryBuildFloat(int wholePart) throws IOException {
        int fractionPart = 0;
        int decimalDigits = 0;
        character = source.nextCharacter();
        if (!characterIsDigit(character)) {
            errorManager.reportError(
                    new LexerErrorInfo(Severity.WARN, position, "Unexpected character while building double"));
            currentToken = new IntToken(wholePart, position);
            return true;
        }
        while (characterIsDigit(character)) {
            int digit = character - '0';
            if ((Integer.MAX_VALUE - digit) / 10 < fractionPart) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "Overflow while building double"));
                return false;
            }

            fractionPart = fractionPart * 10 + digit;
            decimalDigits++;
            character = source.nextCharacter();
        }
        double result = wholePart + (double) fractionPart / Math.pow(10, decimalDigits);
        currentToken = new DoubleToken(result, position);
        return true;
    }

    private int getDateNumber(String unit) throws IOException {
        if (!characterIsDigit(character)) {
            errorManager.reportError(
                    new LexerErrorInfo(Severity.ERROR, position, String.format("Could not build date. Invalid %s unit", unit)));
            return -1;
        }
        int number = character - '0';
        while (characterIsDigit(character = source.nextCharacter())) {
            int digit = character - '0';
            if ((Integer.MAX_VALUE - digit) / 10 - number < 0) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "Overflow while building date"));
                return -1;
            }
            number = number * 10 + digit;
        }
        return number;
    }

    private boolean isTimeUnitSymbolInvalid(String letters) throws IOException {
        if (letters.indexOf(character) == -1) {
            errorManager.reportError(
                    new LexerErrorInfo(Severity.ERROR, position, "Could not build date. Unrecognized time unit"));
            return true;
        }
        if (!((character = source.nextCharacter()) == ':')) {
            errorManager.reportError(
                    new LexerErrorInfo(Severity.WARN, position, "Missing \":\" separator while building date"));
            return false;
        }
        character = source.nextCharacter();
        return false;
    }

    private boolean tryBuildIdentOrKeyword() throws IOException {
        if (!Character.isAlphabetic(character)) {
            return false;
        }
        StringBuilder sB = new StringBuilder(Character.toString(character));
        character = source.nextCharacter();
        while (characterIsDigit(character) || Character.isAlphabetic(character)) {
            if (0 < identifierMaxLength && identifierMaxLength == sB.length()) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "Identifier too long"));
                return false;
            }
            sB.append(Character.toString(character));
            character = source.nextCharacter();
        }
        for (TokenType keywordTokenType : keywordsTokenTypes) {
            if (sB.toString().equals(keywordTokenType.getKeyword())) {
                currentToken = new SimpleToken(keywordTokenType, position);
                return true;
            }
        }
        currentToken = new IdentifierToken(sB.toString(), position);
        return true;
    }
    private boolean tryBuildComment() throws IOException {
        if (character != '#') return false;
        StringBuilder sB = new StringBuilder();
        character = source.nextCharacter();
        while (character != '\n' && character != source.ETX) {
            if (0 < commentMaxLength && commentMaxLength == sB.length()) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "Comment too long"));
                return false;
            }
            sB.append(Character.toString(character));
            character = source.nextCharacter();
        }
        currentToken = new CommentToken(sB.toString(), position);
        return true;
    }

    private boolean tryBuildString() throws IOException {
        if (character != '[') return false;
        StringBuilder sB = new StringBuilder();
        character = source.nextCharacter();
        while (character != ']') {
            if (character == source.ETX){
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "String unmatched"));
                return false;
            }
            if (0 < stringLiteralMaxLength && stringLiteralMaxLength == sB.length()) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "String too long"));
                return false;
            }
            sB.append(Character.toString(tryProcessEscapeCharacter()));
            character = source.nextCharacter();
        }
        currentToken = new StringToken(sB.toString(), position);
        character = source.nextCharacter();
        return true;
    }

    private int tryProcessEscapeCharacter() throws IOException {
        if (character == '\\') {
            character = source.nextCharacter();
            switch (character) {
                case ']':
                    return ']';
                case '\\':
                    return '\\';
                case 'b':
                    return '\b';
                case 'f':
                    return '\f';
                case 'n':
                    return '\n';
                case 'r':
                    return '\r';
                case 't':
                    return '\t';
                default:
                    // includes string literal delimiter
                    errorManager.reportError(
                            new LexerErrorInfo(Severity.WARN, position, "Unrecognized escape sequence"));
                    return character;
            }
        }
        return character;
    }

    @Override
    public Token next() throws IOException {
        while (Character.isWhitespace(character)) {
            character = source.nextCharacter();
        }
        position = new Position(source.getPosition());
        if (character == source.ETX) {
            return new SimpleToken(TokenType.EOF, position);
        }
        if (tryBuildSingleCharToken()
                || tryBuildRelationToken()
                || tryBuildNumber()
                || tryBuildIdentOrKeyword()
                || tryBuildComment()
                || tryBuildString()
        ) {
            return currentToken;
        }
        character = source.nextCharacter();
        errorManager.reportError(
                new LexerErrorInfo(Severity.WARN, position, "Unrecognized token occurred"));

        return new SimpleToken(TokenType.UNKNOWN, position);
    }
}
