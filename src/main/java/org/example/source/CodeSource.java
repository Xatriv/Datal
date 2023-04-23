package org.example.source;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.example.error.ErrorManager;
import org.example.error.LexerErrorInfo;
import org.example.error.Severity;

import java.io.*;


public class CodeSource implements Source {
    public static final int EOF = -1;
    public static final char ETX = 3;

    public static final int HIGH_SURROGATE_MIN = 0xD800;
    public static final int HIGH_SURROGATE_MAX = 0xDBFF;
    public static final int LOW_SURROGATE_MIN = 0xDC00;
    public static final int LOW_SURROGATE_MAX = 0xDFFF;

    private final BufferedReader bufferedReader;

    private final ErrorManager errorManager;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private Position position;
    @Getter
    private String newlineSequence;
    private int character;

    public CodeSource(Reader reader, ErrorManager errorManager) {
        this.position = new Position(1, 1);
        this.bufferedReader = new BufferedReader(reader);
        this.errorManager = errorManager;
    }

    private boolean isEOL() throws IOException {
        if (newlineSequence == null) {
            tryDefineNewlineSequence();
        }
        if (character == '\r') {
            bufferedReader.mark(1);
            if (bufferedReader.read() != '\n') {
                bufferedReader.reset();
                return false;
            }
            if (!newlineSequence.equals("\r\n")) {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "Inconsistent end of line convention."));
                return false;
            }
            return true;
        }
        if (character == '\n') {
            if (newlineSequence.charAt(0) == '\r'){
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "Inconsistent end of line convention."));
                return false;
            }
            bufferedReader.mark(1);
            int secondCharacter = bufferedReader.read();
            if (newlineSequence.length() == 1) {
                if (secondCharacter == '\r') {
                    errorManager.reportError(
                            new LexerErrorInfo(Severity.ERROR, position, "Inconsistent end of line convention."));
                    return false;
                }
                bufferedReader.reset();
                return true;
            }
            if (secondCharacter != '\r') {
                errorManager.reportError(
                        new LexerErrorInfo(Severity.ERROR, position, "Inconsistent end of line convention."));
                return false;
            }
            return true;
        }
        return false;
    }

    void tryDefineNewlineSequence() throws IOException {
        if (character == '\n') {
            bufferedReader.mark(1);
            int secondCharacter = bufferedReader.read();
            newlineSequence = secondCharacter == '\r' ? "\n\r" : "\n";
            bufferedReader.reset();
        } else if (character == '\r') {
            bufferedReader.mark(1);
            int secondCharacter = bufferedReader.read();
            if (secondCharacter == '\n') {
                newlineSequence = "\r\n";
            }
            bufferedReader.reset();
        }
    }

    @Override
    public int nextCharacter() throws IOException {
        if (character == '\n'){
            position.newLine();
        }
        if (character == ETX){
            return character;
        }
        if (character != '\0' && character != '\n'){
            position.incrementColumn();
        }
        character = this.bufferedReader.read();
        if (character == EOF) {
            character = ETX;
            return character;
        }
        if (isEOL()) {
            character = '\n';
            return character;
        }
        if (HIGH_SURROGATE_MIN < character && character < HIGH_SURROGATE_MAX) {
            int lowSurrogate = this.bufferedReader.read();

            if (LOW_SURROGATE_MIN < lowSurrogate && lowSurrogate < LOW_SURROGATE_MAX) {
                character = Character.toCodePoint((char) character, (char) lowSurrogate);
            }
        }
        return character;
    }
}
