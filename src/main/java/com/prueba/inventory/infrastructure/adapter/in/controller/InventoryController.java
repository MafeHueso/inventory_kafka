package com.prueba.inventory.infrastructure.adapter.in.controller;


import com.prueba.inventory.domain.model.CommitCancelRequest;
import com.prueba.inventory.domain.model.Inventory;
import com.prueba.inventory.infrastructure.adapter.in.dto.CommitCancelRequestDTO;
import com.prueba.inventory.application.service.InventoryService;
import com.prueba.inventory.infrastructure.adapter.in.dto.ReserveRequestDTO;
import com.prueba.inventory.infrastructure.adapter.in.dto.StockAdjustmentRequest;
import com.prueba.inventory.infrastructure.adapter.in.mapper.CommitCancelRequestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryQueryService;

    public InventoryController(InventoryService inventoryQueryService) {
        this.inventoryQueryService = inventoryQueryService;
    }

    @Operation(summary = "Consultar stock disponible")
    @ApiResponse(responseCode = "200", description = "Stock encontrado")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @GetMapping("/{storeId}/{productId}")
    public ResponseEntity<?> getStock(@PathVariable @NotBlank String storeId,
                                      @PathVariable @NotBlank String productId) {
        if ("null".equalsIgnoreCase(storeId) || "null".equalsIgnoreCase(productId)) {
            return ResponseEntity.badRequest().body(
                    Map.of("status", 400, "error", "Bad Request", "details", "storeId/productId inválidos")
            );
        }
        return inventoryQueryService.getInventory(storeId, productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Reservar unidades de stock")
    @ApiResponse(responseCode = "200", description = "Reserva exitosa")
    @ApiResponse(responseCode = "409", description = "Stock insuficiente")
    @PostMapping("/{storeId}/{productId}/reserve")
    public ResponseEntity<?> reserve(@PathVariable @NotBlank String storeId,
                                     @PathVariable @NotBlank String productId,
                                     @Valid @RequestBody ReserveRequestDTO reqDto) {
        try {
            String requestId = inventoryQueryService.reserve(storeId, productId, reqDto.getQuantity());

            return ResponseEntity.ok(
                    Map.of("status", 200,
                            "message", "Reserva exitosa",
                            "storeId", storeId,
                            "productId", productId,
                            "quantity", reqDto.getQuantity(),
                            "requestId", requestId)
            );
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(
                    Map.of("status", 409, "error", "Conflict", "details", e.getMessage())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(
                    Map.of("status", 404, "error", "Not Found", "details", e.getMessage())
            );
        }
    }

    @Operation(summary = "Confirmar reserva (venta finalizada)")
    @PostMapping("/{storeId}/{productId}/commit")
    public ResponseEntity<?> commit(@PathVariable @NotBlank String storeId,
                                    @PathVariable @NotBlank String productId,
                                    @Valid @RequestBody CommitCancelRequestDTO reqDto) {

        CommitCancelRequest req = CommitCancelRequestMapper.fromDTO(reqDto);
        inventoryQueryService.commit(storeId, productId, req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cancelar reserva")
    @PostMapping("/{storeId}/{productId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable @NotBlank String storeId,
                                    @PathVariable @NotBlank String productId,
                                    @Valid @RequestBody CommitCancelRequestDTO reqDto) {

        CommitCancelRequest req = CommitCancelRequestMapper.fromDTO(reqDto);
        inventoryQueryService.cancel(storeId, productId, req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Ajuste manual de stock")
    @PatchMapping("/{storeId}/{productId}")
    public ResponseEntity<?> adjust(@PathVariable @NotBlank String storeId,
                                    @PathVariable @NotBlank String productId,
                                    @Valid @RequestBody StockAdjustmentRequest delta) {
        Inventory updated = inventoryQueryService.adjust(storeId, productId, delta.getDelta());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Vista global por producto")
    @GetMapping("/{productId}/global")
    public ResponseEntity<?> globalView(@PathVariable @NotBlank String productId) {
        return ResponseEntity.ok(inventoryQueryService.aggregateByProduct(productId));
    }

}
