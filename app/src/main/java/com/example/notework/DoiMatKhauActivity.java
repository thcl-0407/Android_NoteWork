package com.example.notework;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.notework.Models.Message;
import com.example.notework.Models.User;
import com.example.notework.Retrofit.APIUtils;
import com.example.notework.Retrofit.DataClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoiMatKhauActivity extends AppCompatActivity {
    ImageButton btnCancel_ChangePass;

    Button btnDo_ChangePass;

    EditText etPassword_ChangePass;
    EditText etRePassword_ChangePass;

    LinearLayout container_form_change_pass;
    ProgressBar prb_change_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau);

        //Hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();
        btnCancel_ChangePass_Click();
        btnDo_ChangePass_Click();
    }

    private void btnDo_ChangePass_Click() {
        btnDo_ChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = getIntent().getStringExtra("Email");
                String Password = etPassword_ChangePass.getText().toString();
                String RePassword = etRePassword_ChangePass.getText().toString();

                if(!Password.equals(RePassword)){
                    Toast.makeText(DoiMatKhauActivity.this, "Xác Nhận Mật Khẩu Sai", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User();

                try {
                    user.setEmail(Email);
                    user.setPassword(Password);
                }catch (Exception e){
                    Toast.makeText(DoiMatKhauActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                container_form_change_pass.setVisibility(View.GONE);
                prb_change_pass.setVisibility(View.VISIBLE);

                //Ẩn Nút Huỷ Đổi Mật Khẩu
                btnCancel_ChangePass.setVisibility(View.INVISIBLE);

                Change_Pass(user);
            }
        });
    }

    private void Init_Data() {
        btnCancel_ChangePass = (ImageButton) findViewById(R.id.btnCancel_ChangePass);
        btnDo_ChangePass = (Button) findViewById(R.id.btnDo_ChangePass);
        etPassword_ChangePass = (EditText) findViewById(R.id.etPassword_ChangePass);
        etRePassword_ChangePass = (EditText) findViewById(R.id.etRePassword_ChangePass);

        container_form_change_pass = (LinearLayout) findViewById(R.id.container_form_change_pass);
        prb_change_pass = (ProgressBar) findViewById(R.id.prb_change_pass);
    }

    private void btnCancel_ChangePass_Click() {
        btnCancel_ChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DoiMatKhauActivity.this);
                builder.setTitle("HUỶ CẬP NHẬT MẬT KHẨU");
                builder.setMessage("Bạn có chắc muốn huỷ cập nhật mật khẩu ?");
                builder.setNegativeButton("Đồng Ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setPositiveButton("Tiếp Tục", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                AlertDialog dialog  = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }

    /*
       Call API để get thông tin cá nhân mới nhất của người dùng
    */
    private void Change_Pass(User user) {
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.ChangePass(user);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getSuccess() == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DoiMatKhauActivity.this);
                        builder.setTitle("THÔNG BÁO");
                        builder.setMessage("Cập nhật mật khẩu thành công. Bạn cần đăng nhập lại.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(DoiMatKhauActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                        AlertDialog dialog  = builder.create();
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                    }else {
                        Toast.makeText(DoiMatKhauActivity.this, "Đổi Mật Khẩu Thất Bại", Toast.LENGTH_SHORT).show();

                        container_form_change_pass.setVisibility(View.VISIBLE);
                        prb_change_pass.setVisibility(View.GONE);

                        //Ẩn Nút Huỷ Đổi Mật Khẩu
                        btnCancel_ChangePass.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(DoiMatKhauActivity.this, "Đổi Mật Khẩu Thất Bại", Toast.LENGTH_SHORT).show();
                    Log.e("API Error", "Null");
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("Retrofit Error Change Pass", t.getMessage());
            }
        });
    }
}