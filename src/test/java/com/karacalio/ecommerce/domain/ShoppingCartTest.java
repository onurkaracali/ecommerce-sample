package com.karacalio.ecommerce.domain;

import com.karacalio.ecommerce.domain.delivery.DeliveryCostCalculator;
import com.karacalio.ecommerce.domain.discount.CampaignRepository;
import com.karacalio.ecommerce.domain.printer.CartPrinter;
import com.karacalio.ecommerce.model.campaign.Campaign;
import com.karacalio.ecommerce.model.campaign.Coupon;
import com.karacalio.ecommerce.model.campaign.DiscountType;
import com.karacalio.ecommerce.model.cart.CartItem;
import com.karacalio.ecommerce.model.store.Category;
import com.karacalio.ecommerce.model.store.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingCartTest {

    @Mock
    ProductCollection productCollection;

    @Mock
    DeliveryCostCalculator deliveryCostCalculator;

    @Mock
    CartPrinter cartPrinter;

    @Mock
    CampaignRepository campaignRepository;

    @Test
    public void addItem_shouldAddProductToItemList_byUsingCartItemList() {
        // Given
        ShoppingCart shoppingCart = new ShoppingCart(productCollection, deliveryCostCalculator, cartPrinter, campaignRepository);
        Category category = new Category("Shoes");
        Product product = new Product("NBrand Sport Shoe", category, 379.99);
        int quantity = 1;

        // When
        shoppingCart.addItem(product, quantity);

        // Then
        verify(productCollection, Mockito.times(1)).insertItem(product, quantity);
    }

    @Test
    public void addItem_shouldRetrieveAndApplyCampaignDiscounts_when_itemQuantityLowerThanCampaignMinQuantity() {
        // Given
        ShoppingCart shoppingCart = new ShoppingCart(productCollection, deliveryCostCalculator, cartPrinter, campaignRepository);
        Category bookCategory = new Category("Book Category");

        List<Category> categoriesToReturn = Arrays.asList(bookCategory);
        List<Campaign> campaignsToRetrieve = Arrays.asList(
                new Campaign(bookCategory, DiscountType.AMOUNT, 3, 30)
        );

        List<CartItem> cartItems = Arrays.asList(
                new CartItem(new Product("X Book", bookCategory, 25.00), 1),
                new CartItem(new Product("Y Book", bookCategory, 25.00), 1)
        );

        Product newBook = new Product("Y Book", bookCategory, 50.00);

        when(productCollection.getCategories()).thenReturn(categoriesToReturn);
        when(campaignRepository.findByCategory(bookCategory)).thenReturn(campaignsToRetrieve);
        when(productCollection.getItems()).thenReturn(cartItems);
        when(productCollection.getItemsByCategory(bookCategory)).thenReturn(cartItems);

        // When
        shoppingCart.addItem(newBook, 1);

        // Then
        assertThat(shoppingCart.getCampaignDiscount()).isEqualTo(0);
        assertThat(shoppingCart.getTotalAmountAfterDiscounts()).isEqualTo(50.00);
    }

    @Test
    public void addItem_shouldRetrieveAndApplyCampaignDiscounts_when_amountCampaignAndItemQuantityGreaterThanCampaignMinQuantity() {
        // Given
        ShoppingCart shoppingCart = new ShoppingCart(productCollection, deliveryCostCalculator, cartPrinter, campaignRepository);
        Category bookCategory = new Category("Book Category");

        List<Category> categoriesToReturn = Arrays.asList(bookCategory);
        List<Campaign> campaignsToRetrieve = Arrays.asList(
                new Campaign(bookCategory, DiscountType.AMOUNT, 3, 30)
        );

        List<CartItem> cartItems = Arrays.asList(
                new CartItem(new Product("X Book", bookCategory, 25.00), 1),
                new CartItem(new Product("Y Book", bookCategory, 25.00), 1),
                new CartItem(new Product("Z Book", bookCategory, 50.00), 1)
        );

        Product newBook = new Product("Z Book", bookCategory, 50.00);

        when(productCollection.getCategories()).thenReturn(categoriesToReturn);
        when(campaignRepository.findByCategory(bookCategory)).thenReturn(campaignsToRetrieve);
        when(productCollection.getItems()).thenReturn(cartItems);
        when(productCollection.getItemsByCategory(bookCategory)).thenReturn(cartItems);

        // When
        shoppingCart.addItem(newBook, 1);

        // Then
        assertThat(shoppingCart.getCampaignDiscount()).isEqualTo(30.00);
        assertThat(shoppingCart.getTotalAmountAfterDiscounts()).isEqualTo(70.00);
    }

    @Test
    public void addItem_shouldRetrieveAndApplyCampaignDiscounts_when_rateCampaignAndItemQuantityGreaterThanCampaignMinQuantity() {
        // Given
        ShoppingCart shoppingCart = new ShoppingCart(productCollection, deliveryCostCalculator, cartPrinter, campaignRepository);
        Category bookCategory = new Category("Book Category");

        List<Category> categoriesToReturn = Arrays.asList(bookCategory);
        List<Campaign> campaignsToRetrieve = Arrays.asList(
                new Campaign(bookCategory, DiscountType.RATE, 2, 10)
        );

        List<CartItem> cartItems = Arrays.asList(
                new CartItem(new Product("X Book", bookCategory, 25.00), 1),
                new CartItem(new Product("Y Book", bookCategory, 25.00), 1),
                new CartItem(new Product("Z Book", bookCategory, 50.00), 1)
        );

        Product newBook = new Product("Z Book", bookCategory, 50.00);

        when(productCollection.getCategories()).thenReturn(categoriesToReturn);
        when(campaignRepository.findByCategory(bookCategory)).thenReturn(campaignsToRetrieve);
        when(productCollection.getItems()).thenReturn(cartItems);
        when(productCollection.getItemsByCategory(bookCategory)).thenReturn(cartItems);

        // When
        shoppingCart.addItem(newBook, 1);

        // Then
        assertThat(shoppingCart.getCampaignDiscount()).isEqualTo(10.00);
        assertThat(shoppingCart.getTotalAmountAfterDiscounts()).isEqualTo(90.00);
    }

    @Test
    public void addItem_shouldMaximumDiscountApplied_withMultipleCampaigns() {
        // Given
        ShoppingCart shoppingCart = new ShoppingCart(productCollection, deliveryCostCalculator, cartPrinter, campaignRepository);
        Category bookCategory = new Category("Book Category");

        List<Category> categoriesToReturn = Arrays.asList(bookCategory);
        List<Campaign> campaignsToRetrieve = Arrays.asList(
                new Campaign(bookCategory, DiscountType.AMOUNT, 2, 40),
                new Campaign(bookCategory, DiscountType.RATE, 3, 20)
        );

        List<CartItem> cartItems = Arrays.asList(
                new CartItem(new Product("X Book", bookCategory, 25.00), 1),
                new CartItem(new Product("Y Book", bookCategory, 25.00), 1),
                new CartItem(new Product("Z Book", bookCategory, 50.00), 1)
        );

        Product newBook = new Product("Z Book", bookCategory, 50.00);

        when(productCollection.getCategories()).thenReturn(categoriesToReturn);
        when(campaignRepository.findByCategory(bookCategory)).thenReturn(campaignsToRetrieve);
        when(productCollection.getItems()).thenReturn(cartItems);
        when(productCollection.getItemsByCategory(bookCategory)).thenReturn(cartItems);

        // When
        shoppingCart.addItem(newBook, 1);

        // Then
        assertThat(shoppingCart.getCampaignDiscount()).isEqualTo(40.00);
        assertThat(shoppingCart.getTotalAmountAfterDiscounts()).isEqualTo(60.00);
    }

    @Test
    public void applyCoupon_shouldApplyCouponDiscount_when_couponDiscountTypeIsAmount_and_totalCartAmountIsGreaterThanCouponMinCartAmount() {
        // Given
        Coupon coupon = new Coupon(100.00, 20.00, DiscountType.AMOUNT);
        ShoppingCart shoppingCart = new ShoppingCart(productCollection, deliveryCostCalculator, cartPrinter, campaignRepository);

        List<CartItem> cartItems = Arrays.asList(
                new CartItem(new Product("X Book", new Category("Books"), 20.00), 1),
                new CartItem(new Product("Z Book", new Category("Books"), 90.00), 1)
        );

        when(productCollection.getItems()).thenReturn(cartItems);

        // When
        shoppingCart.applyCoupon(coupon);

        // Then
        assertThat(shoppingCart.getCouponDiscount()).isEqualTo(20.00);
    }

    @Test
    public void applyCoupon_shouldApplyCouponDiscount_when_couponDiscountTypeIsRate_and_totalCartAmountIsGreaterThanCouponMinCartAmount() {
        // Given
        Coupon coupon = new Coupon(100.00, 10.00, DiscountType.RATE);
        ShoppingCart shoppingCart = new ShoppingCart(productCollection, deliveryCostCalculator, cartPrinter, campaignRepository);

        List<CartItem> cartItems = Arrays.asList(
                new CartItem(new Product("X Book", new Category("Books"), 20.00), 1),
                new CartItem(new Product("Z Book", new Category("Books"), 90.00), 1)
        );

        when(productCollection.getItems()).thenReturn(cartItems);

        // When
        shoppingCart.applyCoupon(coupon);

        // Then
        assertThat(shoppingCart.getCouponDiscount()).isEqualTo(11.00);
    }

    @Test
    public void applyCoupon_shouldApplyCouponDiscount_when_totalCartAmountIsLowerThanCouponMinCartAmount() {
        // Given
        Coupon coupon = new Coupon(100.00, 20.00, DiscountType.AMOUNT);
        ShoppingCart shoppingCart = new ShoppingCart(productCollection, deliveryCostCalculator, cartPrinter, campaignRepository);
        Category booksCategory = new Category("Books");

        List<CartItem> cartItems = Arrays.asList(
                new CartItem(new Product("X Book", new Category("Books"), 20.00), 1),
                new CartItem(new Product("Z Book", new Category("Books"), 25.00), 1)
        );

        when(productCollection.getItems()).thenReturn(cartItems);

        // When
        shoppingCart.applyCoupon(coupon);

        // Then
        assertThat(shoppingCart.getCouponDiscount()).isEqualTo(0);
    }

    @Test
    public void applyCoupon_shouldThrowException_when_couponIsNull() {
        // Given
        ShoppingCart shoppingCart = new ShoppingCart(productCollection, deliveryCostCalculator, cartPrinter, campaignRepository);

        // When
        Throwable throwable = catchThrowable(() -> shoppingCart.applyCoupon(null));

        // Then
        assertThat(throwable).isNotNull();
    }

    @Test
    public void getDeliveryCost_shouldCalculateAndReturnDeliveryCost_byUsingDeliveryCostCalculator() {
        // Given
        ShoppingCart shoppingCart = new ShoppingCart(productCollection, deliveryCostCalculator, cartPrinter, campaignRepository);
        when(deliveryCostCalculator.calculateFor(shoppingCart)).thenReturn(100.00);

        // When
        double deliveryCost = shoppingCart.getDeliveryCost();

        // Then
        verify(deliveryCostCalculator, times(1)).calculateFor(shoppingCart);
        assertThat(deliveryCost).isEqualTo(100.00);
    }

    @Test
    public void print_shouldPrintCart_byUsingCartPrinter() {
        // Given
        ShoppingCart shoppingCart = new ShoppingCart(productCollection, deliveryCostCalculator, cartPrinter, campaignRepository);

        // When
        shoppingCart.print();

        // Then
        verify(cartPrinter, times(1)).print(shoppingCart);
    }
}