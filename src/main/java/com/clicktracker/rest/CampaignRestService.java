package com.clicktracker.rest;

import com.clicktracker.dao.CampaignDao;
import com.clicktracker.entity.Campaign;
import com.clicktracker.entity.Platform;
import com.clicktracker.dao.DatabaseDao;
import com.clicktracker.utils.RequestParams;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
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
    }
    /* ADMIN SECTION
     * IMPORTANT: This section would be under authentication(e.g., Apache Shiro)
     */
    // CREATE CAMPAIGN
    @POST
    @Path("/campaigns")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCampaign(
           String campaignJson
    ) throws Exception {
        Campaign campaign = mapper.readValue(campaignJson, Campaign.class);
        Long campaignId = dao.createCampaign(campaign.getName(), campaign.getReferral(), campaign.getPlatforms());
        if (campaignId == null) {
            return Response.status(Response.Status.BAD_REQUEST).type("text/plain").entity("Failed to save campaign").build();
        }
     return Response.status(200).type("text/plain").entity(campaignId).build();
    }
    // GET CAMPAIGN
    @GET
    @Path("/campaigns/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCampaign(
            @PathParam(RequestParams.CAMPAIGN_ID) Long campaignId
    ) throws Exception {
        // Find campaign by id
        Campaign campaign = dao.readCampaign(campaignId, true);
        // Campaign wasn't found, return 404;
        if (campaign == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        // Turn campaign object to json and return it to client
        String campaignInJson;
        campaignInJson = mapper.writeValueAsString(campaign);
        return Response.status(Response.Status.OK).entity(campaignInJson).build();
    }
    // EDIT CAMPAIGN
    @PUT
    @Path("/campaigns/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCampaign(
            String campaignJson,
            @PathParam(RequestParams.CAMPAIGN_ID) Long campaignId
    ) throws Exception {
        // Convert request body to Campaign object
        Campaign campaign = mapper.readValue(campaignJson, Campaign.class);
        // Check if campaign exists first if not send 404 status
        if (dao.readCampaign(campaignId, false) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        // Campaign was found now update it
        dao.updateCampaign(campaign);
        return Response.status(Response.Status.OK).build();
    }
    // DELETE CAMPAIGN
    @DELETE
    @Path("/campaigns")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCampaign(
            @QueryParam(RequestParams.CAMPAIGN_ID) List<Long> campaignIds
    ) throws Exception {
        // Delete campaign by id
        for (Long campaignId : campaignIds) {
            dao.deleteCampaign(campaignId);
        }
        return Response.status(Response.Status.OK).build();
    }
    // GET CAMPAIGNS
    @GET
    @Path("/campaigns")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCampaigns(
            @QueryParam(RequestParams.PLATFORMS) List<Platform> platforms
    ) throws Exception {
        String campaignInJson;
        // List all existing campaigns available on given platform and convert list into json
        campaignInJson = mapper.writeValueAsString(dao.getCampaigns(platforms));
        return Response.status(Response.Status.OK).entity(campaignInJson).build();
    }
}
