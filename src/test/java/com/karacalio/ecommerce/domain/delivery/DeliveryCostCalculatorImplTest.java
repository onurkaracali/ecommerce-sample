package com.karacalio.ecommerce.domain.delivery;

import com.karacalio.ecommerce.model.cart.CartItem;
import com.karacalio.ecommerce.domain.ShoppingCart;
import com.karacalio.ecommerce.model.store.Category;
import com.karacalio.ecommerce.model.store.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeliveryCostCalculatorImplTest {

    @Mock
    ShoppingCart shoppingCart;

    @Test
    public void calculateFor_shouldReturnDeliveryCost() {
        // Given
        Category xCategory = new Category("X Category");
        Category yCategory = new Category("Y Category");
        List<CartItem> productList = Arrays.asList(
                new CartItem(new Product("Product X", xCategory, 40.00),2),
                new CartItem(new Product("Product Y", yCategory, 20.00), 1)
        );

        when(shoppingCart.getItems()).thenReturn(productList);

        DeliveryCostCalculatorImpl defaultDeliveryCostCalculator =
                new DeliveryCostCalculatorImpl(15.00, 10.00, 2.99);

        // When
        double deliveryCost = defaultDeliveryCostCalculator.calculateFor(shoppingCart);

        // Then
        assertThat(deliveryCost).isEqualTo(62.99);
    }
}