package com.prueba.inventory.application.service;


import com.prueba.inventory.application.exception.*;
import com.prueba.inventory.domain.model.CommitCancelRequest;
import com.prueba.inventory.domain.model.Inventory;
import com.prueba.inventory.infrastructure.adapter.out.kafka.InventoryEventProducerKafkaImpl;
import com.prueba.inventory.infrastructure.adapter.out.persistence.entity.InventoryEntity;
import com.prueba.inventory.infrastructure.adapter.out.persistence.entity.InventoryEntityId;
import com.prueba.inventory.infrastructure.adapter.out.persistence.entity.Reservation;
import com.prueba.inventory.infrastructure.adapter.out.persistence.mapper.InventoryMapper;
import com.prueba.inventory.infrastructure.adapter.out.persistence.repository.InventoryRepository;
import com.prueba.inventory.domain.model.InventoryEvent;
import com.prueba.inventory.domain.port.InventoryServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InventoryService implements InventoryServiceRepository {
    private final InventoryRepository inventoryRepository;
    private final Map<String, String> processedRequests = new ConcurrentHashMap<>();
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();
    private final InventoryEventProducerKafkaImpl kafkaProducer;
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    public InventoryService(InventoryRepository inventoryRepository,
                            InventoryEventProducerKafkaImpl kafkaProducer) {
        this.inventoryRepository = inventoryRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public Optional<Inventory> getInventory(String storeId, String productId) {
        InventoryEntityId id = new InventoryEntityId(storeId, productId);
        return inventoryRepository.findById(id)
                .map(InventoryMapper::toDomain);
    }

    @Transactional
    @Override
    public String reserve(String storeId, String productId, int quantity) {

        String requestId = UUID.randomUUID().toString();

        InventoryEntityId id = new InventoryEntityId(storeId, productId);
        InventoryEntity item = inventoryRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));

        if (item.getQuantity() < quantity) {
            throw new InsufficientStockException("Stock insuficiente");
        }

        item.setQuantity(item.getQuantity() - quantity);
        inventoryRepository.save(item);

        reservations.put(requestId, new Reservation(storeId, productId, quantity));

        // publish RESERVED event
        InventoryEvent ev = new InventoryEvent(UUID.randomUUID().toString(), "RESERVED",
                requestId, storeId, productId, quantity, Instant.now());
        kafkaProducer.sendEvent(ev);

        processedRequests.put(requestId, "RESERVED");
        return requestId;
    }

    @Transactional
    @Override
    public void commit(String storeId, String productId, CommitCancelRequest req) {
        Reservation reservation = reservations.get(req.getRequestId());
        if ("CANCEL".equals(processedRequests.get(req.getRequestId()))) {
            throw new ReservationCancelException("Esta reserva ya fue cancelada, no puede ser confirmada.");
        }

        if (reservation == null) {
            throw new ReservationNotFoundException("Reserva no encontrada para confirmación " + req.getRequestId());
        }

        if (!reservation.getStoreId().equals(storeId) || !reservation.getProductId().equals(productId)) {
            throw new ReservationNotApplicable("Reserva no corresponde a este producto/tienda");
        }

        InventoryEvent ev = new InventoryEvent(UUID.randomUUID().toString(), "COMMIT",
                req.getRequestId(), storeId, productId, reservation.getQuantity(), Instant.now());
        kafkaProducer.sendEvent(ev);

        processedRequests.put(req.getRequestId(), "COMMIT");
        reservations.remove(req.getRequestId());
    }

    @Transactional
    @Override
    public void cancel(String storeId, String productId, CommitCancelRequest req) {
        log.info("Cancel request: {}", req);
        log.info("Current reservations: {}", reservations.keySet());
        Reservation reservation = reservations.remove(req.getRequestId());

        if ("COMMIT".equals(processedRequests.get(req.getRequestId()))) {
            throw new ReservationConfirmedException("Esta reserva ya fue confirmada, no puede ser cancelada.");
        }

        if (reservation == null) {
            throw new ReservationNotFoundException("Reserva no encontrada para cancelación" + req.getRequestId());
        }

        if (!reservation.getStoreId().equals(storeId) || !reservation.getProductId().equals(productId)) {
            throw new ReservationNotApplicable("Reserva no corresponde a este producto/tienda");
        }

        InventoryEntityId id = new InventoryEntityId(storeId, productId);
        InventoryEntity item = inventoryRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));

        item.setQuantity(item.getQuantity() + reservation.getQuantity());
        inventoryRepository.save(item);
        // publish CANCEL event
        InventoryEvent ev = new InventoryEvent(UUID.randomUUID().toString(), "CANCEL",
                req.getRequestId(), storeId, productId, item.getQuantity(), Instant.now());
        kafkaProducer.sendEvent(ev);

        processedRequests.put(req.getRequestId(), "CANCEL");
    }

    @Transactional
    @Override
    public Inventory adjust(String storeId, String productId, int delta) {

        InventoryEntityId id = new InventoryEntityId(storeId, productId);
        InventoryEntity item = inventoryRepository.findById(id)
                .orElse(InventoryEntity.builder()
                        .storeId(storeId)
                        .productId(productId)
                        .quantity(0)
                        .build());

        item.setQuantity(item.getQuantity() + delta);

        inventoryRepository.save(item);

        InventoryEntity refreshedItem = inventoryRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));

        return InventoryMapper.toDomain(refreshedItem);

    }

    @Override
    public Map<String,Integer> aggregateByProduct(String productId) {
        List<InventoryEntity> items = inventoryRepository.findByProductId(productId);
        Map<String,Integer> map = new HashMap<>();
        for (InventoryEntity i: items) map.put(i.getStoreId(), i.getQuantity());
        return map;
    }

}
