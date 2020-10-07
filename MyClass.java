import java.io.*;

public class MyClass {
    private char CHAR;
    private boolean newChar;
    private String TOKEN;
    private String SY;
    private Reader reader;

    public MyClass(String path){
        newChar = true;
        File file = new File(path);
        try {
            reader = new InputStreamReader(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public  int getChar(){
        if(!newChar){
            newChar=true;
            return 0;
        }
        try {
            int res=0;
            if((res = reader.read())==-1){
                return -1;
            }
            CHAR = (char)res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getNBC(){
        while(true){
            int res = 0;
            if((res=getChar())==-1) return -1;
            if(!Character.isSpaceChar(CHAR) && CHAR!='\r'&&CHAR!='\n'&&CHAR!='\t') return 0;
        }
    }

    public void CAT(){
        TOKEN+=CHAR;
    }

    public boolean isLetter(){
        return Character.isLetter(CHAR);
    }
    public boolean isDigit(){
        return Character.isDigit(CHAR);
    }
    public void unGetCh(){
        newChar = false;
    }
    public boolean reserve(){
        if(TOKEN.equals("BEGIN")){
            SY = "Begin";
            return true;
        }else if(TOKEN.equals("END")){
            SY = "End";
            return true;
        }else if(TOKEN.equals("FOR")){
            SY = "For";
            return true;
        }else if(TOKEN.equals("IF")){
            SY = "If";
            return true;
        }else if(TOKEN.equals("THEN")){
            SY = "Then";
            return true;
        }else if(TOKEN.equals("ELSE")){
            SY = "Else";
            return true;
        }
        return false;
    }
    public int ATOI(){
        return Integer.valueOf(TOKEN);
    }

    public void ERROR(){
        System.out.println("Unknown");
        System.exit(0);
    }
    public void run(){
        TOKEN = "";
        boolean ifrun=true;
        while(getNBC()!=-1&&ifrun){
            if(isLetter()){
                do{
                    CAT();
                    if(getChar()==-1){ifrun=false;break;}
                }while(isLetter()||isDigit());
                unGetCh();
                if(reserve()) System.out.println(SY);
                else System.out.println("Ident("+TOKEN+")");
            }else if(isDigit()){
                do{
                    CAT();if(getChar()==-1){ifrun=false;break;}
                }while(isDigit());
                unGetCh();
                System.out.println("Int("+ATOI()+")");
            }else if(CHAR=='+') System.out.println("Plus");
            else if(CHAR=='*') System.out.println("Star");
            else if(CHAR==',') System.out.println("Comma");
            else if(CHAR=='(') System.out.println("LParenthesis");
            else if(CHAR==')') System.out.println("RParenthesis");
            else if(CHAR==':') {
                if(getChar()==-1){ifrun=false;}
                if(CHAR=='=') System.out.println("Assign");
                else{
                    unGetCh();
                    System.out.println("Colon");
                }
            }
            else ERROR();
            TOKEN = "";
        }
    }

    public static void main(String[] args) {
        MyClass a = new MyClass(args[0]);
        a.run();
    }
}
