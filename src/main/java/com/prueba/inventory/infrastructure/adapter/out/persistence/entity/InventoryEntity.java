package com.prueba.inventory.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Version;
import lombok.*;

@Entity
@IdClass(InventoryEntityId.class)
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEntity {

    @Id
    private String storeId;

    @Id
    private String productId;

    private int quantity;

    private int delta;

    @Version
    private Long version;
}
