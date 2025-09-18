package com.prueba.inventory.infrastructure.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Reservation {
    private String storeId;
    private String productId;
    private int quantity;
}