package com.ACT.OrderService.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    private String productName;
}