package com.karacalio.ecommerce.domain;

import com.karacalio.ecommerce.model.cart.CartItem;
import com.karacalio.ecommerce.model.store.Category;
import com.karacalio.ecommerce.model.store.Product;

import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ProductCollectionTest {

    @Test
    public void insertItem_shouldInsertNewProductWithQuantity_when_productIsNotInCollection() {
        // Given
        Category category = new Category("Books");
        Product product = new Product("Effective Java Book", category, 40D);
        ProductCollection productCollection = new ProductCollection();
        int quantity = 2;

        // When
        productCollection.insertItem(product, quantity);

        // Then
        assertThat(productCollection.getItems()).isNotNull();
        assertThat(productCollection.getItems()).hasSize(1);
        assertThat(productCollection.getItems().get(0).getProduct()).isEqualTo(product);
        assertThat(productCollection.getItems().get(0).getQuantity()).isEqualTo(quantity);
    }

    @Test
    public void insertItem_shouldIncrementQuantity_when_productAlreadyAddedToCollection() {
        // Given
        Category category = new Category("Books");
        Product product = new Product("Effective Java Book", category, 40D);
        ProductCollection productCollection = new ProductCollection();
        int quantity = 2;
        int expectedQuantity = 4;

        // When
        productCollection.insertItem(product, quantity);
        productCollection.insertItem(product, quantity);

        // Then
        assertThat(productCollection.getItems()).isNotNull();
        assertThat(productCollection.getItems()).hasSize(1);
        assertThat(productCollection.getItems().get(0).getProduct()).isEqualTo(product);
        assertThat(productCollection.getItems().get(0).getQuantity()).isEqualTo(expectedQuantity);
    }

    @Test
    public void insertItem_shouldThrowException_when_quantityIsLowerThanOne() {
        // Given
        Category category = new Category("Books");
        Product product = new Product("Effective Java Book", category, 40D);
        ProductCollection productCollection = new ProductCollection();
        int quantity = 0;

        // When
        Throwable throwable = catchThrowable(() -> productCollection.insertItem(product, quantity));

        // Then
        assertThat(throwable).isNotNull();
    }

    @Test
    public void getItemsByProduct_shouldReturnItemBelongsToGivenProduct() {
        // Given
        Category category = new Category("Books");
        Product product = new Product("Effective Java Book", category, 40D);
        ProductCollection productCollection = new ProductCollection();
        int quantity = 4;

        productCollection.insertItem(product, quantity);

        // When
        Optional<CartItem> cartItem = productCollection.getItemsByProduct(product);

        // Then
        assertThat(cartItem).isPresent();
        assertThat(cartItem.get().getQuantity()).isEqualTo(quantity);
    }

    @Test
    public void getItemsByCategory_shouldReturnItemsBelongToGivenCategory() {
        // Given
        Category category = new Category("Books");
        Product product1 = new Product("Effective Java Book", category, 40D);
        Product product2 = new Product("Java In Action Book", category, 40D);

        ProductCollection productCollection = new ProductCollection();
        int quantity1 = 1;
        int quantity2 = 1;

        productCollection.insertItem(product1, quantity1);
        productCollection.insertItem(product2, quantity2);

        // When
        List<CartItem> itemsByCategory = productCollection.getItemsByCategory(category);

        // Then
        assertThat(itemsByCategory).isNotNull();
        assertThat(itemsByCategory).hasSize(2);
        assertThat(itemsByCategory).containsAnyOf(
                new CartItem(product1, quantity1),
                new CartItem(product2, quantity2)
        );
    }
}