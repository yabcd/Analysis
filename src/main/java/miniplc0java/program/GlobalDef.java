package miniplc0java.program;

import miniplc0java.symboltable.SymbolEntry;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.util.ByteUtil;

import java.util.Arrays;

public class GlobalDef implements Comparable<GlobalDef>{
    private boolean is_const;//u8
    private int[] value;
    int offset;

    /**
     * 生成一条全局变量
     * @param symbolEntry
     * @param name 当类型为函数传入函数名
     */
    public GlobalDef(SymbolEntry symbolEntry,String name){
        is_const = symbolEntry.isConstant();
        offset = symbolEntry.getStackOffset();
        TokenType type = symbolEntry.getType();
        //类型可能为int，double,fn
        if(type==TokenType.Fn){
            this.value = new int[name.length()];
            for(int i=0;i<name.length();i++){
                this.value[i]=(int)name.charAt(i);
            }
        }else{
            this.value = new int[8];
        }
    }
    public GlobalDef(int offset,String value){
        value = value.replace("\\\\","\\").replace("\\n",(char)10+"")
                .replace("\\\"","\"").replace("\\\'","\'");
        is_const = true;
        this.offset = offset;
        this.value = new int[value.length()];
        for(int i=0;i<value.length();i++){
            this.value[i]=(int)value.charAt(i);
        }
    }

    @Override
    public String toString() {
        String[] s = new String[value.length];
        for(int i=0;i<value.length;i++){
            s[i] = Integer.toHexString(value[i]);
        }
        return "GlobalDef{" +
                "is_const=" + is_const +
                ", value=" + Arrays.toString(s) +
                '}';
    }

    @Override
    public int compareTo(GlobalDef o) {
        return this.offset - o.offset;
    }

    public byte[] getBytes(){
        byte[] bytes= new byte[5+value.length];
        bytes[0] = (byte)((is_const)?1:0);
        ByteUtil.addInt(bytes,1,value.length);
        for(int i =0;i<value.length;i++){
            bytes[i+5] = (byte)value[i];
        }
        return bytes;
    }
}