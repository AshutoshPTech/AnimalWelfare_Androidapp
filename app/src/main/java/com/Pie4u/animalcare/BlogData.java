package com.Pie4u.animalcare;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

public class BlogData implements Serializable {

    private String userName;
    private String blogDate;
    private String blogText;
    private String userID;
    private String postKey;
    public @ServerTimestamp
    Timestamp blogTimeStamp;
    private String imageURL;

    public BlogData() {
    }

    public BlogData(String userName, String blogDate, String blogText,String userID) {
        this.userName = userName;
        this.userID = userID;
        this.blogDate = blogDate;
        this.blogText = blogText;
    }

    public Timestamp getBlogTimeStamp() {
        return blogTimeStamp;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUserName() {
        return userName;
    }

    public String getBlogDate() {
        return blogDate;
    }

    public String getBlogText() {
        return blogText;
    }

    public String getUserID() {
        return userID;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

}
