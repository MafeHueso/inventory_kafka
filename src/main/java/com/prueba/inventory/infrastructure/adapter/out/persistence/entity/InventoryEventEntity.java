package com.prueba.inventory.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEventEntity {

    @Id
    private String eventId;

    private String eventType;
    private String requestId;
    private String storeId;
    private String productId;
    private int quantity;
    private Instant timestamp;
}