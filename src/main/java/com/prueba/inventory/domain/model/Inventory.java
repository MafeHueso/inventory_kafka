package com.prueba.inventory.domain.model;

public class Inventory {

    private String storeId;
    private String productId;
    private int quantity;
    private String eventId;

    public Inventory() {}

    public Inventory(String storeId, String productId, int quantity) {
        this(storeId, productId, quantity, null);
    }

    public Inventory(String storeId, String productId, int quantity, String eventId) {
        this.storeId = storeId;
        this.productId = productId;
        this.quantity = quantity;
        this.eventId = eventId;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
}