package com.example.exposysinternshipapp.Models;

public class AppliedModel {
    private String status;
    private String applicationDate;
    private String internshipUrl;
    private String duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getInternshipUrl() {
        return internshipUrl;
    }

    public void setInternshipUrl(String internshipUrl) {
        this.internshipUrl = internshipUrl;
    }
}
