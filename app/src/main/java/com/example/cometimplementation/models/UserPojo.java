package com.example.cometimplementation.models;

public class UserPojo {
    private String name;
    private String number;
    private String img_url;
    public UserPojo(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public UserPojo(String name, String number, String img_url) {
        this.name = name;
        this.number = number;
        this.img_url = img_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
