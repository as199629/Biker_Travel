package com.bikerlifeYang;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

//20210118 S--------------------------------------------
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.location.Address;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.ListView;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

//20210118 O--------------------------------------------

public class Biker_time_map extends AppCompatActivity
        implements OnMapReadyCallback, View.OnClickListener {
    //google map使用FragmentActivity、OnMapReadyCallback

    //plan------------------------------------------------------------------------
    private TextView time_map_check2, backbtn;
    private TextView distanceBtn;
    private AutoCompleteTextView location_start, location_goal;
    private ArrayAdapter<String> autoText;
    private TextView output;
    private Geocoder geocoder;
    private Button transfer;
    private TextView lat;
    private TextView lon;
    private TextView lat_goal;
    private TextView lon_goal;
    private double latitude, longitude, latitude_goal, longitude_goal;
    private Button map_btn;
    //private static final String DB_File = "bikermap.db", DB_TABLE = "E100";
    private static final String DB_FILE = "bikerlife.db", DB_TABLE = "E100";
    private SQLiteDatabase mBikermapDb;
    private TextView output_history;
    private ListView hList;
    private String[] hListArr;
    private ScrollView time_map_history_scroll;
    private Dialog mLoginDlg;

    private int DBversion;
    private FriendDbHelper22 dbHper;
    private ArrayList<String> recSet;

    //plan------------------------------------------------------------------------

    private static final int REQUEST_CODE = 1;
    private Button plan;
    private Intent intent = new Intent();
    private Intent intent01 = new Intent();
    private TextView watch, time_map_pathfinding;
    private Button selectBtn;
    private ImageView main_map;
    private ImageView main_map2;
    private EditText time_map_start;
    private EditText time_map_goal;
    private TextView img_friend1, img_friend2, img_friend3;
    private SeekBar mSeekBar;
    private GoogleMap mMap;
    private Menu menu;

    private static final String TAG = Biker_time_map.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;

    // 載入 Places API
    private PlacesClient placesClient;

    // 載入 Fused Location Provider
    private FusedLocationProviderClient fusedLocationProviderClient;

    //若取得位置不允許或失敗時顯示的預設地點(職訓局)和zoom
    //private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);  //24.17113123820597, 120.61010273294859
    private final LatLng defaultLocation = new LatLng(24.17113123820597, 120.61010273294859);
    //-------------20210118 add
    private final LatLng userLocationA = new LatLng(24.17004914514876, 120.61018707616434);

    //-------------20210118 add
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // 設備當前(最後)所在的地理位置
    private Location lastKnownLocation;

    // 用於儲存活動狀態的鍵
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // 用於選擇當前位置
    private static final int M_MAX_ENTRIES = 5;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLngs;
    private String[][] locations;
    private double dLat;
    private double dLon;
    private int resID;
    private int resID1;
    private BitmapDescriptor image_des;
    private int icosel;
    private LatLng VGPS;
    private String sqlctl;

    private String t_u_id;
    private double t_lat;
    private double t_lon;
    private GoogleSignInAccount account;
    private LocationManager manager;// 取得系統服務的LocationManager物件
    private String addressName;
    private String addressName_goal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);
        super.onCreate(savedInstanceState);

        //從已保存的實例狀態中檢索位置和攝像機位置
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        //檢索呈現地圖的內容視圖
        setContentView(R.layout.biker_time_map);
        u_checkgps();
        setupViewcomponent();
        DBversion=Integer.parseInt(getString(R.string.SQLite_version));
        //20210118 S--------------------------
        initDB();  //打開SQLite
        //20210118 O--------------------------

        //構造一個PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        // 構造一個FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public static void enableStrictMode(Context context) {//有使用到mysql(or遠端的資料庫)都需要這個方法
        StrictMode.setThreadPolicy(//執行緒違例檢測
//                -------------抓取遠端資料庫設定執行續------------------------------(官方文件)
                new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()//磁碟讀寫檢查
                        .detectDiskWrites()//磁碟讀寫檢查
                        .detectNetwork()//檢查UI執行緒中是否有網路請求操作
                        .penaltyLog() //觸發違規(e.g.)時，顯示對違規資訊對話方塊。               //補充 penaltyDeath(). //當觸發違規條件時，直接Crash掉當前應用程式
                        .build());
        StrictMode.setVmPolicy(//虛擬機器檢測
                new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()//檢查 SQLiteCursor 或者 其他 SQLite物件是否被正確關閉
                        .penaltyLog()//觸發違規(e.g.SQLite物件未正確關閉)時，顯示對違規資訊對話方塊。
                        .build());
    }


    private void setupViewcomponent() {
        plan = (Button) findViewById(R.id.time_map_plan);
//        watch = (TextView) findViewById(R.id.time_map_watch);
        time_map_pathfinding = (TextView) findViewById(R.id.time_map_pathfinding);
        main_map = (ImageView) findViewById(R.id.time_map_main_map);
        img_friend1 = (TextView) findViewById(R.id.map_img_fr01);
        img_friend2 = (TextView) findViewById(R.id.map_img_fr02);
        img_friend3 = (TextView) findViewById(R.id.map_img_fr03);
        mSeekBar = (SeekBar) findViewById(R.id.map_seekBar);

        //啟動監聽事件
        plan.setOnClickListener(this);
//        watch.setOnClickListener(this);
        time_map_pathfinding.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarOn);

        selectBtn = (Button) findViewById(R.id.time_map_show);
        selectBtn.setOnClickListener(this);
        //動畫
        //main_map.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_alpha_in));
        plan.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_trans_in));

        //20210118 S--------------------------
        time_map_check2 = (TextView) findViewById(R.id.time_map_check2);
        time_map_check2.setText("✔確認");
        time_map_check2.setTextColor(getResources().getColor(R.color.white));
        backbtn = (TextView) findViewById(R.id.time_map_watch);
        distanceBtn = (TextView) findViewById(R.id.time_map_show);
