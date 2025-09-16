package com.prueba.inventory.adapter.in.controller;


import com.prueba.inventory.domain.model.Inventory;
import com.prueba.inventory.domain.port.in.InventoryQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryQueryController {
    private final InventoryQueryService inventoryQueryService;

    public InventoryQueryController(InventoryQueryService inventoryQueryService) {
        this.inventoryQueryService = inventoryQueryService;
    }

    @GetMapping
    public ResponseEntity<?> getInventory(@RequestParam(required = true) String storeId,
                                          @RequestParam(required = true) String productId) {

        if (storeId == null || storeId.isBlank() || productId == null || productId.isBlank()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Bad Request");
            error.put("status", HttpStatus.BAD_REQUEST.value());
            error.put("message", List.of("storeId y productId no pueden estar vacíos"));
            return ResponseEntity.badRequest().body(error);
        }
        Inventory inventory = inventoryQueryService.getInventory(storeId, productId);
            return ResponseEntity.ok(inventory);

    }
}
