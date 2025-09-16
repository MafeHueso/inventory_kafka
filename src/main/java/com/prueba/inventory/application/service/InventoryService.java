package com.prueba.inventory.application.service;

import com.prueba.inventory.domain.model.Inventory;
import com.prueba.inventory.domain.port.in.InventoryUpdateCommand;
import com.prueba.inventory.domain.port.out.InventoryEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {  //Actualizar el inventario a través de eventos enviados por Kafka.

    private final InventoryEventProducer inventoryEventProducer;
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    public InventoryService(InventoryEventProducer inventoryEventProducer) {
        this.inventoryEventProducer = inventoryEventProducer;
    }

    public Inventory updateInventory(InventoryUpdateCommand command) {
        log.info("Recibida solicitud de actualización: storeId={}, productId={}, quantity={}",
                command.getStoreId(), command.getProductId(), command.getQuantity());

        try {
            Inventory inventory = new Inventory(command.getStoreId(),
                    command.getProductId(),
                    command.getQuantity());
            inventoryEventProducer.sendUpdateEvent(inventory);
            log.info("Evento enviado a Kafka para productId={}", command.getProductId());
            return inventory;
        } catch (Exception e) {
            log.error("Error enviando evento a Kafka", e);
            throw e;
        }

    }


}