//        map_btn = (Button) findViewById(R.id.time_map_back);
        ImageView main_map2 = (ImageView) findViewById(R.id.time_map_main_map2);
        transfer = (Button) findViewById(R.id.time_map_transfer);
        //取得輸出經緯度元件
        output = (TextView) findViewById(R.id.time_map_lblOutput);
        output_history = (TextView) findViewById(R.id.time_map_lblOutput2);
        lat = (TextView) findViewById(R.id.time_map_txtLat_start);
        lon = (TextView) findViewById(R.id.time_map_txtLong_start);
        lat_goal = (TextView) findViewById(R.id.time_map_txtLat_goal);
        lon_goal = (TextView) findViewById(R.id.time_map_txtLong_goal);
        // 取得輸入位址資料元件
        location_start = (AutoCompleteTextView) findViewById(R.id.time_map_start);
        location_goal = (AutoCompleteTextView) findViewById(R.id.time_map_goal);
        // 建立Geocoder物件
        geocoder = new Geocoder(this, Locale.TAIWAN);
        //歷史紀錄清單
//        hList = (ListView) findViewById(R.id.history_list);
//        time_map_history_scroll = (ScrollView) findViewById(R.id.time_map_history_scroll);
//        hListArr = getResources().getStringArray(R.array.biker_map_arr);
//        ArrayAdapter hlist = new ArrayAdapter<String>(this, R.layout.find_list_item, hListArr);
//        hList.setAdapter(hlist);
//        time_map_history_scroll.setVisibility(View.INVISIBLE);
        //AutoCompleteTextView
        autoText = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        // autoText = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
//        autoText.add("后里車站");
//        autoText.add("后里馬場");
//        autoText.add("麗寶樂園");
//        autoText.add("台糖糖廠");
//        autoText.add("台中公園");
//        autoText.add("台中車站");
//        autoText.add("中央公園");
        location_start.setAdapter(autoText);
        location_goal.setAdapter(autoText);
        //動畫
        transfer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_scale_rotate_in));
        //backbtn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_scale_rotate_in));
        //distanceBtn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_scale_rotate_in));
        //啟動監聽事件
        time_map_check2.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        distanceBtn.setOnClickListener(this);
        location_start.setOnClickListener(this);
        location_goal.setOnClickListener(this);
        transfer.setOnClickListener(this);
//        map_btn.setOnClickListener(this);
        //20210118 O--------------------------

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time_map_watch: //切換至碼表頁面
                intent.setClass(Biker_time_map.this, Biker_time_Stopwatch.class);
                setResult(REQUEST_CODE, intent); //REQUEST_CODE 需跟AActivity.class的一樣
                finish();
                startActivity(intent);
                break;
            //20210118 S--------------------------
            case R.id.time_map_check2: //切換至google map導航
                try {
                    if (output.getText().toString() != "經緯度無法辨識\n請更改輸入地址名稱" && output.getText().toString() != "請先輸入\n並轉換地址為經緯度") {
                        double s_lat = Double.parseDouble(lat.getText().toString());
                        double s_lon = Double.parseDouble(lon.getText().toString());
                        double s_lat_goal = Double.parseDouble(lat_goal.getText().toString());
                        double s_lon_goal = Double.parseDouble(lon_goal.getText().toString());
                        String link_address = "http://maps.google.com/maps?f=d&saddr=" + s_lat + ", " + s_lon + "&daddr=" + s_lat_goal + ", " + s_lon_goal + "&hl=tw";
//                    Uri uri = Uri.parse("http://maps.google.com/maps?f=d&saddr=24.17387349666727, 120.60934934548084&daddr=24.17624579441691, 120.6408494529127&hl=tw");
                        Uri uri = Uri.parse(link_address);
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(it);
                    } else {
                        output.setText("請先輸入\n並轉換地址為經緯度");
                        Toast.makeText(getApplicationContext(), "請先輸入並轉換地址為經緯度", Toast.LENGTH_LONG).show(); //彈出視窗
                    }
                } catch (Exception ex) {
                    output.setText("請先輸入\n並轉換地址為經緯度");
                    Toast.makeText(getApplicationContext(), "請先輸入並轉換地址為經緯度", Toast.LENGTH_LONG).show(); //彈出視窗
                }
                break;
