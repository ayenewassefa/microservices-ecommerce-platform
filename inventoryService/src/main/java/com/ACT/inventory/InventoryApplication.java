package com.ACT.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
        System.out.println("=========================================");
        System.out.println("INVENTORY SERVICE STARTED");
        System.out.println("http://localhost:8083");
        System.out.println("=========================================");
    }
}