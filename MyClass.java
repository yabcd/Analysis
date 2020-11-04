import java.io.*;
import java.util.Scanner;

public class MyClass {
    public int analyseLine(char[] line) {
        char[] stack = new char[1000];
        int sp = 0;
        stack[sp++] = '#';
        int read = 0;
        while (stack[1] != '#') {
            int fsp = getvtIndex(stack, sp);
            if (stack[fsp] == '#' && line[read] == '#') return 0;
            //无法比较，终止运行
            if (compare(stack[fsp], line[read]) == null) {
                System.out.println("E");
                return 0;
            }
            //栈内优先级小，进行移入
            if (compare(stack[fsp], line[read]) <= 0) {
                System.out.println("I" + line[read]);
                stack[sp++] = line[read++];
                continue;
            } else {
                int x = getvtIndex(stack, fsp);
                while (compare(stack[x], stack[fsp]) == 0) {
                    fsp = x;
                    if ((x = getvtIndex(stack, fsp)) == -1) break;
                }
                if ((x = getvtIndex(stack, fsp)) != -1) fsp = x;
                rule(stack, fsp, sp);
                sp = fsp + 2;
                stack[sp - 1] = 'N';
//                for(int i=0;i<sp;i++){
//                    System.out.print(" "+stack[i]);
//                }
//                System.out.println();
            }
        }
        return 0;
    }

    public void rule(char[] stack, int fsp, int sp) {
        if (sp - fsp == 4) {
            System.out.println("R");
        } else if (sp - fsp == 2) {
            if (stack[sp - 1] != 'i')
                System.out.println("RE");
            else
                System.out.println("R");
        } else
            System.out.println("RE");
    }

    public int getvtIndex(char[] stack, int sp) {
        if (sp == 0) return -1;
        if (sp == 1 && !isVt(stack[sp - 1])) return -1;
        if (isVt(stack[sp - 1])) return sp - 1;
        return sp - 2;
    }

    public boolean isVt(char s) {
        switch (s) {
            case '+':
            case '*':
            case '(':
            case ')':
            case 'i':
            case '#':
                return true;
        }
        return false;
    }

    public int f(char s) {
        switch (s) {
            case '+':
                return 2;
            case '*':
                return 4;
            case '(':
                return 0;
            case ')':
                return 6;
            case 'i':
                return 6;
            case '#':
                return 0;
        }
        throw new RuntimeException("this vt does not exist");
    }

    public int g(char s) {
        switch (s) {
            case '+':
                return 1;
            case '*':
                return 3;
            case '(':
                return 5;
            case ')':
                return 0;
            case 'i':
                return 5;
            case '#':
                return 0;
        }
        throw new RuntimeException("this vt does not exist");
    }

    public Integer compare(char s1, char s2) {
        if (s1 == 'i' && (s2 == 'i' || s2 == '(')) return null;
        if (s1 == '(' && s2 == '#') return null;
        if (s1 == ')' && (s2 == 'i' || s2 == '(')) return null;
        if (s1 == '#' && (s2 == ')' || s2 == '#')) return null;
        return f(s1) - g(s2);
    }

    public static void main(String[] args) throws FileNotFoundException {
        MyClass myClass = new MyClass();
        Scanner sc = new Scanner(new File(args[0]));
        while (sc.hasNext()) {
            myClass.analyseLine((sc.nextLine() + "#").toCharArray());
        }
    }
}