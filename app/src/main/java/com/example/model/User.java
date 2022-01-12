package com.example.model;

public class User {
    private  String Name;
    private  String birthday;
    private  String Phone;
    private  String favoriteFood;
    private  String imageURL;

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFavoriteFood() {
        return favoriteFood;
    }

    public void setFavoriteFood(String favoriteFood) {
        this.favoriteFood = favoriteFood;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public User() {
    }

    public User(String name, String birthday, String phone, String favoriteFood, String imageURL) {
        Name = name;
        this.birthday = birthday;
        Phone = phone;
        this.favoriteFood = favoriteFood;
        this.imageURL = imageURL;
    }
}
