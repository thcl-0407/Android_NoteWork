package com.example.notework.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Remind implements Serializable {

    @SerializedName("remind_id")
    @Expose
    private Integer remindId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content_remind")
    @Expose
    private String contentRemind;
    @SerializedName("time_remind")
    @Expose
    private String timeRemind;
    @SerializedName("date_remind")
    @Expose
    private String dateRemind;
    @SerializedName("user_id")
    @Expose
    private Integer userId;

    public Integer getRemindId() {
        return remindId;
    }

    public void setRemindId(Integer remindId) {
        this.remindId = remindId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentRemind() {
        return contentRemind;
    }

    public void setContentRemind(String contentRemind) {
        this.contentRemind = contentRemind;
    }

    public String getTimeRemind() {
        return timeRemind;
    }

    public void setTimeRemind(String timeRemind) {
        this.timeRemind = timeRemind;
    }

    public String getDateRemind() {
        return dateRemind;
    }

    public void setDateRemind(String dateRemind) {
        this.dateRemind = dateRemind;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}