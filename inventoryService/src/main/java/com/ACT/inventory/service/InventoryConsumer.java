package com.ACT.inventory.service;

import com.ACT.inventory.model.Inventory;
import com.ACT.inventory.repository.InventoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryConsumer {

    private final InventoryRepository inventoryRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "notificationTopic", groupId = "inventory-group")
    @Transactional
    public void consumeOrderEvent(String message) {
        try {
            log.info(" Inventory Service received message: {}", message);

            JsonNode jsonNode = objectMapper.readTree(message);
            String orderNumber = jsonNode.get("orderNumber").asText();
            log.info("Order event received for order: {}", orderNumber);

            JsonNode itemsNode = jsonNode.get("items");
            if (itemsNode != null && itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    String productId = itemNode.get("productId").asText();
                    int quantity = itemNode.get("quantity").asInt();

                    decreaseStock(productId, quantity);
                }
                log.info("Stock updated for order: {}", orderNumber);
            } else {
                log.warn(" No items found in event for order: {}", orderNumber);
            }

        } catch (Exception e) {
            log.error("Failed to process inventory event: {}", e.getMessage(), e);
        }
    }



    private void decreaseStock(String productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseGet(() -> {
                    log.info(" Product not found in inventory, creating new record for productId: {}", productId);
                    Inventory newInventory = Inventory.builder()
                            .productId(productId)
                            .quantity(0)  // Start with 0, then deduct
                            .build();
                    return inventoryRepository.save(newInventory);
                });

        int newQuantity = inventory.getQuantity() - quantity;

        // Prevent negative stock (optional, but good for business logic)
        if (newQuantity < 0) {
            log.warn(" Stock would become negative! Product: {}, Current: {}, Requested: {}",
                    productId, inventory.getQuantity(), quantity);
            newQuantity = 0;
        }

        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);
        log.info("Stock updated for product {} | New quantity: {}", productId, newQuantity);
    }
}