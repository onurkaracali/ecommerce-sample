package com.karacalio.ecommerce.domain;

import com.karacalio.ecommerce.model.cart.CartItem;
import com.karacalio.ecommerce.model.store.Category;
import com.karacalio.ecommerce.model.store.Product;

import java.util.List;
import java.util.Optional;

public interface ItemCollection {

    void insertItem(Product product, int quantity);

    Optional<CartItem> getItemsByProduct(Product product);

    List<CartItem> getItemsByCategory(Category category);

    List<Category> getCategories();

    List<CartItem> getItems();
}
