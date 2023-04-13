package org.example.lexer;

import org.example.source.ISource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Lexer implements ILexer {

    private String character; //maybe unnecessary
    private Token currentToken;
    private String newlineCharacter;
    private final int identifierMaxLength;
    private final int stringLiteralMaxLength;

    private final ISource source;


    public Lexer(ISource source)  throws IOException {
        this.source = source;
        Properties props = new Properties();
        int identifierMaxLengthProp = -1;
        int stringLiteralMaxLengthProp = -1;
        try{
            InputStream input = new FileInputStream("src/main/java/org/example/lexer/lexer.properties");
            props.load(input);
            identifierMaxLengthProp = readProperty(props, "IDENTIFIER_MAX_LENGTH", -1);
            stringLiteralMaxLengthProp = readProperty(props, "STRING_LITERAL_MAX_LENGTH", -1);
        }
        catch (IOException ignored){}
        finally {
            this.identifierMaxLength = identifierMaxLengthProp;
            this.stringLiteralMaxLength = stringLiteralMaxLengthProp;
        }
    }
    private static int readProperty(Properties props, String key, int defaultValue){
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


    private void printConfig(){
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

    private boolean tryBuildNumber() throws IOException {
        int wholePart = 0;
        int fractionPart = 0;
        int decimalDigits = 0;
        while (Character.isDigit(character.charAt(0))) {
            wholePart *= 10;
            wholePart += Integer.parseInt(character);
            character = source.nextCharacter();
        }
        if (Character.isWhitespace(character.charAt(0))){ //TODO not only whitespace
            currentToken = Token.INTEGER; //TODO value
            return true;
        } else if (character.equals(".")) { //TODO maybe extract somewhere?
            character = source.nextCharacter(); //TODO handle parsing errors (everything other than int.int)
            while (Character.isDigit(character.charAt(0))){
                fractionPart += Integer.parseInt(character);
                decimalDigits += 1;
            }
            double result = wholePart + (double) fractionPart/decimalDigits;
            currentToken = Token.DOUBLE;
            return true;
        } //TODO check for overflow
         return false;
    }

    private boolean tryBuildIdentOrKeyword() {
        return true;
    }

    private boolean tryBuildString(){
        return true;
    }
    @Override
    public Token next() throws IOException {
        int c;
//        StringBuilder builder = new StringBuilder();

//        String hmm = this.source.read();
        while (this.character.isBlank()) ;
//        if ( try ){
//            return Token;
//        }
        return null;
    }
}
