package com.dannproductions.instaclone.DataModels;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

public class Video implements Serializable{

    private String caption;
    private String videoUrl;
    private String date_added;
    private String video_id;
    private String tags;
    private String user_id;

    public Video() {
    }

    public Video(String caption, String videoUrl, String date_added, String video_id, String tags, String user_id) {
        this.caption = caption;
        this.videoUrl = videoUrl;
        this.date_added = date_added;
        this.video_id = video_id;
        this.tags = tags;
        this.user_id = user_id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
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
        return "Video{" +
                "caption='" + caption + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", date_added='" + date_added + '\'' +
                ", video_id='" + video_id + '\'' +
                ", tags='" + tags + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }

}
