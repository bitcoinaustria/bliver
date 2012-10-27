package at.bitcoinaustria.bliver;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * @author apetersson
 */
public class MultisigURI {

    public static final String ENCODING = Charsets.UTF_8.displayName();

    public final URI server_url;
    public final String orderID;
    public final String orderDesc;

    public MultisigURI(URI server_url, String orderID, String orderDesc) {
        this.server_url = server_url;
        this.orderID = orderID;
        this.orderDesc = orderDesc;
    }

    public static MultisigURI from(URI input){
        Preconditions.checkArgument(input.getScheme().equals("multisig"));
        Map<String,String> values = Splitter.on("&").withKeyValueSeparator("=").split(input.getRawSchemeSpecificPart());
        try {
            return new MultisigURI(
                    new URI(decode(values, "server-url"))
                            ,decode(values,"order-id")
                            ,decode(values,"order-description"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static MultisigURI from(String input){
        try {
            return from(new URI(input));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    private static String decode(Map<String, String> values, String key) {
        try {
            return URLDecoder.decode(values.get(key), ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
