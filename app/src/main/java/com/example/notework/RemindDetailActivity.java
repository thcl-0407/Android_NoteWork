package com.example.notework;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.notework.CustomAdapter.NhacNhoAdapter;
import com.example.notework.CustomDate.MyCustomDate;
import com.example.notework.Models.Message;
import com.example.notework.Models.Remind;
import com.example.notework.Notifier.NotifierAlarm;
import com.example.notework.Retrofit.APIUtils;
import com.example.notework.Retrofit.DataClient;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.sql.Struct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemindDetailActivity extends AppCompatActivity {
    final int ADD_REMIND = 9999;
    final int EDIT_REMIND = 8888;

    Button btnLuuNhacNho;
    Button btnXoaNhacNho;

    ImageButton btnBackToReminds;

    ImageButton btnOpen_DatePickerRemind;
    ImageButton btnOpen_TimePickerRemind;

    EditText etTitleRemind;
    EditText etContentRemind;
    EditText etTimeRemind;
    EditText etDateRemind;

    String DateRemind = "";
    Remind remind_edit;
    int Intent_Code;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_detail);

        Init_Data();

        //Hàm này sẽ load dữ liệu để chỉnh sửa nhắc nhở
        Loading_Data();

        Date_SetListener();
        Time_SetListener();
        btnBackToReminds_Click();
        btnLuuNhacNho_Click();
        btnXoaNhacNho_Click();
        btnOpen_DatePickerRemind_Click();
        btnOpen_TimePickerRemind_Click();

        //Hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void btnXoaNhacNho_Click() {
        btnXoaNhacNho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RemindDetailActivity.this);
                builder.setTitle("XOÁ NHẮC NHỞ");
                builder.setMessage("Bạn có chắc muốn xoá nhắc nhở này không ?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XoaNhacNho(remind_edit);
                    }
                });
                builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }

    private void Init_Data() {
        btnBackToReminds = (ImageButton) findViewById(R.id.btnBackToReminds);

        btnOpen_DatePickerRemind = (ImageButton) findViewById(R.id.btnOpen_DatePickerRemind);
        btnOpen_TimePickerRemind = (ImageButton) findViewById(R.id.btnOpen_TimePickerRemind);

        btnLuuNhacNho = (Button) findViewById(R.id.btnLuuNhacNho);
        btnXoaNhacNho = (Button) findViewById(R.id.btnXoaNhacNho);

        etDateRemind = (EditText) findViewById(R.id.etDateRemind);
        etTimeRemind = (EditText) findViewById(R.id.etTimeRemind);
        etTitleRemind = (EditText) findViewById(R.id.etRemindTitle);
        etContentRemind = (EditText) findViewById(R.id.etRemindContent);
    }

    private void Loading_Data() {
        Intent_Code = getIntent().getIntExtra("INTENT_CODE", -1);

        if (Intent_Code != -1) {
            if(Intent_Code == ADD_REMIND){
                btnXoaNhacNho.setVisibility(View.GONE);
            }

            if (Intent_Code == EDIT_REMIND){
                btnXoaNhacNho.setVisibility(View.VISIBLE);

                Bundle bundle = getIntent().getBundleExtra("NhacNhoBundle");
                remind_edit = (Remind) bundle.getSerializable("NhacNho");

                etTitleRemind.setText(remind_edit.getTitle());
                etContentRemind.setText(remind_edit.getContentRemind());
                DateRemind = remind_edit.getDateRemind();
                etDateRemind.setText(remind_edit.getDateRemind());
                etTimeRemind.setText(remind_edit.getTimeRemind());
            }
        }
    }

    private void btnLuuNhacNho_Click() {
        btnLuuNhacNho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String TitleRemind = etTitleRemind.getText().toString();
                String ContentRemind = etContentRemind.getText().toString();
                String TimeRemind = etTimeRemind.getText().toString();

                if (TitleRemind.trim().isEmpty()) {
                    Toast.makeText(RemindDetailActivity.this, "Chưa Nhập Tiêu Đề", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (DateRemind.trim().isEmpty()) {
                    Toast.makeText(RemindDetailActivity.this, "Chưa Chọn Ngày Thực Hiện", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TimeRemind.trim().isEmpty()) {
                    Toast.makeText(RemindDetailActivity.this, "Chưa Chọn Thời Gian Nhắc Nhở", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Thêm Nhắc Nhở
                if (Intent_Code == ADD_REMIND) {
                    Remind remind = new Remind();
                    remind.setTitle(TitleRemind);
                    remind.setContentRemind(ContentRemind);
                    remind.setDateRemind(DateRemind);
                    remind.setTimeRemind(TimeRemind);

                    SharedPreferences preferences = getSharedPreferences("data_login", Context.MODE_PRIVATE);
                    int user_id = preferences.getInt("user_id", -1);

                    if (user_id != -1) {
                        remind.setUserId(user_id);
                    } else {
                        Toast.makeText(RemindDetailActivity.this, "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ThemNhacNho(remind);
                }

                //Cập Nhật Nhắc Nhở
                if (Intent_Code == EDIT_REMIND) {
                    remind_edit.setTitle(TitleRemind);
                    remind_edit.setContentRemind(ContentRemind);
                    remind_edit.setDateRemind(DateRemind);
                    remind_edit.setTimeRemind(TimeRemind);

                    CapNhatNhacNho(remind_edit);
                }
            }
        });
    }

    private void Time_SetListener() {
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (etDateRemind.getText().toString().isEmpty()) {
                    return;
                }

                String Gio = Integer.toString(hourOfDay);
                String Phut = "00";

                if (minute < 10) {
                    Phut = 0 + Integer.toString(minute);
                } else {
                    Phut = Integer.toString(minute);
                }

                etTimeRemind.setText(Gio + ":" + Phut);
            }
        };
    }

    private void btnOpen_TimePickerRemind_Click() {
        btnOpen_TimePickerRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etDateRemind.getText().toString().isEmpty()) {
                    Toast.makeText(RemindDetailActivity.this, "Chưa Chọn Ngày Thực Hiện", Toast.LENGTH_SHORT).show();
                    return;
                }

                Calendar calendar = Calendar.getInstance();
                int gio = calendar.get(Calendar.HOUR_OF_DAY);
                int phut = calendar.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(RemindDetailActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        timeSetListener,
                        gio, phut, true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    private void btnOpen_DatePickerRemind_Click() {
        btnOpen_DatePickerRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int ngay = calendar.get(Calendar.DAY_OF_MONTH);
                int thang = calendar.get(Calendar.MONTH);
                int nam = calendar.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(
                        RemindDetailActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        nam, thang, ngay);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    private void Date_SetListener() {
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int Current_Year = Calendar.getInstance().get(Calendar.YEAR);
                int Current_Month = Calendar.getInstance().get(Calendar.MONTH);
                int Current_Day = Calendar.getInstance().get(Calendar.DATE);

                if (Current_Year > year) {
                    Toast.makeText(RemindDetailActivity.this, "Không Hợp Lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Current_Year == year && Current_Month > month) {
                    Toast.makeText(RemindDetailActivity.this, "Không Hợp Lệ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Current_Year == year && Current_Month == month) {
                    if (Current_Day > dayOfMonth) {
                        Toast.makeText(RemindDetailActivity.this, "Không Hợp Lệ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                String ngay = Integer.toString(dayOfMonth);
                String thang = Integer.toString(month + 1);
                String nam = Integer.toString(year);

                DateRemind = thang + "-" + ngay + "-" + nam;
                etDateRemind.setText(ngay + "-" + thang + "-" + nam);
            }
        };
    }

    private void btnBackToReminds_Click() {
        btnBackToReminds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ThemNhacNho(Remind remind) {
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.AddRemind(remind);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getSuccess() == 0) {
                        Toast.makeText(RemindDetailActivity.this, "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                    } else {
                        //Set Thông Báo
                        SetRemind(remind);

                        Intent intent02 = new Intent(RemindDetailActivity.this, NotesActivity.class);
                        intent02.putExtra("NgayThucHien", DateRemind);
                        setResult(RESULT_OK, intent02);
                        finish();
                    }
                } else {
                    Log.e("API Error", "Null");
                    Toast.makeText(RemindDetailActivity.this, "Không Thể Kết Nối Đến Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }

    private void CapNhatNhacNho(Remind remind) {
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.UpdateRemind(remind);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getSuccess() == 0) {
                        Toast.makeText(RemindDetailActivity.this, "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                    } else {
                        //Set Thông Báo
                        SetRemind(remind);

                        Intent intent02 = new Intent(RemindDetailActivity.this, NotesActivity.class);
                        intent02.putExtra("NgayThucHien", DateRemind);
                        setResult(RESULT_OK, intent02);
                        finish();
                    }
                } else {
                    Log.e("API Error", "Null");
                    Toast.makeText(RemindDetailActivity.this, "Không Thể Kết Nối Đến Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }

    private void XoaNhacNho(Remind remind){
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.DeleteRemind(remind.getRemindId());
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getSuccess() == 0) {
                        Toast.makeText(RemindDetailActivity.this, "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent02 = new Intent(RemindDetailActivity.this, NotesActivity.class);
                        intent02.putExtra("NgayThucHien", remind.getDateRemind());
                        setResult(RESULT_OK, intent02);
                        finish();
                    }
                } else {
                    Log.e("API Error", "Null");
                    Toast.makeText(RemindDetailActivity.this, "Không Thể Kết Nối Đến Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
    }

    private void SetRemind(Remind remind){
        int Year = MyCustomDate.GetYear(remind.getDateRemind());
        int Month = MyCustomDate.GetMonth(remind.getDateRemind());
        int Day = MyCustomDate.GetDay(remind.getDateRemind());
        int Hour = MyCustomDate.GetHour(remind.getTimeRemind());
        int Minute = MyCustomDate.GetMinute(remind.getTimeRemind());

        /*
        Log.e("Nam", String.valueOf(Year));
        Log.e("Thang", String.valueOf(Month));
        Log.e("Ngay", String.valueOf(Day));
        Log.e("Gio", String.valueOf(Hour));
        Log.e("Minute", String.valueOf(Minute));*/

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        calendar.set(Year, (Month - 1), Day, Hour, Minute, 0);

        Bundle bundle = new Bundle();
        bundle.putSerializable("NhacNho", remind);

        Intent intent = new Intent(RemindDetailActivity.this, NotifierAlarm.class);
        intent.putExtra("NhacNhoBundle", bundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(RemindDetailActivity.this, remind.getUserId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}