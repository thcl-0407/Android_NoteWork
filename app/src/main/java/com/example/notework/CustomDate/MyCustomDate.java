package com.example.notework.CustomDate;

import java.util.Date;

public class MyCustomDate {
    public static int GetDay(String Date){
       String[] temp = Date.split("-");
       return Integer.parseInt(temp[1]);
    }

    public static int GetMonth(String Date){
        String[] temp = Date.split("-");
        return Integer.parseInt(temp[0]);
    }

    public static int GetYear(String Date){
        String[] temp = Date.split("-");
        return Integer.parseInt(temp[2]);
    }

    public static int GetHour(String Time){
        String[] temp = Time.split(":");
        return Integer.parseInt(temp[0]);
    }

    public static int GetMinute(String Time){
        String[] temp = Time.split(":");
        return Integer.parseInt(temp[1]);
    }
}
