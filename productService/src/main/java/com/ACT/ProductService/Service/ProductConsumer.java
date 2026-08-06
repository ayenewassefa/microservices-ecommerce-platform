package com.ACT.ProductService.Service;

import com.ACT.ProductService.Model.Product;
import com.ACT.ProductService.Repository.ProductRepository;
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
public class ProductConsumer {

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "notificationTopic", groupId = "product-group")
    @Transactional
    public void consumeOrderEvent(String message) {
        try {
            log.info(" Product Service received message: {}", message);

            JsonNode jsonNode = objectMapper.readTree(message);
            String orderNumber = jsonNode.get("orderNumber").asText();
            log.info("Processing order: {}", orderNumber);

            JsonNode itemsNode = jsonNode.get("items");
            if (itemsNode != null && itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    String productId = itemNode.get("productId").asText(); // we'll send productId
                    int quantity = itemNode.get("quantity").asInt();

                    decreaseProductStock(productId, quantity);
                }
                log.info(" Stock updated for order: {}", orderNumber);
            } else {
                log.warn("No items found in event for order: {}", orderNumber);
            }

        } catch (Exception e) {
            log.error("Failed to process inventory event: {}", e.getMessage(), e);
        }
    }

    private void decreaseProductStock(String productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        int newStock = product.getStock() - quantity;
        if (newStock < 0) {
            log.warn("Stock would become negative! Product: {}, Current: {}, Requested: {}",
                    productId, product.getStock(), quantity);
            newStock = 0;
        }

        product.setStock(newStock);
        productRepository.save(product);
        log.info("Stock updated for product {} | New stock: {}", productId, newStock);
    }
}