package com.prueba.inventory.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
 public class StockAdjustmentRequest {
    @NotNull(message = "delta es obligatorio")
    private int delta;
}
