package com.example.notework.Retrofit;

import com.example.notework.Models.Message;
import com.example.notework.Models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DataClient {
    @POST("add")
    Call<Message> AddUser(@Body User user);

    @POST("login")
    Call<Message> Login(@Body User user);
}
