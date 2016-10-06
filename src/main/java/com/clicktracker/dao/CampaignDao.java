package com.clicktracker.dao;

import com.clicktracker.entity.Campaign;
import com.clicktracker.entity.Platform;
import java.sql.SQLException;
import java.util.List;


public interface CampaignDao{

    Long createCampaign(String name, String referral, List<Platform> platforms) throws Exception;

    Campaign readCampaign(Long campaignId, Boolean countClicks) throws SQLException;

    void updateCampaign(Campaign campaign) throws Exception;

    void deleteCampaign(Long campaignId) throws SQLException;

    Campaign trackClick (Long campaignId) throws SQLException;

    Long countClicks(Long campaignId) throws SQLException;

}



