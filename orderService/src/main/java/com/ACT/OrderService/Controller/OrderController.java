package com.ACT.OrderService.Controller;

import com.ACT.OrderService.Dto.OrderRequest;
import com.ACT.OrderService.Service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> placeOrder(
            @RequestBody OrderRequest orderRequest,
            @RequestHeader(value = "Authorization", required = false) String token) {  // ✅ Accept token
        String result = orderService.placeOrder(orderRequest, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}