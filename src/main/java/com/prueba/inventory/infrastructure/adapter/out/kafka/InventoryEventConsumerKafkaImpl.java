package com.prueba.inventory.infrastructure.adapter.out.kafka;

import com.prueba.inventory.infrastructure.adapter.out.persistence.entity.InventoryEntity;
import com.prueba.inventory.infrastructure.adapter.out.persistence.entity.InventoryEntityId;
import com.prueba.inventory.infrastructure.adapter.out.persistence.entity.InventoryEventEntity;
import com.prueba.inventory.infrastructure.adapter.out.persistence.mapper.InventoryMapper;
import com.prueba.inventory.infrastructure.adapter.out.persistence.repository.InventoryRepository;
import com.prueba.inventory.infrastructure.adapter.out.persistence.repository.InventoryEventRepository;
import com.prueba.inventory.domain.model.InventoryEvent;
import com.prueba.inventory.domain.port.InventoryEventConsumer;
import com.prueba.inventory.domain.port.InventoryEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class InventoryEventConsumerKafkaImpl implements InventoryEventConsumer {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventRepository processedEventRepository;
    private final InventoryEventProducer producer;
    private static final Logger log = LoggerFactory.getLogger(InventoryEventConsumerKafkaImpl.class);

    public InventoryEventConsumerKafkaImpl(
            InventoryRepository inventoryRepository,
            InventoryEventRepository processedEventRepository,
            InventoryEventProducer producer) {
        this.inventoryRepository = inventoryRepository;
        this.processedEventRepository = processedEventRepository;
        this.producer = producer;
    }

    @Override
    @Transactional
    @KafkaListener(topics = "inventory-events", groupId = "inventory-group", containerFactory = "inventoryKafkaListenerContainerFactory")
    public void consumerInventoryEvent(InventoryEvent event) {

        if (processedEventRepository.existsById(event.getEventId())) {
            log.info("Evento duplicado ignorado: {}", event.getEventId());
            return;
        }

        if (event.getQuantity() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        log.info("Evento recibido: store={}, product={}, quantity={}",
                event.getStoreId(), event.getProductId(), event.getQuantity());

        InventoryEntityId id = new InventoryEntityId(event.getStoreId(), event.getProductId());

        InventoryEntity item = inventoryRepository.findById(id)
                .orElse(InventoryMapper.toEntity(event));

        switch (event.getEventType()) {
            case "RESERVED":
                item.setQuantity(item.getQuantity() - event.getQuantity());
                break;
            case "CANCEL":
            case "RESERVATION_EXPIRED":
                item.setQuantity(item.getQuantity() + event.getQuantity());
                break;
            case "COMMIT":
                // stock ya se descontó en RESERVED
                break;
            case "STATE":
                // opcional: sincronización desde otra fuente
                break;
            default:
                log.warn("Tipo de evento desconocido: {}", event.getEventType());
        }

        inventoryRepository.save(item);

        processedEventRepository.save(new InventoryEventEntity(
                event.getEventId(),
                event.getEventType(),
                event.getRequestId(),
                event.getStoreId(),
                event.getProductId(),
                event.getQuantity(),
                event.getTimestamp()
        ));

        InventoryEvent stateEvent = new InventoryEvent(
                UUID.randomUUID().toString(),
                "STATE",
                event.getRequestId(),
                event.getStoreId(),
                event.getProductId(),
                item.getQuantity(),
                Instant.now()
        );
        producer.sendState(stateEvent);

    }
}
