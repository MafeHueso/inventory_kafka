package com.prueba.inventory.domain.port;

import com.prueba.inventory.domain.model.InventoryEvent;

public interface InventoryEventProducer {
    void sendEvent(InventoryEvent inventoryEvent);
    void sendState(InventoryEvent stateEvent);
}
