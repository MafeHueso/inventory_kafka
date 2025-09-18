package com.prueba.inventory.infrastructure.adapter.out.kafka;

import com.prueba.inventory.domain.model.InventoryEvent;
import com.prueba.inventory.domain.port.InventoryEventProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class InventoryEventProducerKafkaImpl implements InventoryEventProducer {

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    public InventoryEventProducerKafkaImpl(KafkaTemplate<String, InventoryEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendEvent(InventoryEvent inventoryEvent) {
        if (inventoryEvent.getEventId() == null) {
            inventoryEvent.setEventId(java.util.UUID.randomUUID().toString());
        }
        // Enviar el evento de actualización al tópico Kafka
        kafkaTemplate.send("inventory-events", inventoryEvent.getRequestId(), inventoryEvent);
    }

    // Para publicar estado eventual en topic compactado
    public void sendState(InventoryEvent stateEvent) {
        if (stateEvent.getEventId() == null) {
            stateEvent.setEventId(UUID.randomUUID().toString());
        }
        if (stateEvent.getTimestamp() == null) {
            stateEvent.setTimestamp(Instant.now());
        }
        String key = stateEvent.getStoreId() + ":" + stateEvent.getProductId();
        kafkaTemplate.send("inventory-state", key, stateEvent);
    }
}
