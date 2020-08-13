package com.doan.timnhatro.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.doan.timnhatro.R;
import com.doan.timnhatro.adapter.MotelRoomAdapter;
import com.doan.timnhatro.adapter.ResultNullAdapter;
import com.doan.timnhatro.adapter.UtilitiesAdapter;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.model.MotelRoom;
import com.doan.timnhatro.model.Utilities;
import com.doan.timnhatro.utils.AccountUtils;
import com.doan.timnhatro.utils.LocationUtils;
import com.doan.timnhatro.view.CreatePostsActivity;
import com.doan.timnhatro.view.LoginActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.sephiroth.android.library.rangeseekbar.RangeSeekBar;

public class HouseFragment extends Fragment {

    //private View container;
    Locale localeVN = new Locale("vi", "VN");
    private NumberFormat numberFormat = NumberFormat.getInstance(localeVN);
    private RecyclerView recRoomsFeatured, recyclerViewListUtilities;
    private MotelRoomAdapter motelRoomAdapter;
    private ResultNullAdapter resultNullAdapter;
    private Button SearchNearBtn, AddPostBtn, btnChonKhoangGia, btnLoaiPhong, btnUse, btnSearch, btnSearchMotelRoom;
    private ArrayList<MotelRoom> arrayMotelRoom = new ArrayList<>();
    private ArrayList<MotelRoom> arrayMotelRoomTmp = new ArrayList<>();
    private ArrayList<String> arrayList = new ArrayList<>();
    //private ArrayList<MotelRoom> arrayMotelRoomSave = new ArrayList<>();
    private Spinner spinner;
    private LinearLayout linearLayoutTypeRoom;
    private RelativeLayout relativeLayoutPriceRange;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehaviorTypeRoom;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehaviorPriceRange;
    private ListView listView;
    private TextView txtDisplay;
    private RangeSeekBar rangeSeekBar;
    private Dialog dialog;
    private ImageButton imgButton;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dataRef;

    boolean isClickSpinner = false, isClickButtonRange = false, isClickButtonTypeRoom = false, isClickItem = false;
    long number1, number2;
    Bundle bundle;
    StringBuilder builder = new StringBuilder();

    public HouseFragment() {
        // Required empty public constructor
    }

