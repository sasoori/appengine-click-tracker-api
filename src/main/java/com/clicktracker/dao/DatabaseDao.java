package com.clicktracker.dao;

import com.clicktracker.entity.Campaign;
import com.clicktracker.entity.Platform;
import com.clicktracker.rest.CampaignRestService;
import com.clicktracker.utils.Constants;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DatabaseDao implements CampaignDao {

    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final Logger logger = Logger.getLogger(CampaignRestService.class.getName());
    private static final CampaignDao dao = new DatabaseDao();

    // Inject driver for MySQL connector
    public DatabaseDao() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    // Create new campaign and save it to datastore
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
    // Execute query and search for campaign by id. On match convert it to campaign object
    @Override
    public Campaign readCampaign(Long campaignId, Boolean countClicks) throws SQLException {
        Query q = new Query().setFilter(new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, KeyFactory.createKey("Campaign", campaignId)));
        PreparedQuery pq = datastore.prepare(q);
        Entity result = pq.asSingleEntity();
        Campaign campaign = null;
        if (result != null) {
            campaign = new Campaign(result, countClicks);
        }
        return campaign;
    }
    // Update/overwrite campaign
    @Override
    public void updateCampaign(Campaign campaign) throws Exception {
        Key campaignKey = KeyFactory.createKey("Campaign", campaign.getId());
        Entity campaignEntity = datastore.get(campaignKey);
        campaignEntity.setProperty("name", campaign.getName());
        campaignEntity.setProperty("referral", campaign.getReferral());
        campaignEntity.setProperty("platforms", Platform.enumListToStringList(campaign.getPlatforms()));
        datastore.put(campaignEntity);
    }
    // Create key from id and then delete campaign
    @Override
    public void deleteCampaign(Long campaignId) throws SQLException {
        Key campaignKey = KeyFactory.createKey("Campaign", campaignId);
        datastore.delete(campaignKey);
    }
    // First verify if campaign exists and then add click to database
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
    // Get clicks for specific campaign by querying entries that are marked with campaign id
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
    // Search for campaigns on given platform
    @Override
    public List<Campaign> getCampaigns(List<Platform> platforms) throws Exception {
        Query q = new Query("Campaign");
        if (!platforms.isEmpty()) {
            q.setFilter(new Query.FilterPredicate("platforms", Query.FilterOperator.IN, Platform.enumListToStringList(platforms)));
        }
        PreparedQuery pq = datastore.prepare(q);
        List<Campaign> campaigns = new ArrayList<>();
        for (Entity result : pq.asIterable()) {
            campaigns.add(new Campaign(result, true));
        }
        return campaigns;
    }
}
