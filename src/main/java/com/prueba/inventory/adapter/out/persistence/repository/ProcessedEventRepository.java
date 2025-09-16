package com.prueba.inventory.adapter.out.persistence.repository;

import com.prueba.inventory.adapter.out.persistence.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}
