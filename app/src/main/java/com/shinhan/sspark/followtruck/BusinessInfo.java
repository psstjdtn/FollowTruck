package com.shinhan.sspark.followtruck;

/**
 * Created by 60029517 on 2017-11-10.
 */

public class BusinessInfo {
    private int id;
    private String businessid;
    private String name;
    private String context;
    private double latitude ;
    private double longitude;
    private int business_state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBusinessid() {
        return businessid;
    }

    public void setBusinessid(String businessid) {
        this.businessid = businessid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getBusiness_state() {
        return business_state;
    }

    public void setBusiness_state(int business_state) {
        this.business_state = business_state;
    }
}