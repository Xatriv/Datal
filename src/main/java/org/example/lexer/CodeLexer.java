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

    private String character;
    private Token currentToken;
    private final int identifierMaxLength;
    private final int stringLiteralMaxLength;
    private final int commentMaxLength;

    private Position position;

    private final Source source;
    private final ErrorManager errorManager;


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
                currentToken = new SimpleToken(type, position);
                return true;
            }
        }
        return false;
    }

    private boolean tryBuildRelationToken() throws IOException {
        if (character.equals("=")) {
            if ((character = source.nextCharacter()).equals("=")) {
                currentToken = new SimpleToken(TokenType.EQUALS, position);
                character = source.nextCharacter();
                return true;
            }
            currentToken = new SimpleToken(TokenType.ASSIGN, position);
            return true;
        }
        if (character.equals("!")) {
            if ((character = source.nextCharacter()).equals("=")) {
                currentToken = new SimpleToken(TokenType.NOT_EQUAL, position);
                character = source.nextCharacter();
            } else {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.WARN, position, "Could not build relation operator."));
            }
            return true;
        }
        if (character.equals("<")) {
            if ((character = source.nextCharacter()).equals("=")) {
                currentToken = new SimpleToken(TokenType.LESS_OR_EQUAL_THAN, position);
                character = source.nextCharacter();
                return true;
            }
            currentToken = new SimpleToken(TokenType.LESS_THAN, position);
            return true;
        }
        if (character.equals(">")) {
            if ((character = source.nextCharacter()).equals("=")) {
                currentToken = new SimpleToken(TokenType.MORE_OR_EQUAL_THAN, position);
                character = source.nextCharacter();
                return true;
            }
            currentToken = new SimpleToken(TokenType.MORE_THAN, position);
            return true;
        }
        return false;
    }

    private boolean tryBuildNumber() throws IOException {
        if (!charIsDigit(character)) return false;

        int wholePart = character.charAt(0) - '0';
        while (charIsDigit(character = source.nextCharacter())) {
            int digit = character.charAt(0) - '0';
            if ((Integer.MAX_VALUE - digit) / 10 - wholePart < 0) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "Overflow while building integer"));
                return false;
            }
            wholePart = wholePart * 10 + digit;
        }
        if (character.equals(".")) {
            int fractionPart = 0;
            int decimalDigits = 0;
            character = source.nextCharacter();
            if (!charIsDigit(character)) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.INFO, position, "Unexpected character while building double"));
                currentToken = new IntToken(wholePart, position);
                return true;
            }
            while (charIsDigit(character)) {
                int digit = character.charAt(0) - '0';
                if ((Integer.MAX_VALUE - digit) / 10 - fractionPart < 0) {
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
        if (character.matches("[yYAB]")) {
            boolean isEraAC = character.matches("[yYA]");
            if (character.matches("[AB]") && !(character = source.nextCharacter()).equals("C")) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.INFO, position, "Unexpected character while building date. Era must be either y, Y, AC or BC "));
                currentToken = new IntToken(wholePart, position);
                return true;
            }
            if (!(character = source.nextCharacter()).equals(":")) {
                currentToken = new PeriodToken(new Period(wholePart, 0, 0, 0, 0, 0), position);
                return true;
            }
            character = source.nextCharacter();

            int monthValue;
            if ((monthValue = getDateNumber("month")) < 0) return false;
            if (!verifyTimeUnitSymbol("[mM]")) return false;

            int dayValue;
            if ((dayValue = getDateNumber("day")) < 0) return false;
            if (!verifyTimeUnitSymbol("[dD]")) return false;

            int hourValue;
            if ((hourValue = getDateNumber("hour")) < 0) return false;
            if (!verifyTimeUnitSymbol("[hH]")) return false;

            int minuteValue;
            if ((minuteValue = getDateNumber("minute")) < 0) return false;
            if (!verifyTimeUnitSymbol("'")) return false;


            int secondValue;
            if ((secondValue = getDateNumber("second")) < 0) return false;
            if (!character.matches("\"")){
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
        if (character.matches("[mM]")) {
            character = source.nextCharacter();
            currentToken = new PeriodToken(new Period(0, wholePart, 0, 0, 0, 0), position);
            return true;
        }
        if (character.matches("[dD]")) {
            character = source.nextCharacter();
            currentToken = new PeriodToken(new Period(0, 0, wholePart, 0, 0, 0), position);
            return true;
        }
        if (character.matches("[hH]")) {
            character = source.nextCharacter();
            currentToken = new PeriodToken(new Period(0, 0, 0, wholePart, 0, 0), position);
            return true;
        }
        if (character.equals("'")) {
            character = source.nextCharacter();
            currentToken = new PeriodToken(new Period(0, 0, 0, 0, wholePart, 0), position);
            return true;
        }
        if (character.equals("\"")) {
            character = source.nextCharacter();
            currentToken = new PeriodToken(new Period(0, 0, 0, 0, 0, wholePart), position);
            return true;
        }
        currentToken = new IntToken(wholePart, position);
        return true;
    }

    private int getDateNumber(String unit) throws IOException {
        if (!charIsDigit(character)) {
            errorManager.reportError(
                    new LexerErrorInfo(Severity.ERROR, position, String.format("Could not build date. Invalid %s unit", unit)));
            return -1;
        }
        int number = character.charAt(0) - '0';
        while (charIsDigit(character = source.nextCharacter())) {
            int digit = character.charAt(0) - '0';
            if ((Integer.MAX_VALUE - digit) / 10 - number < 0) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "Overflow while building date"));
                return -1;
            }
            number = number * 10 + digit;
        }
        return number;
    }

    private boolean verifyTimeUnitSymbol(String regex) throws IOException {
        if (!character.matches(regex)) {
            errorManager.reportError(
                    new LexerErrorInfo(Severity.ERROR, position, "Could not build date. Unrecognized time unit"));
            return false;
        }
        if (!(character = source.nextCharacter()).equals(":")) {
            errorManager.reportError(
                    new LexerErrorInfo(Severity.WARN, position, "Missing \":\" separator while building date"));
            return true;
        }
        character = source.nextCharacter();
        return true;
    }

    private boolean charIsDigit(String ch) {
        return ch.charAt(0) >= '0' && ch.charAt(0) <= '9';
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
            if ( 0 <= identifierMaxLength && identifierMaxLength < sB.length()) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "Identifier too long"));
                return false;
            }
            sB.append(character);
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

    private boolean isEndOfKeywordOrIdent(String ch) {
        return ch.isBlank() || ch.equals("\0") || ch.matches("[+\\-*/.\\\\!:;'\"()\\[\\]{}<>=#]");
    }

    private boolean tryBuildString() throws IOException {
        if (!character.equals("[")) return false;
        StringBuilder sB = new StringBuilder();
        character = source.nextCharacter();
        while (!character.equals("]")) {
            if ( 0 <= stringLiteralMaxLength &&  stringLiteralMaxLength < sB.length()) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "String too long"));
                return false;
            }
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
        currentToken = new StringToken(sB.toString(), position);
        character = source.nextCharacter();
        return true;
    }
    private boolean tryBuildComment() throws IOException {
        if (!character.equals("#")) return false;
        StringBuilder sB = new StringBuilder();
        while (!(character = source.nextCharacter()).equals(source.getNewlineCharacter())){
            if (0 <= commentMaxLength && commentMaxLength < sB.length()){
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "Comment too long"));
                return false;
            }
            sB.append(character);
        }
        character = source.nextCharacter();
        currentToken = new CommentToken(sB.toString(), position);
        return true;
    }

    @Override
    public Token next() throws IOException {
        while ((character).isBlank()) {
            character = source.nextCharacter();
        }
        position = new Position(source.getPosition().getLine(), source.getPosition().getColumn());
        if (character.equals(TokenType.EOF.getKeyword())) {
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

        return new SimpleToken(TokenType.PLUS, position);
    }
}
