package miniplc0java;

import miniplc0java.analyser.Analyser;
import miniplc0java.error.TokenizeError;
import miniplc0java.tokenizer.StringIter;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class TokenizerTest {

    private Tokenizer init(String path){
        File file = new File(path);
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringIter it = new StringIter(sc);
        return new Tokenizer(it);
    }

    @Test
    public void test() throws TokenizeError {
        Tokenizer tokenizer = init("C:\\Users\\hp\\IdeaProjects\\miniplc0-java-master\\src\\test\\java\\miniplc0java\\test1.txt");
        Token token = tokenizer.nextToken();
        while(token.getTokenType()!=TokenType.EOF){
            System.out.println(token);
            token = tokenizer.nextToken();
        }
        System.out.println(token);
    }
}