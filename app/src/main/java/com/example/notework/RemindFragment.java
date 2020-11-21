package com.example.notework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.notework.CustomAdapter.NhacNhoAdapter;
import com.example.notework.Models.Message;
import com.example.notework.Models.Note;
import com.example.notework.Models.Remind;
import com.example.notework.Retrofit.APIUtils;
import com.example.notework.Retrofit.DataClient;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.Wave;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RemindFragment extends Fragment {
    final int ADD_REMIND = 9999;
    final int EDIT_REMIND = 8888;

    View viewRemind;

    ImageButton btnOpenSearchRemind;
    ImageButton btnOpenCreateRemind;

    EditText etSearchRemind;

    ListView lvReminds;

    ArrayList<Remind> Reminds;
    NhacNhoAdapter RemindAdapter;

    CalendarView cvRemind;
    ProgressBar prb_loading_remind;
    ConstraintLayout container_search_remind;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewRemind = inflater.inflate(R.layout.fragment_remind, container, false);

        Init_Data();
        btnOpenSearchRemind_Click();
        btnOpenCreateRemind_Click();
        cvRemind_Changed();
        lvReminds_ItemClick();

        //Show Progress Bar
        prb_loading_remind.setVisibility(View.VISIBLE);
        lvReminds.setVisibility(View.GONE);

        registerForContextMenu(lvReminds);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+07:00"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String selectedDate = simpleDateFormat.format(calendar.getTime());

        SharedPreferences preferences = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
        int user_id = preferences.getInt("user_id", -1);

        if (user_id != -1) {
            GetNhacNho(user_id, selectedDate);
        }

        return viewRemind;
    }

    private void lvReminds_ItemClick() {
        lvReminds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("NhacNho", Reminds.get(position));

                Intent intent = new Intent(getActivity(), RemindDetailActivity.class);
                intent.putExtra("INTENT_CODE", EDIT_REMIND);
                intent.putExtra("NhacNhoBundle", bundle);
                startActivityForResult(intent, EDIT_REMIND);
            }
        });
    }

    private void cvRemind_Changed() {
        cvRemind.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //Show Progress Bar
                prb_loading_remind.setVisibility(View.VISIBLE);
                lvReminds.setVisibility(View.GONE);

                String Day = Integer.toString(dayOfMonth);
                String Month = Integer.toString(month + 1);
                String Year = Integer.toString(year);

                SharedPreferences preferences = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
                int user_id = preferences.getInt("user_id", -1);

                String selectedDate = Month + "-" + Day + "-" + Year;

                if (user_id != -1) {
                    GetNhacNho(user_id, selectedDate);
                }
            }
        });
    }

    private void btnOpenCreateRemind_Click() {
        btnOpenCreateRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RemindDetailActivity.class);
                intent.putExtra("INTENT_CODE", ADD_REMIND);
                startActivityForResult(intent, ADD_REMIND);
            }
        });
    }

    private void Init_Data() {
        btnOpenSearchRemind = (ImageButton) viewRemind.findViewById(R.id.btnOpenSearchRemind);
        btnOpenCreateRemind = (ImageButton) viewRemind.findViewById(R.id.btnThemNhacNho);

        etSearchRemind = (EditText) viewRemind.findViewById(R.id.etSearchRemind);
        lvReminds = (ListView) viewRemind.findViewById(R.id.lvReminds);
        container_search_remind = (ConstraintLayout) viewRemind.findViewById(R.id.container_search_remind);

        prb_loading_remind = (ProgressBar) viewRemind.findViewById(R.id.prb_loading_remind);
        Sprite sprite = new ThreeBounce();
        prb_loading_remind.setIndeterminateDrawable(sprite);

        cvRemind = (CalendarView) viewRemind.findViewById(R.id.cvRemind);
    }

    private void btnOpenSearchRemind_Click() {
        btnOpenSearchRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (container_search_remind.getVisibility() == View.GONE) {
                    container_search_remind.setVisibility(View.VISIBLE);
                    etSearchRemind.setText("");
                } else {
                    container_search_remind.setVisibility(View.GONE);
                    etSearchRemind.setText("");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_REMIND && resultCode == getActivity().RESULT_OK) {
            String selectedDate = data.getStringExtra("NgayThucHien");

            try {
                cvRemind.setDate(new SimpleDateFormat("MM-dd-yyyy").parse(selectedDate).getTime(), true, true);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SharedPreferences preferences = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
            int user_id = preferences.getInt("user_id", -1);

            if (user_id != -1) {
                GetNhacNho(user_id, selectedDate);
            }
        }

        if (requestCode == EDIT_REMIND && resultCode == getActivity().RESULT_OK) {
            String selectedDate = data.getStringExtra("NgayThucHien");

            try {
                cvRemind.setDate(new SimpleDateFormat("MM-dd-yyyy").parse(selectedDate).getTime(), true, true);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SharedPreferences preferences = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
            int user_id = preferences.getInt("user_id", -1);

            if (user_id != -1) {
                GetNhacNho(user_id, selectedDate);
            }
        }
    }

    private void GetNhacNho(int UserID, String date) {
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.GetRemindByUserID(UserID, date);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    prb_loading_remind.setVisibility(View.GONE);
                    lvReminds.setVisibility(View.VISIBLE);

                    if (message.getSuccess() == 1) {
                        Reminds = message.getReminds();
                        RemindAdapter = new NhacNhoAdapter(getActivity(), Reminds);
                        lvReminds.setAdapter(RemindAdapter);
                    } else {
                        Reminds = message.getReminds();
                        RemindAdapter = new NhacNhoAdapter(getActivity(), Reminds);
                        lvReminds.setAdapter(RemindAdapter);
                    }
                } else {
                    Log.e("API Error", "Null");
                    Toast.makeText(getActivity(), "Không Thể Kết Nối Đến Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
            }
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.lvReminds) {
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.menu_item_remind, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.im_XoaNhacNho:
                XoaNhacNho(Reminds.get(menuInfo.position));
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void XoaNhacNho(Remind remind) {
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.DeleteRemind(remind.getRemindId());
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getSuccess() == 0) {
                        Toast.makeText(getActivity(), "Có Lỗi Xảy Ra", Toast.LENGTH_SHORT).show();
                    } else {
                        GetNhacNho(remind.getUserId(), remind.getDateRemind());
                        Reminds.remove(remind);
                        RemindAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("API Error", "Null");
                    Toast.makeText(getActivity(), "Không Thể Kết Nối Đến Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
            }
        });
    }
}