package org.example.source;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.*;


public class FileSource implements Source {
    public static final int EOF = -1;
    public static final String ETX = "\0";

    public static final int HIGH_SURROGATE_MIN = 0xD800;
    public static final int HIGH_SURROGATE_MAX = 0xDBFF;
    public static final int LOW_SURROGATE_MIN = 0xDC00;
    public static final int LOW_SURROGATE_MAX = 0xDFFF;

    private final BufferedReader bufferedReader;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private Position position;
    @Getter
    private String newlineCharacter;
    private int character;

    public FileSource(String fileName) throws FileNotFoundException {
        this.position = new Position(1, 1);
        this.bufferedReader = new BufferedReader(new FileReader(fileName));
    }

    private boolean isEOL() throws IOException {
        if (character == '\n') {
            bufferedReader.mark(1);
            character = bufferedReader.read();
            if (character == '\r') {
                if (newlineCharacter == null) {
                    newlineCharacter = "\n\r";
                }
            } else {
                if (newlineCharacter == null) {
                    newlineCharacter = "\n";
                }
                bufferedReader.reset();
            }
            return true;
        } else if (character == '\r') {
            bufferedReader.mark(1);
            character = bufferedReader.read();
            if (character == '\n') {
                if (newlineCharacter == null) {
                    newlineCharacter = "\r\n";
                }
                return true;
            }
            bufferedReader.reset();
            return false;
        }
        return false;
    }

    @Override
    public String nextCharacter() throws IOException {
        if ((character = this.bufferedReader.read()) == EOF) {
            return ETX;
        }
        if (isEOL()) {
            position.newLine();
            return newlineCharacter;
        }
        if (HIGH_SURROGATE_MIN < character && character < HIGH_SURROGATE_MAX) {
            int lowSurrogate = this.bufferedReader.read();
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
