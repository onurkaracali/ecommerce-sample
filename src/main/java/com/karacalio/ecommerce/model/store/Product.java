package com.karacalio.ecommerce.model.store;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
    private String title;
    private Category category;
    private double price;
}
