package com.example.mobile_client.model;

import java.io.Serializable;

/*
 * File Name: Reservation.java
 * Description: Model to store Reservation details.
 * Author: IT20168704
 */

public class Reservation implements Serializable {
    private String name;
    private String mobile;
    private String email;
    private String date;
    private String details;
    private String tickets;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTickets() { return tickets; }

    public void setTickets(String tickets) {
        this.tickets = tickets;
    }
}
