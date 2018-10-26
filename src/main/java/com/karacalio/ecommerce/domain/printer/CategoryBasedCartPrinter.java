package com.karacalio.ecommerce.domain.printer;

import com.karacalio.ecommerce.domain.Cart;
import com.karacalio.ecommerce.model.cart.CartItem;
import com.karacalio.ecommerce.model.store.Category;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CategoryBasedCartPrinter implements CartPrinter {

    @Override
    public void print(Cart cart) {
        List<CartItem> cartItems = cart.getItems();

        // Group by categories
        Map<Category, List<CartItem>> cartItemsMapByCategory = groupCartItemsByProductCategory(cartItems);

        // Sort categories alphabetically and print
        cartItemsMapByCategory
                .keySet()
                .stream()
                .sorted(Comparator.comparing(Category::getTitle))
                .forEach(category -> {
                    printCategory(category.getTitle());
                    printCartItemsInCategory(cartItemsMapByCategory.get(category));
                    printCategorySeparator();
                });

        printTotalCartPriceAndDiscounts(cart);
    }

    private Map<Category, List<CartItem>> groupCartItemsByProductCategory(List<CartItem> cartItems) {
        return cartItems.stream()
                .collect(Collectors.groupingBy(cartItem -> cartItem.getProduct().getCategory()));
    }

    private void printCategory(String category) {
        System.out.println("Products In Category : " + category);
    }

    private void printCartItemsInCategory(List<CartItem> cartItems) {
        cartItems.forEach(cartItem -> {
            System.out.println("+ Product Name : " + cartItem.getProduct().getTitle());
            System.out.println("  Quantity : " + cartItem.getQuantity());
            System.out.println("  Unit Price : " + cartItem.getProduct().getPrice());

            double totalPrice = cartItem.getProduct().getPrice() * cartItem.getQuantity();
            System.out.println("  Total Price : " + totalPrice);
            System.out.println();
        });
    }

    private void printCategorySeparator() {
        System.out.println("---");
    }

    private void printTotalCartPriceAndDiscounts(Cart cart) {
        System.out.println("Cart Total Price : " + cart.getTotalAmountAfterDiscounts());
        System.out.println("Delivery Cost : " + cart.getDeliveryCost());
    }
}
