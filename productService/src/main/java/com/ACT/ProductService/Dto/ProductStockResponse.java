package com.ACT.ProductService.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockResponse {
    private String productId;
    private Integer stock;
    private boolean inStock;
}