package com.clicktracker.rest;

import com.clicktracker.dao.CampaignDao;
import com.clicktracker.entity.Campaign;
import com.clicktracker.entity.Platform;
import com.clicktracker.dao.DatabaseDao;
import com.clicktracker.utils.RequestParams;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

@Path("")
public class CampaignRestService {
    private static final Logger log = Logger.getLogger(CampaignRestService.class.getName());
    private static final CampaignDao dao = new DatabaseDao();
    private static final ObjectMapper mapper = new ObjectMapper();

    // AD CLICKED
    @GET
    @Path("/adclick")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findCampaign(
            @QueryParam(RequestParams.CAMPAIGN_ID) Long campaignId
    ) throws Exception {

        Campaign campaign = dao.trackClick(campaignId);
        URI location;
        if (campaign == null) {
            location = new URI("http://outfit7.com");
        } else {
            location = new URI(campaign.getReferral());
        }
        // returns 301 https://support.google.com/webmasters/answer/93633?hl=en&ref_topic=6001951
        return Response.status(Response.Status.MOVED_PERMANENTLY).location(location).build();
        // return Response.status(200).build();
    }

    // CREATE CAMPAIGN
    @POST
    @Path("/campaign/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCampaign(
            @QueryParam(RequestParams.CAMPAIGN_NAME) String name,
            @QueryParam(RequestParams.REFERRAL) String referral,
            @QueryParam(RequestParams.PLATFORMS) List<Platform> platforms
    ) throws Exception {

        long campaignId = dao.createCampaign(name, referral, platforms);
        if (campaignId == 0) {
            return Response.status(Response.Status.BAD_REQUEST).type("text/plain").entity("Failed to save campaign").build();
        }
        return Response.status(200).entity(campaignId).build();
    }

    // GET CAMPAIGN
    @GET
    @Path("/campaign/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCampaign(
            @PathParam(RequestParams.CAMPAIGN_ID) Long campaignId
    ) throws Exception {

        Campaign campaign = dao.readCampaign(campaignId, true);
        if (campaign == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        String campaignInJson;
        campaignInJson = mapper.writeValueAsString(campaign);

        return Response.status(Response.Status.OK).entity(campaignInJson).build();
    }

    // EDIT CAMPAIGN
    @PUT
    @Path("/campaign/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCampaign(String campaignJson,
               @PathParam(RequestParams.CAMPAIGN_ID) Long campaignId
    ) throws Exception {
        Campaign campaign = mapper.readValue(campaignJson, Campaign.class);
        // check if campaign exists first
        if (dao.readCampaign(campaignId, false) == null) {
            return Response.status(Response.Status.NOT_FOUND).type("text/plain").entity("Campaign doesn't exists").build();
        }
        dao.updateCampaign(campaign);

        return Response.status(200).build();
    }

    // DELETE CAMPAIGN
    @DELETE
    @Path("/campaign/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCampaign(
            @PathParam(RequestParams.CAMPAIGN_ID) Long campaignId
    ){
        try {
            dao.deleteCampaign(campaignId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Response.status(200).entity(campaignId).build();
    }

    // GET CAMPAIGNS
    @Path("/campaigns")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCampaigns(
            @QueryParam(RequestParams.CAMPAIGN_ID) Long campaignId
    ) throws Exception {
        return null;
    }
}
