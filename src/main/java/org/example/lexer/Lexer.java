package org.example.lexer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Lexer implements ILexer {

    private final int identifierMaxLength;
    private final int stringLiteralMaxLength;

    public Lexer(BufferedReader br)  throws IOException {
        Properties props = new Properties();
        InputStream input = new FileInputStream("lexer.properties");
        props.load(input);
        identifierMaxLength = Integer.parseInt(props.getProperty("IDENTIFIER_MAX_LENGTH"));
        stringLiteralMaxLength = Integer.parseInt(props.getProperty("STRING_LITERAL_MAX_LENGTH"));
    }


    private void printConfig(){
        System.out.println(this.identifierMaxLength);
        System.out.println(this.stringLiteralMaxLength);
    }

}
