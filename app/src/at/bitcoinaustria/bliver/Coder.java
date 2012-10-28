package at.bitcoinaustria.bliver;

import com.google.common.base.Charsets;

import java.io.UnsupportedEncodingException;

/**
 * @author apetersson
 */
public class Coder {

    public static byte[] base64Decode(String bytes) {
        return org.spongycastle.util.encoders.Base64.decode(bytes);
    }

    public static String base64Encode(byte[] bytes) {
        try {
            return new String(org.spongycastle.util.encoders.Base64.encode(bytes), Charsets.US_ASCII.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] hexToByte(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteToHex(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
          /*  if (i > 0)
                sb.append(':');*/
            sb.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
