package com.prueba.inventory.application.command;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryUpdateCommand {
    private String storeId;
    private String productId;
    private int quantity;
}
