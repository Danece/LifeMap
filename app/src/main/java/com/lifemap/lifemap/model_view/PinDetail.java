package com.lifeMap.lifemap.model_view;


public class PinDetail {

    private String title;
    private String date;
    private String country;
    private String markerStyle;
    private String markerType;
    private Double longitude;
    private Double latitude;
    private String markerImageUuid;

    public String getMarkerImageUuid() {
        return markerImageUuid;
    }

    public void setMarkerImageUuid(String markerImageUuid) {
        this.markerImageUuid = markerImageUuid;
    }

    public PinDetail() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMarkerStyle() {
        return markerStyle;
    }

    public void setMarkerStyle(String markerStyle) {
        this.markerStyle = markerStyle;
    }

    public String getMarkerType() {
        return markerType;
    }

    public void setMarkerType(String markerType) {
        this.markerType = markerType;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public PinDetail(String title, String date, String country, String markerStyle, String markerType, Double longitude, Double latitude, String markerImageUuid) {
        super();
        this.title = title;
        this.date = date;
        this.country = country;
        this.markerStyle = markerStyle;
        this.markerType = markerType;
        this.longitude = longitude;
        this.latitude = latitude;
        this.markerImageUuid = markerImageUuid;
    }
}
