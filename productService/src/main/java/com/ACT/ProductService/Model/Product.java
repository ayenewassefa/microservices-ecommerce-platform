package com.ACT.ProductService.Model;

import lombok.*;

import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import org.springframework.data.annotation.Id;

@Document(value="Product")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {
    @Id
    private String id;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;


}
