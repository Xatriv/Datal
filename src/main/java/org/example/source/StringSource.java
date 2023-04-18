package org.example.source;

import java.io.*;


public class StringSource implements Source{
    public static final int EOF = -1;
    public static final String ETX = "\0";

    public static final int HIGH_SURROGATE_MIN = 0xD800;
    public static final int HIGH_SURROGATE_MAX = 0xDBFF;
    public static final int LOW_SURROGATE_MIN = 0xDC00;
    public static final int LOW_SURROGATE_MAX = 0xDFFF;

    private final StringReader stringReader;
    public StringSource(String code) {
        this.stringReader = new StringReader(code);
    }
    @Override
    public String nextCharacter() throws IOException {
        int c;
        if((c = this.stringReader.read()) == EOF){
            return ETX;
        }
//        if (c == '\\') {
//            c = this.stringReader.read();
//            switch (c) {
//                case '\\':
//                    c = '\\';
//                    break;
//                case 'b':
//                    c = '\b';
//                    break;
//                case 'f':
//                    c = '\f';
//                    break;
//                case 'n':
//                    c = '\n';
//                    break;
//                case 'r':
//                    c = '\r';
//                    break;
//                case 't':
//                    c = '\t';
//                    break;
//                default:
//                    // includes string literal delimiter
//                    c = character);
        if ( HIGH_SURROGATE_MIN < c && c < HIGH_SURROGATE_MAX){
            int lowSurrogate = this.stringReader.read();
            if (LOW_SURROGATE_MIN < lowSurrogate && lowSurrogate < LOW_SURROGATE_MAX){
                int codePoint = Character.toCodePoint((char) c, (char) lowSurrogate);
                return new String(new int[]{codePoint}, 0, 1);
            }
        }
        return new String(Character.toChars(c));
    }
}
