package miniplc0java.program;

import miniplc0java.util.ByteUtil;

import java.util.ArrayList;
import java.util.List;

public class o0 {
    public String getMagic() {
        return magic;
    }

    public String getVersion() {
        return version;
    }

    public List<GlobalDef> getGlobals() {
        return globals;
    }

    public void setGlobals(List<GlobalDef> globals) {
        this.globals = globals;
    }

    public List<FunctionDef> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionDef> functions) {
        this.functions = functions;
    }

    public o0(List<GlobalDef> globals, List<FunctionDef> functions) {
        this.globals = globals;
        this.functions = functions;
    }
    public o0(){
        this.globals = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    final String magic = "72303b3e";
    final String version = "00000001";
    List<GlobalDef> globals;
    List<FunctionDef> functions;

    public byte[] getBytes(){
        byte[] res = new byte[8];
        int num1 = Integer.valueOf(magic,16);
        int num2 = Integer.valueOf(version,16);
        ByteUtil.addInt(res,0,num1);
        ByteUtil.addInt(res,4,num2);
        byte[] b = new byte[4];
        ByteUtil.addInt(b,0,globals.size());
        res = ByteUtil.catBytes(res,b);
        for(GlobalDef g:globals){
            res = ByteUtil.catBytes(res,g.getBytes());
        }

        ByteUtil.addInt(b,0,functions.size());
        res = ByteUtil.catBytes(res,b);
        for(FunctionDef f:functions){
            res = ByteUtil.catBytes(res,f.getBytes());
        }
        return res;
    }

}
