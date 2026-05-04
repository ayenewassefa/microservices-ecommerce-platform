package com.Act.inventory.repository;

import com.Act.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // Note the "In" at the end of the method name
    List<Inventory> findBySkuCodeIn(List<String> skuCode);
}