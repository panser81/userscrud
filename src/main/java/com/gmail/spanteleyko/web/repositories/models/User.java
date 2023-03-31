package com.gmail.spanteleyko.web.repositories.models;

public class User {
    private String username;
    private Integer id;
    private Integer age;
    private Short isActive;
    private String address;
    private String telephone;
    private String password;

    public String getUsername() {
        return this.username;
    }

    public Integer getId() {
        return this.id;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setIsActive(Short isActive) {
        this.isActive = isActive;
    }

    public short getIsActive() {
        return this.isActive;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
