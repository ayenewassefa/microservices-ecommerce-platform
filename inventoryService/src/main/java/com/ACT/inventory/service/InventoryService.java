package com.ACT.inventory.service;

import com.ACT.inventory.dto.InventoryResponse;
import com.ACT.inventory.model.Inventory;
import com.ACT.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public List<InventoryResponse> isInStock(List<String> productIds) {
        return productIds.stream()
                .map(productId -> {
                    Inventory inventory = inventoryRepository.findByProductId(productId)  // ✅ Updated
                            .orElse(null);
                    if (inventory == null) {
                        return InventoryResponse.builder()
                                .productId(productId)
                                .inStock(false)
                                .build();
                    }
                    return mapToResponse(inventory);
                })
                .collect(Collectors.toList());
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .productId(inventory.getProductId())
                .productName(inventory.getProductName())
                .description(inventory.getDescription())
                .quantity(inventory.getQuantity())
                .inStock(inventory.getQuantity() > 0)
                .price(inventory.getPrice())
                .category(inventory.getCategory())
                .brand(inventory.getBrand())
                .needsReorder(inventory.getQuantity() < 10)
                .build();
    }
}