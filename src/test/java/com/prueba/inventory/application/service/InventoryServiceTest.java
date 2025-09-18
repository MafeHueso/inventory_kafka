package com.prueba.inventory.application.service;

import com.prueba.inventory.application.exception.InsufficientStockException;
import com.prueba.inventory.application.exception.ReservationConfirmedException;
import com.prueba.inventory.application.exception.ReservationNotFoundException;
import com.prueba.inventory.domain.model.CommitCancelRequest;
import com.prueba.inventory.domain.model.Inventory;
import com.prueba.inventory.domain.model.InventoryEvent;
import com.prueba.inventory.infrastructure.adapter.out.kafka.InventoryEventProducerKafkaImpl;
import com.prueba.inventory.infrastructure.adapter.out.persistence.entity.InventoryEntity;
import com.prueba.inventory.infrastructure.adapter.out.persistence.entity.InventoryEntityId;
import com.prueba.inventory.infrastructure.adapter.out.persistence.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    private InventoryRepository inventoryRepository;
    private InventoryEventProducerKafkaImpl kafkaProducer;
    private InventoryService inventoryService;

    @BeforeEach
    public void setUp() {
        inventoryRepository = mock(InventoryRepository.class);
        kafkaProducer = mock(InventoryEventProducerKafkaImpl.class);
        inventoryService = new InventoryService(inventoryRepository, kafkaProducer);
    }

    @Test
    public void testGetInventory_ProductFound() {
        InventoryEntity mockInventoryEntity = InventoryEntity.builder()
                .storeId("1")
                .productId("123")
                .quantity(10)
                .version(1L)
                .build();

        InventoryEntityId expectedId = new InventoryEntityId("1", "123");
        when(inventoryRepository.findById(expectedId)).thenReturn(Optional.of(mockInventoryEntity));

        Optional<Inventory> result = inventoryService.getInventory("1", "123");

        ArgumentCaptor<InventoryEntityId> captor = ArgumentCaptor.forClass(InventoryEntityId.class);
        verify(inventoryRepository).findById(captor.capture());
        System.out.println("Id capturado: " + captor.getValue());

        assertTrue(result.isPresent());
        assertEquals(10, result.get().getQuantity());
    }

    @Test
    public void testGetInventory_ProductNotFound() {
        InventoryEntityId id = new InventoryEntityId("1", "123");
        when(inventoryRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Inventory> result = inventoryService.getInventory("1", "123");

        assertFalse(result.isPresent());
    }

    @Test
    public void testReserve_Success() {

        InventoryEntity mockInventoryEntity = InventoryEntity.builder()
                .storeId("1")
                .productId("123")
                .quantity(10)
                .version(1L)
                .build();

        InventoryEntityId id = new InventoryEntityId("1", "123");

        when(inventoryRepository.findById(id)).thenReturn(Optional.of(mockInventoryEntity));
        when(inventoryRepository.save(any(InventoryEntity.class))).thenReturn(mockInventoryEntity);

        doNothing().when(kafkaProducer).sendEvent(any(InventoryEvent.class));

        String requestId = inventoryService.reserve("1", "123", 5);

        assertNotNull(requestId);

        assertEquals(5, mockInventoryEntity.getQuantity());

        verify(kafkaProducer, times(1)).sendEvent(any(InventoryEvent.class));

       verify(inventoryRepository).save(mockInventoryEntity);
    }

    @Test
    public void testReserve_InsufficientStock() {

        InventoryEntity mockInventoryEntity = InventoryEntity.builder()
                .storeId("1")
                .productId("123")
                .quantity(10)  // stock disponible
                .version(1L)
                .build();

        InventoryEntityId id = new InventoryEntityId("1", "123");

        when(inventoryRepository.findById(id)).thenReturn(Optional.of(mockInventoryEntity));

        assertThrows(InsufficientStockException.class, () -> {
            inventoryService.reserve("1", "123", 15);
        });

        verify(inventoryRepository, never()).save(any());
        verify(kafkaProducer, never()).sendEvent(any());
    }

    @Test
    public void testCommit_Success() {

        InventoryEntity mockInventoryEntity = InventoryEntity.builder()
                .storeId("1")
                .productId("123")
                .quantity(10)
                .version(1L)
                .build();

        InventoryEntityId id = new InventoryEntityId("1", "123");
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(mockInventoryEntity));
        when(inventoryRepository.save(any(InventoryEntity.class))).thenReturn(mockInventoryEntity);

        doNothing().when(kafkaProducer).sendEvent(any(InventoryEvent.class));

        String requestId = inventoryService.reserve("1", "123", 5);

        CommitCancelRequest req = new CommitCancelRequest(requestId);

        inventoryService.commit("1", "123", req);

        assertThrows(ReservationConfirmedException.class, () -> {
            inventoryService.cancel("1", "123", req);
        });

        verify(kafkaProducer, times(2)).sendEvent(any(InventoryEvent.class));
    }

    @Test
    public void testCommit_ReservationNotFound() {
        CommitCancelRequest req = new CommitCancelRequest("invalidRequestId");

        assertThrows(ReservationNotFoundException.class, () -> {
            inventoryService.commit("1", "123", req);
        });
    }

    @Test
    public void testCancel_Success() {

        InventoryEntity mockInventoryEntity = InventoryEntity.builder()
                .storeId("1")
                .productId("123")
                .quantity(10)
                .version(1L)
                .build();

        InventoryEntityId id = new InventoryEntityId("1", "123");
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(mockInventoryEntity));
        when(inventoryRepository.save(any(InventoryEntity.class))).thenReturn(mockInventoryEntity);

        doNothing().when(kafkaProducer).sendEvent(any(InventoryEvent.class));

        String requestId = inventoryService.reserve("1", "123", 5);

        CommitCancelRequest req = new CommitCancelRequest(requestId);

        inventoryService.cancel("1", "123", req);

        assertThrows(ReservationNotFoundException.class, () -> {
            inventoryService.cancel("1", "123", req);
        });

        verify(kafkaProducer, times(2)).sendEvent(any(InventoryEvent.class));
    }

    @Test
    public void testCancel_ReservationNotFound() {
        CommitCancelRequest req = new CommitCancelRequest("invalidRequestId");

        assertThrows(ReservationNotFoundException.class, () -> {
            inventoryService.cancel("1", "123", req);
        });
    }

    @Test
    public void testAdjust_ValidAdjustment() {

        InventoryEntity mockInventoryEntity = InventoryEntity.builder()
                .storeId("1")
                .productId("123")
                .quantity(10)
                .version(1L)
                .build();

        InventoryEntityId id = new InventoryEntityId("1", "123");

        when(inventoryRepository.findById(id)).thenReturn(Optional.of(mockInventoryEntity));
        when(inventoryRepository.save(any(InventoryEntity.class))).thenReturn(mockInventoryEntity);
        when(inventoryRepository.findById(id)).thenReturn(Optional.of(mockInventoryEntity));

        Inventory updatedInventory = inventoryService.adjust("1", "123", 5);

        assertEquals(15, updatedInventory.getQuantity());
    }

    @Test
    public void testAdjust_ProductNotFound() {
        InventoryEntityId id = new InventoryEntityId("1", "123");

        when(inventoryRepository.findById(id))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(
                        InventoryEntity.builder()
                                .storeId("1")
                                .productId("123")
                                .quantity(5)
                                .build()
                ));

        when(inventoryRepository.save(any(InventoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Inventory updatedInventory = inventoryService.adjust("1", "123", 5);

        assertEquals(5, updatedInventory.getQuantity());
    }

    @Test
    public void testAggregateByProduct_Success() {

         InventoryEntity store1 = InventoryEntity.builder()
                .storeId("1")
                .productId("123")
                .quantity(10)
                .build();

        InventoryEntity store2 = InventoryEntity.builder()
                .storeId("2")
                .productId("123")
                .quantity(15)
                .build();

        when(inventoryRepository.findByProductId("123")).thenReturn(List.of(store1, store2));

        Map<String, Integer> aggregated = inventoryService.aggregateByProduct("123");

        assertEquals(2, aggregated.size());
        assertEquals(10, aggregated.get("1"));
        assertEquals(15, aggregated.get("2"));
    }



}
