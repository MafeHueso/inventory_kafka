package com.prueba.inventory.domain.port;

import com.prueba.inventory.domain.model.InventoryEvent;

public interface InventoryEventConsumer {
    void consumerInventoryEvent(InventoryEvent event);
}
