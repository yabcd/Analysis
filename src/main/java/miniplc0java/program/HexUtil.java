package miniplc0java.program;

public class HexUtil {

    public static String byteToHexString(byte b){
        int i = Byte.toUnsignedInt(b);
        String hex = Integer.toHexString(i);
        int length = hex.length();
        return hex.substring(length-2,length);
    }
    public static String string2HexString(String strPart) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString.append(strHex);
        }
        return hexString.toString();
    }
}
