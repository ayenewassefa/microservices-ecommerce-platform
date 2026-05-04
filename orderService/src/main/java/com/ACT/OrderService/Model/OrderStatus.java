package com.ACT.OrderService.Model;

public enum OrderStatus {
    PENDING("Order is pending confirmation"),
    CONFIRMED("Order has been confirmed"),
    SHIPPED("Order has been shipped"),
    DELIVERED("Order has been delivered"),
    CANCELLED("Order has been cancelled");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
