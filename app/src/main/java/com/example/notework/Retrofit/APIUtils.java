package com.example.notework.Retrofit;

public class APIUtils {
    public static final String baseURL = "http://54.179.131.22:3000/api/";

    public static DataClient getData(){
        return RetrofitClient.getClient(baseURL).create(DataClient.class);
    }
}