    ViewFlipper viewFlipper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_house, container, false);

        initView(v);

        setUpRecyclerView();

        SearchNearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
        AddPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccountUtils.getInstance().getAccount() != null) {
                    startActivity(new Intent(getActivity().getApplicationContext(), CreatePostsActivity.class));
                    return;
                }
                startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
            }
        });
        SliderShow(v);

        btnSearch.setOnClickListener(v1 -> showDialogSearch(getContext()));

        return v;
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(getContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_PERMISSION);
            return false;
        }
        if (LocationUtils.getInstance().isGPSEnable()) {
            loadFragment(new MapsFragment());
            return true;
        }

        new AlertDialog.Builder(getActivity().getApplicationContext())
                .setTitle("GPS Chưa Được Kích Hoạt")
                .setMessage("Vui lòng kích hoạt GPS và thử lại sau")
                .setPositiveButton("Trở lại", null)
                .show();

        return false;
    }

    private void loadFragment(MapsFragment mapsFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, mapsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setUpRecyclerView() {
        recRoomsFeatured.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recRoomsFeatured.setLayoutManager(layoutManager);

        FirebaseDatabase.getInstance().getReference().child("MotelRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                            arrayMotelRoomTmp.add(motelRoom);
                        }

                        for (int i= 0 ; i< arrayMotelRoomTmp.size(); i++){
                            arrayMotelRoom.add(arrayMotelRoomTmp.get(arrayMotelRoomTmp.size()-1-i));
                        }

                        motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                        recRoomsFeatured.setAdapter(motelRoomAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //Loc data theo ten thanh pho
    private void setUpRecyclerViewWithNameCity(final String nameCity) {
        recRoomsFeatured.setHasFixedSize(true);
        recRoomsFeatured.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        FirebaseDatabase.getInstance().getReference()
                .child("MotelRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                            if (motelRoom.getCity().equals(nameCity)) {
                                arrayMotelRoom.add(motelRoom);
                            }
                        }
                        if (arrayMotelRoom.size()>0){
                            motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                            recRoomsFeatured.setAdapter(motelRoomAdapter);
                        }else {


                            arrayList.add("Không có kết quả nào phù hợp!");
                            resultNullAdapter = new ResultNullAdapter(arrayList);
                            recRoomsFeatured.setAdapter(resultNullAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //Loc data theo ten loai phong nhu: Nha nguyen can, chung cu, nha tro,....
    private void setUpRecyclerViewWithTypeRoom(final String nameTypeRoom) {
        recRoomsFeatured.setHasFixedSize(true);
        recRoomsFeatured.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        FirebaseDatabase.getInstance().getReference()
                .child("MotelRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                            if (motelRoom != null) {
                                if (motelRoom.getNameMotelRoom().equals(nameTypeRoom)) {
                                    arrayMotelRoom.add(motelRoom);
                                }
                            }
                        }

                        if (arrayMotelRoom.size()>0){
                            motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                            recRoomsFeatured.setAdapter(motelRoomAdapter);
                        }else {


                            arrayList.add("Không có kết quả nào phù hợp!");
                            resultNullAdapter = new ResultNullAdapter(arrayList);
                            recRoomsFeatured.setAdapter(resultNullAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setUpRecyclerViewWithRangePrice(final long a, final long b) {
        recRoomsFeatured.setHasFixedSize(true);
        recRoomsFeatured.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        FirebaseDatabase.getInstance().getReference()
                .child("MotelRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                            if (motelRoom != null) {
                                if ((motelRoom.getPrice() >= a) && (motelRoom.getPrice() <= b)) {
                                    arrayMotelRoom.add(motelRoom);
                                }
                            }
                        }

                        if (arrayMotelRoom.size()>0){
                            motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                            recRoomsFeatured.setAdapter(motelRoomAdapter);
                        }else {


                            arrayList.add("Không có kết quả nào phù hợp!");
                            resultNullAdapter = new ResultNullAdapter(arrayList);
                            recRoomsFeatured.setAdapter(resultNullAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SliderShow(View v) {
        int image[] = {R.drawable.slide1, R.drawable.slide2, R.drawable.slide3};
        viewFlipper = v.findViewById(R.id.vf_image_slider);

        for (int i = 0; i < image.length; i++) {
            flipperImage(image[i]);
        }
    }

    private void initView(View v) {
        recRoomsFeatured = v.findViewById(R.id.recRoomsFeatured);
        SearchNearBtn = v.findViewById(R.id.btn_findNear);
        AddPostBtn = v.findViewById(R.id.btn_addPost);
        btnSearch = v.findViewById(R.id.btn_findRoom);
    }

    public void flipperImage(int image) {
        ImageView imageView = new ImageView(getActivity());
        imageView.setBackgroundResource(image);
        viewFlipper.addView(imageView);
        viewFlipper.setFlipInterval(5000);
        viewFlipper.setAutoStart(true);

        //animation

        viewFlipper.setInAnimation(getActivity(), android.R.anim.slide_in_left);
        viewFlipper.setOutAnimation(getActivity(), android.R.anim.slide_out_right);
    }

    private void showDialogSearch(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_dialog);

        linearLayoutTypeRoom = dialog.findViewById(R.id.layout_type_room);
        relativeLayoutPriceRange = dialog.findViewById(R.id.layout_price_range);
        bottomSheetBehaviorTypeRoom = BottomSheetBehavior.from(linearLayoutTypeRoom);
        bottomSheetBehaviorPriceRange = BottomSheetBehavior.from(relativeLayoutPriceRange);
        btnLoaiPhong = dialog.findViewById(R.id.button_type_room);
        btnChonKhoangGia = dialog.findViewById(R.id.button_choose_range_price);
        btnSearchMotelRoom = dialog.findViewById(R.id.button_search);
        imgButton = dialog.findViewById(R.id.imagebutton_cancel);
        recyclerViewListUtilities = dialog.findViewById(R.id.recyclerview_list_utilities);
        rangeSeekBar = dialog.findViewById(R.id.range_seekBar);
        txtDisplay = dialog.findViewById(R.id.textview_price_range);

        bottomSheetBehaviorTypeRoom.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehaviorPriceRange.setState(BottomSheetBehavior.STATE_HIDDEN);

        spinner = dialog.findViewById(R.id.spinner_khuvuc);
        listView = dialog.findViewById(R.id.listview_type_room);
        btnUse = dialog.findViewById(R.id.button_used);

        bundle = new Bundle();

        dialog.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        dialog.getWindow().getAttributes().horizontalMargin = 10.0f;
        dialog.getWindow().getAttributes().verticalMargin = 10.0f;

        setUpRecyclerViewListUtilities(recyclerViewListUtilities);

        getDataCityFromFirebase();

        imgButton.setOnClickListener(v -> dialog.cancel());

        btnSearchMotelRoom.setOnClickListener(v -> {
            if (bundle != null) {
                String nameCity = bundle.getString("nameCity");
                String nameTypeRoom = bundle.getString("nameTypeRoom");
                long rangePrice1 = bundle.getLong("rangePrice1");
                long rangePrice2 = bundle.getLong("rangePrice2");
                String utilities = bundle.getString("utilities");

                if (!nameCity.equals("Chọn tỉnh/ thành phố") && nameTypeRoom != null && (rangePrice1 != 0 && rangePrice2 != 0)) {
                    arrayMotelRoom.clear();
                    displayListMotelRoomWithAllCondition(nameCity, nameTypeRoom, rangePrice1, rangePrice2);
                } else if (!nameCity.equals("Chọn tỉnh/ thành phố") && nameTypeRoom != null) {
                    arrayMotelRoom.clear();
                    displayListhMotelRoomWithNameCityAndNameTypeRoom(nameCity, nameTypeRoom);
                } else if (nameTypeRoom != null && rangePrice1 != 0 && rangePrice2 != 0) {
                    arrayMotelRoom.clear();
                    displayListMotelRoomWithRangePriceAndNameTypeRoom(nameTypeRoom, rangePrice1, rangePrice2);
                }
                else if (!nameCity.equals("Chọn tỉnh/ thành phố") && rangePrice1 != 0 && rangePrice2 != 0) {
                    arrayMotelRoom.clear();
                    displayListMotelRoomWithRangePriceAndNameCity(nameCity, rangePrice1, rangePrice2);
                }
                else {
                    if (!nameCity.equals("Chọn tỉnh/ thành phố")) {
                        arrayMotelRoom.clear();
                        setUpRecyclerViewWithNameCity(nameCity);
                    } else if (nameTypeRoom != null) {
                        arrayMotelRoom.clear();
                        setUpRecyclerViewWithTypeRoom(nameTypeRoom);
                    }
                    else if(rangePrice1 != 0 && rangePrice2 != 0){
                        arrayMotelRoom.clear();
                        setUpRecyclerViewWithRangePrice(rangePrice1, rangePrice2);
                    }
                    else if (isClickItem){
                        arrayMotelRoom.clear();
                        displayPostWithUtilities(utilities);
                    }
                }
            }

            dialog.cancel();
        });

        btnLoaiPhong.setOnClickListener(listener);
        btnChonKhoangGia.setOnClickListener(listener);

        dialog.show();
    }

    private void displayPostWithUtilities(String data) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        dataRef = firebaseDatabase.getReference().child("MotelRoom");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                    if(motelRoom.getListUtilities().contains(data)){
                        arrayMotelRoom.add(motelRoom);
                    }
                }
                motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                recRoomsFeatured.setAdapter(motelRoomAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private View.OnClickListener listener = v -> {
        switch (v.getId()) {
            case R.id.button_choose_range_price:
                showBottomSheetPriceRange();
                break;
            case R.id.button_type_room:
                showBottomSheetTypeRoom();
                break;
        }
    };

    private void getDataCityFromFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        dataRef = firebaseDatabase.getReference().child("CiTy");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String nameCity = dataSnapshot.getValue(String.class);
                    arrayList.add(nameCity);
                }

                setUpSpinner(arrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpSpinner(final List<String> list) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bundle.putString("nameCity", list.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showBottomSheetTypeRoom() {
        bottomSheetBehaviorTypeRoom.setState(BottomSheetBehavior.STATE_COLLAPSED);
        setUpListViewTypeRoom();
    }

    private void setUpListViewTypeRoom() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        dataRef = firebaseDatabase.getReference().child("typemotel");
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final ArrayList<String> arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String typeRoom = dataSnapshot.getValue(String.class);
                    arrayList.add(typeRoom);
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(arrayAdapter);

                listView.setOnItemClickListener((parent, view, position, id) -> {
                    String nameTypeRoom = arrayList.get(position);
                    btnLoaiPhong.setText(nameTypeRoom);
                    bundle.putString("nameTypeRoom", nameTypeRoom);

                    bottomSheetBehaviorTypeRoom.setState(BottomSheetBehavior.STATE_HIDDEN);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showBottomSheetPriceRange() {
        bottomSheetBehaviorPriceRange.setState(BottomSheetBehavior.STATE_COLLAPSED);
        txtDisplay.setText("Giá từ 0đ đến 10.000.000đ");
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onProgressChanged(RangeSeekBar rangeSeekBar, int i, int i1, boolean b) {
                number1 = Long.parseLong(String.valueOf(i)) * 100000;
                number2 = Long.parseLong(String.valueOf(i1)) * 100000;
                txtDisplay.setText("Giá từ " + numberFormat.format(number1) + "đ đến " + numberFormat.format(number2) + "đ");
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar rangeSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar rangeSeekBar) {

            }
        });

        btnUse.setOnClickListener(v -> {
            btnChonKhoangGia.setText(numberFormat.format(number1) + "đ đến " + numberFormat.format(number2) + "đ");
            bottomSheetBehaviorPriceRange.setState(BottomSheetBehavior.STATE_HIDDEN);
            bundle.putLong("rangePrice1", number1);
            bundle.putLong("rangePrice2", number2);
        });
    }

    private void displayListhMotelRoomWithNameCityAndNameTypeRoom(final String str1,
                                                                  final String str2) {
        FirebaseDatabase.getInstance().getReference().child("MotelRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                            if (motelRoom.getCity().equals(str1) && motelRoom.getNameMotelRoom().equals(str2)) {
                                arrayMotelRoom.add(motelRoom);
                            }
                        }

                        if (arrayMotelRoom.size()>0){
                            motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                            recRoomsFeatured.setAdapter(motelRoomAdapter);
                        }else {


                            arrayList.add("Không có kết quả nào phù hợp!");
                            resultNullAdapter = new ResultNullAdapter(arrayList);
                            recRoomsFeatured.setAdapter(resultNullAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void displayListMotelRoomWithRangePriceAndNameCity(final String str1,
                                                               final long a, final long b) {
        FirebaseDatabase.getInstance().getReference().child("MotelRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                            if (motelRoom.getCity().equals(str1) &&
                                    (motelRoom.getPrice() >= a && motelRoom.getPrice() <= b)) {
                                arrayMotelRoom.add(motelRoom);
                            }
                        }

                        if (arrayMotelRoom.size()>0){
                            motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                            recRoomsFeatured.setAdapter(motelRoomAdapter);
                        }else {


                            arrayList.add("Không có kết quả nào phù hợp!");
                            resultNullAdapter = new ResultNullAdapter(arrayList);
                            recRoomsFeatured.setAdapter(resultNullAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void displayListMotelRoomWithRangePriceAndNameTypeRoom(final String str1,
                                                                   final long a, final long b) {
        FirebaseDatabase.getInstance().getReference().child("MotelRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                            if (motelRoom.getNameMotelRoom().equals(str1) &&
                                    (motelRoom.getPrice() >= a && motelRoom.getPrice() <= b)) {
                                arrayMotelRoom.add(motelRoom);
                            }
                        }

                        if (arrayMotelRoom.size()>0){
                            motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                            recRoomsFeatured.setAdapter(motelRoomAdapter);
                        }else {


                            arrayList.add("Không có kết quả nào phù hợp!");
                            resultNullAdapter = new ResultNullAdapter(arrayList);
                            recRoomsFeatured.setAdapter(resultNullAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void displayListMotelRoomWithAllCondition(final String str1, final String str2,
                                                      final long a, final long b) {
        FirebaseDatabase.getInstance().getReference().child("MotelRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MotelRoom motelRoom = dataSnapshot.getValue(MotelRoom.class);
                            if (motelRoom.getCity().equals(str1) && motelRoom.getNameMotelRoom().equals(str2) &&
                                    (motelRoom.getPrice() >= a && motelRoom.getPrice() <= b)) {
                                arrayMotelRoom.add(motelRoom);
                            }
                        }

                        if (arrayMotelRoom.size()>0){
                            motelRoomAdapter = new MotelRoomAdapter(arrayMotelRoom);
                            recRoomsFeatured.setAdapter(motelRoomAdapter);
                        }else {


                            arrayList.add("Không có kết quả nào phù hợp!");
                            resultNullAdapter = new ResultNullAdapter(arrayList);
                            recRoomsFeatured.setAdapter(resultNullAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setUpRecyclerViewListUtilities(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

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

        final UtilitiesAdapter adapter = new UtilitiesAdapter(arrayList, getContext());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemUtilitiesClickListener(stringArrayList -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0; i<stringArrayList.size(); i++){
                stringBuilder.append(stringArrayList.get(i) + ", ");
            }
            String dataUtilities = stringBuilder.toString();
            bundle.putString("utilities", dataUtilities);
            isClickItem = true;
        });
    }
}
