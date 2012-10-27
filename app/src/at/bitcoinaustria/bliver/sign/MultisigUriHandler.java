package at.bitcoinaustria.bliver.sign;

import at.bitcoinaustria.bliver.MultisigUri;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.WrongNetworkException;
import com.google.bitcoin.uri.BitcoinURI;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author apetersson
 */
public class MultisigUriHandler {

    final Signer signer;

    public MultisigUriHandler(Signer signer) {
        this.signer = signer;
    }

    private final static String testurl = "multisig:server-url=http%3A%2F%2F10.200.1.73%2Fmultisig&order-id=123&order-description=testbestellung%20123";

    public BitcoinURI fromMultisigUri(MultisigUri uri) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(uri.server_url);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("public-key", signer.getPublicKey()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity responseEntity =  Preconditions.checkNotNull(response.getEntity());
            String responseStr = EntityUtils.toString(responseEntity);
            ArrayList<String> lines = Lists.newArrayList(Splitter.on('\n').split(responseStr));
            String multisigAddr = lines.get(0);

            Address address = new Address(null, multisigAddr);
            String ret = BitcoinURI.convertToBitcoinURI(address, uri.amount.toBigInteger(), "order:" + uri.orderDesc, null);
            return new BitcoinURI(ret);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (WrongNetworkException e) {
            throw new RuntimeException(e);
        } catch (AddressFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
