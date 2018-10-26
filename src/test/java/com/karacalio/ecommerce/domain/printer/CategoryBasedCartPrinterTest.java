package com.karacalio.ecommerce.domain.printer;

import com.karacalio.ecommerce.domain.Cart;
import com.karacalio.ecommerce.model.cart.CartItem;
import com.karacalio.ecommerce.model.store.Category;
import com.karacalio.ecommerce.model.store.Product;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CategoryBasedCartPrinterTest {

    private PrintStream originalSystemOut;
    private PrintStream systemOut;
    private ByteArrayOutputStream outContent;

    @Mock
    Cart cart;

    @Before
    public void setup() {
        originalSystemOut = System.out;
        outContent = new ByteArrayOutputStream();
        systemOut = new PrintStream(outContent);
        System.setOut(systemOut);
    }

    @After
    public void tearDown() {
        System.setOut(originalSystemOut);
    }

    @Test
    public void print_shouldPrintAllCartItemsInShoppingCartByCategory_withGivenCartItems() {
        // Given
        Category xCategory = new Category("X Category");
        Category yCategory = new Category("Y Category");

        List<CartItem> cartItems = Arrays.asList(
                new CartItem(new Product("X Product", xCategory, 20.00), 1),
                new CartItem(new Product("X1 Product", xCategory, 20.00), 1),
                new CartItem(new Product("Y Product", yCategory, 40.00), 3)
        );

        when(cart.getItems()).thenReturn(cartItems);
        when(cart.getTotalAmountAfterDiscounts()).thenReturn(160.00);
        when(cart.getDeliveryCost()).thenReturn(20.00);

        CategoryBasedCartPrinter categoryBasedCartPrinter = new CategoryBasedCartPrinter();

        String expectedOutput = "Products In Category : X Category\n" +
                "+ Product Name : X Product\n" +
                "  Quantity : 1\n" +
                "  Unit Price : 20.0\n" +
                "  Total Price : 20.0\n" +
                "\n" +
                "+ Product Name : X1 Product\n" +
                "  Quantity : 1\n" +
                "  Unit Price : 20.0\n" +
                "  Total Price : 20.0\n" +
                "\n" +
                "---\n" +
                "Products In Category : Y Category\n" +
                "+ Product Name : Y Product\n" +
                "  Quantity : 3\n" +
                "  Unit Price : 40.0\n" +
                "  Total Price : 120.0\n" +
                "\n" +
                "---\n" +
                "Cart Total Price : 160.0\n" +
                "Delivery Cost : 20.0\n";

        // When
        categoryBasedCartPrinter.print(cart);

        // Then
        Assertions.assertThat(outContent.toString()).isEqualTo(expectedOutput);
    }
}