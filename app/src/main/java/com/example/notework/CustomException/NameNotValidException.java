package com.example.notework.CustomException;

public class NameNotValidException extends Exception {
    public NameNotValidException(){
        super("Họ Tên Không Hợp Lệ");
    }
}
