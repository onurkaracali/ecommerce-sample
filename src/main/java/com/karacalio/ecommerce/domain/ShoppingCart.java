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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ShoppingCart implements Cart {

    private final ItemCollection itemCollection;
    private final DeliveryCostCalculator deliveryCostCalculator;
    private final CartPrinter cartPrinter;
    private final CampaignRepository campaignRepository;

    private double campaignDiscountAmount;
    private double couponDiscountAmount;

    public ShoppingCart(ItemCollection itemCollection,
                        DeliveryCostCalculator deliveryCostCalculator,
                        CartPrinter cartPrinter,
                        CampaignRepository campaignRepository) {
        this.itemCollection = itemCollection;
        this.deliveryCostCalculator = deliveryCostCalculator;
        this.cartPrinter = cartPrinter;
        this.campaignRepository = campaignRepository;
    }

    @Override
    public void addItem(Product product, int quantity) {
        itemCollection.insertItem(product, quantity);

        applyDiscountForCategoriesInCollection();
    }

    @Override
    public List<CartItem> getItems() {
        return itemCollection.getItems();
    }

    @Override
    public void applyCoupon(Coupon coupon) {
        coupon = Optional.ofNullable(coupon)
                .orElseThrow(() -> new IllegalArgumentException("Coupon can not be null to apply discount"));
        this.couponDiscountAmount = calculateCouponDiscount(coupon);
    }

    @Override
    public double getTotalPrice() {
        return getItems().stream()
                .map(productCartItem -> productCartItem.getProduct().getPrice() * productCartItem.getQuantity())
                .reduce(0.00, Double::sum);
    }

    @Override
    public double getTotalAmountAfterDiscounts() {
        return getTotalPrice() - campaignDiscountAmount - couponDiscountAmount;
    }

    @Override
    public double getCampaignDiscount() {
        return campaignDiscountAmount;
    }

    @Override
    public double getCouponDiscount() {
        return couponDiscountAmount;
    }

    @Override
    public double getDeliveryCost() {
        return deliveryCostCalculator.calculateFor(this);
    }

    @Override
    public void print() {
        cartPrinter.print(this);
    }

    private void applyDiscountForCategoriesInCollection() {
        List<Category> itemCategories = itemCollection.getCategories();

        this.campaignDiscountAmount = itemCategories.stream()
                .map(this::findMaxDiscountForCategory)
                .max(Comparator.comparing(Function.identity()))
                .orElse(0D);
    }

    private Double findMaxDiscountForCategory(Category itemCategory) {
        List<Campaign> campaigns = campaignRepository.findByCategory(itemCategory);
        List<CartItem> itemsByCategory = itemCollection.getItemsByCategory(itemCategory);

        return campaigns.stream()
                .map(campaign -> calculateCampaignDiscountForItems(campaign, itemsByCategory))
                .max(Comparator.comparing(Function.identity()))
                .orElse(0D);
    }

    private double calculateCampaignDiscountForItems(Campaign campaign, List<CartItem> itemsOfCategory) {
        if (campaign.getMinimumQuantity() <= itemsOfCategory.size()) {
            if (DiscountType.AMOUNT.equals(campaign.getDiscountType())) {
                return campaign.getDiscountAmount();
            } else if (DiscountType.RATE.equals(campaign.getDiscountType())) {
                double totalAmount = getTotalAmountOfGivenItems(itemsOfCategory);
                return totalAmount * campaign.getDiscountAmount() / 100;
            }
        }
        return 0D;
    }

    private double getTotalAmountOfGivenItems(List<CartItem> itemsOfCategory) {
        return itemsOfCategory.stream()
                .map(cartItem -> cartItem.getQuantity() * cartItem.getProduct().getPrice())
                .reduce(0.0, Double::sum);
    }

    public double calculateCouponDiscount(Coupon coupon) {
        double totalAmountOfCart = getTotalPrice();
        if (totalAmountOfCart >= coupon.getMinimumCartAmount()) {
            if (DiscountType.AMOUNT.equals(coupon.getDiscountType())) {
                return coupon.getDiscountAmount();
            } else if (DiscountType.RATE.equals(coupon.getDiscountType())) {
                return totalAmountOfCart * coupon.getDiscountAmount() / 100;
            }
        }
        return 0;
    }
}
