package com.example.mobile_client.model;

/*
 * File Name: ChangePasswordRequest.java
 * Description: Request Model to store Password Changes details.
 * Author: IT20123468
 */

public class ChangePasswordRequest {

    private String currentPassword;
    private String newPassword;

    // Default constructor
    public ChangePasswordRequest() {}

    // Constructor with parameters
    public ChangePasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    // Getter for currentPassword
    public String getCurrentPassword() {
        return currentPassword;
    }

    // Setter for currentPassword
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    // Getter for newPassword
    public String getNewPassword() {
        return newPassword;
    }

    // Setter for newPassword
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
