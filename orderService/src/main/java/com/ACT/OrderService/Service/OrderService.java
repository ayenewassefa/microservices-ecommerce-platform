package com.ACT.OrderService.Service;

import com.ACT.OrderService.Dto.OrderLineItemsDto;
import com.ACT.OrderService.Dto.OrderRequest;
import com.ACT.OrderService.Dto.OrderResponse;
import com.ACT.OrderService.Dto.ProductStockResponse;
import com.ACT.OrderService.Event.OrderItem;
import com.ACT.OrderService.Event.OrderPlacedEvent;
import com.ACT.OrderService.Model.Order;
import com.ACT.OrderService.Model.OrderLineItems;
import com.ACT.OrderService.Repository.OrderRepository;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final ObservationRegistry observationRegistry;
    private final OrderRepository orderRepository;
    private final WebClient webClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    // ✅ Accept token parameter
    public String placeOrder(OrderRequest orderRequest, String token) {
        return Observation.createNotStarted("place-order-span", observationRegistry)
                .observe(() -> {
                    log.info("=== Place Order Request Received ===");

                    // Validate stock for each product
                    for (OrderLineItemsDto item : orderRequest.getOrderLineItemsDtoList()) {
                        boolean isAvailable = checkProductAvailability(item.getProductId(), item.getQuantity(), token);
                        if (!isAvailable) {
                            log.warn("❌ Product {} has insufficient stock!", item.getProductId());
                            throw new IllegalArgumentException(
                                    "Product " + item.getProductId() + " has insufficient stock! Requested: " + item.getQuantity()
                            );
                        }
                    }

                    // Create and save order
                    Order order = new Order();
                    order.setOrderNumber(UUID.randomUUID().toString());
                    order.setStatus("CONFIRMED");

                    List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                            .stream()
                            .map(this::mapToDto)
                            .collect(Collectors.toList());
                    order.setOrderLineItemsList(orderLineItems);

                    orderRepository.save(order);
                    log.info("✅ Order saved: {}", order.getOrderNumber());

                    // Publish Kafka event
                    publishOrderEvent(order, orderLineItems);

                    log.info("✅ Order CONFIRMED and Kafka event sent: {}", order.getOrderNumber());
                    return "Order Placed Successfully";
                });
    }

    // ✅ Check availability with token
    private boolean checkProductAvailability(String productId, int requestedQuantity, String token) {
        try {
            ProductStockResponse response = getProductStock(productId, token);
            log.info("Product {} stock: {}, requested: {}", productId, response.getStock(), requestedQuantity);
            return response.getStock() >= requestedQuantity;
        } catch (Exception e) {
            log.error("❌ Failed to check stock for product {}: {}", productId, e.getMessage());
            return false;
        }
    }

    // ✅ Get product stock with token
    private ProductStockResponse getProductStock(String productId, String token) {
        WebClient.RequestHeadersSpec<?> request = webClient.get()
                .uri("http://localhost:8081/api/product/" + productId + "/stock");

        // If token is provided, add it to the Authorization header
        if (token != null && !token.isEmpty()) {
            request.header("Authorization", token);
        }

        return request.retrieve()
                .bodyToMono(ProductStockResponse.class)
                .block();
    }

    private void publishOrderEvent(Order order, List<OrderLineItems> orderLineItems) {
        try {
            List<OrderItem> items = orderLineItems.stream()
                    .map(item -> new OrderItem(
                            item.getProductId(),
                            item.getQuantity(),
                            item.getPrice(),
                            item.getProductName()
                    ))
                    .collect(Collectors.toList());

            OrderPlacedEvent event = new OrderPlacedEvent(order.getOrderNumber(), items);
            kafkaTemplate.send("notificationTopic", event);
            log.info("✅ Kafka event sent for order: {} | Items: {}", order.getOrderNumber(), items.size());

        } catch (Exception e) {
            log.error("❌ Failed to send Kafka event: {}", e.getMessage(), e);
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto dto) {
        OrderLineItems item = new OrderLineItems();
        item.setProductId(dto.getProductId());
        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        item.setProductName(dto.getProductName());
        return item;
    }

    private OrderLineItemsDto mapToLineItemDto(OrderLineItems item) {
        OrderLineItemsDto dto = new OrderLineItemsDto();
        dto.setProductId(item.getProductId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setProductName(item.getProductName());
        return dto;
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return mapToOrderResponse(order);
    }

    public OrderResponse getOrderByNumber(String orderNumber) {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .filter(order -> order.getOrderNumber().equals(orderNumber))
                .findFirst()
                .map(this::mapToOrderResponse)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderLineItemsDto> itemsDto = order.getOrderLineItemsList().stream()
                .map(this::mapToLineItemDto)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderLineItemsList(itemsDto)
                .createdAt(order.getCreatedAt())
                .status(order.getStatus())
                .build();
    }
}