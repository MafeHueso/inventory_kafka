package com.prueba.inventory.domain.port.out;

import com.prueba.inventory.domain.model.Inventory;

public interface InventoryEventConsumer {
    void consumeInventoryEvent(Inventory inventory);
}