//            case R.id.time_map_watch2: //切換至碼表頁面
//                intent.setClass(Biker_time_map.this, Biker_time_Stopwatch.class);
//                startActivity(intent);
//                break;
//            case R.id.time_map_show: //切換距離/預計時間
//                if (distanceBtn.isSelected()) {
//                    distanceBtn.setSelected(false);
//                    distanceBtn.setText("總距離");
//                } else {
//                    distanceBtn.setSelected(true);
//                    distanceBtn.setText("預計時間");
//                }
//                break;
            case R.id.time_map_start: //出發點
                String s = location_start.getText().toString();
                autoText.add(s);
                break;
            case R.id.time_map_goal: //目的地
                String s2 = location_goal.getText().toString();
                autoText.add(s2);
                break;
            case R.id.time_map_transfer: // 將地址轉換成經緯度座標
                addrToLatLon();   // 將地址轉換成經緯度座標
                break;
//            case R.id.time_map_back: //返回導航頁面
//                intent.setClass(Biker_time_map_plan.this, Biker_time_map.class);
//                startActivity(intent);
//                break;

            //20210118 O--------------------------

        }

        //切換顯示隱藏使用者所在位置
        if (v == selectBtn) {
            if (selectBtn.isSelected()) {
                selectBtn.setSelected(false);
                selectBtn.setText(getString(R.string.biker_map_show));
//                img_friend1.setAlpha((float) 0.0);
//                img_friend2.setAlpha((float) 0.0);
//                img_friend3.setAlpha((float) 0.0);
                img_friend1.setVisibility(View.INVISIBLE);
                img_friend2.setVisibility(View.INVISIBLE);
                img_friend3.setVisibility(View.INVISIBLE);
                mSeekBar.setVisibility(View.INVISIBLE);
            } else {
                selectBtn.setSelected(true);
                selectBtn.setText(getString(R.string.biker_map_hide));
//                img_friend1.setAlpha((float) 1.0);
//                img_friend2.setAlpha((float) 1.0);
//                img_friend3.setAlpha((float) 1.0);
                img_friend1.setVisibility(View.VISIBLE);
                img_friend2.setVisibility(View.VISIBLE);
                img_friend3.setVisibility(View.VISIBLE);
                mSeekBar.setVisibility(View.VISIBLE);

            }
        }

    }

    //20210130新增s
    //新增資料至MySQL
    private void mysql_insert() {//新增
        //取得目前使用者ID
        SharedPreferences u_id = getSharedPreferences("USER_ID", 0);
        t_u_id = u_id.getString("USER_ID", "");

        //取得目前使用者所在地經緯度
        t_lat = lastKnownLocation.getLatitude();
        t_lon = lastKnownLocation.getLongitude();

        ArrayList<String> E200_ValuePairs = new ArrayList<>();//存取上述三個資料為陣列
        E200_ValuePairs.add(t_u_id);
        E200_ValuePairs.add(String.valueOf(t_lat)); //double轉string並存進陣列
        E200_ValuePairs.add(String.valueOf(t_lon));
        try {//延遲Thread 睡眠0.5秒
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------//實際執行新增資料至mysql
        String result = DBConnector22.executeInsert(E200_ValuePairs);
//-----------------------------------------------
    }

    //新增資料至MySQL(搜尋歷史紀錄)
    private void mysql_insert_history() {//新增
//        dbHper.insertRec(addressName, addressName_goal, longitude, latitude, longitude_goal, latitude_goal);   //將資料傳入資料庫20210115
        //取得目前使用者ID
        SharedPreferences u_id = getSharedPreferences("USER_ID", 0);
        t_u_id = u_id.getString("USER_ID", "");

        ArrayList<String> E100_ValuePairs = new ArrayList<>();
        E100_ValuePairs.add(t_u_id);
        E100_ValuePairs.add(addressName);
        E100_ValuePairs.add(addressName_goal);
        E100_ValuePairs.add(String.valueOf(longitude));
        E100_ValuePairs.add(String.valueOf(latitude));
        E100_ValuePairs.add(String.valueOf(longitude_goal));
        E100_ValuePairs.add(String.valueOf(latitude_goal));


        try {//延遲Thread 睡眠0.5秒
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------//實際執行新增資料至mysql
        String result_history = DBConnector22.executeInsert_history(E100_ValuePairs);
//-----------------------------------------------
    }

    //檢查是否開啟GPS
    private void u_checkgps() {
        // 取得系統服務的LocationManager物件
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 檢查是否有啟用GPS
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 顯示對話方塊啟用GPS
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("定位管理")
                    .setCancelable(false)
                    .setIcon(R.drawable.time_stopwatch_gpsicon)
                    .setMessage("GPS目前狀態是尚未啟用.\n"
                            + "請問你是否現在就設定啟用GPS?")
                    .setPositiveButton("啟用", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //前往手機設定
                            // 使用Intent物件啟動設定程式來更改GPS設定
                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("不啟用", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "GPS未開啟", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }
    //20210130新增o

    //20210118 S--------------------------
// 將地址轉換成經緯度座標
    public void addrToLatLon() {
        output_history.setText("");
        addressName = location_start.getText().toString();
        addressName_goal = location_goal.getText().toString();
        try {
            // 取得經緯度座標清單的List物件
            List<Address> listGPSAddress = geocoder.getFromLocationName(addressName, 1);
            List<Address> listGPSAddress_goal = geocoder.getFromLocationName(addressName_goal, 1);
            // 有找到經緯度座標
            if (listGPSAddress != null) {
                latitude = listGPSAddress.get(0).getLatitude();
                longitude = listGPSAddress.get(0).getLongitude();
                latitude_goal = listGPSAddress_goal.get(0).getLatitude();
                longitude_goal = listGPSAddress_goal.get(0).getLongitude();
                output.setText("出發點\n緯度: " + latitude +
                        "\n經度: " + longitude + "\n目的地\n緯度: " + latitude_goal +
                        "\n經度: " + longitude_goal);
                lat.setText(String.valueOf(latitude)); // 指定值
                lon.setText(String.valueOf(longitude));
                lat_goal.setText(String.valueOf(latitude_goal)); // 指定值
                lon_goal.setText(String.valueOf(longitude_goal));
                //u_dbadd();   //將資料傳入資料庫 20210115
                dbHper.insertRec(addressName, addressName_goal, longitude, latitude, longitude_goal, latitude_goal);   //將資料傳入資料庫20210115
                mysql_insert_history(); //最新搜尋歷史紀錄存進MySQL
            }
        } catch (Exception ex) {
            output.setText("經緯度無法辨識\n請更改輸入地址名稱");
            Toast.makeText(getApplicationContext(), "經緯度無法辨識\n請更改輸入地址名稱", Toast.LENGTH_LONG).show(); //彈出視窗
        }
    }

    // 啟動Google地圖
    public void startGoogleMap() {
        // 取得經緯度座標
        float latitude = Float.parseFloat(lat.getText().toString());
        float longitude = Float.parseFloat(lon.getText().toString());
        // 建立URI字串
        String uri = String.format("geo:%f,%f?z=18", latitude, longitude);
        // 建立Intent物件
        Intent geoMap = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(geoMap);  // 啟動活動
    }

    //---------------------------------
    //輸入資料庫 20210115關
//    private void u_dbadd() {
//
//        ContentValues newRow = new ContentValues(); //SQLite 一筆資料
//        //newRow.put("E101", userID);  //UserID
//        //綠字為欄位名稱 前key 後value
//        newRow.put("E102", location_start.getText().toString());  //出發點
//        newRow.put("E103", location_goal.getText().toString());  //目的地
//        newRow.put("E104", longitude);  //出發點經度
//        newRow.put("E105", latitude);  //出發點緯度
//        newRow.put("E106", longitude_goal);  //終點經度
//        newRow.put("E107", latitude_goal);  //終點緯度
//
//        mBikermapDb.insert(DB_TABLE, null, newRow);  //呼叫SQLite
//    }
    //顯示資料庫資料
    private void u_dblist() {
        output_history.setText("");
        //Cursor cur_list = mBikermapDb.query(
        Cursor cur_list = dbHper.getDatabase(getApplicationContext()).query(
                true,
                DB_TABLE,
                new String[]{"id", "E101", "E102", "E103", "E104", "E105", "E106", "E107"},
                null,
                null,
                null,
                null,
                null,
                null);
        // 依據 SQLite 的查詢 (select) 語法
// 在 SQLiteDatabase 類別中定義了多種可以接收不同參數的查詢方法
// 這些方法大致可以分為 query 開頭與 rawQuery 開頭兩類。
// query 開頭的方法可以接受多個參數
// 每一個參數可以對應到 SQLite select 語法中的某些值
// 消除所有重複的記錄，並只獲取唯一一次記錄(distinct) true false
// 表格名稱 (table)
// 欄位名稱 (columns)
// 查詢條件 (selection)
// 查詢條件的值 (selectionArgs)
// 欄位群組 (groupBy)
// 排序方式 (orderBy)
// 回傳資料的筆數限制 (limit) 等。
// (cancellationSignal)
        if (cur_list == null) return;
        ;
        if (cur_list.getCount() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.biker_map_nodata), Toast.LENGTH_LONG).show();
        } else {
            cur_list.moveToFirst(); //將游標移至第一行
            output_history.setText(cur_list.getString(0) + " ,"
                    + cur_list.getString(2) + " ,"
                    + cur_list.getString(3));
            while (cur_list.moveToNext()) {
                output_history.append("\n" + cur_list.getString(0) + " ,"
                        + cur_list.getString(2) + " ,"
                        + cur_list.getString(3));
            }
            cur_list.close();
        }
    }
    //---------------------------------
    //20210118 O--------------------------

    public void onValueAnimatorTest(final View view) {
        // 獲取屏幕寬度
        final int maxWidth = getWindowManager().getDefaultDisplay().getWidth();
        ValueAnimator valueAnimator = (ValueAnimator) AnimatorInflater.loadAnimator(this, R.animator.value_animator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private IntEvaluator mEvaluator = new IntEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                // 當前動畫值，即為當前寬度比例值
                int currentValue = (Integer) animator.getAnimatedValue();
                // 根據比例更改目標view的寬度
                view.getLayoutParams().width = maxWidth * currentValue / 200;
                view.requestLayout();
            }
        });
        valueAnimator.start();
    }

    //調整使用者圖標透明度
    private SeekBar.OnSeekBarChangeListener mSeekBarOn = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            img_friend1.getBackground().setAlpha(progress * 255 / 100);
            img_friend1.setAlpha((float) (progress / 100.0));
            img_friend2.setAlpha((float) (progress / 100.0));
            img_friend3.setAlpha((float) (progress / 100.0));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };


    //    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(24.172127, 120.610313);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));     // 放大地圖到 16 倍大
