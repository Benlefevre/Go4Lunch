package com.benlefevre.go4lunch.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class Restaurant {

    private String name;
    private String uid;
    private String mail;
    private String phoneNumber;
    private LatLng location;
    private String address;
    private double rating;
    private List<HashMap<String,String>> openingHours;

    public Restaurant() {
    }

    public Restaurant(String uid, String name, String mail, String phoneNumber) {
        this.uid = uid;
        this.name = name;
        this.mail = mail;
        this.phoneNumber = phoneNumber;
    }

    //    ------------------------------------ Getters -------------------------------------------------
    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getMail() {
        return mail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public double getRating() {
        return rating;
    }

    public List<HashMap<String, String>> getOpeningHours() {
        return openingHours;
    }

    //    ------------------------------------ Setters -------------------------------------------------

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setLocation(HashMap<String,Object> map) {
        this.location = new LatLng((double)map.get("latitude"),(double)map.get("longitude"));
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setOpeningHours(List<HashMap<String, String>> openingHours) {
        this.openingHours = openingHours;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                ", mail='" + mail + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", location=" + location +
                ", address='" + address + '\'' +
                ", rating=" + rating +
                ", openingHours=" + openingHours +
                '}';
    }
}
