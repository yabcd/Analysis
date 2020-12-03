package miniplc0java;

import miniplc0java.analyser.Analyser;
import miniplc0java.error.TokenizeError;
import miniplc0java.program.HexUtil;
import miniplc0java.tokenizer.StringIter;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class TokenizerTest {

    private Tokenizer init(String path) {
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
        while (token.getTokenType() != TokenType.EOF) {
            System.out.println(token);
            token = tokenizer.nextToken();
        }
        System.out.println(token);
    }

    @Test
    public void enumTest() {
        double d = 1.2;
        long l = Double.doubleToLongBits(d);
        byte[] b = new byte[8];
        b[0] = (byte) (l & 0x000000000000FFL);
        b[1] = (byte) ((l & 0x0000000000FF00L) >> 8);
        b[2] = (byte) ((l & 0x0000000000FF0000L) >> 16);
        b[3] = (byte) ((l & 0x00000000FF000000L) >> 24);
        b[4] = (byte) ((l & 0x000000FF00000000L) >> 32);
        b[5] = (byte) ((l & 0x0000FF0000000000L) >> 40);
        b[6] = (byte) ((l & 0x00FF000000000000L) >> 48);
        b[7] = (byte) ((l & 0xFF00000000000000L) >> 56);
        for(int i=0;i<8;i++){
            System.out.print(HexUtil.byteToHexString(b[i])+" ");
        }

        System.out.println(Long.toHexString(Double.doubleToLongBits(d)));
        System.out.println(Long.toBinaryString(l));
    }

    @Test
    public void hexTest(){
        byte b = -1;
        System.out.println(Integer.toHexString(Integer.valueOf(b)));
        System.out.println(Integer.valueOf("0a",16));
    }
}