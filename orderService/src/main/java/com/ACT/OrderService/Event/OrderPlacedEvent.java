package com.ACT.OrderService.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent {
    private String orderNumber;
    private List<OrderItem> items;

    public OrderPlacedEvent(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}