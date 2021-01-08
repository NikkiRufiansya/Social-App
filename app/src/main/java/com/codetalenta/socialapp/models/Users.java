package com.codetalenta.socialapp.models;

public class Users {
    private int id;
    private String userName, photo;

    public Users(int id, String userName, String photo) {
        this.id = id;
        this.userName = userName;
        this.photo = photo;
    }

    public Users() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


}
