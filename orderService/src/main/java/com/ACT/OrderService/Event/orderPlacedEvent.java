package com.ACT.OrderService.Event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class orderPlacedEvent {
    private String orderNumber;


}
