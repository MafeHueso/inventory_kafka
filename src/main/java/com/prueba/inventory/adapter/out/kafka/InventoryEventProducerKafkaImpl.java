package com.prueba.inventory.adapter.out.kafka;

import com.prueba.inventory.domain.model.Inventory;
import com.prueba.inventory.domain.port.out.InventoryEventProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventProducerKafkaImpl implements InventoryEventProducer {

    private final KafkaTemplate<String, Inventory> kafkaTemplate;

    public InventoryEventProducerKafkaImpl(KafkaTemplate<String, Inventory> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendUpdateEvent(Inventory inventory) {
        if (inventory.getEventId() == null) {
            inventory.setEventId(java.util.UUID.randomUUID().toString());
        }
        // Enviar el evento de actualización al tópico Kafka
        kafkaTemplate.send("inventory-topic", inventory.getProductId(), inventory);
    }
}
