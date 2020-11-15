package com.example.notework;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class NoteDetailActivity extends AppCompatActivity {

    Button btnLuuGhiChu;
    ImageButton btnBackToNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        //Hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init_Data();
        btnBackToNotes_Click();
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
    }
}