package com.willowtreeapps.namegame.network.api.model2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Person2 implements Serializable {


//    @Expose
//    @SerializedName("socialLinks")
//    private List<String> socialLinks;
    @Expose
    @SerializedName("bio")
    private String bio;
    @Expose
    @SerializedName("headshot")
    private Headshot headshot;
    @Expose
    @SerializedName("lastName")
    private String lastName;
    @Expose
    @SerializedName("firstName")
    private String firstName;
    @Expose
    @SerializedName("jobTitle")
    private String jobTitle;
    @Expose
    @SerializedName("slug")
    private String slug;
    @Expose
    @SerializedName("type")
    private String type;
    @Expose
    @SerializedName("id")
    private String id;

//    public List<String> getSocialLinks() {
//        return socialLinks;
//    }

//    public void setSocialLinks(List<String> socialLinks) {
//        this.socialLinks = socialLinks;
//    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Headshot getHeadshot() {
        return headshot;
    }

    public void setHeadshot(Headshot headshot) {
        this.headshot = headshot;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
