package com.karacalio.ecommerce.domain.discount;

import com.karacalio.ecommerce.model.campaign.Campaign;
import com.karacalio.ecommerce.model.store.Category;

import java.util.List;

public class MockCampaignRepository implements CampaignRepository {

    private List<Campaign> campaignList;

    public MockCampaignRepository(List<Campaign> campaignList) {
        this.campaignList = campaignList;
    }

    @Override
    public List<Campaign> findByCategory(Category category) {
        return campaignList;
    }
}
