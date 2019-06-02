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
    private List<HashMap<String, String>> openingHours;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    //    ------------------------------------ Setters -------------------------------------------------

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(HashMap<String, Object> map) {
        this.location = new LatLng((double) map.get("latitude"), (double) map.get("longitude"));
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<HashMap<String, String>> getOpeningHours() {
        return openingHours;
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
