package com.ashabanebert.mukemergency.model;

import com.google.gson.annotations.SerializedName;

public class LocationUpdate {

    @SerializedName("lat")
    String lat;
    @SerializedName("lng")
    String lng;
    @SerializedName("updated_at")
    String time;
    @SerializedName("id")
    int id;
    @SerializedName("user")
    int user;

    public LocationUpdate(){

    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
