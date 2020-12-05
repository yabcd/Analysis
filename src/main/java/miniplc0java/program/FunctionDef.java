package miniplc0java.program;

import miniplc0java.instruction.Instruction;
import miniplc0java.util.ByteUtil;

import java.util.ArrayList;

public class FunctionDef {
    int name;

    public void setName(int name) {
        this.name = name;
    }

    int return_slots;
    int param_slots;

    public void setLoc_slots(int loc_slots) {
        this.loc_slots = loc_slots;
    }

    int loc_slots;
    ArrayList<Instruction> body;

    public FunctionDef(int name, int return_slots, int param_slots, int loc_slots, ArrayList<Instruction> body) {
        this.name = name;
        this.return_slots = return_slots;
        this.param_slots = param_slots;
        this.loc_slots = loc_slots;
        this.body = body;
    }

    public FunctionDef(int name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FunctionDef{" +
                "name=" + name +
                ", return_slots=" + return_slots +
                ", param_slots=" + param_slots +
                ", loc_slots=" + loc_slots +
                ", body=" + body +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        return this.name==((FunctionDef)o).name;
    }

    public byte[] getBytes(){
        byte[] bytes = new byte[20];
        ByteUtil.addInt(bytes,0,name);
        ByteUtil.addInt(bytes,4,return_slots);
        ByteUtil.addInt(bytes,8,param_slots);
        ByteUtil.addInt(bytes,12,loc_slots);
        ByteUtil.addInt(bytes,16,body.size());
        byte[] b = new byte[0];
        for(Instruction i:body){
            b = ByteUtil.catBytes(b,i.getBytes());
        }
        return ByteUtil.catBytes(bytes,b);
    }
}
