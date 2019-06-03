package com.benlefevre.go4lunch.models;

import java.util.Date;

public class User {

    private String uid;
    private String displayName;
    private String mail;
    private String urlPhoto;
    private String restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private Date choiceDate;

    public User() {
    }

    public User(String uid, String displayName, String mail, String urlPhoto) {
        this.uid = uid;
        this.displayName = displayName;
        this.mail = mail;
        this.urlPhoto = urlPhoto;
    }


//  -----------------------------------  Getters  --------------------------------------------------

    public String getUid() {
        return uid;
    }

    public String getMail() {
        return mail;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public Date getChoiceDate() {
        return choiceDate;
    }

//  -----------------------------------  Setters  --------------------------------------------------

    public void setUid(String uid) {
        this.uid = uid;
    }


    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public void setChoiceDate(Date choiceDate) {
        this.choiceDate = choiceDate;
    }


    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", mail='" + mail + '\'' +
                ", urlPhoto='" + urlPhoto + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", restaurantAddress='" + restaurantAddress + '\'' +
                ", choiceDate=" + choiceDate +
                '}';
    }
}

