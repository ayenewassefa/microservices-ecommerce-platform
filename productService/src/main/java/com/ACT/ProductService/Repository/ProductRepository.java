package com.ACT.ProductService.Repository;

import com.ACT.ProductService.Model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByProductName(String productName);
}