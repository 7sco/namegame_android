package com.willowtreeapps.namegame.network.api.model2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Headshot {
    @Expose
    @SerializedName("width")
    private int width;
    @Expose
    @SerializedName("height")
    private int height;
    @Expose
    @SerializedName("alt")
    private String alt;
    @Expose
    @SerializedName("url")
    private String url;
    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("mimeType")
    private String mimeType;
    @Expose
    @SerializedName("type")
    private String type;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
