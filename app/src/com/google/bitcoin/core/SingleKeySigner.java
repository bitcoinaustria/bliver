package com.google.bitcoin.core;

import at.bitcoinaustria.bliver.Coder;
import at.bitcoinaustria.bliver.Net;
import at.bitcoinaustria.bliver.Signer;

/**
 * @author apetersson
 */
public class SingleKeySigner {


    public static final String DEMO_TX = "AQAAAAFggi6YBu1ZfEccwTYRyXaL3MH4C3ggKQO2pMDFd/5gsgEAAAC2AEkwRgIhALyusQtuYHOS" +
            "SpbsK53aUqlW8Lie/xF/Jpm9/k5aOdujAiEA9pygz9RDZHXggdV2muiJZ9/YME7mjJZySwLmVoCr" +
            "j9cBTGlSIQL8crn5nLRlKnu7iwhQsHvnkl0Y4gq3VwcSBmFFkcUCOiEDsI325nNhm5P8DdOb5w17" +
            "9WhzJB/P3p6HMy15uH3oD80hAj16J2iFVDWyIQA8sj8m2VCk7iLz1HyYM3eDJtIhJTr8U67/////" +
            "AYCWmAAAAAAAGXapFMNCegWDDBBOzBtIfys0G+q2v9OpiKwAAAAA";

    public static void main(String[] args) {
        try {
            Transaction demoTx = new Transaction(Net.NETWORK, Coder.base64Decode(DEMO_TX));
            Transaction signed = new SingleKeySigner(Signer.DEMO_SIGNER).signed(demoTx);
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
    }

    final Signer signer;

    public SingleKeySigner(Signer signer) {
        this.signer = signer;
    }

    public Transaction signed(Transaction transaction) {
        byte[] connectedPubKeyScript = transaction.getInputs().get(0).getOutpoint().getConnectedPubKeyScript();
        Sha256Hash sha256Hash = transaction.hashTransactionForSignature(0, connectedPubKeyScript, Transaction.SigHash.SINGLE, false);
        String sign = signer.sign(sha256Hash.toString());
        byte[] scriptBytes = transaction.getInputs().get(0).getScriptBytes();
        try {
            Script script = new Script(Net.NETWORK, scriptBytes, 0, scriptBytes.length);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        return null;

    }

}
