package com.karacalio.ecommerce.model.cart;

import com.karacalio.ecommerce.model.store.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartItem {
    private Product product;
    private int quantity;
}
