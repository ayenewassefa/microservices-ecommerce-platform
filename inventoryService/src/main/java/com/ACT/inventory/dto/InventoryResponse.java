package com.ACT.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    private String productId;
    private String productName;
    private String description;
    private Integer quantity;
    private boolean inStock;
    private BigDecimal price;
    private String category;
    private String brand;
    private boolean needsReorder;
}