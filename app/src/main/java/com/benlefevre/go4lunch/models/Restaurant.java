package com.benlefevre.go4lunch.models;

public class Restaurant {

    private String name;
    private String uid;
    private String mail;
    private String phoneNumber;

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
}
