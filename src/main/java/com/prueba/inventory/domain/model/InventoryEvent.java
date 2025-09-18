package com.prueba.inventory.domain.model;

import java.time.Instant;

public class InventoryEvent {
    private String eventId;
    private String eventType;
    private String requestId;
    private String storeId;
    private String productId;
    private int quantity;
    private Instant timestamp;

    public InventoryEvent() {}

    public InventoryEvent(String eventId, String eventType, String requestId, String storeId, String productId, int quantity, Instant timestamp) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.requestId = requestId;
        this.storeId = storeId;
        this.productId = productId;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
