package miniplc0java.program;

import miniplc0java.symboltable.SymbolEntry;
import miniplc0java.tokenizer.TokenType;

import java.util.Arrays;

public class GlobalDef implements Comparable<GlobalDef>{
    private boolean is_const;
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
}