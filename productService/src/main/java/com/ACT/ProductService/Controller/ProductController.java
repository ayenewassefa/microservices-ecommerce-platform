package com.ACT.ProductService.Controller;

import com.ACT.ProductService.Dto.ProductRequest;
import com.ACT.ProductService.Dto.ProductResponse;
import com.ACT.ProductService.Dto.ProductStockResponse;
import com.ACT.ProductService.Model.Product;
import com.ACT.ProductService.Repository.ProductRepository;
import com.ACT.ProductService.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;
  private final ProductRepository productRepository;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('admin')")
  public void createProduct(@RequestBody ProductRequest productRequest) {
    productService.createProduct(productRequest);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<ProductResponse> getAllProducts() {
    return productService.getAllProducts();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public ProductResponse getProductById(@PathVariable String id) {
    return productService.getProductById(id);
  }

  @GetMapping("/{id}/stock")
  @ResponseStatus(HttpStatus.OK)
  public ProductStockResponse getProductStock(@PathVariable String id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    return ProductStockResponse.builder()
            .productId(product.getId())
            .stock(product.getStock())
            .inStock(product.getStock() > 0)
            .build();
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('admin')")
  public ProductResponse updateProduct(@PathVariable String id, @RequestBody ProductRequest request) {
    return productService.updateProduct(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('admin')")
  public void deleteProduct(@PathVariable String id) {
    productService.deleteProduct(id);
  }
}