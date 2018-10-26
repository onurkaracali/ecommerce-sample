package com.karacalio.ecommerce.domain;

import com.karacalio.ecommerce.domain.delivery.DeliveryCostCalculator;
import com.karacalio.ecommerce.domain.delivery.DeliveryCostCalculatorImpl;
import com.karacalio.ecommerce.domain.discount.CampaignRepository;
import com.karacalio.ecommerce.domain.discount.MockCampaignRepository;
import com.karacalio.ecommerce.domain.printer.CartPrinter;
import com.karacalio.ecommerce.domain.printer.CategoryBasedCartPrinter;
import com.karacalio.ecommerce.model.campaign.Campaign;
import com.karacalio.ecommerce.model.campaign.DiscountType;
import com.karacalio.ecommerce.model.store.Category;
import com.karacalio.ecommerce.model.store.Product;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShoppingCartIT {

    private PrintStream originalSystemOut;
    private PrintStream systemOut;
    private ByteArrayOutputStream outContent;

    @Before
    public void setup() {
        originalSystemOut = System.out;
        outContent = new ByteArrayOutputStream();
        systemOut = new PrintStream(outContent);
        System.setOut(systemOut);
    }

    @Test
    public void testShouldAddGivenItems_and_printCart_whenNoCampaignAndNoCouponGiven() {
        // Given
        Category techCategory = new Category("Tech Category");

        Product techProduct = new Product("Tech Product", techCategory, 200.00);
        Product techProduct2 = new Product("Other Tech Product", techCategory, 300.00);

        ItemCollection itemCollection = new ProductCollection();

        double costPerDelivery = 10.00;
        double costPerProduct = 20.00;
        double deliveryFixedCost = 2.99;

        DeliveryCostCalculator deliveryCostCalculator = new DeliveryCostCalculatorImpl(costPerDelivery, costPerProduct, deliveryFixedCost);
        CartPrinter cartPrinter = new CategoryBasedCartPrinter();
        CampaignRepository campaignRepository = new MockCampaignRepository(Collections.emptyList());

        String expectedPrint = "Products In Category : Tech Category\n" +
                "+ Product Name : Tech Product\n" +
                "  Quantity : 2\n" +
                "  Unit Price : 200.0\n" +
                "  Total Price : 400.0\n" +
                "\n" +
                "+ Product Name : Other Tech Product\n" +
                "  Quantity : 3\n" +
                "  Unit Price : 300.0\n" +
                "  Total Price : 900.0\n" +
                "\n" +
                "---\n" +
                "Cart Total Price : 1300.0\n" +
                "Delivery Cost : 112.99\n";

        ShoppingCart shoppingCart = new ShoppingCart(itemCollection, deliveryCostCalculator, cartPrinter, campaignRepository);

        // When
        shoppingCart.addItem(techProduct, 2);
        shoppingCart.addItem(techProduct2, 3);
        shoppingCart.print();

        // Then
        Assertions.assertThat(outContent.toString()).isEqualTo(expectedPrint);
    }

    @Test
    public void testShouldAddGivenItems_and_printCart_withCampaigns() {
        // Given
        Category techCategory = new Category("Tech Category");

        Product techProduct = new Product("Tech Product", techCategory, 200.00);
        Product techProduct2 = new Product("Other Tech Product", techCategory, 300.00);

        ItemCollection itemCollection = new ProductCollection();

        List<Campaign> campaigns = Arrays.asList(
                new Campaign(techCategory, DiscountType.AMOUNT, 2, 40)
        );

        double costPerDelivery = 10.00;
        double costPerProduct = 20.00;
        double deliveryFixedCost = 2.99;

        DeliveryCostCalculator deliveryCostCalculator = new DeliveryCostCalculatorImpl(costPerDelivery, costPerProduct, deliveryFixedCost);
        CartPrinter cartPrinter = new CategoryBasedCartPrinter();
        CampaignRepository campaignRepository = new MockCampaignRepository(campaigns);

        String expectedPrint = "Products In Category : Tech Category\n" +
                "+ Product Name : Tech Product\n" +
                "  Quantity : 2\n" +
                "  Unit Price : 200.0\n" +
                "  Total Price : 400.0\n" +
                "\n" +
                "+ Product Name : Other Tech Product\n" +
                "  Quantity : 3\n" +
                "  Unit Price : 300.0\n" +
                "  Total Price : 900.0\n" +
                "\n" +
                "---\n" +
                "Cart Total Price : 1260.0\n" +
                "Delivery Cost : 112.99\n";

        ShoppingCart shoppingCart = new ShoppingCart(itemCollection, deliveryCostCalculator, cartPrinter, campaignRepository);

        // When
        shoppingCart.addItem(techProduct, 2);
        shoppingCart.addItem(techProduct2, 3);
        shoppingCart.print();

        // Then
        Assertions.assertThat(outContent.toString()).isEqualTo(expectedPrint);
    }
}
