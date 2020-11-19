package com.example.notework;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class RemindFragment extends Fragment {
    View viewRemind;

    ImageButton btnOpenSearchRemind;

    EditText etSearchRemind;

    ListView lvReminds;

    ConstraintLayout container_search_remind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewRemind = inflater.inflate(R.layout.fragment_remind, container, false);

        Init_Data();
        btnOpenSearchRemind_Click();

        return viewRemind;
    }

    private void Init_Data(){
        btnOpenSearchRemind = (ImageButton) viewRemind.findViewById(R.id.btnOpenSearchRemind);

        etSearchRemind = (EditText) viewRemind.findViewById(R.id.etSearchRemind);
        lvReminds = (ListView) viewRemind.findViewById(R.id.lvReminds);
        container_search_remind = (ConstraintLayout) viewRemind.findViewById(R.id.container_search_remind);
    }

    private void btnOpenSearchRemind_Click(){
        btnOpenSearchRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (container_search_remind.getVisibility() == View.GONE){
                    container_search_remind.setVisibility(View.VISIBLE);
                    etSearchRemind.setText("");
                }else {
                    container_search_remind.setVisibility(View.GONE);
                    etSearchRemind.setText("");
                }
            }
        });
    }
}