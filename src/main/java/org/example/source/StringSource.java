package org.example.source;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.*;


public class StringSource implements Source {
    public static final int EOF = -1;
    public static final String ETX = "\0";

    public static final int HIGH_SURROGATE_MIN = 0xD800;
    public static final int HIGH_SURROGATE_MAX = 0xDBFF;
    public static final int LOW_SURROGATE_MIN = 0xDC00;
    public static final int LOW_SURROGATE_MAX = 0xDFFF;

    private final StringReader stringReader;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private Position position;
    @Getter
    private String newlineCharacter;

    private int character;

    public StringSource(String code) {
        this.position = new Position(1, 0);
        this.stringReader = new StringReader(code);
    }

    private boolean isEOL() throws IOException {
        if (character == '\n') {
            stringReader.mark(1);
            character = stringReader.read();
            if (character == '\r') {
                if (newlineCharacter == null) {
                    newlineCharacter = "\n\r";
                }
            } else {
                if (newlineCharacter == null) {
                    newlineCharacter = "\n";
                }
                stringReader.reset();
            }
            return true;
        } else if (character == '\r') {
            stringReader.mark(1);
            character = stringReader.read();
            if (character == '\n') {
                if (newlineCharacter == null) {
                    newlineCharacter = "\r\n";
                }
                return true;
            }
            stringReader.reset();
            return false;
        }
        return false;
    }

    @Override
    public String nextCharacter() throws IOException {
        if ((character = this.stringReader.read()) == EOF) {
            return ETX;
        }
        if (isEOL()) {
            position.newLine();
            return newlineCharacter;
        }
        if (HIGH_SURROGATE_MIN < character && character < HIGH_SURROGATE_MAX) {
            int lowSurrogate = this.stringReader.read();
            if (LOW_SURROGATE_MIN < lowSurrogate && lowSurrogate < LOW_SURROGATE_MAX) {
                int codePoint = Character.toCodePoint((char) character, (char) lowSurrogate);
                position.incrementColumn();
                return new String(new int[]{codePoint}, 0, 1);
            }
        }
        position.incrementColumn();
        return new String(Character.toChars(character));
    }
}
