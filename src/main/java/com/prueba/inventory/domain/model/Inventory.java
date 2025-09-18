package com.prueba.inventory.domain.model;

public class Inventory {

    private String storeId;
    private String productId;
    private int quantity;
    private String eventId;
    private int delta;
    private Long version;


    public Inventory() {}

    public Inventory(String storeId, String productId, int quantity, int delta, Long version) {
        this(storeId, productId, quantity, null, delta, version);
    }

    public Inventory(String storeId, String productId, int quantity, String eventId, int delta, Long version) {
        this.storeId = storeId;
        this.productId = productId;
        this.quantity = quantity;
        this.eventId = eventId;
        this.delta = delta;
        this.version = version;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
}