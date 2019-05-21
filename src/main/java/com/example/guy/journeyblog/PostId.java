package com.example.guy.journeyblog;

import com.google.firebase.firestore.Exclude;

import io.reactivex.annotations.NonNull;

public class PostId {
    @Exclude
    public String Postid;
    public <T extends PostId>T withId(@NonNull final String id)
    {
        this.Postid=id;
        return (T) this;
    }
}
