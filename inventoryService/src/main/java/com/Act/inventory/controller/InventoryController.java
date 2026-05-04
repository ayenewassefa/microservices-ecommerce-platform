package com.Act.inventory.controller;

import com.Act.inventory.dto.InventoryResponse;
import com.Act.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // We use @RequestParam now to support multiple SKU codes
    // URL: http://localhost:8083/api/inventory?skuCode=iphone_13&skuCode=iphone_13_red
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }
}