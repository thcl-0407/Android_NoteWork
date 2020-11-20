package com.example.notework.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Message {

    @SerializedName("success")
    @Expose
    private Integer success;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("users")
    @Expose
    private ArrayList<User> user;

    @SerializedName("notes")
    @Expose
    private ArrayList<Note> notes;

    @SerializedName("reminds")
    @Expose
    private ArrayList<Remind> reminds;

    public ArrayList<User> getUser() {
        return user;
    }

    public void setUser(ArrayList<User> user) {
        this.user = user;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public ArrayList<Remind> getReminds() {
        return reminds;
    }

    public void setReminds(ArrayList<Remind> reminds) {
        this.reminds = reminds;
    }
}