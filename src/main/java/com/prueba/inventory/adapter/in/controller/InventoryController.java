package com.prueba.inventory.adapter.in.controller;

import com.prueba.inventory.adapter.in.dto.InventoryUpdateRequest;
import com.prueba.inventory.application.service.InventoryService;
import com.prueba.inventory.domain.model.Inventory;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateInventory(@Valid @RequestBody InventoryUpdateRequest command) {
        log.info("Petición recibida para actualizar inventario: {}", command);

        Inventory updatedInventory = inventoryService.updateInventory(command);

        log.info("Inventario actualizado y evento enviado a Kafka. productId={}",
                updatedInventory.getProductId());

        // 202 Accepted es apropiado porque la actualización es async (event-driven)
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(updatedInventory);
    }
}
