package com.prueba.inventory.infrastructure.adapter.out.persistence.repository;

import com.prueba.inventory.infrastructure.adapter.out.persistence.entity.InventoryEntity;
import com.prueba.inventory.infrastructure.adapter.out.persistence.entity.InventoryEntityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryEntity, InventoryEntityId> {
       List<InventoryEntity> findByProductId(String productId);
}
