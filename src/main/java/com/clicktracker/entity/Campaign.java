package com.clicktracker.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    public Campaign() {}

    public Campaign(Long id, String name, String referral, List<Platform> platforms, Long adClicks) {
        this.id = id;
        this.name = name;
        this.referral = referral;
        this.platforms = platforms;
        this.adClicks = adClicks;
    }

    public Campaign(Long id, String name, String referral, List<Platform> platforms) {
        this.id = id;
        this.name = name;
        this.referral = referral;
        this.platforms = platforms;
    }

    public String toString() {
        return "id: " + id + "\ncampaign name: " + name + "\nreferral: " + referral + "\nplatforms: " +  platforms + "\nadClicks: " + adClicks;
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

    public Long getAdClicks() {
        return adClicks;
    }

    public void setAdClicks(Long adClicks) {
        this.adClicks = adClicks;
    }

}
