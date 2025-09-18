package com.prueba.inventory.infrastructure.adapter.out.persistence.repository;

import com.prueba.inventory.infrastructure.adapter.out.persistence.entity.InventoryEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryEventRepository extends JpaRepository<InventoryEventEntity, String> {
}
