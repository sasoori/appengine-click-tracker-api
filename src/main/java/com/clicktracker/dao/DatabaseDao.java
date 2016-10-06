package com.clicktracker.dao;

import com.clicktracker.entity.Campaign;
import com.clicktracker.entity.Platform;
import com.clicktracker.rest.CampaignRestService;
import com.clicktracker.utils.Constants;
import com.google.appengine.api.datastore.*;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.sql.*;
import java.sql.DriverManager;

public class DatabaseDao implements CampaignDao {

    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final Logger logger = Logger.getLogger(CampaignRestService.class.getName());
    private static final CampaignDao dao = new DatabaseDao();

    public DatabaseDao() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Long createCampaign(String name, String referral, List<Platform> platforms) throws Exception {
        try {
            Entity campaign = new Entity("Campaign");
            campaign.setProperty("name", name);
            campaign.setProperty("referral", referral);
            campaign.setProperty("platforms", Platform.enumListToStringList(platforms));
            campaign.setProperty("created", new Date());
            Key campaignEntity = datastore.put(campaign);
            return campaignEntity.getId();

        } catch (Exception error) {
            error.printStackTrace();
            return null;
        }
    }

    @Override
    public Campaign readCampaign(Long campaignId, Boolean countClicks) throws SQLException {
        //Find campaign by id
        Query.Filter keyFilter = new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, KeyFactory.createKey("Campaign", campaignId));
        Query q = new Query().setFilter(keyFilter);
        List<Entity> results = datastore.prepare(q).asList(FetchOptions.Builder.withDefaults());
        Campaign campaign = null;
        for (Entity campaignEntity : results) {
            campaign = new Campaign(campaignEntity.getKey().getId(), (String) campaignEntity.getProperty("name"), (String) campaignEntity.getProperty("referral"), (List<Platform>) campaignEntity.getProperty("platforms"));
        }
        if (countClicks && campaign != null) {
            campaign.setAdClicks(dao.countClicks(campaign.getId()));
        }
        return campaign;
    }

    @Override
    public void updateCampaign(Campaign campaign) throws Exception {
        Key campaignKey = KeyFactory.createKey("Campaign", campaign.getId());
        Entity campaignEntity = datastore.get(campaignKey);
        campaignEntity.setProperty("name", campaign.getName());
        campaignEntity.setProperty("referral", campaign.getReferral());
        campaignEntity.setProperty("platforms", Platform.enumListToStringList(campaign.getPlatforms()));
        datastore.put(campaignEntity);
    }

    @Override
    public void deleteCampaign(Long campaignId) throws SQLException {
        Key campaignKey = KeyFactory.createKey("Campaign", campaignId);
        datastore.delete(campaignKey);
    }

    @Override
    public Campaign trackClick(Long campaignId) throws SQLException {
        Campaign campaign = dao.readCampaign(campaignId, false);
        if (campaign != null) {
            Connection conn = DriverManager.getConnection(Constants.SqlUrl);
            String sqlCreate = "INSERT INTO `click`.`clicks` (`campaign_id`) VALUES ('" + campaignId + "')";
            Statement stateManager = conn.createStatement();
            stateManager.execute(sqlCreate);
            campaign.setAdClicks(dao.countClicks(campaign.getId()));
            return campaign;
        }
        return null;
    }

    @Override
    public Long countClicks(Long campaignId) throws SQLException {
        Long totalClicks = 0L;
        try {
            Connection conn = DriverManager.getConnection(Constants.SqlUrl);
            String readCampaignClicksString = "SELECT COUNT(*) AS total FROM `clicks` WHERE campaign_id = " + campaignId;
            ResultSet queryResult = conn.createStatement().executeQuery(readCampaignClicksString);
            if (queryResult.next()) {
                totalClicks = queryResult.getLong("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalClicks;
    }
}
