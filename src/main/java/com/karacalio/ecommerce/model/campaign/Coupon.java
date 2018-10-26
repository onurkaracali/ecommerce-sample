package com.karacalio.ecommerce.model.campaign;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Coupon {
    private double minimumCartAmount;
    private double discountAmount;
    private DiscountType discountType;
}