//    }


    // 活動暫停時保存地圖的狀態
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }


//    /**
//     * Sets up the options menu.
//     * @param menu The options menu.
//     * @return Boolean.
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.current_place_menu, menu);
//        return true;
//    }
//
//    /**
//     * Handles a click on the menu option to get a place.
//     * @param item The menu item to handle.
//     * @return Boolean.
//     */
//    // [START maps_current_place_on_options_item_selected]
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.option_get_place) {
//            showCurrentPlace();
//        }
//        return true;
//    }
    // [END maps_current_place_on_options_item_selected]

    //在地圖可用時進行操作 準備使用地圖時會觸發此回調
    //onMapReady 設定預設的值 maps_current_place_on_map_ready
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // [START_EXCLUDE]
        // [START map_current_place_set_info_window_adapter]
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet2);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });
        // [END map_current_place_set_info_window_adapter]

        // Prompt the user for permission.
        getLocationPermission();
        // [END_EXCLUDE]

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }
    // [END maps_current_place_on_map_ready]

    //獲取設備的當前位置，並定位地圖的攝像頭
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                mysql_insert(); //20210130新增 若有取得使用者所在地就存進MySQL
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    // [END maps_current_place_get_device_location]

    //獲取位置權限
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    //處理請求位置權限的結果 maps_current_place_on_request_permissions_result]
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    //顯示地圖上的當前位置-如果用戶已授予位置權限  [START maps_current_place_show_current_place]
    private void showCurrentPlace() {
        if (map == null) {
            return;
        }

        if (locationPermissionGranted) {
            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                    Place.Field.LAT_LNG);

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.newInstance(placeFields);

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final Task<FindCurrentPlaceResponse> placeResult =
                    placesClient.findCurrentPlace(request);
            placeResult.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FindCurrentPlaceResponse likelyPlaces = task.getResult();

                        // Set the count, handling cases where less than 5 entries are returned.
                        int count;
                        if (likelyPlaces.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                            count = likelyPlaces.getPlaceLikelihoods().size();
                        } else {
                            count = M_MAX_ENTRIES;
                        }

                        int i = 0;
                        likelyPlaceNames = new String[count];
                        likelyPlaceAddresses = new String[count];
                        likelyPlaceAttributions = new List[count];
                        likelyPlaceLatLngs = new LatLng[count];

                        for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                            // Build a list of likely places to show the user.
                            likelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                            likelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                            likelyPlaceAttributions[i] = placeLikelihood.getPlace()
                                    .getAttributions();
                            likelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                            i++;
                            if (i > (count - 1)) {
                                break;
                            }
                        }

                        // Show a dialog offering the user the list of likely places, and add a
                        // marker at the selected place.
                        Biker_time_map.this.openPlacesDialog();
                    } else {
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                }
            });

            //-----------------------------------------------20210118 add
            map.addMarker(new MarkerOptions()
                    .title("User甲位置")
                    .position(userLocationA)
                    .snippet("User甲位置"));
            //-----------------------------------------------20210118 add


        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            map.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(defaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }


    //[START maps_current_place_open_places_dialog]
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = likelyPlaceLatLngs[which];
                String markerSnippet = likelyPlaceAddresses[which];
                if (likelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + likelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                map.addMarker(new MarkerOptions()
                        .title(likelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(likelyPlaceNames, listener)
                .show();
    }
    // [END maps_current_place_open_places_dialog]

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    // [START maps_current_place_update_location_ui]
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    // [END maps_current_place_update_location_ui]

    //20210125 S--------------------------
    private void showloc() {
        // 將所有景點位置顯示
        for (int i = 0; i < locations.length; i++) {
            String[] sLocation = locations[i][1].split(",");
            dLat = Double.parseDouble(sLocation[0]); // 南北緯
            dLon = Double.parseDouble(sLocation[1]); // 東西經
            String vtitle = locations[i][0];
            resID = 0;
            resID1 = 0;
            // --- 設定所選位置之當地圖片 ---
            // raw 目錄下 存放 q01.png ~ q06.png  t01.png ~t07.png 超出範圍 用 t99.png & q99.png
            if (i >= 0 && i < 8) {
                String idName = "t" + String.format("%02d", i);
                String imgName = "q" + String.format("%02d", i);
                resID = getResources().getIdentifier(idName, "drawable", getPackageName());
                resID1 = getResources().getIdentifier(imgName, "drawable", getPackageName());
                image_des = BitmapDescriptorFactory.fromResource(resID);// 使用照片
            } else {
                resID = getResources().getIdentifier("t99", "drawable", getPackageName());
                resID1 = getResources().getIdentifier("q99", "drawable", getPackageName());
            }

            // --- 設定所選位置之當地圖片 ---//
            switch (icosel) {
                case 0:
                    image_des = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN); // 使用系統水滴
                    break;
                case 1:
                    // 運用巨集
                    image_des = BitmapDescriptorFactory.fromResource(resID);// 使用照片
                    break;
            }
            //-----------------------------------------
            vtitle = vtitle + "#" + resID1; //存放圖片號碼
            //-----------------------------------------
            VGPS = new LatLng(dLat, dLon);// 更新成欲顯示的地圖座標
            // --- 根據所選位置項目顯示地圖/標示文字與圖片 ---//

            map.addMarker(new MarkerOptions()
                            .position(VGPS)
                            .alpha(0.9f)
                            .title(i + "." + vtitle)
                            .snippet("緯度:" + String.valueOf(dLat) + "\n經度:" + String.valueOf(dLon))
                            .infoWindowAnchor(0.5f, 0.95f)
                            .icon(image_des)
                    // .draggable(true) //設定maker 可移動
            );
            //--------------------使用自定義式窗-------------------------------------------------------
            map.setInfoWindowAdapter(new CustomInfoWindowAdapter());//外圓內方
        }
    }

    //20210125 O--------------------------

    //20210118 S--------------------------
    private void initDB() {

        if (dbHper == null) {
            dbHper = new FriendDbHelper22(this, DB_FILE, null, DBversion);
        }
        //-----------210128新增
        dbmysql();
        //-----------
        recSet = dbHper.getRecSet(); //all data 讀取所有資料
    }

    //0129新增S------------
    private void dbmysql() {
        sqlctl = "SELECT * FROM member ORDER BY id ASC";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            String result = DBConnector22.executeQuery(nameValuePairs);
            /**************************************************************************
             * SQL 結果有多筆資料時使用JSONArray
             * 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            JSONArray jsonArray = new JSONArray(result);
            // -------------------------------------------------------
            if (jsonArray.length() > 0) { // MySQL 連結成功有資料
//--------------------------------------------------------
                int rowsAffected = dbHper.clearRec();                 // 匯入前,刪除所有SQLite資料
//--------------------------------------------------------
                // 處理JASON 傳回來的每筆資料
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    ContentValues newRow = new ContentValues();
                    // --(1) 自動取的欄位 --取出 jsonObject 每個欄位("key","value")-----------------------
                    Iterator itt = jsonData.keys();
                    while (itt.hasNext()) {
                        String key = itt.next().toString();
                        String value = jsonData.getString(key); // 取出欄位的值
                        if (value == null) {
                            continue;
                        } else if ("".equals(value.trim())) {
                            continue;
                        } else {
                            jsonData.put(key, value.trim());
                        }
                        // ------------------------------------------------------------------
                        newRow.put(key, value.toString()); // 動態找出有幾個欄位
                        // -------------------------------------------------------------------
                    }
                    // ---(2) 使用固定已知欄位---------------------------
                    // newRow.put("id", jsonData.getString("id").toString());
                    // newRow.put("name",
                    // jsonData.getString("name").toString());
                    // newRow.put("grp", jsonData.getString("grp").toString());
                    // newRow.put("address", jsonData.getString("address")
                    // -------------------加入SQLite---------------------------------------
                    long rowID = dbHper.insertRec_m(newRow);

                }
                //  Toast.makeText(getApplicationContext(), "共匯入 " + Integer.toString(jsonArray.length()) + " 筆資料", Toast.LENGTH_SHORT).show();
                // ---------------------------
            } else {
                //   Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
            recSet = dbHper.getRecSet();  //重新載入SQLite
            // --------------------------------------------------------
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }


//        sqlctl = "SELECT * FROM member ORDER BY id ASC";//sql
//        ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
//        try {
//            String result = DBConnector.executeQuery(nameValuePairs);
//
//            /**************************************************************************
//             * SQL 結果有多筆資料時使用JSONArray
//             * 只有一筆資料時直接建立JSONObject物件 JSONObject
//             * jsonData = new JSONObject(result);
//             **************************************************************************/
//            JSONArray jsonArray = new JSONArray(result);//抓到的所有資料
//            // -------------------------------------------------------
//            if (jsonArray.length() > 0) { // MySQL 連結成功有資料
//
//                int rowsAffected = dbHper.clearRec();                 // 匯入前,刪除所有SQLite資料
//
//                // 處理JASON 傳回來的每筆資料
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonData = jsonArray.getJSONObject(i);
//                    ContentValues newRow = new ContentValues();
//                    // --(1) 自動取的欄位 --取出 jsonObject 每個欄位("key","value")-----------------------
//                    Iterator itt = jsonData.keys();
//                    while (itt.hasNext()) {
//                        String key = itt.next().toString();
//                        String value = jsonData.getString(key); // 取出欄位的值
//                        if (value == null) {
//                            continue;
//                        } else if ("".equals(value.trim())) {
//                            continue;
//                        } else {
//                            jsonData.put(key, value.trim());
//                        }
//                        // ------------------------------------------------------------------
//                        newRow.put(key, value.toString()); // 動態找出有幾個欄位
//                        // -------------------------------------------------------------------
//                    }
//                    // ---(2) 使用固定已知欄位---------------------------
//                    // newRow.put("id", jsonData.getString("id").toString());
//                    // newRow.put("name",
//                    // jsonData.getString("name").toString());
//                    // newRow.put("grp", jsonData.getString("grp").toString());
//                    // newRow.put("address", jsonData.getString("address")
//                    // -------------------加入SQLite---------------------------------------
//                    long rowID = dbHper.insertRec_m(newRow);
//                    //下面是老爹版本，偶有顯示問題
//                    //Toast.makeText(getApplicationContext(), "共匯入 " + Integer.toString(jsonArray.length() ) + " 筆資料", Toast.LENGTH_SHORT).show();
//                }
//                //下面是修正版本，應該沒問題(正確顯示抓到的資料數)
//                Toast.makeText(getApplicationContext(), "共匯入 " + dbHper.RecCount()  + " 筆資料", Toast.LENGTH_SHORT).show();
//
//                // ---------------------------
//            } else {
//                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
//            }
//            recSet = dbHper.getRecSet();  //重新載入SQLite
//            //自己加的(非老爹版本)
//            showRec(0);
//            //
//            u_setspinner(); //重構
//            // --------------------------------------------------------
//        } catch (Exception e) {
//            Log.d(TAG, e.toString());
//        }
    }

    //0129新增O------------
    @Override
    protected void onPause() {
        super.onPause();
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dbHper == null) {
            dbHper = new FriendDbHelper22(this, DB_FILE, null, DBversion);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }
    }


    //20210118 O--------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    ////----------------------------------以下是menu用的方法----------------
    private void Signin_menu() {//登入時顯示的menu
        menu.setGroupVisible(R.id.g01, false);
        menu.setGroupVisible(R.id.g02, true);
    }

    private void Signout_menu() {//登出時顯示的menu
        menu.setGroupVisible(R.id.g01, true);
        menu.setGroupVisible(R.id.g02, false);
    }

    @Override
    public void onBackPressed() {
//super.onBackPressed();
    }


    @Override//顯示出menu　item選項的icon
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.biker_menu, menu);
        this.menu = menu;
        menu.setGroupVisible(R.id.g01, false);
        menu.setGroupVisible(R.id.g02, false);
        //menu.add(Menu.NONE, MyActivity.MENU_ID, 0, "Your Title").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//        menu.findItem(R.id.Item07).setTitle("碼表");

        //20210118 S--------------------------
