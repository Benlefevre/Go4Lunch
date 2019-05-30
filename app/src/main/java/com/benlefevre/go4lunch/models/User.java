package com.benlefevre.go4lunch.models;

public class User {

    private String uid;
    private String displayName;
    private String mail;
    private String urlPhoto;

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

    public String getDisplayName() {
        return displayName;
    }

    public String getMail() {
        return mail;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

//  -----------------------------------  Setters  --------------------------------------------------


    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }
}
