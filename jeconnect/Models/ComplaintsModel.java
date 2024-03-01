package com.pranavamrute.jeconnect.Models;

public class ComplaintsModel {

    String title, date, status, description, complaintID;

    public ComplaintsModel(String title, String date, String status, String description, String complaintID) {
        this.title = title;
        this.date = date;
        this.status = status;
        this.description = description;
        this.complaintID = complaintID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComplaintID() {
        return complaintID;
    }

    public void setComplaintID(String complaintID) {
        this.complaintID = complaintID;
    }
}
