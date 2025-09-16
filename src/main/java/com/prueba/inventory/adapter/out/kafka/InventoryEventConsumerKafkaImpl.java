package com.prueba.inventory.adapter.out.kafka;

import com.prueba.inventory.adapter.out.persistence.entity.InventoryEntityId;
import com.prueba.inventory.adapter.out.persistence.entity.ProcessedEvent;
import com.prueba.inventory.adapter.out.persistence.mapper.InventoryMapper;
import com.prueba.inventory.adapter.out.persistence.repository.InventoryRepository;
import com.prueba.inventory.adapter.out.persistence.repository.ProcessedEventRepository;
import com.prueba.inventory.domain.model.Inventory;
import com.prueba.inventory.domain.port.out.InventoryEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InventoryEventConsumerKafkaImpl implements InventoryEventConsumer {

    private final InventoryRepository inventoryRepository;
    private final ProcessedEventRepository processedEventRepository;
    private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumerKafkaImpl.class);

    public InventoryEventConsumerKafkaImpl(
            InventoryRepository inventoryRepository,
            ProcessedEventRepository processedEventRepository) {
        this.inventoryRepository = inventoryRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Override
    @Transactional
    @KafkaListener(topics = "inventory-topic", groupId = "inventory-group", containerFactory = "inventoryKafkaListenerContainerFactory")
    public void consumeInventoryEvent(Inventory event) {

        if (event.getEventId() == null) {
            log.warn("Evento sin eventId recibido. Podría no ser idempotente.");
        } else if (processedEventRepository.existsById(event.getEventId())) {
            log.info("Evento duplicado ignorado: {}", event.getEventId());
            return; //Evita reprocesar
        }

        if (event.getQuantity() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        log.info("Evento recibido: store={}, product={}, quantity={}",
                event.getStoreId(), event.getProductId(), event.getQuantity());

        InventoryEntityId id = new InventoryEntityId(event.getStoreId(), event.getProductId());

        inventoryRepository.findById(id)
                .ifPresentOrElse(existing -> {
                    if (existing.getQuantity() != event.getQuantity()) {
                        existing.setQuantity(event.getQuantity());
                        inventoryRepository.save(existing);
                        log.info("Inventario actualizado: {}", id);
                    } else {
                        log.info("Evento ignorado (idempotente): misma cantidad que la actual.");
                    }
                }, () -> {
                    // Inserta si no existe
                    inventoryRepository.save(InventoryMapper.toEntity(event));
                    log.info("Inventario creado: {}", id);
                });
        if (event.getEventId() != null) { //Registrar que el evento fue procesado
            processedEventRepository.save(new ProcessedEvent(
                    event.getEventId(), "INVENTORY_UPDATE", System.currentTimeMillis()
            ));
        }
    }
}
