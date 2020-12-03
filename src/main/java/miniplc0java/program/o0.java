package miniplc0java.program;

import java.util.ArrayList;
import java.util.List;

public class o0 {
    public int getMagic() {
        return magic;
    }

    public int getVersion() {
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

    final int magic = 0x72303b3e;
    final int version = 0x00000001;
    List<GlobalDef> globals;
    List<FunctionDef> functions;
}
