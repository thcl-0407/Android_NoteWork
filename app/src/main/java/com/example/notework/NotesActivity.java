package com.example.notework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NotesActivity extends AppCompatActivity {
    Fragment selected_fragment;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        //Hide Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout_container_fragment, new NoteFragment()).commit();

        Init_Data();
        bottomNavigationView_ItemClick();
    }

    protected void Init_Data(){
        bottomNavigationView  = findViewById(R.id.bottom_navigation_main);
    }

    private void bottomNavigationView_ItemClick() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.im_note:
                        selected_fragment = new NoteFragment();
                        break;
                    case R.id.im_remind:
                        selected_fragment = new RemindFragment();
                        break;
                    case R.id.im_profile:
                        selected_fragment = new ProfileFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_container_fragment, selected_fragment).commit();
                return true;
            }
        });
    }
}