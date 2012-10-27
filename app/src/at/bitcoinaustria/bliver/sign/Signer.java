package at.bitcoinaustria.bliver.sign;

import com.google.bitcoin.core.DumpedPrivateKey;
import com.google.bitcoin.core.ECKey;
import com.google.common.annotations.VisibleForTesting;

import java.math.BigInteger;

/**
 * simplified version with only one private key
 * @author apetersson
 */
public class Signer {

    @VisibleForTesting
    final ECKey privateKey;

    public Signer(String privateKey) {
        this.privateKey = new ECKey(new BigInteger(privateKey));
    }

    public static String createPrivateKey(){
        return new BigInteger(new ECKey().getPrivKeyBytes()).toString();
    }

    public String getPrivateKey(){
        return new BigInteger(privateKey.getPrivKeyBytes()).toString();
    }

    public String sign(String transactionInput){
        byte[] inputBytes = new BigInteger(transactionInput, 16).toByteArray();
        return new BigInteger(privateKey.sign(inputBytes)).toString(16);
    }
    public String getPublicKey(){
        return toHex(privateKey.getPubKey());
    }

    private String toHex(byte[] key) {
      return  new BigInteger(key).toString(16);
    }


}
