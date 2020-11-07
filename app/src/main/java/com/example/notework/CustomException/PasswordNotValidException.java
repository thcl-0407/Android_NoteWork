package com.example.notework.CustomException;

public class PasswordNotValidException extends Exception {
    public PasswordNotValidException(){
        super("Mật Khẩu Dài Từ 6 Kí Tự Trở Lên");
    }
}
