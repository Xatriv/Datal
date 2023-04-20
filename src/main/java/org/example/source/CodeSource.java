package org.example.source;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.*;


public class CodeSource implements Source {
    public static final int EOF = -1;
    public static final char ETX = '\0';

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

    public CodeSource(Reader reader) {
        this.position = new Position(1, 0);
        this.bufferedReader = new BufferedReader(reader);
    }

    private boolean isEOL() throws IOException {
        if (newlineCharacter == null) {
            bufferedReader.mark(1);
            if (character == '\n') {
                int secondCharacter = bufferedReader.read();
                newlineCharacter = secondCharacter == '\r' ? "\n\r" : "\n";
            } else if (character == '\r') {
                int secondCharacter = bufferedReader.read();
                if (secondCharacter == '\n') {
                    newlineCharacter = "\r\n";
                }
            }
            bufferedReader.reset();
        }

        if (character == '\r'){
            bufferedReader.mark(1);
            if (bufferedReader.read() != '\n'){
                bufferedReader.reset();
                return false;
            }
            if (!newlineCharacter.equals("\r\n")){
                //throw
                return false;
            }
            return true;
        }
        if (character == '\n'){
            bufferedReader.mark(1);
            int secondCharacter = bufferedReader.read();
            if (newlineCharacter.length() == 1){
                if (secondCharacter == '\r'){
                    // throw
                    return false;
                }
                bufferedReader.reset();
                return true;
            }
            if (secondCharacter != '\r'){
                //throw
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int nextCharacter() throws IOException {
        if ((character = this.bufferedReader.read()) == EOF) {
            return ETX;
        }
        if (isEOL()) {
            position.newLine();
            return '\n';
        }
        if (HIGH_SURROGATE_MIN < character && character < HIGH_SURROGATE_MAX) {
            int lowSurrogate = this.bufferedReader.read();

            if (LOW_SURROGATE_MIN < lowSurrogate && lowSurrogate < LOW_SURROGATE_MAX) {
                int codePoint = Character.toCodePoint((char) character, (char) lowSurrogate);
                position.incrementColumn();
                return codePoint;
            }
        }
        position.incrementColumn();
        return character;
    }
}
