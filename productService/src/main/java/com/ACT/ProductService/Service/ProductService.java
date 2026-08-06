package com.ACT.ProductService.Service;

import com.ACT.ProductService.Dto.ProductRequest;
import com.ACT.ProductService.Dto.ProductResponse;
import com.ACT.ProductService.Model.Product;
import com.ACT.ProductService.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
        Integer requestStock = productRequest.getStock() != null ? productRequest.getStock() : 0;
        Optional<Product> existingProduct = productRepository.findByProductName(productRequest.getProductName());

        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            int newStock = product.getStock() + productRequest.getStock();
            product.setStock(newStock);
            productRepository.save(product);
            log.info("Product already exists. Stock increased by {} | New stock: {}",
                    productRequest.getStock(), newStock);
            return;
        }

        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .productDescription(productRequest.getProductDescription())
                .productPrice(productRequest.getProductPrice())
                .stock(productRequest.getStock())
                .build();

        productRepository.save(product);
        log.info(" New product created: {}", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return mapToProductResponse(product);
    }

    public ProductResponse updateProduct(String id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setProductName(request.getProductName());
        product.setProductDescription(request.getProductDescription());
        product.setProductPrice(request.getProductPrice());
        product.setStock(request.getStock());

        Product updated = productRepository.save(product);
        log.info(" Product updated: {}", updated.getId());
        return mapToProductResponse(updated);
    }

    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepository.delete(product);
        log.info(" Product deleted: {}", id);
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .productName(product.getProductName())
                .productDescription(product.getProductDescription())
                .productPrice(product.getProductPrice())
                .stock(product.getStock())
                .build();
    }
}