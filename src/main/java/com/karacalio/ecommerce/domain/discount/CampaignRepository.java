package com.karacalio.ecommerce.domain.discount;

import com.karacalio.ecommerce.model.campaign.Campaign;
import com.karacalio.ecommerce.model.store.Category;

import java.util.List;

public interface CampaignRepository {
    List<Campaign> findByCategory(Category category);
}
