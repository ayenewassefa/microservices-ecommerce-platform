package com.ACT.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequest {
    private String productId;
    private String productName;
    private String description;
    private Integer quantity;
    private BigDecimal price;
    private String category;
    private String brand;
}