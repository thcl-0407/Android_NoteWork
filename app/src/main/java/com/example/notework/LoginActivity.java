package com.example.notework;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notework.Models.Message;
import com.example.notework.Models.User;
import com.example.notework.Retrofit.APIUtils;
import com.example.notework.Retrofit.DataClient;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    LinearLayout container_form;

    EditText etEmail;
    EditText etPassword;

    Button btnOpen_SignUp;
    Button btnLogin;

    ProgressBar prb_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();
        btnOpen_SignUp_Click();
        btnLogin_Click();
    }

    protected void Init_Data() {
        etPassword = (EditText) findViewById(R.id.etPassword_Login);
        etEmail = (EditText) findViewById(R.id.etEmail_Login);

        btnOpen_SignUp = (Button) findViewById(R.id.btnOpen_SignUp);
        btnLogin = (Button) findViewById(R.id.btnDo_Login);

        prb_login = (ProgressBar) findViewById(R.id.prb_login);
        Sprite wave = new Wave();
        prb_login.setIndeterminateDrawable(wave);
        prb_login.setVisibility(View.GONE);

        container_form = (LinearLayout) findViewById(R.id.container_form);
    }

    //Click Nút "Bạn Chưa Có Tài Khoản"
    protected void btnOpen_SignUp_Click() {
        btnOpen_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //Click Nút "Đăng Nhập"
    private void btnLogin_Click() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = etEmail.getText().toString();
                String Password = etPassword.getText().toString();

                User user = new User();

                try {
                    user.setEmail(Email);
                    user.setPassword(Password);
                }catch (Exception e){
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                container_form.setVisibility(View.GONE);
                prb_login.setVisibility(View.VISIBLE);

                //Call API Đăng Nhập
                Login(user);
            }
        });
    }

    //Call API Đăng Nhập
    protected void Login(User user){
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.Login(user);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();
                if(message != null){
                    if (message.getSuccess() == 1){
                        SharedPreferences preferences = getSharedPreferences("data_login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("email", user.getEmail());
                        editor.commit();

                        Intent intent = new Intent(LoginActivity.this, NotesActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this, "Đăng Nhập Thất Bại", Toast.LENGTH_SHORT).show();
                        container_form.setVisibility(View.VISIBLE);
                        prb_login.setVisibility(View.GONE);
                    }
                }else {
                    NotifyLoginProcess("Không Thể Kết Nối Với Hệ Thống (-1)");
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                NotifyLoginProcess("Không Thể Kết Nối Với Hệ Thống (-1)");
                Log.e("Retrofit Error Login", t.getMessage());
            }
        });
    }

    protected void NotifyLoginProcess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("THÔNG BÁO");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                container_form.setVisibility(View.VISIBLE);
                prb_login.setVisibility(View.GONE);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}