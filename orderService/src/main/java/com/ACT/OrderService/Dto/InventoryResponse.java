package com.ACT.OrderService.Dto;

import lombok.Data;

@Data
public class InventoryResponse {
    private String skuCode;
    private boolean isInStock;
}

