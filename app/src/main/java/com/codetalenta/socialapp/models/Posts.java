package com.codetalenta.socialapp.models;

public class Posts {
    private int id, likes, comments;
    private String date, desc, photo;
    private Users users;
    private boolean selfLike;

    public Posts(int id, int likes, int comments, String date, String desc, String photo, Users users, boolean selfLike) {
        this.id = id;
        this.likes = likes;
        this.comments = comments;
        this.date = date;
        this.desc = desc;
        this.photo = photo;
        this.users = users;
        this.selfLike = selfLike;
    }

    public Posts() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public boolean isSelfLike() {
        return selfLike;
    }

    public void setSelfLike(boolean selfLike) {
        this.selfLike = selfLike;
    }
}
