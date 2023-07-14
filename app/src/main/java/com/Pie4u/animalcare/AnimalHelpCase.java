package com.Pie4u.animalcare;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;

public class AnimalHelpCase implements Serializable {

    private String userName,desc,userNo,userUid;
    private String animalType,cityType;
    private String userLocation;
    private ArrayList<String> photourl;
    private double latitude;
    private String rescueStatus;
    private double longitude;
    private boolean accepted;
    private boolean isCompleted;
    private String rescuerUid;
    private String rescueDocumentId;
    public @ServerTimestamp
    Timestamp timestamp;


    public AnimalHelpCase() {
    }

    public AnimalHelpCase(String userName, String animalType, String cityType, String userLocation, double latitude, double longitude, ArrayList<String> photourl, Boolean accepted, String desc) {
        this.animalType = animalType;
        this.cityType = cityType;
        this.userName = userName;
        this.userLocation = userLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photourl=photourl;
        this.accepted=accepted;
        this.desc=desc;
        this.isCompleted = false;
        this.rescuerUid = "null";
        this.rescueStatus = "Waiting for rescue";
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setRescueStatus(String rescueStatus) {
        this.rescueStatus = rescueStatus;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getRescuerUid() {
        return rescuerUid;
    }

    public void setRescuerUid(String rescuerUid) {
        this.rescuerUid = rescuerUid;
    }

    public String getRescueDocumentId() {
        return rescueDocumentId;
    }

    public void setRescueDocumentId(String rescueDocumentId) {
        this.rescueDocumentId = rescueDocumentId;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRescueStatus() {
        return rescueStatus;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getDesc() {
        return desc;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getCityType() {
        return cityType;
    }

    public String getUserName() {
        return userName;
    }

    public String getAnimalType() {
        return animalType;
    }


    public String getUserLocation() {
        return userLocation;
    }

    public ArrayList<String> getPhotourl() {
        return photourl;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
