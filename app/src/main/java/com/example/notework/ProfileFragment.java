package com.example.notework;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.notework.Models.Message;
import com.example.notework.Models.User;
import com.example.notework.Retrofit.APIUtils;
import com.example.notework.Retrofit.DataClient;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    View profileView;
    ShimmerFrameLayout shimmer_layout;
    LinearLayout container_form_profile;
    LinearLayout container_button_update_profile;

    ImageButton btnSettingProfile;

    EditText etFirstName;
    EditText etLastName;
    EditText etEmail;

    BottomNavigationView bottom_navigation_main;

    Button btnCapNhatProfile;
    Button btnHuyCapNhat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.fragment_profile, container, false);

        Init_Data();

        btnSettingProfile_Click();
        btnHuyCapNhat_Click();
        btnCapNhatProfile_Click();

        //Lấy log đăng nhập lưu trên máy
        SharedPreferences preferences_login = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);

        //Lấy log thông tin người dùng lưu trên máy
        SharedPreferences preferences_profile = getActivity().getSharedPreferences("data_profile", Context.MODE_PRIVATE);

        String Email_Login = preferences_login.getString("email", "");
        String Email_Profile = preferences_profile.getString("email", "");

        //Nếu như thông tin email người dùng lưu trên máy bị xoá thì sẽ cập nhật lại
        /*Ngược lại nếu như email thông tin người dùng có tồn tại
        và bằng với email đăng nhập
        thì lấy thông tin đã lưu trên máy hiển thị.
        * */
        if (Email_Login != "" && Email_Profile == "") {
            Loading_Profile_By_Email(Email_Login);
        } else {
            if (Email_Login != "" && Email_Profile != "") {
                if (Email_Login.equals(Email_Profile)) {
                    shimmer_layout.setVisibility(View.GONE);
                    container_form_profile.setVisibility(View.VISIBLE);

                    String First_Name = preferences_profile.getString("first_name", "");
                    String Last_Name = preferences_profile.getString("last_name", "");

                    etFirstName.setText(First_Name);
                    etLastName.setText(Last_Name);
                    etEmail.setText(Email_Profile);
                } else {
                    Loading_Profile_By_Email(Email_Login);
                }
            }
        }

        return profileView;
    }

    //Khỏi tạo các giá trị dữ liệu và ánh xạ với các control trên màn hình
    private void Init_Data() {
        shimmer_layout = (ShimmerFrameLayout) profileView.findViewById(R.id.shimmer_view_container);
        shimmer_layout.startShimmer();
        shimmer_layout.stopShimmer();

        container_form_profile = (LinearLayout) profileView.findViewById(R.id.container_form_profile);
        container_button_update_profile = (LinearLayout) profileView.findViewById(R.id.container_button_update_profile);

        btnSettingProfile = (ImageButton) profileView.findViewById(R.id.btnSetting_Profile);

        etEmail = (EditText) profileView.findViewById(R.id.etEmail_Profile);
        etFirstName = (EditText) profileView.findViewById(R.id.etFirstName_Profile);
        etLastName = (EditText) profileView.findViewById(R.id.etLastName_Profile);

        bottom_navigation_main = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation_main);

        btnHuyCapNhat = (Button) profileView.findViewById(R.id.btnHuyCapNhat);
        btnCapNhatProfile = (Button) profileView.findViewById(R.id.btnCapNhatProfile);
    }

    //Khởi tạo menu Settings
    private void Init_PopupMenu() {
        PopupMenu popup = new PopupMenu(getActivity(), btnSettingProfile);
        popup.inflate(R.menu.menu_profile_navigation_bar);
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.im_SuaThongTin:
                        bottom_navigation_main.setVisibility(View.GONE);
                        container_button_update_profile.setVisibility(View.VISIBLE);
                        btnSettingProfile.setVisibility(View.INVISIBLE);

                        etFirstName.setEnabled(true);
                        etLastName.setEnabled(true);

                        etFirstName.setBackgroundResource(R.drawable.border_bottom_edit_text);
                        etLastName.setBackgroundResource(R.drawable.border_bottom_edit_text);

                        etFirstName.setPadding(5, 5, 5, 5);
                        etLastName.setPadding(5, 5, 5, 5);
                        break;
                    case R.id.im_DoiMatKhau:
                        DoiMatKhau();
                        break;
                    case R.id.im_DangXuat:
                        DangXuat();
                        break;
                }

                return true;
            }
        });
    }

    //Click nút cập nhật profile
    private void btnCapNhatProfile_Click() {
        btnCapNhatProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FirstName = etFirstName.getText().toString();
                String LastName = etLastName.getText().toString();

                SharedPreferences preferences_login = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
                String Email_Login = preferences_login.getString("email", "");

                User user = new User();

                try {
                    user.setEmail(Email_Login);
                    user.setFirstName(FirstName);
                    user.setLastName(LastName);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                Update_FullName_by_Email(user);
            }
        });
    }

    private void btnHuyCapNhat_Click() {
        btnHuyCapNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("HUỶ CẬP NHẬT");
                builder.setMessage("Bạn có chắc muốn huỷ bỏ ?");
                builder.setNegativeButton("Đồng Ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bottom_navigation_main.setVisibility(View.VISIBLE);
                        container_button_update_profile.setVisibility(View.GONE);
                        btnSettingProfile.setVisibility(View.VISIBLE);

                        etFirstName.setEnabled(false);
                        etLastName.setEnabled(false);

                        etFirstName.setBackgroundResource(android.R.color.transparent);
                        etLastName.setBackgroundResource(android.R.color.transparent);

                        etFirstName.setPadding(0, 0, 0, 0);
                        etLastName.setPadding(0, 0, 0, 0);
                    }
                });
                builder.setPositiveButton("Tiếp Tục Chỉnh Sửa", new DialogInterface.OnClickListener() {
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

    //Click hiển thị menu Settings
    private void btnSettingProfile_Click() {
        btnSettingProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Init_PopupMenu();
            }
        });
    }

    /*
        Call API để get thông tin cá nhân mới nhất của người dùng
     */
    private void Loading_Profile_By_Email(String Email) {
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.GetUserByEmail(Email);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getUser().size() == 1) {
                        String FirstName = message.getUser().get(0).getFirstName();
                        String LastName = message.getUser().get(0).getLastName();

                        etFirstName.setText(FirstName);
                        etLastName.setText(LastName);
                        etEmail.setText(Email);

                        //Lưu lại thông tin mới cập nhật vào trong bộ nhớ máy
                        SharedPreferences preferences_profile = getActivity().getSharedPreferences("data_profile", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences_profile.edit();
                        editor.putString("first_name", FirstName);
                        editor.putString("last_name", LastName);
                        editor.putString("email", Email);
                        editor.commit();

                        shimmer_layout.setVisibility(View.GONE);
                        container_form_profile.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e("API Error", "Null");
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("Retrofit Error Loading Profile", t.getMessage());
            }
        });
    }

    /*
        Call API để cập nhật họ tên của người dùng
     */
    private void Update_FullName_by_Email(User user) {
        DataClient dataClient = APIUtils.getData();
        Call<Message> call = dataClient.UpdateFullName(user);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Message message = (Message) response.body();

                if (message != null) {
                    if (message.getSuccess() == 1) {
                        Loading_Profile_By_Email(user.getEmail());

                        bottom_navigation_main.setVisibility(View.VISIBLE);
                        container_button_update_profile.setVisibility(View.GONE);
                        btnSettingProfile.setVisibility(View.VISIBLE);

                        etFirstName.setEnabled(false);
                        etLastName.setEnabled(false);

                        etFirstName.setBackgroundResource(android.R.color.transparent);
                        etLastName.setBackgroundResource(android.R.color.transparent);

                        etFirstName.setPadding(0, 0, 0, 0);
                        etLastName.setPadding(0, 0, 0, 0);
                    } else {
                        Toast.makeText(getActivity(), "Cập Nhật Thất Bại", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("API Error", "Null");
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.e("Retrofit Error Updating Profile", t.getMessage());
            }
        });
    }

    //Đăng xuất khỏi hệ thống
    private void DangXuat() {
        //Thông báo hỏi xác nhận đăng xuất
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ĐĂNG XUẤT");
        builder.setMessage("Bạn có chắc muốn đăng xuất ?");

        //Nếu người dùng chọn đồng ý
        builder.setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Xoá các dữ liệu tài khoản trên máy
                SharedPreferences preferences_login = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences_login.edit();
                editor.remove("email");
                editor.commit();

                SharedPreferences preferences_profile = getActivity().getSharedPreferences("data_profile", Context.MODE_PRIVATE);
                editor = preferences_profile.edit();
                editor.remove("first_name");
                editor.remove("last_name");
                editor.remove("email");
                editor.commit();

                //Kết thúc ---> chuyển sang màn hình đăng nhập
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        //Nếu người dùng chọn huỷ
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

    private void DoiMatKhau() {
        SharedPreferences preferences_login = getActivity().getSharedPreferences("data_login", Context.MODE_PRIVATE);
        String Email = preferences_login.getString("email", "");

        if (Email != "") {
            Intent intent = new Intent(getActivity(), DoiMatKhauActivity.class);
            intent.putExtra("Email", Email);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Không Thể Đổi Mật Khẩu Lúc Này", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}