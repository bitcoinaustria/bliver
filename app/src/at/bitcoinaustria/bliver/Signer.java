package at.bitcoinaustria.bliver;

import com.google.bitcoin.core.DumpedPrivateKey;
import com.google.bitcoin.core.ECKey;
import com.google.common.annotations.VisibleForTesting;

import java.math.BigInteger;

/**
 * simplified version with only one private key
 * @author apetersson
 */
public class Signer {

    public static final String DEMO_PRIVATE_KEY = "47f1b377f5c234da70d999f5674e57a8387fd6c1017943afbdd42fd6e13fbc59";
    public static final String DEMO_PUBLIC_KEY = "04b8d36946d1d96c8d6215a982fe7fbe81072294c2a5290eed5119bda05b72947b35372bd691394b438dc184dfb168e1d0bb3a85b129ebaa6c0bb0d40876b75e0c";

    public static Signer DEMO_SIGNER = new Signer(DEMO_PRIVATE_KEY);

    @VisibleForTesting
    final ECKey privateKey;

    public Signer(String privateKey) {
        this.privateKey = new ECKey(new BigInteger(privateKey,16));
    }

    public static String createPrivateKey(){
        return byteToHex(new ECKey().getPrivKeyBytes());
    }

    public DumpedPrivateKey getPrivateKey(){
        return privateKey.getPrivateKeyEncoded(Net.NETWORK);
        //return byteToHex(privateKey.getPrivKeyBytes());
    }

    public String sign(String transactionInput){
        byte[] inputBytes = hexToByte(transactionInput);
        return new BigInteger(privateKey.sign(inputBytes)).toString(16);
    }

    public String getPublicKey(){
        return byteToHex(privateKey.getPubKey());
    }

    public static byte[] hexToByte(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String byteToHex(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++){
          /*  if (i > 0)
                sb.append(':');*/
            sb.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}
