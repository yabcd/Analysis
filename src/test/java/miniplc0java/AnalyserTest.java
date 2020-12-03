package miniplc0java;

import miniplc0java.analyser.Analyser;
import miniplc0java.error.CompileError;
import miniplc0java.program.o0;
import miniplc0java.tokenizer.StringIter;
import miniplc0java.tokenizer.Tokenizer;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.*;

public class AnalyserTest {
    private Analyser init(String path){
        File file = new File(path);
        Scanner sc = null;
        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringIter it = new StringIter(sc);
        Tokenizer tokenizer = new Tokenizer(it);
        return new Analyser(tokenizer);
    }

    private void testAnalyser(String path) throws CompileError {
        Analyser analyser = init("C:\\Users\\hp\\IdeaProjects\\miniplc0-java-master\\src\\test\\java\\miniplc0java\\"+path);
        o0 analyse = analyser.analyse();
        byte[] bytes = analyse.getBytes();
        for(byte b:bytes){
            System.out.print(Integer.toHexString(Integer.valueOf(b))+" ");
        }
        System.out.println(analyse.getGlobals());
        System.out.println(analyse.getFunctions());
    }
    @Test
    public void test1() throws CompileError {
        testAnalyser("test1.txt");
    }

    @Test
    public void test2() throws CompileError {
        testAnalyser("test2.txt");
    }

}
