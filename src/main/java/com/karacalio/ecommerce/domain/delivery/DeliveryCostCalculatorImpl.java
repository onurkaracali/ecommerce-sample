package com.karacalio.ecommerce.domain.delivery;

import com.karacalio.ecommerce.domain.Cart;
import com.karacalio.ecommerce.model.cart.CartItem;
import com.karacalio.ecommerce.model.store.Product;

import java.util.List;

public class DeliveryCostCalculatorImpl implements DeliveryCostCalculator {

    private final double costPerDelivery;
    private final double costPerProduct;
    private final double fixedCost;

    public DeliveryCostCalculatorImpl(double costPerDelivery, double costPerProduct, double fixedCost) {
        this.costPerDelivery = costPerDelivery;
        this.costPerProduct = costPerProduct;
        this.fixedCost = fixedCost;
    }

    @Override
    public double calculateFor(Cart cart) {
        List<CartItem> cartItems = cart.getItems();

        long numberOfDeliveries = cartItems.stream()
                .map(CartItem::getProduct)
                .map(Product::getCategory)
                .distinct()
                .count();

        Integer numberOfProducts = cartItems.stream()
                .map(cartItem -> cartItem.getQuantity())
                .reduce(0, Integer::sum);

        return (costPerDelivery * numberOfDeliveries) + (costPerProduct * numberOfProducts) + fixedCost;
    }
}
