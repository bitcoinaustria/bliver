package at.bitcoinaustria.bliver;

import at.bitcoinaustria.bliver.db.Delivery;
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
public class MultisigUri {

    public static final String ENCODING = Charsets.UTF_8.displayName();

    public final URI server_url;
    public final String orderID;
    public final String orderDesc;
    public final Bitcoins amount;

    public MultisigUri(Delivery delivery) {
        this(URI.create(delivery.getServerUrl()), delivery.getOrderId(), delivery.getOrderDescription(), delivery.getAmount());
    }

    public static void main(String[] args) {

    }

    public MultisigUri(URI server_url, String orderID, String orderDesc, Bitcoins amount) {
        this.server_url = server_url;
        this.orderID = orderID;
        this.orderDesc = orderDesc;
        this.amount = amount;
    }

    public static MultisigUri from(URI input) {
        Preconditions.checkArgument(input.getScheme().equals("multisig"));
        Map<String, String> values = Splitter.on("&").withKeyValueSeparator("=").split(input.getRawSchemeSpecificPart());
        try {
            return new MultisigUri(
                    new URI(decode(values, "server-url"))
                    , decode(values, "order-id")
                    , decode(values, "order-description")
                    , Bitcoins.valueOf(Long.parseLong(decode(values, "amount")))
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static MultisigUri from(String input) {
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

    @Override
    public String toString() {
        return "MultisigUri{" +
                "server_url=" + server_url +
                ", orderID='" + orderID + '\'' +
                ", orderDesc='" + orderDesc + '\'' +
                '}';
    }
}
