package com.karacalio.ecommerce.domain;

import com.karacalio.ecommerce.model.campaign.Coupon;
import com.karacalio.ecommerce.model.cart.CartItem;
import com.karacalio.ecommerce.model.store.Product;

import java.util.List;

public interface Cart {

    List<CartItem> getItems();
    void addItem(Product product, int quantity);
    void applyCoupon(Coupon coupon);
    double getTotalPrice();
    double getTotalAmountAfterDiscounts();
    double getCampaignDiscount();
    double getCouponDiscount();
    double getDeliveryCost();
    void print();
}
