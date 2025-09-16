package com.prueba.inventory.adapter.out;

import com.prueba.inventory.adapter.out.exception.ProductNotFoundException;
import com.prueba.inventory.adapter.out.exception.StoreNotFoundException;
import com.prueba.inventory.adapter.out.persistence.entity.InventoryEntityId;
import com.prueba.inventory.adapter.out.persistence.mapper.InventoryMapper;
import com.prueba.inventory.adapter.out.persistence.repository.InventoryRepository;
import com.prueba.inventory.domain.model.Inventory;
import com.prueba.inventory.domain.port.in.InventoryQueryService;
import org.springframework.stereotype.Service;

@Service
public class InventoryQueryServiceImpl implements InventoryQueryService {
    private final InventoryRepository inventoryRepository;

    public InventoryQueryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Inventory getInventory(String storeId, String productId) {
        boolean storeExists = inventoryRepository.existsByStoreId(storeId);
        if (!storeExists) {
            throw new StoreNotFoundException("Tienda no encontrada");
        }
        return inventoryRepository.findById(new InventoryEntityId(storeId, productId))
                .map(InventoryMapper::toDomain)
                .orElseThrow(() -> new ProductNotFoundException("Producto no encontrado"));
    }


}
