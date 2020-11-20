package com.example.notework;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.AlertDialog;
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
import android.widget.Toast;

import com.example.notework.CustomAdapter.GhiChuAdapter;
import com.example.notework.Models.Message;
import com.example.notework.Models.Note;
import com.example.notework.Retrofit.APIUtils;
import com.example.notework.Retrofit.DataClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteDetailActivity extends AppCompatActivity {
    final int ADD_CODE = 999;
    final int EDIT_CODE = 888;

    Button btnLuuGhiChu;
    Button btnXoaGhiChu;
    ImageButton btnBackToNotes;

    EditText etNoteTitle;
    EditText etNoteContent;

    int NOTE_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        //Hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        NOTE_CODE = getIntent().getIntExtra("NOTE_CODE", 0);

        Init_Data();
        btnBackToNotes_Click();
        btnLuuGhiChu_Click();
        btnXoaGhiChu_Click();

        //Ẩn Nút Xoá Ghi Chú
        if(NOTE_CODE == ADD_CODE){
            btnXoaGhiChu.setVisibility(View.GONE);
        }

        //Load Nội Dung Ghi Chú và Hiện Nút Xoá Ghi Chú
        if(NOTE_CODE == EDIT_CODE){
            btnXoaGhiChu.setVisibility(View.VISIBLE);

            Bundle bundle = getIntent().getBundleExtra("NoteEdit_Bundle");
            Note note = (Note) bundle.getSerializable("NoteEdit");

            etNoteTitle.setText(note.getTitle());
            etNoteContent.setText(note.getContentNote());
        }
    }

    private void btnXoaGhiChu_Click() {
        btnXoaGhiChu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Xử Lý Xoá Ghi Chú Khi Kiểm Tra Code Intent = Edit Code
                if(NOTE_CODE == EDIT_CODE){
                    Bundle bundle = getIntent().getBundleExtra("NoteEdit_Bundle");
                    Note note = (Note) bundle.getSerializable("NoteEdit");

                    XoaGhiChu(note);
                }
            }
        });
    }

    private void btnLuuGhiChu_Click() {
        btnLuuGhiChu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Xử Lý Thêm Ghi Chú
                if(NOTE_CODE == ADD_CODE){
                    String Title = etNoteTitle.getText().toString();
                    String Content = etNoteContent.getText().toString();

                    if(Title.isEmpty()){
                        Toast.makeText(NoteDetailActivity.this, "Chưa Nhập Tiêu Đề Ghi Chú", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Note note = new Note();

                    note.setTitle(Title);
                    note.setContentNote(Content);

                    SharedPreferences preferences = getSharedPreferences("data_login", Context.MODE_PRIVATE);
                    int user_id = preferences.getInt("user_id", -1);

                    if(user_id != -1){
                        note.setUserId(user_id);
                    }else {
                        Toast.makeText(NoteDetailActivity.this, "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ThemGhiChu(note);
                }

                //Xử Lý Chỉnh Sửa Ghi Chú
                if(NOTE_CODE == EDIT_CODE){
                    String Title = etNoteTitle.getText().toString();
                    String Content = etNoteContent.getText().toString();

                    if(Title.isEmpty()){
                        Toast.makeText(NoteDetailActivity.this, "Chưa Nhập Tiêu Đề Ghi Chú", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Bundle bundle = getIntent().getBundleExtra("NoteEdit_Bundle");
                    Note note = (Note) bundle.getSerializable("NoteEdit");

                    note.setTitle(Title);
                    note.setContentNote(Content);

                    CapNhatGhiChu(note);
                }
            }
        });
    }

    //Click quay về danh sách ghi chú
    private void btnBackToNotes_Click() {
        btnBackToNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Init_Data(){
        btnBackToNotes = (ImageButton) findViewById(R.id.btnBackToNotes);
        btnLuuGhiChu = (Button) findViewById(R.id.btnLuuGhiChu);
        btnXoaGhiChu = (Button) findViewById(R.id.btnXoaGhiChu);
        etNoteTitle = (EditText) findViewById(R.id.etNoteTitle);
        etNoteContent = (EditText) findViewById(R.id.etNoteContent);
    }

    private void ThemGhiChu(Note note){
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.AddNote(note);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getSuccess() == 1) {
                        Intent intent_02 = new Intent(NoteDetailActivity.this, NotesActivity.class);
                        setResult(RESULT_OK, intent_02);
                        finish();
                    }else {
                        Toast.makeText(NoteDetailActivity.this, "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API Error", "Null");
                    Toast.makeText(NoteDetailActivity.this, "Không Thể Kết Nối Đến Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("Retrofit Error Add Note", t.getMessage());
            }
        });
    }

    private void CapNhatGhiChu(Note note){
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.UpdateNote(note);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getSuccess() == 1) {
                        Intent intent_02 = new Intent(NoteDetailActivity.this, NotesActivity.class);
                        setResult(RESULT_OK, intent_02);
                        finish();
                    }else {
                        Toast.makeText(NoteDetailActivity.this, "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API Error", "Null");
                    Toast.makeText(NoteDetailActivity.this, "Không Thể Kết Nối Đến Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("Retrofit Error Add Note", t.getMessage());
            }
        });
    }

    //Call API Xoá Ghi Chú
    private void Delete_Note(Note note) {
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.DeleteNote(note.getNoteId());
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getSuccess() == 0) {
                        Toast.makeText(NoteDetailActivity.this, "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent_02 = new Intent(NoteDetailActivity.this, NotesActivity.class);
                        setResult(RESULT_OK, intent_02);
                        finish();
                    }
                } else {
                    Log.e("API Error", "Null");
                    Toast.makeText(NoteDetailActivity.this, "Không Thể Kết Nối Đến Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("Retrofit Error Loading Notes", t.getMessage());
            }
        });
    }


    //Xử Lý Xoá Ghi Chú
    private void XoaGhiChu(Note note){
        //Hỏi xác nhận người dùng trước khi xoá
        AlertDialog.Builder builder = new AlertDialog.Builder(NoteDetailActivity.this);
        builder.setTitle("XOÁ GHI CHÚ");
        builder.setMessage("Bạn có chắc muốn xoá ghi chú này không ?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Delete_Note(note);
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
}