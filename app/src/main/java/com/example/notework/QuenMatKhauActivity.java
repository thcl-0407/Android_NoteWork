package com.example.notework;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.notework.CustomException.EmailNotValidException;
import com.example.notework.Models.Message;
import com.example.notework.Models.User;
import com.example.notework.Retrofit.APIUtils;
import com.example.notework.Retrofit.DataClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuenMatKhauActivity extends AppCompatActivity {
    ImageButton btnBackToLogin_QMK;
    Button btnDo_GetPassword;

    EditText etEmail_Recovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quen_mat_khau);

        //Hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();
        btnBackToLogin_QMK_Click();
        btnDo_GetPassword_Click();
    }

    private void btnDo_GetPassword_Click() {
        btnDo_GetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();

                try {
                    user.setEmail(etEmail_Recovery.getText().toString());
                } catch (EmailNotValidException e) {
                    Toast.makeText(QuenMatKhauActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                ChangePass(user.getEmail());
            }
        });
    }

    private void Init_Data(){
        btnBackToLogin_QMK = (ImageButton) findViewById(R.id.btnBackToLogin_QMK);
        btnDo_GetPassword = (Button) findViewById(R.id.btnDo_GetPassword);

        etEmail_Recovery = (EditText) findViewById(R.id.etEmail_Recovery);
    }

    private void btnBackToLogin_QMK_Click() {
        btnBackToLogin_QMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ChangePass(String Email){
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.GetUserByEmail(Email);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getUser().size() == 1) {
                        Intent intent = new Intent(QuenMatKhauActivity.this, DoiMatKhauActivity.class);
                        intent.putExtra("Email", Email);
                        startActivity(intent);
                    }else {
                        Toast.makeText(QuenMatKhauActivity.this, "Email Không Tồn Tại" , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API Error", "Null");
                    Toast.makeText(QuenMatKhauActivity.this, "Có Lỗi Xảy Ra" , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("Retrofit Error Loading Profile", t.getMessage());
                Toast.makeText(QuenMatKhauActivity.this, "Có Lỗi Xảy Ra" , Toast.LENGTH_SHORT).show();
            }
        });
    }
}