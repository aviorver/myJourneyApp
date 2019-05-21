package com.example.guy.journeyblog;


import android.util.Log;

import java.util.Date;


import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class JourneyPost extends PostId {

    public String user_id, image_url, dest, image_thumb;
    public Date timestamp;

    public JourneyPost() {}

    public JourneyPost(String user_id, String image_url, String dest, String image_thumb, Date timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.dest = dest;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return dest;
    }

    public void setDesc(String dest) {
        this.dest = dest;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}