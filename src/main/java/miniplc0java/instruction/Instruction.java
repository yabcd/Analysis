package miniplc0java.instruction;

import miniplc0java.util.ByteUtil;

import java.io.PushbackInputStream;
import java.util.Objects;

public class Instruction {
    private Operation opt;
    Long x;
    boolean ifx;

    public Instruction(Operation opt) {
        this.opt = opt;
        this.x = 0L;
        ifx = false;
    }

    public Instruction(Operation opt, Long x) {
        this.opt = opt;
        this.x = x;
        ifx = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Instruction that = (Instruction) o;
        return opt == that.opt && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opt, x);
    }

    public Operation getOpt() {
        return opt;
    }

    public void setOpt(Operation opt) {
        this.opt = opt;
    }

    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        ifx = true;this.x = x;
    }

    @Override
    public String toString() {
        if(ifx){
            return opt+" "+x;
        }
        return opt.toString();
    }

    public byte[] getBytes(){
        byte[] bytes = null;
        int code = Integer.valueOf(this.opt.getBytes(),16);
        if(opt== Operation.PUSH){
            bytes = new byte[9];
            ByteUtil.addInt(bytes,1,(int)(x>>32));
            ByteUtil.addInt(bytes,5,(int)(x>>0));
        }else if(ifx){
            bytes = new byte[5];
            ByteUtil.addInt(bytes,1,(int)(x>>0));
        }else{
            bytes = new byte[1];
        }
        bytes[0] = (byte)code;
        return bytes;
    }
}
