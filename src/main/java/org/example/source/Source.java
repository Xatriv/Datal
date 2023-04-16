package org.example.source;

import java.io.*;


public class Source implements ISource{
    public static final int EOF = -1;
    public static final String ETX = "\0";

    public static final int HIGH_SURROGATE_MIN = 0xD800;
    public static final int HIGH_SURROGATE_MAX = 0xDBFF;
    public static final int LOW_SURROGATE_MIN = 0xDC00;
    public static final int LOW_SURROGATE_MAX = 0xDFFF;

    private final BufferedReader bufferedReader;
    public Source(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        this.bufferedReader = new BufferedReader(new FileReader(fileName));
    }
    @Override
    public String nextCharacter() throws IOException {
        int c;
        if((c = this.bufferedReader.read()) == EOF){
            return ETX;
        }
        if ( HIGH_SURROGATE_MIN < c && c < HIGH_SURROGATE_MAX){
            int lowSurrogate = this.bufferedReader.read();
            if (LOW_SURROGATE_MIN < lowSurrogate && lowSurrogate < LOW_SURROGATE_MAX){
                int codePoint = Character.toCodePoint((char) c, (char) lowSurrogate);
                return new String(new int[]{codePoint}, 0, 1);
            }
        }
        return new String(Character.toChars(c));
    }
}
