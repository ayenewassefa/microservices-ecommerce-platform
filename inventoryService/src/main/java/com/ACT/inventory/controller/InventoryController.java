package com.ACT.inventory.controller;

import com.ACT.inventory.dto.InventoryRequest;
import com.ACT.inventory.dto.InventoryResponse;
import com.ACT.inventory.model.Inventory;
import com.ACT.inventory.repository.InventoryRepository;
import com.ACT.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('admin')")
    public List<InventoryResponse> isInStock(@RequestParam List<String> productId) {
        return inventoryService.isInStock(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin')")
    public InventoryResponse addInventory(@RequestBody InventoryRequest request) {
        Inventory existingInventory = inventoryRepository.findByProductId(request.getProductId())
                .orElse(null);

        if (existingInventory != null) {
            existingInventory.setQuantity(existingInventory.getQuantity() + request.getQuantity());
            existingInventory.setProductName(request.getProductName());
            existingInventory.setDescription(request.getDescription());
            existingInventory.setPrice(request.getPrice());
            existingInventory.setCategory(request.getCategory());
            existingInventory.setBrand(request.getBrand());
            inventoryRepository.save(existingInventory);
            return mapToResponse(existingInventory);
        } else {
            Inventory inventory = Inventory.builder()
                    .productId(request.getProductId())
                    .productName(request.getProductName())
                    .description(request.getDescription())
                    .quantity(request.getQuantity())
                    .price(request.getPrice())
                    .category(request.getCategory())
                    .brand(request.getBrand())
                    .build();
            inventoryRepository.save(inventory);
            return mapToResponse(inventory);
        }
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