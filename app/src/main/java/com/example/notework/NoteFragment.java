package com.example.notework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.notework.CustomAdapter.GhiChuAdapter;
import com.example.notework.Models.Message;
import com.example.notework.Models.Note;
import com.example.notework.Retrofit.APIUtils;
import com.example.notework.Retrofit.DataClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteFragment extends Fragment {
    final int ADD_NOTE = 999;

    View view;
    ImageButton btnCreatNote;
    ImageButton btnOpenSearchNote;

    EditText etSearchNote;

    ConstraintLayout container_search_note;

    ArrayList<Note> Notes;
    RecyclerView rvNote;
    GhiChuAdapter NoteAdapter;

    SwipeRefreshLayout srlGhiChu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_note, container, false);

        Init_Data();

        btnCreatNote_Click();
        btnOpenSearchNote_Click();

        //Khoá Refresh Khi Bắt Đầu Loading Dữ Liệu Ghi Chú
        srlGhiChu.setEnabled(false);

        //Load Danh Sách Ghi Chú
        SharedPreferences preferences = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
        int user_id = preferences.getInt("user_id", -1);

        if(user_id != -1){
            Loading_Note(user_id);
        }

        srlGhiChu_RefreshListener();
        return view;
    }

    private void srlGhiChu_RefreshListener() {
        srlGhiChu.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Notes.clear();

                //Load Danh Sách Ghi Chú
                SharedPreferences preferences = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
                int user_id = preferences.getInt("user_id", -1);

                if(user_id != -1){
                    Loading_Note(user_id);
                }

                NoteAdapter.notifyDataSetChanged();
                srlGhiChu.setRefreshing(false);
            }
        });
    }

    private void btnOpenSearchNote_Click() {
        btnOpenSearchNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Xoá Text Search
                etSearchNote.setText("");

                if(container_search_note.getVisibility() == View.GONE){
                    container_search_note.setVisibility(View.VISIBLE);
                    return;
                }
                if(container_search_note.getVisibility() == View.VISIBLE){
                    container_search_note.setVisibility(View.GONE);
                    return;
                }
            }
        });
    }

    private void Init_Data(){
        btnCreatNote = (ImageButton) view.findViewById(R.id.btnCreatNote);
        btnOpenSearchNote = (ImageButton) view.findViewById(R.id.btnOpenSearchNote);

        container_search_note = (ConstraintLayout) view.findViewById(R.id.container_search_note);
        etSearchNote = (EditText) view.findViewById(R.id.etSearchNote);

        Notes = new ArrayList<>();
        rvNote = (RecyclerView) view.findViewById(R.id.rvGhiChu);

        srlGhiChu = (SwipeRefreshLayout) view.findViewById(R.id.srlGhiChu);
    }

    //Click nút Tạo mới Ghi Chú
    private void btnCreatNote_Click() {
        btnCreatNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
                startActivityForResult(intent, ADD_NOTE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void Loading_Note(int UserID){
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.GetNoteByUserId(UserID);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getNotes().size() > 0) {
                        for (int i = 0; i < message.getNotes().size(); i++){
                            int NoteID = message.getNotes().get(i).getNoteId();
                            String Title = message.getNotes().get(i).getTitle();
                            String Content = message.getNotes().get(i).getContentNote();

                            Note note = new Note();
                            note.setNoteId(NoteID);
                            note.setTitle(Title);
                            note.setContentNote(Content);
                            note.setUserId(UserID);

                            Notes.add(note);
                        }

                        NoteAdapter = new GhiChuAdapter(getActivity(), Notes);
                        NoteAdapter.notifyDataSetChanged();

                        rvNote.setAdapter(NoteAdapter);
                        rvNote.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

                        //Cho Phép Refresh
                        srlGhiChu.setEnabled(true);
                    }
                } else {
                    Log.e("API Error", "Null");
                    Toast.makeText(getActivity(), "Không Thể Kết Nối Đến Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("Retrofit Error Loading Notes", t.getMessage());
            }
        });
    }
}