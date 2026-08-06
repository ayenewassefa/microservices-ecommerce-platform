package com.ACT.OrderService.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String id;
    private String orderNumber;
    private List<OrderLineItemsDto> orderLineItemsList;
    private LocalDateTime createdAt;
    private String status;
}