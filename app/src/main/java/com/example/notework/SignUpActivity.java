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

public class SignUpActivity extends AppCompatActivity {
    LinearLayout container_form_sign_up;

    Button btnOpen_Login;
    Button btnDo_SignUp;

    EditText etEmail_SignUp;
    EditText etPassword_SignUp;
    EditText etRePassword_SignUp;
    EditText etFirstName_SignUp;
    EditText etLastName_SignUp;

    ProgressBar prb_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();
        btnOpen_Login_Click();
        btnDo_SignUp_Click();
    }

    protected void Init_Data() {
        btnOpen_Login = (Button) findViewById(R.id.btnOpen_Login);
        btnDo_SignUp = (Button) findViewById(R.id.btnDo_SignUp);

        etEmail_SignUp = (EditText) findViewById(R.id.etEmail_SignUp);
        etPassword_SignUp = (EditText) findViewById(R.id.etPassword_SignUp);
        etRePassword_SignUp = (EditText) findViewById(R.id.etRePassword_SignUp);
        etFirstName_SignUp = (EditText) findViewById(R.id.etFirstName_SignUp);
        etLastName_SignUp = (EditText) findViewById(R.id.etLastName_SignUp);

        prb_sign_up = (ProgressBar) findViewById(R.id.prb_sign_up);
        Sprite wave = new Wave();
        prb_sign_up.setIndeterminateDrawable(wave);

        container_form_sign_up = (LinearLayout) findViewById(R.id.container_form_sign_up);
    }

    //Click Nút "Đăng Ký"
    private void btnDo_SignUp_Click() {
        btnDo_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FirstName = etFirstName_SignUp.getText().toString().trim();
                String LastName = etLastName_SignUp.getText().toString().trim();
                String Email = etEmail_SignUp.getText().toString().trim();
                String Password = etPassword_SignUp.getText().toString().trim();
                String RePassword = etRePassword_SignUp.getText().toString().trim();

                if (FirstName.isEmpty() || LastName.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Chưa Nhập Họ Tên", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Email.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Chưa Nhập Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Password.isEmpty() || RePassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Chưa Nhập Mật Khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Password.compareTo(RePassword) != 0) {
                    Toast.makeText(SignUpActivity.this, "Xác Nhận Mật Khẩu Sai", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User();

                try {
                    user.setFirstName(FirstName);
                    user.setLastName(LastName);
                    user.setEmail(Email);
                    user.setPassword(RePassword);
                } catch (Exception e) {
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                container_form_sign_up.setVisibility(View.GONE);
                prb_sign_up.setVisibility(View.VISIBLE);

                //Call API Đăng Ký User
                SignUp(user);
            }
        });
    }

    //Click Nút "Bạn Đã Có Tài Khoản"
    protected void btnOpen_Login_Click() {
        btnOpen_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //Call API Đăng Ký User
    protected void SignUp(User user) {
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.AddUser(user);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getSuccess() == 1) {
                        SharedPreferences preferences = getSharedPreferences("data_login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("email", user.getEmail());
                        editor.commit();

                        NotifySignUpProcess("Đăng Ký Thành Công. Tự Động Đăng Nhập", true);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Đăng Ký Thất Bại", Toast.LENGTH_SHORT).show();
                        container_form_sign_up.setVisibility(View.VISIBLE);
                        prb_sign_up.setVisibility(View.GONE);
                    }
                } else {
                    NotifySignUpProcess("Không Thể Kết Nối Với Hệ Thống (-1)", false);
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                NotifySignUpProcess("Không Thể Kết Nối Với Hệ Thống (-1)", false);
                Log.e("Retrofit Error Sign Up", t.getMessage());
            }
        });
    }

    protected void NotifySignUpProcess(String message, Boolean status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("THÔNG BÁO");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(status){
                    Intent intent = new Intent(SignUpActivity.this, NotesActivity.class);
                    startActivity(intent);
                    finish();
                }

                container_form_sign_up.setVisibility(View.VISIBLE);
                prb_sign_up.setVisibility(View.GONE);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}