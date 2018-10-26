package com.karacalio.ecommerce.model.campaign;

import com.karacalio.ecommerce.model.store.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Campaign {
    private Category category;
    private DiscountType discountType;
    private int minimumQuantity;
    private double discountAmount;
}
