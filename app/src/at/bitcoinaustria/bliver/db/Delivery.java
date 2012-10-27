package at.bitcoinaustria.bliver.db;

import at.bitcoinaustria.bliver.Bitcoins;

public class Delivery {

    private Long id;
    private String serverUrl;
    private String orderId;
    private String orderDescription;
    private OrderStatus orderStatus;
    private Bitcoins amount;
    private String multisigAddress;
    private String txInputHash;
    private String txId;

    public Delivery() {
    }

    public Delivery(String serverUrl, String orderId, String orderDescription, OrderStatus orderStatus, Bitcoins amount, String multisigAddress, String txInputHash, String txId) {
        this.serverUrl = serverUrl;
        this.orderId = orderId;
        this.orderDescription = orderDescription;
        this.orderStatus = orderStatus;
        this.amount = amount;
        this.multisigAddress = multisigAddress;
        this.txInputHash = txInputHash;
        this.txId = txId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Bitcoins getAmount() {
        return amount;
    }

    public void setAmount(Bitcoins amount) {
        this.amount = amount;
    }

    public String getMultisigAddress() {
        return multisigAddress;
    }

    public void setMultisigAddress(String multisigAddress) {
        this.multisigAddress = multisigAddress;
    }

    public String getTxInputHash() {
        return txInputHash;
    }

    public void setTxInputHash(String txInputHash) {
        this.txInputHash = txInputHash;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    @Override
    /* used as label in list view! */
    public String toString() {
        return getOrderDescription();
    }

}