//        menu.add(0, 0, 0, "導航").setIcon(android.R.drawable.ic_media_play);
        menu.add(0, 1, 0, "碼表").setIcon(android.R.drawable.ic_media_play);
        menu.add(0, 2, 0, "開啟導航").setIcon(android.R.drawable.ic_media_play);
        menu.add(0, 3, 0, "歷史紀錄").setIcon(android.R.drawable.ic_media_play);
        menu.add(0, 4, 0, "轉換經緯度").setIcon(android.R.drawable.ic_media_play);
        menu.add(0, 5, 0, "清除歷史紀錄").setIcon(android.R.drawable.ic_media_play);
        menu.add(0, 6, 0, "關閉歷史紀錄").setIcon(android.R.drawable.ic_media_play);
        //20210118 O--------------------------
        //20210118 關
//        menu.add(0, 1, 0, "碼表").setIcon(android.R.drawable.ic_media_play);
//        menu.add(0, 2, 0, "規劃路線").setIcon(android.R.drawable.ic_media_play);
        menu.add(0, 7, 0, "切換顯示隱藏").setIcon(android.R.drawable.ic_media_play);


        return true;  //返回值為“true”,表示菜單可見，即顯示菜單
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            //20210118 關
//            case 1: //切換至碼表頁面
//                watch.callOnClick();
//                break;
//            case 2: //切換至規畫路徑頁面
//                time_map_pathfinding.callOnClick();
//                break;
//            case 3: //切換顯示隱藏使用者
//                selectBtn.callOnClick();
//                break;
            //20210118 S--------------------------
            case 1: //切換至碼表頁面
                backbtn.callOnClick();
                break;
            case 2: //切換至google map
                time_map_check2.callOnClick();
                break;
            case 3: //歷史紀錄
                u_dblist();
                break;
            case 4: //轉換經緯度
                transfer.callOnClick();
                break;
            case 5: //清除歷史紀錄
