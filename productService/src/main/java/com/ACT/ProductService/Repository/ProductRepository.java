package com.ACT.ProductService.Repository;

import com.ACT.ProductService.Model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
