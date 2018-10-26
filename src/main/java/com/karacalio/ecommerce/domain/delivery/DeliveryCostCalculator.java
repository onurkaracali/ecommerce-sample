package com.karacalio.ecommerce.domain.delivery;

import com.karacalio.ecommerce.domain.Cart;

public interface DeliveryCostCalculator {

    double calculateFor(Cart cart);
}
