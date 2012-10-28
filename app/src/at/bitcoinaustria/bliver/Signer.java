package at.bitcoinaustria.bliver;

import com.google.bitcoin.core.DumpedPrivateKey;
import com.google.bitcoin.core.ECKey;
import com.google.common.annotations.VisibleForTesting;

import java.math.BigInteger;

/**
 * simplified version with only one private key
 *
 * @author apetersson
 */
public class Signer {

    public static final String DEMO_PRIVATE_KEY = "47f1b377f5c234da70d999f5674e57a8387fd6c1017943afbdd42fd6e13fbc59";
    //public static final String DEMO_PUBLIC_KEY = "04b8d36946d1d96c8d6215a982fe7fbe81072294c2a5290eed5119bda05b72947b35372bd691394b438dc184dfb168e1d0bb3a85b129ebaa6c0bb0d40876b75e0c";

    public static Signer DEMO_SIGNER = new Signer(DEMO_PRIVATE_KEY, 11);

    public static void main(String[] args) {
        System.out.println("addr " + DEMO_SIGNER.privateKey.toAddress(Net.NETWORK));
        System.out.println("privkey-dumped " + DEMO_SIGNER.getPrivateKey());
        System.out.println("privkey hex " + DEMO_SIGNER.privateKey);
    }

    @VisibleForTesting
    final ECKey privateKey;

    public Signer(String privateKey, int offset) {
        this.privateKey = new ECKey(new BigInteger(privateKey, 16).add(BigInteger.valueOf(offset)));
    }

    public static String createPrivateKey() {
        return Coder.byteToHex(new ECKey().getPrivKeyBytes());
    }

    public DumpedPrivateKey getPrivateKey() {
        return privateKey.getPrivateKeyEncoded(Net.NETWORK);
        //return byteToHex(privateKey.getPrivKeyBytes());
    }

    public String sign(String transactionInput) {
        byte[] inputBytes = Coder.hexToByte(transactionInput);
        return new BigInteger(privateKey.sign(inputBytes)).toString(16);
    }

    public String getPublicKey() {
        return Coder.byteToHex(privateKey.getPubKey());
    }

}
