package com.prueba.inventory.domain.port.out;

import com.prueba.inventory.domain.model.Inventory;

public interface InventoryEventProducer {
    void sendUpdateEvent(Inventory inventory);
}
