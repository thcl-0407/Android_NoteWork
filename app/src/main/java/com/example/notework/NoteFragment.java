package com.example.notework;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.notework.CustomAdapter.GhiChuAdapter;
import com.example.notework.Models.Message;
import com.example.notework.Models.Note;
import com.example.notework.Retrofit.APIUtils;
import com.example.notework.Retrofit.DataClient;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.ThreeBounce;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteFragment extends Fragment {
    final int ADD_NOTE = 999;
    final int EDIT_NOTE = 888;

    View view;
    ImageButton btnCreatNote;
    ImageButton btnOpenSearchNote;

    EditText etSearchNote;

    ConstraintLayout container_search_note;

    ArrayList<Note> Notes;
    RecyclerView rvNote;
    GhiChuAdapter NoteAdapter;
    GhiChuAdapter.OnItemClickListener listener;

    SwipeRefreshLayout srlGhiChu;
    ProgressBar prb_loading_note;
    StaggeredGridLayoutManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_note, container, false);

        Init_Data();

        btnCreatNote_Click();
        btnOpenSearchNote_Click();

        etSearchNote_TextChanged();

        //Khoá Refresh Khi Bắt Đầu Loading Dữ Liệu Ghi Chú
        srlGhiChu.setEnabled(false);

        rvNote.setVisibility(View.GONE);
        prb_loading_note.setVisibility(View.VISIBLE);

        //Load Danh Sách Ghi Chú
        SharedPreferences preferences = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
        int user_id = preferences.getInt("user_id", -1);

        if (user_id != -1) {
            Loading_Note(user_id);
        }

        srlGhiChu_RefreshListener();
        rvNote_ItemClick();

        return view;
    }

    private void rvNote_ItemClick() {
        listener = new GhiChuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("NoteEdit", Notes.get(position));
                intent.putExtra("NoteEdit_Bundle", bundle);
                intent.putExtra("NOTE_CODE", EDIT_NOTE);
                startActivityForResult(intent, EDIT_NOTE);
            }
        };
    }

    private void etSearchNote_TextChanged() {
        etSearchNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (container_search_note.getVisibility() == View.GONE) {
                    return;
                }

                String Key_Value = etSearchNote.getText().toString().trim();

                ArrayList<Note> Results = new ArrayList<>();
                for (Note note : Notes) {
                    if (note.getTitle().toLowerCase().contains(Key_Value.toLowerCase())) {
                        Results.add(note);
                    }
                }

                if (Results.size() > 0) {
                    NoteAdapter = new GhiChuAdapter(Results, listener);
                    rvNote.setAdapter(NoteAdapter);
                    manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    rvNote.setLayoutManager(manager);
                    NoteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void srlGhiChu_RefreshListener() {
        srlGhiChu.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Notes.clear();

                //Load Danh Sách Ghi Chú
                SharedPreferences preferences = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
                int user_id = preferences.getInt("user_id", -1);

                if (user_id != -1) {
                    rvNote.setVisibility(View.GONE);
                    prb_loading_note.setVisibility(View.VISIBLE);
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

                if (container_search_note.getVisibility() == View.GONE) {
                    container_search_note.setVisibility(View.VISIBLE);
                    return;
                }
                if (container_search_note.getVisibility() == View.VISIBLE) {
                    container_search_note.setVisibility(View.GONE);
                    return;
                }
            }
        });
    }

    private void Init_Data() {
        btnCreatNote = (ImageButton) view.findViewById(R.id.btnCreatNote);
        btnOpenSearchNote = (ImageButton) view.findViewById(R.id.btnOpenSearchNote);

        container_search_note = (ConstraintLayout) view.findViewById(R.id.container_search_note);
        etSearchNote = (EditText) view.findViewById(R.id.etSearchNote);

        Notes = new ArrayList<>();
        rvNote = (RecyclerView) view.findViewById(R.id.rvGhiChu);

        srlGhiChu = (SwipeRefreshLayout) view.findViewById(R.id.srlGhiChu);

        prb_loading_note = (ProgressBar) view.findViewById(R.id.prb_loading_note);
        Sprite sprite = new ThreeBounce();
        prb_loading_note.setIndeterminateDrawable(sprite);
    }

    //Click nút Tạo mới Ghi Chú
    private void btnCreatNote_Click() {
        btnCreatNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
                intent.putExtra("NOTE_CODE", ADD_NOTE);
                startActivityForResult(intent, ADD_NOTE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE && resultCode == getActivity().RESULT_OK) {
            Notes.clear();

            //Load Danh Sách Ghi Chú
            SharedPreferences preferences = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
            int user_id = preferences.getInt("user_id", -1);

            if (user_id != -1) {
                Loading_Note(user_id);
            }
        }

        if (requestCode == EDIT_NOTE && resultCode == getActivity().RESULT_OK) {
            Notes.clear();

            //Load Danh Sách Ghi Chú
            SharedPreferences preferences = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
            int user_id = preferences.getInt("user_id", -1);

            if (user_id != -1) {
                Loading_Note(user_id);
            }
        }
    }

    private void Loading_Note(int UserID) {
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.GetNoteByUserId(UserID);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getNotes().size() == 0) {
                        rvNote.setVisibility(View.VISIBLE);
                        prb_loading_note.setVisibility(View.GONE);
                    }

                    if (message.getNotes().size() > 0) {
                        for (int i = 0; i < message.getNotes().size(); i++) {
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

                        NoteAdapter = new GhiChuAdapter(Notes, listener);
                        NoteAdapter.notifyDataSetChanged();

                        rvNote.setAdapter(NoteAdapter);
                        manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                        rvNote.setLayoutManager(manager);

                        //Cho Phép Refresh
                        srlGhiChu.setEnabled(true);

                        rvNote.setVisibility(View.VISIBLE);
                        prb_loading_note.setVisibility(View.GONE);
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

    //Item Menu Long Click Trên Item Của Danh Sách Ghi Chú
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int Note_Index =  NoteAdapter.GetPosition();

        switch (item.getItemId()) {
            case 101:
                XoaGhiChu(Note_Index);
                break;
        }

        return super.onContextItemSelected(item);
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
                        Toast.makeText(getActivity(), "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                    }else {
                        Notes.remove(note);
                        NoteAdapter.notifyDataSetChanged();
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


    //Xử Lý Xoá Ghi Chú
    private void XoaGhiChu(int Note_Index){
        //Hỏi xác nhận người dùng trước khi xoá
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("XOÁ GHI CHÚ");
        builder.setMessage("Bạn có chắc muốn xoá ghi chú này không ?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Delete_Note(Notes.get(Note_Index));
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