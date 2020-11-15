package com.example.notework.Retrofit;

import com.example.notework.Models.Message;
import com.example.notework.Models.Note;
import com.example.notework.Models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DataClient {
    @POST("add")
    Call<Message> AddUser(@Body User user);

    @POST("login")
    Call<Message> Login(@Body User user);

    @GET("email={email}")
    Call<Message> GetUserByEmail(@Path("email") String Email);

    @POST("change/user")
    Call<Message> UpdateFullName(@Body User user);

    @POST("change/pass")
    Call<Message> ChangePass(@Body User user);

    @POST("note/add")
    Call<Message> AddNote(@Body Note note);

    @GET("note/id={id}")
    Call<Message> GetNoteByUserId(@Path("id") int UserID);
}
