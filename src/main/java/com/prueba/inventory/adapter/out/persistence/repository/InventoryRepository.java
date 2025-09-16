package com.prueba.inventory.adapter.out.persistence.repository;

import com.prueba.inventory.adapter.out.persistence.entity.InventoryEntity;
import com.prueba.inventory.adapter.out.persistence.entity.InventoryEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryEntity, InventoryEntityId> {
    boolean existsByStoreId(String storeId);
}
