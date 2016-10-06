package com.clicktracker.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;

public class Campaign {

    @JsonProperty
    private Long id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String referral;
    @JsonProperty
    private List<Platform> platforms;
    @JsonProperty
    private Long adClicks;
    @JsonProperty
    private Date created;

    public Campaign() {}

    public Campaign(Long id, String name, String referral, List<Platform> platforms, Long adClicks, Date created) {
        this.id = id;
        this.name = name;
        this.referral = referral;
        this.platforms = platforms;
        this.adClicks = adClicks;
        this.created = created;
    }
    public Campaign(Long id, String name, String referral, List<Platform> platforms, Date created) {
        this.id = id;
        this.name = name;
        this.referral = referral;
        this.platforms = platforms;
        this.created = created;
    }

    public String toString() {
        return "\nid: " + id + "\ncampaign name: " + name + "\nreferral: " + referral + "\nplatforms: " +  platforms + "\nadClicks: " + adClicks + "\ncreated: " + created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferral() {
        return referral;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Platform> platforms) {
        this.platforms = platforms;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getAdClicks() {
        return adClicks;
    }

    public void setAdClicks(Long adClicks) {
        this.adClicks = adClicks;
    }

}
