package com.willowtreeapps.namegame.network.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Person implements Serializable {



    @SerializedName("bio")
    private String bio;

    @SerializedName("headshot")
    private Headshot headshot;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("jobTitle")
    private String jobTitle;

    @SerializedName("slug")
    private String slug;

    @SerializedName("type")
    private String type;

    @SerializedName("id")
    private String id;


    public Person(String id,
                  String type,
                  String slug,
                  String jobTitle,
                  String firstName,
                  String lastName,
                  Headshot headshot) {
        this.id = id;
        this.type = type;
        this.slug = slug;
        this.jobTitle = jobTitle;
        this.firstName = firstName;
        this.lastName = lastName;
        this.headshot = headshot;
    }

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
