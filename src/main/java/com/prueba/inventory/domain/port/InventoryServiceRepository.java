package com.prueba.inventory.domain.port;

import com.prueba.inventory.domain.model.CommitCancelRequest;
import com.prueba.inventory.domain.model.Inventory;
import java.util.Map;
import java.util.Optional;

public interface InventoryServiceRepository {
    Optional<Inventory> getInventory(String storeId, String productId);
    String reserve(String storeId, String productId, int quantity);
    void commit(String storeId, String productId, CommitCancelRequest req);
    void cancel(String storeId, String productId, CommitCancelRequest req);
    Inventory adjust(String storeId, String productId, int delta);
    Map<String,Integer> aggregateByProduct(String productId);

}
