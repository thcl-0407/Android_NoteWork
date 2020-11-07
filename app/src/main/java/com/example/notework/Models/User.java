package com.example.notework.Models;

import com.example.notework.Character.VNCharacter;
import com.example.notework.CustomException.EmailNotValidException;
import com.example.notework.CustomException.NameNotValidException;
import com.example.notework.CustomException.PasswordNotValidException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    protected final String regex_check_email = "^([a-zA-Z]+\\.?-?[0-9]*)+\\w*\\.?-?([a-zA-Z]+[0-9]*)+@[a-zA-Z]+[0-9]*(\\.[a-zA-Z]{2,4}){1,2}$";
    protected final String regex_check_name = "^(\\s?[A-Z]([a-z])+\\s?)+$";

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) throws NameNotValidException {
        String HoTen = VNCharacter.removeAccent(firstName);

        if (HoTen.matches(regex_check_name)) {
            this.firstName = firstName;
        } else {
            throw new NameNotValidException();
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) throws NameNotValidException {
        String HoTen = VNCharacter.removeAccent(lastName);

        if (HoTen.matches(regex_check_name)) {
            this.lastName = lastName;
        } else {
            throw new NameNotValidException();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws EmailNotValidException {
        if (email.matches(regex_check_email)) {
            this.email = email;
        } else {
            throw new EmailNotValidException();
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws PasswordNotValidException {
        if (password.length() < 6) {
            throw new PasswordNotValidException();
        } else {
            this.password = password;
        }
    }
}