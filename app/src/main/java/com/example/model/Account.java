package com.example.model;

public class Account {
    private String id;
    private String phone;
    private String password;
    private String role;
    private String IsLockUp;

    public Account() {
    }

    public Account(String id, String phone, String password, String role, String isLockUp) {
        this.id = id;
        this.phone = phone;
        this.password = password;
        this.role = role;
        IsLockUp = isLockUp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIsLockUp() {
        return IsLockUp;
    }

    public void setIsLockUp(String isLockUp) {
        IsLockUp = isLockUp;
    }
}
