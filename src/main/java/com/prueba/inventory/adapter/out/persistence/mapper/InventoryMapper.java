package com.prueba.inventory.adapter.out.persistence.mapper;

import com.prueba.inventory.adapter.out.persistence.entity.InventoryEntity;
import com.prueba.inventory.domain.model.Inventory;

public class InventoryMapper {
    public static InventoryEntity toEntity(Inventory domain) {
        return InventoryEntity.builder()
                .storeId(domain.getStoreId())
                .productId(domain.getProductId())
                .quantity(domain.getQuantity())
                .build();
    }

    public static Inventory toDomain(InventoryEntity entity) {
        return new Inventory(entity.getStoreId(), entity.getProductId(), entity.getQuantity());
    }
}