//                output_history.setText("");
//                mBikermapDb.delete(DB_TABLE, null, null);
//                Toast.makeText(getApplicationContext(), getString(R.string.biker_map_dataClean), Toast.LENGTH_LONG).show();
                // 清空
                //一般dialog-----------------------------
//                MyAlertDialog aldDial = new MyAlertDialog(Biker_time_map_plan.this);
//                aldDial.setTitle(getString(R.string.m_altitle_map));
//                aldDial.setMessage(getString(R.string.m_message_map));
//                aldDial.setIcon(android.R.drawable.ic_dialog_info);
//                aldDial.setCancelable(false); //返回鍵關閉
//                aldDial.setButton(BUTTON_POSITIVE, "確定清空", aldBtListener);
//                aldDial.setButton(BUTTON_NEGATIVE, "取消清空", aldBtListener);
//                aldDial.show();
                //一般dialog-----------------------------
                //新版dialog-----------------------------
                mLoginDlg = new Dialog(Biker_time_map.this);
                //mLoginDlg.setTitle(getString(R.string.biker_map_dialog_title));
                mLoginDlg.setCancelable(false);//一定要給我選，true的話可以按旁邊躲掉
//R.layout.profile_dialog當作Dialog的畫面
                mLoginDlg.setContentView(R.layout.alert_dialog);
                Button mLoginDlg_btnOK = (Button) mLoginDlg.findViewById(R.id.train_btnOK);
                Button mLoginDlg_btnCancel = (Button) mLoginDlg.findViewById(R.id.train_btnCancel);
                TextView mLoginDlg_warning = (TextView) mLoginDlg.findViewById(R.id.tarin_waring);
                mLoginDlg_warning.setText(getString(R.string.m_message_map));//更改dialog的字
                mLoginDlg_btnCancel.setOnClickListener(mDlgListener);
                mLoginDlg_btnOK.setOnClickListener(mDlgListener);
                mLoginDlg.show();
                //新版dialog-----------------------------
                break;
            case 6: //關閉歷史紀錄
                output_history.setText("");
                break;

            case 7: //切換顯示隱藏使用者
                selectBtn.callOnClick();
                break;

