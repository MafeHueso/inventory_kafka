package com.prueba.inventory.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ReserveRequestDTO {
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private int quantity;
}