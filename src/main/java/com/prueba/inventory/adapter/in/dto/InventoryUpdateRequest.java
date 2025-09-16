package com.prueba.inventory.adapter.in.dto;

import com.prueba.inventory.domain.port.in.InventoryUpdateCommand;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class InventoryUpdateRequest implements InventoryUpdateCommand {

    @NotBlank(message = "storeId no puede estar vacío")
    private String storeId;

    @NotBlank(message = "productId no puede estar vacío")
    private String productId;

    @Min(value = 0, message = "quantity no puede ser negativo")
    private int quantity;
}
