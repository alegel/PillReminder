package com.alegel.user.pillreminder.patients;

import java.io.Serializable;

/**
 * Created by user on 18.05.2016.
 */
public class Patient implements Serializable {
    private String name;
    private String phone;

    public Patient(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name + "\n" + phone;
    }
}
