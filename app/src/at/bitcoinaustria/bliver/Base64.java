package at.bitcoinaustria.bliver;

import com.google.common.base.Charsets;

import java.io.UnsupportedEncodingException;

/**
 * @author apetersson
 */
public class Base64 {

    public static byte[] decode(String bytes) {
        return org.spongycastle.util.encoders.Base64.decode(bytes);
    }

    public static String encode(byte[] bytes) {
        try {
            return new String(org.spongycastle.util.encoders.Base64.encode(bytes), Charsets.US_ASCII.toString()) ;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
