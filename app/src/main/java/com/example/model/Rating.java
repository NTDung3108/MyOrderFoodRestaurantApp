package com.example.model;

public class Rating {
    private String senderphone;
    private String foodId;
    private String rateValue;
    private String comment;
    private String image;

    public String getSenderphone() {
        return senderphone;
    }

    public void setSenderphone(String senderphone) {
        this.senderphone = senderphone;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Rating(String senderphone, String foodId, String rateValue, String comment, String image) {
        this.senderphone = senderphone;
        this.foodId = foodId;
        this.rateValue = rateValue;
        this.comment = comment;
        this.image = image;
    }

    public Rating() {
    }
}
