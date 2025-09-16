package com.prueba.inventory.domain.port.in;

import com.prueba.inventory.domain.model.Inventory;

public interface InventoryQueryService {
    Inventory getInventory(String storeId, String productId);
}
