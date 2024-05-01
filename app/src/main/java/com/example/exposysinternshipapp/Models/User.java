package com.example.exposysinternshipapp.Models;

import java.util.Map;

public class User {
    private String userName;
    private String email;
    private String resume;
    private String profileImage;
    private String bio;
    private String location;
    private String phoneNumber;
    private boolean resumeStatus;

    private Map<String, AppliedModel> appliedInternship;
    public User(){

    }

    public boolean isResumeStatus() {
        return resumeStatus;
    }

    public void setResumeStatus(boolean resumeStatus) {
        this.resumeStatus = resumeStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Map<String, AppliedModel> getAppliedInternship() {
        return appliedInternship;
    }

    public void setAppliedInternship(Map<String, AppliedModel> appliedInternship) {
        this.appliedInternship = appliedInternship;
    }
}
