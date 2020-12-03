package miniplc0java.util;

public class ByteUtil {
    public static void addInt(byte[] bytes,int offset,int num){
        bytes[offset+0] = (byte)((num >> 24) & 0xFF);
        bytes[offset+1] = (byte)((num >> 16) & 0xFF);
        bytes[offset+2] = (byte)((num >> 8) & 0xFF);
        bytes[offset+3] = (byte)(num & 0xFF);
    }

    public static void addbytes(byte[] bytes,int offset,byte[] add){
        for(int i = 0;i<add.length;i++){
            bytes[i+offset] = add[i];
        }
    }

    public static byte[] catBytes(byte[] bytes1,byte[] bytes2){
        byte[] res = new byte[bytes1.length+bytes2.length];
        addbytes(res,0,bytes1);
        addbytes(res,bytes1.length,bytes2);
        return res;
    }
}
