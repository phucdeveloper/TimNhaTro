package com.doan.timnhatro.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.doan.timnhatro.R;
import com.doan.timnhatro.adapter.PictureArrayAdapter;
import com.doan.timnhatro.adapter.UtilitiesAdapter;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.model.MotelRoom;
import com.doan.timnhatro.model.Utilities;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Typeface.BOLD;

public class DetailMotelRoomActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private MotelRoom motelRoom;
    private CircleImageView imgAvatar;
    private TextView txtNameMotelRoom,txtTime,txtPrice,txtStreet,edtDescribe,txtName,txtPhoneNumber;
    private RecyclerView recyclerViewListUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_motel_room);

        initView();
        getIntentData();
        createToolbar();
        createArrayPicture();
        loadUI();
    }

    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_32dp);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    private void loadUI() {
        txtNameMotelRoom.setText(motelRoom.getNameMotelRoom());

        @SuppressLint("SimpleDateFormat")
        SpannableString formatTime = new SpannableString("Ngày đăng:   " + new SimpleDateFormat("HH:mm:ss   dd/MM/yyyy").format(Long.valueOf(motelRoom.getId())));
        formatTime.setSpan(new StyleSpan(BOLD),0,10,0);
        formatTime.setSpan(new ForegroundColorSpan(Color.BLACK),0,10,0);
        txtTime.setText(formatTime);

        SpannableString formatPrice = new SpannableString("Giá phòng:   " + new DecimalFormat("###,###").format(motelRoom.getPrice()).replace(",",".") + " VND");
        formatPrice.setSpan(new StyleSpan(BOLD),0,10,0);
        txtPrice.setText(formatPrice);

        SpannableString formatStreet = new SpannableString("Đường:   " + motelRoom.getStreet() + ",  " + motelRoom.getDistrict() + ",  " + motelRoom.getCity());
        formatStreet.setSpan(new StyleSpan(BOLD),0,6,0);
        txtStreet.setText(formatStreet);

        edtDescribe.setText(motelRoom.getDescribe());

        Glide.with(this)
                .load(motelRoom.getAccount().getAvatar())
                .into(imgAvatar);

        txtName.setText(motelRoom.getAccount().getName());

        SpannableString formatPhoneNumber = new SpannableString("Số điện thoại:   " + motelRoom.getAccount().getPhoneNumber());
        formatPhoneNumber.setSpan(new StyleSpan(BOLD),0,14,0);
        formatTime.setSpan(new ForegroundColorSpan(Color.BLACK),0,14,0);
        txtPhoneNumber.setText(formatPhoneNumber);

        setUpRecyclerViewListUtilities(recyclerViewListUtilities);
    }

    private void getIntentData() {
        motelRoom = getIntent().getParcelableExtra(Constants.MOTEL_ROOM);
    }

    private void createArrayPicture() {
        PictureArrayAdapter pictureArrayAdapter = new PictureArrayAdapter(motelRoom.getArrayPicture());
        viewPager.setAdapter(pictureArrayAdapter);
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        txtNameMotelRoom = findViewById(R.id.txtNameMotelRoom);
        txtTime = findViewById(R.id.txtTime);
        txtPrice = findViewById(R.id.txtPrice);
        txtStreet = findViewById(R.id.txtStreet);
        edtDescribe = findViewById(R.id.edtDescribe);
        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        recyclerViewListUtilities = findViewById(R.id.recyclerview_list_utilities);

    }

    private void setUpRecyclerViewListUtilities(RecyclerView recyclerView) {
        final ArrayList<Utilities> arrayList = new ArrayList<>();
        arrayList.add(new Utilities("WC riêng", R.drawable.icons8_wc_50, false));
        arrayList.add(new Utilities("Cửa sổ", R.drawable.icons8_open_window_50, false));
        arrayList.add(new Utilities("Wifi", R.drawable.icons8_wi_fi_24, true));
        arrayList.add(new Utilities("Chủ riêng", R.drawable.icons8_private_50, false));
        arrayList.add(new Utilities("Máy nước nóng", R.drawable.icons8_water_heater_50, false));
        arrayList.add(new Utilities("Tủ lạnh", R.drawable.icons8_fridge_50, false));
        arrayList.add(new Utilities("Gác lửng", R.drawable.icons8_stairs_50, false));
        arrayList.add(new Utilities("Tủ đồ", R.drawable.icons8_closet_50, false));
        arrayList.add(new Utilities("Thú cưng", R.drawable.icons8_pet_commands_summon_50, false));
        arrayList.add(new Utilities("Chỗ để xe", R.drawable.icons8_parking_50, false));
        arrayList.add(new Utilities("An ninh", R.drawable.icons8_smart_home_checked_50, false));
        arrayList.add(new Utilities("Tự do", R.drawable.icons8_clock_50, true));
        arrayList.add(new Utilities("Máy lạnh", R.drawable.icons8_air_conditioner_50, false));
        arrayList.add(new Utilities("Nhà bếp", R.drawable.icons8_kitchen_room_50, false));
        arrayList.add(new Utilities("Máy giặt", R.drawable.icons8_washing_50, false));
        arrayList.add(new Utilities("Giường", R.drawable.icons8_bed_50, false));
        arrayList.add(new Utilities("Ti vi", R.drawable.icons8_tv_50, false));

        ArrayList<Utilities> listDataUtilities = new ArrayList<>();
        String listUtilities = motelRoom.getListUtilities();
        String[] data = listUtilities.split(", ");
        for (int i=0; i<data.length; i++){
            for (int j=0; j<arrayList.size(); j++){
                if (data[i].equals(arrayList.get(j).getNameUtilities())){
                    listDataUtilities.add(arrayList.get(j));
                }
            }
        }
        recyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(DetailMotelRoomActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);

        final UtilitiesAdapter adapter = new UtilitiesAdapter(listDataUtilities, DetailMotelRoomActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("MissingPermission")
    public void onClickCallNow(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_PERMISSION);
                return;
            }
        }

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + motelRoom.getAccount().getPhoneNumber()));
        startActivity(intent);
    }

    public void onClickDirect(View view) {
        Intent intent = new Intent(this,DirectActivity.class);
        intent.putExtra(Constants.POSITION,motelRoom.getPosition());
        startActivity(intent);
    }
}
