package com.ACT.ProductService.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;  // ← IMPORT THIS!

import java.math.BigDecimal;

@Document(value = "Product")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {
    @Id
    private String id;

    @Field("productName")
    private String productName;

    @Field("productDescription")
    private String productDescription;

    @Field("productPrice")
    private BigDecimal productPrice;
    @Field(name = "stock")
    private Integer stock;

}