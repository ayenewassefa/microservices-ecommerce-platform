package com.ACT.OrderService.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemsDto {
    private String productId;
    private BigDecimal price;
    private Integer quantity;
    private String productName;
}