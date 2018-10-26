package com.karacalio.ecommerce.domain;

import com.karacalio.ecommerce.model.cart.CartItem;
import com.karacalio.ecommerce.model.store.Category;
import com.karacalio.ecommerce.model.store.Product;

import java.util.*;
import java.util.stream.Collectors;

public class ProductCollection implements ItemCollection {

    private Map<Product, CartItem> productCartItemMap;

    ProductCollection() {
        productCartItemMap = new HashMap<>();
    }

    @Override
    public void insertItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity should be greater than 0");
        }

        CartItem cartItem = Optional.ofNullable(productCartItemMap.get(product))
                .orElse(new CartItem(product, 0));
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        productCartItemMap.put(product, cartItem);
    }

    @Override
    public Optional<CartItem> getItemsByProduct(Product product) {
        return Optional.ofNullable(productCartItemMap.get(product));
    }

    @Override
    public List<CartItem> getItemsByCategory(Category category) {
        return productCartItemMap.values()
                .stream()
                .filter(cartItem -> cartItem.getProduct().getCategory().equals(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> getCategories() {
        return productCartItemMap.values().stream()
                .map(CartItem::getProduct)
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<CartItem> getItems() {
        return new ArrayList<>(productCartItemMap.values());
    }

}
