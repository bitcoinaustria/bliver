package at.bitcoinaustria.bliver.db;

public enum OrderStatus {

    RECEIVED_MULTISIG("initial signature requested"),
    RECEIVED_PAYMENT_ADDRESS("payment requested"),
    AWAITING_DELIVERY("awaiting delivery"),
    SENT_CONFIRMATION("confirmation sent"),
    RECEIVED_TRANSACTION("received"),
    CANCELED("canceled");

    private final String caption;

    private OrderStatus(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

}
