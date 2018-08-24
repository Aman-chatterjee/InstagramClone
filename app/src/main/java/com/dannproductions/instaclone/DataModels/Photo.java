package com.dannproductions.instaclone.DataModels;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Photo implements Serializable{

    private String caption;
    private String imageUrl;
    private String date_added;
    private String photo_id;
    private String tags;
    private String user_id;




    public Photo() {
    }

    public Photo(String caption, String imageUrl, String date_added, String photo_id, String tags, String user_id) {

        this.caption = caption;
        this.imageUrl = imageUrl;
        this.date_added = date_added;
        this.photo_id = photo_id;
        this.tags = tags;
        this.user_id = user_id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    @Override
    public String toString() {
        return "Photo{" +
                "caption='" + caption + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", date_added='" + date_added + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", tags='" + tags + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }

}
