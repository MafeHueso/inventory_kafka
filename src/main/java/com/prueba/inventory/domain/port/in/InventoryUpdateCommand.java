package com.prueba.inventory.domain.port.in;

public interface InventoryUpdateCommand {
    String getStoreId();
    String getProductId();
    int getQuantity();
}
