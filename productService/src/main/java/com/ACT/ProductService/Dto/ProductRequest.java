package com.ACT.ProductService.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ProductRequest {
private String productName;
private String productDescription;
private BigDecimal productPrice;
private Integer stock;
}
