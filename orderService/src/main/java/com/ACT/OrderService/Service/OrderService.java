package com.ACT.OrderService.Service;

import com.ACT.OrderService.Dto.InventoryResponse;
import com.ACT.OrderService.Dto.OrderLineItemsDto;
import com.ACT.OrderService.Dto.OrderRequest;
import com.ACT.OrderService.Event.orderPlacedEvent;
import com.ACT.OrderService.Model.Order;
import com.ACT.OrderService.Model.OrderLineItems;
import com.ACT.OrderService.Repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 1. ADD THIS IMPORT
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j // 2. ADD THIS ANNOTATION - This creates the 'log' variable
public class OrderService {
    private final ObservationRegistry observationRegistry;
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, orderPlacedEvent> kafkaTemplate;

    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    public String placeOrder(OrderRequest orderRequest) {
        return Observation.createNotStarted("place-order-span", observationRegistry)
                .observe(() -> {
                    log.info("Now placing order..."); // Now 'log' will work!

                    Order order = new Order();
                    order.setOrderNumber(UUID.randomUUID().toString());

                    List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                            .stream()
                            .map(this::mapToDto)
                            .toList();

                    order.setOrderLineItemsList(orderLineItems);

                    List<String> skuCodes = order.getOrderLineItemsList().stream()
                            .map(OrderLineItems::getSkuCode)
                            .toList();

                    // Call Inventory Service
                    InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                            .uri("http://inventory-service/api/inventory",
                                    uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                            .retrieve()
                            .bodyToMono(InventoryResponse[].class)
                            .block();

                    boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                            .allMatch(InventoryResponse::isInStock);

                    if (allProductsInStock) {
                        orderRepository.save(order);
                        kafkaTemplate.send("notification-topic",new orderPlacedEvent(order.getOrderNumber());
                        return "Order Placed Successfully";
                    } else {
                        throw new IllegalArgumentException("Product is not in stock, please try again later");
                    }
                }); // 3. Corrected the closing brace and semicolon for the Observation block
    }

    // Fallback method stays outside the observation block
    public String fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        log.error("Order Service failed: {}", runtimeException.getMessage());
        return "Oops! Something went wrong, please order after some time!";
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}