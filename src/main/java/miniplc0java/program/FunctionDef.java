package miniplc0java.program;

import miniplc0java.instruction.Instruction;

import java.util.ArrayList;

public class FunctionDef {
    int name;
    int return_slots;
    int param_slots;
    int loc_slots;
    ArrayList<Instruction> body;

    public FunctionDef(int name, int return_slots, int param_slots, int loc_slots, ArrayList<Instruction> body) {
        this.name = name;
        this.return_slots = return_slots;
        this.param_slots = param_slots;
        this.loc_slots = loc_slots;
        this.body = body;
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
}
