package com.ACT.ProductService.Controller;

import com.ACT.ProductService.Dto.ProductRequest;
import com.ACT.ProductService.Dto.ProductResponse;
import com.ACT.ProductService.Model.Product;
import com.ACT.ProductService.Service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

  private  final ProductService productService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public String createProduct(@RequestBody ProductRequest productRequest) {
    productService.createProduct(productRequest);
    return "Product Created Successfully";
  }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
       return productService.getAllProducts();
    }
}