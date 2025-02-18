package com.example.notework.Retrofit;

import com.example.notework.Models.Message;
import com.example.notework.Models.Note;
import com.example.notework.Models.Remind;
import com.example.notework.Models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DataClient {

    //Call API Người Dùng

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

    //****************************************************************************

    //Call API Ghi Chú

    @POST("note/add")
    Call<Message> AddNote(@Body Note note);

    @GET("note/id={id}")
    Call<Message> GetNoteByUserId(@Path("id") int UserID);

    @POST("note/update")
    Call<Message> UpdateNote(@Body Note note);

    @POST("note/delete/id={id}")
    Call<Message> DeleteNote(@Path("id") int NoteID);

    //****************************************************************************

    //Call API Remind

    @POST("remind/add")
    Call<Message> AddRemind(@Body Remind remind);

    @POST("remind/update")
    Call<Message> UpdateRemind(@Body Remind remind);

    @GET("remind/id={id}&date={date}")
    Call<Message> GetRemindByUserID(@Path("id") int UserID, @Path("date") String date);

    @GET("remind/delete/id={id}")
    Call<Message> DeleteRemind(@Path("id") int RemindID);
}
