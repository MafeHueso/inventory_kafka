package com.prueba.inventory.infrastructure.adapter.out.persistence.mapper;

import com.prueba.inventory.infrastructure.adapter.out.persistence.entity.InventoryEntity;
import com.prueba.inventory.domain.model.Inventory;
import com.prueba.inventory.domain.model.InventoryEvent;

public class InventoryMapper {
  public static Inventory toDomain(InventoryEntity entity) {
        if (entity == null) return null;

        return new Inventory(
                entity.getStoreId(),
                entity.getProductId(),
                entity.getQuantity(),
                null,
                0,
                entity.getVersion()
        );
    }

    public static InventoryEntity toEntity(InventoryEvent event) {
        return InventoryEntity.builder()
                .storeId(event.getStoreId())
                .productId(event.getProductId())
                .quantity(event.getQuantity())
                .build();
    }
}
