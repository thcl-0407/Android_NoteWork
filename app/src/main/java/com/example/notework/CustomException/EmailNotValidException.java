package com.example.notework.CustomException;

public class EmailNotValidException extends Exception{
    public EmailNotValidException(){
        super("Email Không Hợp Lệ");
    }
}