//            case 0: //切換至導航頁面
//                intent.setClass(Biker_time_map.this, Biker_time_map.class);
//                startActivity(intent);
//                break;
            //20210118 O--------------------------
            case R.id.action_settings:  //返回
                Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //20210118 S--------------------------
//新版dialog-----------------------------
    private View.OnClickListener mDlgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.train_btnOK:
                    output_history.setText("");
                    //dbHper.deleteRec(DB_TABLE, null, null);
                    dbHper.deleteRec();
                    Toast.makeText(getApplicationContext(), getString(R.string.biker_map_dataClean), Toast.LENGTH_LONG).show();
                    mLoginDlg.cancel();
                    break;
                case R.id.train_btnCancel:
                    Toast.makeText(getApplicationContext(), getString(R.string.biker_map_dataClean_cancel), Toast.LENGTH_LONG).show();
                    mLoginDlg.cancel();
                    break;
            }
        }
    };
    //新版dialog-----------------------------
//    //一般dialog-----------------------------
//    private DialogInterface.OnClickListener aldBtListener = new DialogInterface.OnClickListener() {
//
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            switch (which) {
//                case BUTTON_POSITIVE:
//                    output_history.setText("");
//                    mBikermapDb.delete(DB_TABLE, null, null);
//                    Toast.makeText(getApplicationContext(), getString(R.string.biker_map_dataClean), Toast.LENGTH_LONG).show();
//                    break;
//                case BUTTON_NEGATIVE:
//                    Toast.makeText(getApplicationContext(), getString(R.string.biker_map_dataClean_cancel), Toast.LENGTH_LONG).show();
//                    break;
//            }
//        }
//    };
//    //一般dialog-----------------------------

    //20210118 O--------------------------

    //20210125S---------------------------
    //========自訂義infowindow class============//
    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker marker) {
            // 依指定layout檔，建立地標訊息視窗View物件
            // --------------------------------------------------------------------------------------
            // 單一框
            // View infoWindow=
            // getLayoutInflater().inflate(R.layout.custom_info_window,
            // null);
            // 有指示的外框
            View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_content, null);
            infoWindow.setAlpha(0.5f);
            // ----------------------------------------------
            // 顯示地標title
            TextView title = ((TextView) infoWindow.findViewById(R.id.title));
            String[] ss = marker.getTitle().split("#");
            title.setText(ss[0]);
            // 顯示地標snippet
            TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
            snippet.setText(marker.getSnippet());
            // 顯示圖片
            ImageView imageview = ((ImageView) infoWindow.findViewById(R.id.content_ico));
            imageview.setImageResource(Integer.parseInt(ss[1]));

            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            Toast.makeText(getApplicationContext(), "getInfoContents", Toast.LENGTH_LONG).show();
            return null;
        }
    }

//// -------------subclass end------------------------------
// ===================main class end ===========================

    //20210125O---------------------------

}
