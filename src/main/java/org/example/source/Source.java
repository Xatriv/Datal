package org.example.source;

import java.io.*;


public class Source implements ISource{
    public static final int EOF = -1;
    public static final String ETX = "\0";
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
        return new String(Character.toChars(c));
    }
}
