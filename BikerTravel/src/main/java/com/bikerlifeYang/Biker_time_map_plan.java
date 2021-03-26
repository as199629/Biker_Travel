package com.bikerlifeYang;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.internal.maps.zzt;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Locale;

//20210118 O--------------------------------------------
import android.util.Base64;

import com.google.android.gms.common.ConnectionResult;

import static android.graphics.BitmapFactory.decodeStream;
import static java.lang.Thread.sleep;
//20210215adds
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Shader;
//20210215addo
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Biker_time_map_plan extends AppCompatActivity
        implements OnMapReadyCallback, View.OnClickListener, RoutingListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnInfoWindowClickListener {
    //google map使用FragmentActivity、OnMapReadyCallback  OnMarkerClickListener,
    //-----------------所需要申請的權限數組---------------
    private static final String[] permissionsArray = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION};
    private List<String> permissionsList = new ArrayList<String>();
    //申請權限後的返回碼
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
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
    //    private String[][] locations;
    private static String[][] locations = {
            {"我的位置", "24.172127,120.610313"},
            {"中區職訓", "24.172127,120.610313"},
            {"東海大學路思義教堂", "24.179051,120.600610"},
            {"台中公園湖心亭", "24.144671,120.683981"},
            {"秋紅谷", "24.1674900,120.6398902"},
            {"台中火車站", "24.136829,120.685011"},
            {"國立科學博物館", "24.1579361,120.6659828"}};
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

    private String addressName = "目前所在地";
    private String addressName_goal = "";
    private Uri User_IMAGE;
    private Uri noiconimg;
    private CircleImgView img;
    private String t_name = "";
    private String t_id = "";
    private String t_imgUrl = "";
    private String t_email = "";
    private Bitmap bmp;
    private Paint paint = new Paint();
    private Bitmap newBitmapSize;

    //20210211adds
    //polyline object
    private List<Polyline> polylines = null;
    protected LatLng startFindroutes = null;
    protected LatLng endFindroutes = null;
    private static String[] mapType = {"街道圖", "衛星圖", "地形圖", "混合圖", "開啟路況", "關閉路況"};
    private Spinner mSpnMapType;
    private Bitmap circleBitmap;
    //20210211addo
    //20210214adds
    String[] arrayFriendLocation;
    String[] arrayFriendLocation2;
    //20210215adds
    private static final int photoMargin = 30;
    private static final int margin = 20;
    private static final int triangleMargin = 10;
    //20210215addo
    //20210216adds
    LatLng markerStart, markerEnd = null;
    //20210216addo
    // 20210225adds Control按鈕設定
    private LocationManager locationManager;  // 取得系統服務的LocationManager物件
    private CheckBox checkBox;
    private ScrollView controlScroll;
    private HashMap<String, Object> hashMapMarker = new HashMap<String, Object>();
    private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
    private int location_no = 0;
    private float Anchor_x = 0.5f;
    private float Anchor_y = 0.5f;
    private String provider = null; // 提供資料
    long minTime = 5000;// ms
    float minDist = 5.0f;// meter
    private Marker markerMe;
    private int tracesel = 0;
    private TextView tmsg;
    private String where = "";
    private Marker markerI;
    private double lng_I;
    private double lat_I;
    private MenuItem m_traceon;
    private MenuItem m_traceoff;
    private MenuItem m_navon;
    private MenuItem m_navoff;
    private MenuItem m_historyon;
    private MenuItem m_historyoff;
    private ArrayList<HashMap<String, Object>> arrayListgpx = new ArrayList<HashMap<String, Object>>();
    private ArrayList<LatLng> mytraceGpx;// 追蹤我的位置
    Marker[] friendMarker = new Marker[100]; //顯示團員位置陣列
    private int friendLocation=1;
    private MenuItem m_memberlist_no;
    private MenuItem m_memberlist;
    Handler handler=new Handler();
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_time_map);

        //從已保存的實例狀態中檢索位置和攝像機位置
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        //檢索呈現地圖的內容視圖

        checkRequiredPermission(this);     //  檢查SDK版本, 確認是否獲得權限.
        u_checkgps();
        setupViewcomponent();
        DBversion = Integer.parseInt(getString(R.string.SQLite_version));
        //20210118 S--------------------------
        initDB();  //打開SQLite
        //20210118 O--------------------------

        //構造一個PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
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
        mSpnMapType = (Spinner) this.findViewById(R.id.spnMapType); //20210211adds 新增
        Show_MapType(); //設定地圖顯示圖層
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
        tmsg = (TextView) findViewById(R.id.msg);
        lat = (TextView) findViewById(R.id.time_map_txtLat_start);
        lon = (TextView) findViewById(R.id.time_map_txtLong_start);
        lat_goal = (TextView) findViewById(R.id.time_map_txtLat_goal);
        lon_goal = (TextView) findViewById(R.id.time_map_txtLong_goal);
        // 取得輸入位址資料元件
        location_start = (AutoCompleteTextView) findViewById(R.id.time_map_start); //fromEdit
        location_goal = (AutoCompleteTextView) findViewById(R.id.time_map_goal); //toEdit
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


        // 20210225adds Control按鈕設定
        checkBox = (CheckBox) this.findViewById(R.id.checkcontrol);
        controlScroll = (ScrollView) this.findViewById(R.id.Scroll01);
        checkBox.setOnCheckedChangeListener(chklistener);
        controlScroll.setVisibility(View.INVISIBLE);
        // 20210225addo Control按鈕設定
//        handler.postDelayed(go_map,3000);


    }

    private Runnable go_map =new Runnable() {
        @Override
        public void run() {
            try {
                Bundle bundle = Biker_time_map_plan.this.getIntent().getExtras();
                String aaa=bundle.getString("home_Add");
                location_goal.setText(bundle.getString("home_Add"));

                transfer.callOnClick();
            }catch (Exception e)
            {

            }

        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.time_map_watch: //切換至碼表頁面
                intent.setClass(Biker_time_map_plan.this, Biker_time_Stopwatch.class);
                setResult(REQUEST_CODE, intent); //REQUEST_CODE 需跟AActivity.class的一樣
                finish();
                startActivity(intent);
                break;
            //20210118 S--------------------------
            case R.id.time_map_check2: //切換至google map導航 確認
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

//                asyncGetDirection(); //20210201add_s
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
    //20210201add_s
//    private void asyncGetDirection() {  //GoogleMap
//        if(map==null){
//            return;
//        }
//        map.clear();
//        final Dialog dialog = ProgressDialog.show(this, "計算路徑", "計算中");
//        new DirectionApiHelper(this).getDirection(fromEdit.getEditableText().toString(), toEdit.getEditableText().toString(), new OnEasyApiCallbackListener() {
//            @Override
//            public void onDone(EasyResponseObject response) {
//                dialog.dismiss();
//                try {
//                    Direction direction = new Direction();
//                    EasyResponseObjectParser.startParsing(response.getBody(), direction);
//                    drawPath(direction);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//    //將解析完的資料在 Google Map 上畫出結果
//    private void drawPath(Direction direction) {
//        if (direction.routes.size() == 0 || direction.routes.get(0).legs.size() == 0) {
//            return;
//        }
//        Direction.Leg leg = direction.routes.get(0).legs.get(0);
//
//        //polylineOptions.add 可以加入繪製的座標點；polylineOptions.color 用來設定路線的顏色；polylineOptions.width 用來設定路線的寬度
//        PolylineOptions polylineOptions = new PolylineOptions();
//        for (int i = 0; i < leg.steps.size(); i++) {
//            Direction.Step step = leg.steps.get(i);
//            polylineOptions.add(new LatLng(step.start_location.lat, step.start_location.lng));
//
////            起終點  透過第一個 step 以及最後一個 step，可以知道起始位置以及最後的座標位置，然後在這個座標位置上面用 MarkerOptions 設定 Marker 的座標以及一些顯示的參數，在呼叫 googleMap.addMarker() 將 MarkerOptions 加進 Google  Map 中
//            if (i == 0) {
//                LatLng latLng = new LatLng(step.start_location.lat, step.start_location.lng);
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//                markerOptions.title("起點");
//                map.addMarker(markerOptions);
//            }
//            if (i == leg.steps.size() - 1) {
//                LatLng latLng = new LatLng(step.end_location.lat, step.end_location.lng);
//                polylineOptions.add(latLng);
//
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//                markerOptions.title("終點");
//                map.addMarker(markerOptions);
//            }
//        }
//        polylineOptions.color(0xfffd8364);
//        polylineOptions.width(polylineOptions.getWidth() * 2.5f);
//        //
//        map.addPolyline(polylineOptions);
//        //
//        if (leg.steps.size() > 0) {
//            Direction.Step step = leg.steps.get(0);
//            moveCamera(new LatLng(step.start_location.lat, step.start_location.lng));
//        }
//    }
//    private void moveCamera(LatLng latLng) {
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
//        map.moveCamera(cameraUpdate);
//        cameraUpdate = CameraUpdateFactory.zoomTo(14);
//        map.moveCamera(cameraUpdate);
//    }

    //20210201add_o

    //20210130add_s
    //新增資料至MySQL
    private void mysql_insert() {//新增
        //取得目前使用者ID
        SharedPreferences u_id = getSharedPreferences("USER_ID", 0);
        //t_u_id = u_id.getString("USER_ID", "");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        t_u_id = account.getId();

        //取得目前使用者所在地經緯度
        t_lat = lastKnownLocation.getLatitude();
        t_lon = lastKnownLocation.getLongitude();
        //存入目前使用者資料
        ArrayList<String> E200_ValuePairs = new ArrayList<>();//存取上述三個資料為陣列
        E200_ValuePairs.add(t_u_id);
        E200_ValuePairs.add(String.valueOf(t_lat)); //double轉string並存進陣列
        E200_ValuePairs.add(String.valueOf(t_lon));
        try {//延遲Thread 睡眠0.5秒
            sleep(500);
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
//        t_u_id = u_id.getString("USER_ID", "");

        ArrayList<String> E100_ValuePairs = new ArrayList<>();
        E100_ValuePairs.add(t_u_id);
        E100_ValuePairs.add(addressName);
        E100_ValuePairs.add(addressName_goal);
        E100_ValuePairs.add(String.valueOf(longitude));
        E100_ValuePairs.add(String.valueOf(latitude));
        E100_ValuePairs.add(String.valueOf(longitude_goal));
        E100_ValuePairs.add(String.valueOf(latitude_goal));


        try {//延遲Thread 睡眠0.5秒
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------//實際執行新增資料至mysql
        String result_history = DBConnector22.executeInsert_history(E100_ValuePairs);
//-----------------------------------------------
    }

    //20210214adds
    // 讀取MySQL 資料
    private void dbmysqlFriendLocation() {
//        sqlctl = "SELECT E200.E201,E200.E202,E200.E203,A100.A102,A100.A104 FROM E200 INNER JOIN A100 ON E200.E201=A100.A101";
//        sqlctl = "SELECT E200.E201,E200.E202,E200.E203,A100.A102,A100.A104 FROM E200 INNER JOIN A100 ON E200.E201=A100.A101 WHERE A100.id IN(SELECT DISTINCT G202 FROM G200 WHERE G203 IN(SELECT DISTINCT G203 FROM G200 INNER JOIN A100 ON G200.G202=A100.id WHERE G203 IN(SELECT G101 FROM G100 WHERE G108 IN (SELECT DISTINCT C201 FROM C200 INNER JOIN A100 ON C200.C202=A100.id WHERE A100.A101=104043476158528995903) AND G110='0') AND A100.A101=104043476158528995903))";
            sqlctl = "SELECT E200.E201,E200.E202,E200.E203,A100.A102,A100.A104 FROM E200 INNER JOIN A100 ON E200.E201=A100.A101 WHERE A100.id IN(SELECT DISTINCT G202 FROM G200 WHERE G203 IN(SELECT DISTINCT G203 FROM G200 INNER JOIN A100 ON G200.G202=A100.id WHERE G203 IN(SELECT G101 FROM G100 WHERE G108 IN (SELECT DISTINCT C201 FROM C200 INNER JOIN A100 ON C200.C202=A100.id WHERE A100.A101= " + t_u_id + " ) AND G110='0') AND A100.A101= " + t_u_id + " ))  and E200.E201 <> " + t_u_id + " ";
            int aa = 0;
            ArrayList<String> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(sqlctl);
            try {
                String result = DBConnector22.executeQueryFriendLocation(nameValuePairs);
//=========================================
                //=========================================
                JSONArray jsonArray = new JSONArray(result);
                int jasoArrayLength = jsonArray.length() - 1; //20210215adds
                ArrayList<String> stringArray = new ArrayList<String>(); //20210214adds
                arrayFriendLocation = toStringArray(jsonArray);
                // -------------------------------------------------------
                if (jsonArray.length() > 0) { // MySQL 連結成功有資料
                    //--------------------------------------------------------
//                int rowsAffected = dbHper.clearRec();                 // 匯入前,刪除所有SQLite資料
                    //--------------------------------------------------------
                    //清除舊marker
                    if (friendMarker != null) {
                        for (int j = 0; j < friendMarker.length; j++) {
                            if(friendMarker[j]!=null) friendMarker[j].remove();
                        }
                    }

                    // 處理JASON 傳回來的每筆資料
                    //顯示使用者頭像 多位 inner join


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        String stringE201 = jsonData.getString("E201").toString(); //20210215adds 取json陣列的值 gid
                        String stringE202 = jsonData.getString("E202").toString();
                        String stringE203 = jsonData.getString("E203").toString();
                        String stringA102 = jsonData.getString("A102").toString(); //使用者姓名
                        String stringA104 = jsonData.getString("A104").toString(); //使用者頭像
//                    output_history.setText(stringE201 + "," + stringE202 + "," + stringE203 + "," + stringA104 + "\n");  //20210214改
//                    output_history.append(stringE201 + "\n");  //20210214改 顯示團員
//                    userLocationAddMarker(stringE201, stringE202, stringE203, stringA102, stringA104); //20210301關 避免重複渲染
                        //--------------------------陣列marker
                        try {
                            URL url2 = new URL(stringA104);
                            Bitmap bmp2 = decodeStream(url2.openConnection().getInputStream());
                            Bitmap bmp3 = Bitmap.createScaledBitmap(bmp2, 200, 200, true); //更改圖示大小
//                        Marker[] friendMarker = new Marker[100];
//                        if(friendMarker!=null) friendMarker=null;
//                        if(friendMarker!=null)  friendMarker = new Marker[100];

//                        if(friendMarker[i]!=null) friendMarker[i].remove();

                            friendMarker[i] = map.addMarker(new MarkerOptions()
                                    .title(stringA102)
                                    .position(new LatLng(Double.parseDouble(stringE202), Double.parseDouble(stringE203)))
                                    .snippet("所在地")
//                    .icon(BitmapDescriptorFactory.fromBitmap(toRoundCorner(bmp3,14)))
                                    .icon(BitmapDescriptorFactory.fromBitmap(transform(bmp3)))
                                    .anchor(0.5f, 0.5f)
                                    .draggable(false)
                                    .flat(true));
                            markerStart = new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude());
                            markerEnd = new LatLng(Double.parseDouble(stringE202), Double.parseDouble(stringE203));
//            onMarkerClick(b_marker);

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                            output_history.setText(e.toString());
                            e.printStackTrace();
                        }

                        //--------------------------陣列marker


//                    try {
//                        URL url2 = new URL(stringA104);
//                        Bitmap bmp2 = decodeStream(url2.openConnection().getInputStream());
//                        map.addMarker(new MarkerOptions()
//                                .title(stringA102)
//                                .position(new LatLng(Double.parseDouble(stringE202), Double.parseDouble(stringE203)))
//                                .snippet("所在地")
//                                .icon(BitmapDescriptorFactory.fromBitmap(bmp2))
//                                .anchor(0.5f, 0.5f)
//                                .draggable(false)
//                                .flat(true));
//                    } catch (Exception e) {
//                        Log.d(TAG,e.toString());
//                        output_history.setText(e.toString());
//                        e.printStackTrace();
//                    }
//                    userLocationAddMarker(stringE201, stringE202, stringE203, stringA102, stringA104);
//
//                    stringArray.add(jsonData.toString());  //20210214adds
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
//
//                        }
//                        // ------------------------------------------------------------------
//                        newRow.put(key, value.toString()); // 動態找出有幾個欄位
////                        output_history.append(key); //20210214adds
////                        output_history.append(value);
////                        output_history.append(stringArray[0][0]);
////                        output_history.append(arrayFriendLocation[1]);
//
//                        // -------------------------------------------------------------------
//                    }
//                    // -------------------加入SQLite---------------------------------------
////                    long rowID = dbHper.insertRec_m(newRow);
                    }
                    arrayFriendLocation2 = arrayFriendLocation.toString().split("", 5);
//                output_history.append(arrayFriendLocation[1].split('"',15)); //20210214adds
//                                output_history.append(arrayFriendLocation2[2]); //20210214adds
//                output_history.append(stringArray.get(0));  //20210214改
                    // ---------------------------
                } else {
//                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString() + "123");
            }


    }

    //20210215adds
    private void userLocationAddMarker(String b_stringE201, String b_stringE202, String b_stringE203, String b_stringA102, String b_stringA104) {
        try {
            URL url2 = new URL(b_stringA104);
            Bitmap bmp2 = decodeStream(url2.openConnection().getInputStream());
            Bitmap bmp3 = Bitmap.createScaledBitmap(bmp2, 200, 200, true); //更改圖示大小

            Marker b_marker = map.addMarker(new MarkerOptions()
                    .title(b_stringA102)
                    .position(new LatLng(Double.parseDouble(b_stringE202), Double.parseDouble(b_stringE203)))
                    .snippet("所在地")
//                    .icon(BitmapDescriptorFactory.fromBitmap(toRoundCorner(bmp3,14)))
                    .icon(BitmapDescriptorFactory.fromBitmap(transform(bmp3)))
                    .anchor(0.5f, 0.5f)
                    .draggable(false)
                    .flat(true));
            markerStart = new LatLng(lastKnownLocation.getLatitude(),
                    lastKnownLocation.getLongitude());
            markerEnd = new LatLng(Double.parseDouble(b_stringE202), Double.parseDouble(b_stringE203));
//            onMarkerClick(b_marker);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            output_history.setText(e.toString());
            e.printStackTrace();
        }
        //
//        map.addMarker(new MarkerOptions()  //使用者頭像 目前所在地
//                        .title(b_stringA102)
//                        .position(new LatLng(Double.parseDouble(b_stringE202), Double.parseDouble(b_stringE203)))
//                        .snippet("所在地")
////                .icon(BitmapDescriptorFactory.fromPath(b_stringA104))
////                        .icon( BitmapDescriptorFactory.fromBitmap(toRoundCorner(bmp, 14)))
//                .icon( BitmapDescriptorFactory.fromBitmap(toRoundCorner(convertStringToIcon(b_stringA104), 14)))
//                        .anchor(0.5f, 0.5f)
//                        .draggable(false)
//                        .flat(true));
//        map.addMarker(new MarkerOptions()
//                .title("目前")
//                .position(new LatLng(22.90851123769516, 120.21193830300008))
//                .snippet("所在地")
////                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.t00))
////                                        .icon("https://lh3.googleusercontent.com/a-/AOh14GjpOhFPxsPFT1JZtICJ1oGSPxo_kG7a_FuAhi1H=s96-c")
//                .icon(BitmapDescriptorFactory.fromPath("https://lh3.googleusercontent.com/a-/AOh14GjpOhFPxsPFT1JZtICJ1oGSPxo_kG7a_FuAhi1H=s96-c"))
//                .anchor(0.5f, 0.5f)
//                .draggable(false)
//                .flat(true));
//        try {
//            URL url2 = new URL("https://lh3.googleusercontent.com/a-/AOh14GjpOhFPxsPFT1JZtICJ1oGSPxo_kG7a_FuAhi1H=s96-c");
//            Bitmap bmp2 = decodeStream(url2.openConnection().getInputStream());
//            map.addMarker(new MarkerOptions()
//                    .title("目前")
//                    .position(new LatLng(22.90851123769516, 120.21193830300008))
//                    .snippet("所在地")
////                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.t00))
////                                        .icon("https://lh3.googleusercontent.com/a-/AOh14GjpOhFPxsPFT1JZtICJ1oGSPxo_kG7a_FuAhi1H=s96-c")
////                                            .icon(BitmapDescriptorFactory.fromPath("https://lh3.googleusercontent.com/a-/AOh14GjpOhFPxsPFT1JZtICJ1oGSPxo_kG7a_FuAhi1H=s96-c"))
//                    .icon(BitmapDescriptorFactory.fromBitmap(bmp2))
//                    .anchor(0.5f, 0.5f)
//                    .draggable(false)
//                    .flat(true));
//        } catch (Exception e) {
//            Log.d(TAG,e.toString());
//            e.printStackTrace();
//        }
    }

    public static Bitmap convertStringToIcon(String st) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(st, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public Bitmap transform(final Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        float r = size / 2f;
        Bitmap output = Bitmap.createBitmap(size + triangleMargin, size + triangleMargin, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);
        Paint paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
        paintBorder.setColor(Color.parseColor("#33E6CC"));
        paintBorder.setStrokeWidth(margin);
        canvas.drawCircle(r, r, r - margin, paintBorder);
        int x = source.getWidth();
        final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());
        Paint trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trianglePaint.setStrokeWidth(2);
        trianglePaint.setColor(Color.parseColor("#33E6CC"));
        trianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        trianglePaint.setAntiAlias(true);
        Path triangle = new Path();
        triangle.setFillType(Path.FillType.EVEN_ODD);
        triangle.moveTo(size - margin, size / 2);
        triangle.lineTo(size / 2, size + triangleMargin);
        triangle.lineTo(margin, size / 2);
        triangle.close();
        canvas.drawPath(triangle, trianglePaint);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawCircle(r, r, r - photoMargin, paint);
        if (source != output) {
            source.recycle();
        }
        return output;
    }

    //20210215addo
    public static String[] toStringArray(JSONArray array) {
        if (array == null)
            return null;
        String[] arr = new String[array.length()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = array.optString(i);
        }
        return arr;
    }
    //20210214addo

    //檢查是否開啟GPS
    private void u_checkgps() {
        // 取得系統服務的LocationManager物件
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 檢查是否有啟用GPS
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
//        addressName = location_start.getText().toString(); //20210211adds 關
//        addressName = location_start.getText().toString(); //20210211adds 改 由自行輸入出發點改為抓取目前所在地 關
        addressName_goal = location_goal.getText().toString();
        try {
            // 取得經緯度座標清單的List物件
//            List<Address> listGPSAddress = geocoder.getFromLocationName(addressName, 1); //20210211adds 關
            List<Address> listGPSAddress_goal = geocoder.getFromLocationName(addressName_goal, 1);
            // 有找到經緯度座標
            if (listGPSAddress_goal != null) { //20210211adds  listGPSAddress != null  改 listGPSAddress_goal
//                latitude = listGPSAddress.get(0).getLatitude();//20210211adds 改 由自行輸入出發點改為抓取目前所在地
//                longitude = listGPSAddress.get(0).getLongitude();
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude(); //20210211addo 改 由自行輸入出發點改為抓取目前所在地

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
                dbHper.insertRec("目前所在地", addressName_goal, longitude, latitude, longitude_goal, latitude_goal);   //將資料傳入資料庫20210115
                mysql_insert_history(); //最新搜尋歷史紀錄存進MySQL
                //20210211adds 導航 路徑
                startFindroutes = new LatLng(lastKnownLocation.getLatitude(),
                        lastKnownLocation.getLongitude());
                endFindroutes = new LatLng(latitude_goal, longitude_goal);
                //start route finding
//                Findroutes(startFindroutes, endFindroutes);
                Findroutes(new LatLng(lat_I, lng_I), endFindroutes);

                //20210211addo 導航 路徑
            }
        } catch (Exception ex) {
            output.setText("經緯度無法辨識\n請更改輸入地址名稱");
//            output.setText("\n\n\n\n\n"+ex.toString());
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
//                    + cur_list.getString(2) + " ,"
                    + cur_list.getString(3));
            while (cur_list.moveToNext()) {
                output_history.append("\n" + cur_list.getString(0) + " ,"
//                        + cur_list.getString(2) + " ,"
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
        //        開啟 Google Map 拖曳功能
        map.getUiSettings().setScrollGesturesEnabled(true);
//        右下角的導覽及開啟 Google Map功能
        map.getUiSettings().setMapToolbarEnabled(true);
//        左上角顯示指北針，要兩指旋轉才會出現
        map.getUiSettings().setCompassEnabled(true);
//        右下角顯示縮放按鈕的放大縮小功能
        map.getUiSettings().setZoomControlsEnabled(true);
        // --------------------------------

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
        map.setOnInfoWindowClickListener(this);
//        map.setOnMarkerClickListener(this);
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
                                showMarkerI();
                                //-------------------------0131add o
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

    //0131add s 圓形圖
    //將原始圖像裁剪成正方形 
    private Bitmap dealRawBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
//獲取寬度
        int minWidth = width > height ? height : width;
//計算正方形的範圍
        int leftTopX = (width - minWidth) / 2;
        int leftTopY = (height - minWidth) / 2;
//裁剪成正方形
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, leftTopX, leftTopY, minWidth, minWidth, null, false);
        return scaleBitmap(newBitmap);
    }

    private Bitmap scaleBitmap(Bitmap bitmap) {
//        int width = bitmap.getWidth();
//        //一定要強轉成float 不然有可能因為精度不夠 出現 scale為0 的錯誤
//        float scale = (float) width / (float) bitmap.getWidth();
//        Matrix matrix = new Matrix();
//        matrix.postScale(scale, scale);
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // 使用 Matrix 物件
        Matrix matrix = new Matrix();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

//想要的大小
        int newWidth = 200;
        int newHeight = 200;

//計算比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

// 設定 Matrix 物件，設定 x,y 向的縮放比例
        matrix.postScale(scaleWidth, scaleHeight);

// 設定 Matrix 物件，設定順時針旋轉90度
//        matrix.postRotate(90.0F);

/**
 * creatBitmp的參數依序如下：
 * 原圖資源
 * 第一個pixel點的x座標
 * 第一個pixel點的y座標
 * 圖片的總pixel行
 * 圖片的總pixel列
 * Matrix 的設定
 * 是否filter圖片
 */
        newBitmapSize = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap toRoundCorner(Bitmap b_bitmap, int pixels) {

//指定為 ARGB_4444 可以減小圖片大小
        Bitmap output = Bitmap.createBitmap(b_bitmap.getWidth(), b_bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, b_bitmap.getWidth(), b_bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = b_bitmap.getWidth();
        canvas.drawCircle(x / 2, x / 2, x / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(b_bitmap, rect, rect, paint);
        return dealRawBitmap(scaleBitmap(output));
    }
    //0131add o

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
                        Biker_time_map_plan.this.openPlacesDialog();
                    } else {
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                }
            });

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

                //20210131add s
//                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                //20210131add o

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                // 20210131add
//                image_des = BitmapDescriptorFactory.fromResource(t_imgUrl);// 使用照片
//                String imageURLBase = "your url";
                URL imageURL = null;
                try {
                    imageURL = new URL(t_imgUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                URLConnection connection = null;
                try {
                    connection = imageURL.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream iconStream = null;
                try {
                    iconStream = connection.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bmp = decodeStream(iconStream);


                map.addMarker(new MarkerOptions()
                        .title(likelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.t00))
                        .anchor(0.5f, 0.5f)
                        .draggable(false)
                        .flat(true));
//                                .icon(BitmapDescriptorFactory.fromBitmap(bmp))

//                        .icon(BitmapDescriptorFactory.fromResource(User_IMAGE))


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


    //20210118 O--------------------------

    //20210225adds Control按鈕設定
    private CheckBox.OnCheckedChangeListener chklistener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (checkBox.isChecked()) {
                controlScroll.setVisibility(View.VISIBLE);
            } else {
                controlScroll.setVisibility(View.INVISIBLE);
            }
        }
    };

    private boolean isChecked(int id) {
        return ((CheckBox) findViewById(id)).isChecked();
    }

    private boolean checkReady() {
        if (map == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //        右下角顯示縮放按鈕的放大縮小功能
    public void setZoomButtonsEnabled(View v) {
        if (!checkReady()) return;
        map.getUiSettings().setZoomControlsEnabled(((CheckBox) v).isChecked());
    }

    //        左上角顯示指北針，要兩指旋轉才會出現
    public void setCompassEnabled(View v) {
        if (!checkReady()) return;
        map.getUiSettings().setCompassEnabled(((CheckBox) v).isChecked());
    }

    public void setMyLocationLayerEnabled(View v) {
        if (!checkReady()) return;
        //----------取得定位許可-----------------------
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //----顯示我的位置ICO-------
            map.setMyLocationEnabled(((CheckBox) v).isChecked());
        } else {
            Toast.makeText(getApplicationContext(), "GPS定位權限未允許", Toast.LENGTH_LONG).show();
        }
    }

    //----可用捲動手勢操控,用手指平移或捲動來拖曳地圖
    public void setScrollGesturesEnabled(View v) {
        if (!checkReady()) return;
        map.getUiSettings().setScrollGesturesEnabled(((CheckBox) v).isChecked());
    }

    //----縮放手勢按兩下按一下或兩指拉大拉小----
    public void setZoomGesturesEnabled(View v) {
        if (!checkReady()) return;
        map.getUiSettings().setZoomGesturesEnabled(((CheckBox) v).isChecked());
    }

    //----傾斜手勢改變地圖的傾斜角度兩指上下拖曳來增加/減少傾斜角度----
    public void setTiltGesturesEnabled(View v) {
        if (!checkReady()) return;
        map.getUiSettings().setTiltGesturesEnabled(((CheckBox) v).isChecked());
    }

    //----旋轉手勢兩指旋轉地圖----
    public void setRotateGesturesEnabled(View v) {
        if (!checkReady()) return;
        map.getUiSettings().setRotateGesturesEnabled(((CheckBox) v).isChecked());
    }

    //20210225addo Control按鈕設定
    //20210226adds GPS
    // ============ GPS =================
    //** onMyLocationButtonClick  位置變更狀態監視/
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getApplicationContext(), "返回GPS目前位置", Toast.LENGTH_LONG).show();
        return true;
    }

    //*********************************************/
    /* 檢查GPS 是否開啟 */
    private boolean initLocationProvider() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            return true;
        }
        return false;
    }

    //-------------------
    private void nowaddress() {
// 取得上次已知的位置
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(provider);
            updateWithNewLocation(location);
            return;
        }
// 監聽 GPS Listener----------------------------------
// long minTime = 5000;// ms
// float minDist = 5.0f;// meter
//---網路和GPS來取得定位，因為GPS精準度比網路來的更好，所以先使用網路定位、
// 後續再用GPS定位，如果兩者皆無開啟，則跳無法定位的錯誤訊息
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location = null;
        if (!(isGPSEnabled || isNetworkEnabled))
            tmsg.setText("GPS 未開啟");
        else {
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        minTime, minDist, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                tmsg.setText("使用網路GPS");
            }
//------------------------
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        minTime, minDist, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                tmsg.setText("使用精確GPS");
            }
        }

    }

    private void updateWithNewLocation(Location location) {
        if (location != null) {
            double lng = location.getLongitude();// 經度
            double lat = location.getLatitude();// 緯度
            lng_I = lng;
            lat_I = lat;
            double high = location.getAltitude(); //高度 海拔
            float speed = location.getSpeed();// 速度
            long time = location.getTime();// 時間
            String timeString = getTimeString(time);
            where = "經度: " + lng + "\n緯度: " + lat + "\n速度: " + speed + "\n時間: " + timeString + "\n高度: " + high + "\nProvider: "
                    + provider;
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            updateUI(account);
            t_u_id = account.getId();
            ArrayList<String> E200_ValuePairs2 = new ArrayList<>();//存取上述三個資料為陣列
            E200_ValuePairs2.add(t_u_id);
            E200_ValuePairs2.add(String.valueOf(lat)); //double轉string並存進陣列
            E200_ValuePairs2.add(String.valueOf(lng));
            String result = DBConnector22.executeInsert(E200_ValuePairs2);
            showMarkerI();
            //===============================
            if (tracesel == 1) {
                hashMapMarker = new HashMap<String, Object>();
                hashMapMarker.put("lat", Double.toString(lat));
                hashMapMarker.put("lng", Double.toString(lng));
                hashMapMarker.put("vtitle", Integer.toString(location_no));
                hashMapMarker.put("timeString", timeString);
                arrayList.add(hashMapMarker);
                location_no++;
// 標記"我的位置"
                showMarkerMe(lat, lng);
                cameraFocusOnMe(lat, lng);
                //-------------------------------------
                trackMe(lat, lng);  //軌跡圖
                //-------------------------------------
            } else {
                // 標記"我的位置"
                showMarkerMe(lat, lng);
                //            cameraFocusOnMe(lat, lng);
            }
            //===============================
        } else {
            where = "*位置訊號消失*";
        }
        // 位置改變顯示
        output.setText(where);
    }

    //追蹤目前我的位置畫軌跡圖
    private void trackMe(double lat, double lng) {
        for (int i = 0; i < arrayList.size(); i++) {   //i < arrayList.size()改100
            String vtitle = arrayList.get(i).get("vtitle").toString();
            String timeString = arrayList.get(i).get("timeString").toString();
            dLat = Double.valueOf(arrayList.get(i).get("lat").toString());
            dLon = Double.valueOf(arrayList.get(i).get("lng").toString());

            image_des = BitmapDescriptorFactory.fromResource(R.drawable.c0b);// 使用照片
            MarkerOptions markerOpt = new MarkerOptions();
            markerOpt.position(new LatLng(dLat, dLon));
            String imgName = "c00";
            resID1 = getResources().getIdentifier(imgName, "drawable", getPackageName());
            markerOpt.title(vtitle + "-" + timeString + "#" + resID1);
//            markerOpt.title(vtitle + "-" + timeString + "#");
//            markerOpt.snippet(getString(R.string.lat) + dLat + getString(R.string.lon) + dLon);
            markerOpt.snippet(where);
            markerOpt.infoWindowAnchor(Anchor_x, Anchor_y); //0.5 0.9
            markerOpt.draggable(true);
            markerOpt.icon(image_des);
            markerMe = map.addMarker(markerOpt);
//--------------------使用自定義式窗-------------------------------------------------------
            map.setInfoWindowAdapter(new CustomInfoWindowAdapter());//外圓內方
//-----------------------------------------
        }
    }

    /***********************************************
     * timeInMilliseconds
     ***********************************************/
    private String getTimeString(long timeInMilliseconds) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(timeInMilliseconds);
    }

    // cameraFocusOnMe
    private void cameraFocusOnMe(double lat, double lng) {
        CameraPosition camPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(map.getCameraPosition().zoom)
                .build();
        /* 移動地圖鏡頭 */
        map.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
//        tmsg.setText("目前Zoom:" + map.getCameraPosition().zoom);
    }

    //*** 顯示目前位置*/
    private void showMarkerMe(double lat, double lng) {
        if (markerMe != null) markerMe.remove();
        VGPS = new LatLng(lat, lng);  // 更新成欲顯示的地圖座標
        locations[0][1] = lat + "," + lng;  //陣列我的位置會一直變
    }

    private void showMarkerI() {
        //使用者頭像 所在地 (CircleImgView)
        URL imageURL = null;
        URLConnection connection = null;
        InputStream iconStream = null;
        try {
            imageURL = new URL(t_imgUrl);
            connection = imageURL.openConnection();
            iconStream = connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bmp = decodeStream(iconStream);
        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, 200, 200, true); //更改圖示大小
        circleBitmap = transform(bmp2);

        if (markerI != null) markerI.remove();
        markerI = map.addMarker(new MarkerOptions()
                .title("目前")
                .position(new LatLng(lat_I,
                        lng_I))
                .snippet("所在地")
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.t00))
//                                        .icon("https://lh3.googleusercontent.com/a-/AOh14GjpOhFPxsPFT1JZtICJ1oGSPxo_kG7a_FuAhi1H=s96-c")
                .icon(BitmapDescriptorFactory.fromBitmap(circleBitmap))
                .anchor(0.5f, 1f)
                .draggable(false)
                .flat(true));
    }

    /*** 位置變更狀態監視*/
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
            //如果開啟好友位置的話
//            dbmysqlFriendLocation();


            tmsg.setText("目前Zoom:" + map.getCameraPosition().zoom);
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
            tmsg.setText("GPS close");
        }

        @Override
        public void onProviderEnabled(String provider) {
            tmsg.setText("GPS Enabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    tmsg.setText("Out of Service");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    tmsg.setText("Temporarily Unavailable");
                    break;
                case LocationProvider.AVAILABLE:
                    tmsg.setText("Available");
                    break;
            }
        }
    };

    //20210226addo GPS


    ////----------------------------------以下是menu用的方法----------------
    private void Signin_menu() {//登入時顯示的menu
        menu.setGroupVisible(R.id.g01, false);
        menu.setGroupVisible(R.id.g02, true);
    }

    private void Signout_menu() {//登出時顯示的menu
        menu.setGroupVisible(R.id.g01, true);
        menu.setGroupVisible(R.id.g02, false);
    }

    //20210131 add s-----------------------


    private void updateUI(GoogleSignInAccount account) {
        GoogleSignInAccount aa = account;
        int aaa = 1;
        if (account != null) {
//            mStatusTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));
//            String g_DisplayName=account.getDisplayName(); //暱稱
//            String g_Email=account.getEmail();  //信箱
//            String g_GivenName=account.getGivenName(); //Firstname
//            String g_FamilyName=account.getFamilyName(); //Last name


//-------改變圖像--------------
            User_IMAGE = account.getPhotoUrl();
//            int a=10;

            //沒有照片的話
            if (User_IMAGE == null) {
                noiconimg = Uri.parse("https://lh3.googleusercontent.com/pw/ACtC-3f7ifqOfGrkeKoxWel1YUubvk1UzdlwSpsIY_Wfxa3jCYE75R1aYZlFtZd-jvFPzp5aUNfJksNAtXYj0OhzV-brFWU7E81L8H5td0SZTDgeWDp7PdVcBwKYxChccjyhUsTjVb2L8Zrqh7xJEGBIuhyK=w200-h192-no?authuser=0");
                User_IMAGE = noiconimg;
//                return;
            }
            img = (CircleImgView) findViewById(R.id.google_icon_mapmarker);  //0131
            //取得目前使用者資料
            t_id = account.getId();
            t_name = account.getDisplayName();
            t_email = account.getEmail();
            t_imgUrl = User_IMAGE + "";


//            show_msgLYT.setText("Email:"+account.getEmail()+
//                    "\n 全名:"+account.getDisplayName()+
//                    "\n Firstname:"+account.getGivenName()+
//                    "\n Last name:"+account.getFamilyName()+
//                    "\n Id:"+account.getId()+
//                    "\n 頭貼網址:"+User_IMAGE
//            );
            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    String url = params[0];
                    return getBitmapFromURL(url);
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    img.setImageBitmap(result);
                    super.onPostExecute(result);
                }
            }.execute(User_IMAGE.toString().trim());
            //-------------------------
//            String g_id=account.getId();
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
//            googleloginLYT.setVisibility(View.GONE);
//            googlelogoutLYT.setVisibility(View.VISIBLE);

//            handler.postDelayed(updateTimer, 3000);// 停多久開始做這個動作
        } else {


            t_id = "";
            t_name = "";
            t_email = "";
            t_imgUrl = "";
//            show_msgLYT.setText(R.string.login_signout);
//
//            googleloginLYT.setVisibility(View.VISIBLE);
//            googlelogoutLYT.setVisibility(View.GONE);
        }


    }

    public static Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    //20210228adds GPX

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = getRealPathFromURI_API19(this, uri);
            if (map != null) map.clear();  //218
            if (arrayListgpx != null) arrayListgpx.clear();
            if (mytraceGpx != null) mytraceGpx.clear();
            if (markerMe != null) markerMe.remove();
            if (uri != null) {
                try {
                    File gpxFile = new File(path);
                    List<Location> gpxList = decodeGPX(gpxFile);

                    for (int i = 0; i < gpxList.size(); i++) {

                        Double lat = ((Location) gpxList.get(i)).getLatitude();
                        Double lon = ((Location) gpxList.get(i)).getLongitude();
                        trackMeGpx(lat, lon);//軌跡圖
                        VGPS = new LatLng(lat, lon);
                        if (i == 0) {
                            image_des = BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED); //使用系統水滴
                            map.addMarker(new MarkerOptions().position(VGPS).title("起點").icon(image_des));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS, DEFAULT_ZOOM));
                        } else if (i == gpxList.size() - 1) {
                            image_des = BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN); //使用系統水滴
                            map.addMarker(new MarkerOptions().position(VGPS).title("終點").icon(image_des));
                        }

                        //超過10分鐘 怕點太多 就5個點露一個
                        if (gpxList.size() / 600 > 0) {
                            int ez_gpx = gpxList.size() / 600;
                            i += ez_gpx + 4;
                            boolean ok_end = false;
                            if (gpxList.size() - i <= 0) {
                                map.addMarker(new MarkerOptions().position(VGPS).title("終點").icon(image_des));
                            }
                        }
                    }
                } catch (Exception message) {
//                    t001.setText(uri.getPath());
                }
            } else {
//                t001.setText("無效的檔案路徑!!");
            }
        } else {
//            t001.setText("取消選擇檔案!!");
        }

    }

    //解析GPX
    private List<Location> decodeGPX(File file) {
        List<Location> list = new ArrayList<Location>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            FileInputStream fileInputStream = new FileInputStream(file);
            Document document = documentBuilder.parse(fileInputStream);
            Element elementRoot = document.getDocumentElement();
            NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");
            for (int i = 0; i < nodelist_trkpt.getLength(); i++) {
                Node node = nodelist_trkpt.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String newLatitude = attributes.getNamedItem("lat").getTextContent();
                Double newLatitude_double = Double.parseDouble(newLatitude);

                String newLongitude = attributes.getNamedItem("lon").getTextContent();
                Double newLongitude_double = Double.parseDouble(newLongitude);

                String newLocationName = newLatitude + ":" + newLongitude;
                Location newLocation = new Location(newLocationName);
                newLocation.setLatitude(newLatitude_double);
                newLocation.setLongitude(newLongitude_double);
                list.add(newLocation);
            }

            fileInputStream.close();

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    //轉換正確路徑 因為有符號
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";

        // ExternalStorageProvider
        String docId = DocumentsContract.getDocumentId(uri);
        String[] split = docId.split(":");
        String type = split[0];

        if ("primary".equalsIgnoreCase(type)) {
            return Environment.getExternalStorageDirectory() + "/" + split[1];
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                //getExternalMediaDirs() added in API 21
                File[] external = context.getExternalMediaDirs();
                if (external.length > 1) {
                    filePath = external[1].getAbsolutePath();
                    filePath = filePath.substring(0, filePath.indexOf("Android")) + split[1];
                }
            } else {
                filePath = "/storage/" + type + "/" + split[1];
            }
            return filePath;
        }
    }

    //追蹤目前我的位置畫軌跡圖
    private void trackMeGpx(double lat, double lng) {
        if (mytraceGpx == null) {
            mytraceGpx = new ArrayList<LatLng>();
        }
        mytraceGpx.add(new LatLng(lat, lng));
        //畫線
        PolylineOptions polylineOpt = new PolylineOptions();
        for (LatLng latlng : mytraceGpx) {
            polylineOpt.add(latlng);
        }
        polylineOpt.color(Color.BLUE); // 軌跡顏色
        Polyline line = map.addPolyline(polylineOpt);
        line.setWidth(10); // 線寬軌跡寬度
        line.setPoints(mytraceGpx);//這句話才把線加進去
    }

    //20210228addo GPX

    //20210131 add o-----------------------
    private void checkRequiredPermission(Activity activity) {
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size() != 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new
                    String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    //20210201add_s------------------------
    @Override
    protected void onStart() {
        super.onStart();
        // --START on_start_sign_in--
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        //--END on_start_sign_in--
        Log.d(TAG, "onStart");
        if (initLocationProvider()) {
            nowaddress();
        } else {
            output.setText("GPS未開啟,請先開啟定位！");
        }
            handler.postDelayed(go_map,3000);



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbHper == null) {
            dbHper = new FriendDbHelper22(this, DB_FILE, null, DBversion);
        }
        Log.d(TAG, "onResume");


    }

    @Override
    protected void onPause() {
        super.onPause();
        // 開啟中的話就關掉
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }
        handler.removeCallbacks(friend_Location);

        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 開啟中的話就關掉
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }
        Log.d(TAG, "onStop");
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        handler.removeCallbacks(friend_Location);
        handler.removeCallbacks(go_map);
    }

    //20210201add_o------------------------
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

    //20210211adds
// function to find Routes.
    public void Findroutes(LatLng Start, LatLng End) {
        map.clear(); //20210214adds

        if (Start == null || End == null) {
            Toast.makeText(Biker_time_map_plan.this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.BIKING)   //0211改 TravelMode.DRIVING 改 BIKING
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(getString(R.string.maps_api_key))  //also define your api key here.
                    .build();
            routing.execute();
        }
        showMarkerI();
//    getDeviceLocation();  //20210214adds
        //20210214adds
//        map.addMarker(new MarkerOptions()  //使用者頭像 目前所在地
//                .title("目前")
//                .position(new LatLng(lastKnownLocation.getLatitude(),
//                        lastKnownLocation.getLongitude()))
//                .snippet("所在地")
//                .icon(BitmapDescriptorFactory.fromBitmap(circleBitmap))
//                .anchor(0.5f, 0.5f)
//                .draggable(false)
//                .flat(true));
        //20210214addo
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(Biker_time_map_plan.this, "尋找路徑中...", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(startFindroutes);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = map.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k - 1);
                polylines.add(polyline);

            } else {

            }

        }

//        //Add Marker on route starting position
//        MarkerOptions startMarker = new MarkerOptions();
//        startMarker.position(polylineStartLatLng);
//        startMarker.title("所在地");
//        map.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("目的地");
        map.addMarker(endMarker);
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(startFindroutes, endFindroutes);
    }

    //
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        Findroutes(startFindroutes,endFindroutes);
//    }
//
//更改地圖模式
    private void Show_MapType() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        for (int i = 0; i < mapType.length; i++)
            adapter.add(mapType[i]);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnMapType.setAdapter(adapter);
        //-----------設定ARGB透明度----
        mSpnMapType.setPopupBackgroundDrawable(new ColorDrawable(0xF2FFFFFF));
        mSpnMapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    switch (position) {
                        case 0:
                            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);//道路地圖。
                            break;
                        case 1:
                            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);//衛星空照圖
                            break;
                        case 2:
                            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);//地形圖
                            break;
                        case 3:
                            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);//道路地圖混合空照圖
                            break;
                        case 4://開啟路況
                            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);//道路地圖。
                            map.setTrafficEnabled(true);//交通路況圖
                            break;
                        case 5://關閉路況
                            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);//道路地圖。
                            map.setTrafficEnabled(false);//交通路況圖
                            break;
                    }
                } catch (Exception e) {
                    Log.d("Error=>", e.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


//    //marker監聽
//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        Findroutes(markerStart, markerEnd);
//        return false;
//    }

    //20210211addo


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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.biker_map_menu, menu);
        this.menu = menu;
        m_traceon = menu.findItem(R.id.menu_trace_on);
        m_traceoff = menu.findItem(R.id.menu_trace_off);
        m_traceon.setVisible(true);
        m_traceoff.setVisible(false);
        m_historyon = menu.findItem(R.id.menu_history_on);
        m_historyoff = menu.findItem(R.id.menu_history_off);
        m_historyon.setVisible(true);
        m_historyoff.setVisible(false);
        m_navon = menu.findItem(R.id.menu_navon);
        m_navoff = menu.findItem(R.id.menu_navoff);
        m_navon.setVisible(false);
        m_navoff.setVisible(false);


        m_memberlist_no = menu.findItem(R.id.menu_memberlist_no);
        m_memberlist_no.setVisible(false);
        m_memberlist = menu.findItem(R.id.menu_memberlist);


//        menu.setGroupVisible(R.id.g01, false);
//        menu.setGroupVisible(R.id.g02, false);
        //menu.add(Menu.NONE, MyActivity.MENU_ID, 0, "Your Title").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//        menu.findItem(R.id.Item07).setTitle("碼表");

        //20210118 S--------------------------
//        menu.add(0, 0, 0, "導航").setIcon(android.R.drawable.ic_media_play);
//        menu.add(0, 1, 0, "碼表").setIcon(android.R.drawable.ic_media_play);
//        menu.add(0, 2, 0, "開啟導航").setIcon(android.R.drawable.ic_media_play);
//        menu.add(0, 3, 0, "歷史紀錄").setIcon(android.R.drawable.ic_media_play);
//        menu.add(0, 4, 0, "轉換經緯度").setIcon(android.R.drawable.ic_media_play);
//        menu.add(0, 5, 0, "清除歷史紀錄").setIcon(android.R.drawable.ic_media_play);
//        menu.add(0, 6, 0, "關閉歷史紀錄").setIcon(android.R.drawable.ic_media_play);
        //20210118 O--------------------------
        //20210118 關
//        menu.add(0, 1, 0, "碼表").setIcon(android.R.drawable.ic_media_play);
//        menu.add(0, 2, 0, "規劃路線").setIcon(android.R.drawable.ic_media_play);
//        menu.add(0, 7, 0, "切換顯示隱藏").setIcon(android.R.drawable.ic_media_play);

//        menu.add(0, 8, 0, "路徑").setIcon(android.R.drawable.ic_media_play); //20210211adds
//        menu.add(0, 9, 0, "顯示資料").setIcon(android.R.drawable.ic_media_play); //20210214adds

//        menu.add(0, 10, 0, "開啟軌跡圖").setIcon(android.R.drawable.ic_media_play); //20210226adds menu_trace_on 開啟軌跡圖
//        menu.add(0, 11, 0, "關閉軌跡圖").setIcon(android.R.drawable.ic_media_play); //20210226adds menu_trace_off 關閉軌跡圖
//        menu.add(0, 12, 0, "清除軌跡圖").setIcon(android.R.drawable.ic_media_play); //20210226adds menu_trace_clear 清除軌跡圖


        return true;  //返回值為“true”,表示菜單可見，即顯示菜單
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_trace_on:  //開啟軌跡圖
                tracesel = 1;
                m_traceon.setVisible(false);
                m_traceoff.setVisible(true);
                break;
            case R.id.menu_trace_off:  //關閉軌跡圖
                tracesel = 0;
                m_traceon.setVisible(true);
                m_traceoff.setVisible(false);
                break;
            case R.id.menu_trace_clear:  //清除軌跡圖
                if (map != null) map.clear();
                if (arrayList != null) arrayList.clear();
                if (arrayListgpx != null) arrayListgpx.clear();
                if (mytraceGpx != null) mytraceGpx.clear();
                break;
            case R.id.action_settings:
                this.finish();
                break;
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
//            case 1: //切換至碼表頁面
//                backbtn.callOnClick();
//                break;
//            case 2: //切換至google map
//                time_map_check2.callOnClick();
//                break;
            case R.id.menu_history_on: //歷史紀錄  3
                u_dblist();
                m_historyon.setVisible(false);
                m_historyoff.setVisible(true);
                break;
//            case 4: //轉換經緯度
//                transfer.callOnClick();
//                break;
            case R.id.menu_history_clear: //清除歷史紀錄  5
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
                mLoginDlg = new Dialog(Biker_time_map_plan.this);
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
            case R.id.menu_history_off: //關閉歷史紀錄 6
                output_history.setText("");
                m_historyon.setVisible(true);
                m_historyoff.setVisible(false);
                break;
            //===========================//解析GPX
            case R.id.menu_map_gpx: //解析GPX
                Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Download");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
                startActivityForResult(intent, 0);
                break;
            //===========================//解析GPX

//            case 7: //切換顯示隱藏使用者
//                selectBtn.callOnClick();
//                break;
            //20210211adds
//            case 8: //路徑
//                intent.setClass(Biker_time_map_plan.this, MainActivity.class);
//                startActivity(intent);
//                break;
            //20210211addo
            //20210214adds
            case R.id.menu_memberlist:  //顯示團員位置 顯示資料  9
                friendLocation=1;
                m_memberlist_no.setVisible(true);
                m_memberlist.setVisible(false);

                handler.post(friend_Location);

//                dbmysqlFriendLocation();
                break;
            case R.id.menu_memberlist_no:
                friendLocation=0;
                handler.removeCallbacks(friend_Location);
                m_memberlist_no.setVisible(false);
                m_memberlist.setVisible(true);
                if (friendMarker != null) {
                    for (int j = 0; j < friendMarker.length; j++) {
                        if(friendMarker[j]!=null) friendMarker[j].remove();
                    }
                }
                break;
            //20210214addo

//            case 0: //切換至導航頁面
//                intent.setClass(Biker_time_map.this, Biker_time_map.class);
//                startActivity(intent);
//                break;
//            //20210118 O--------------------------
//            case 10:  //開啟軌跡圖
//                tracesel = 1;
//                menu.removeItem(10);
//                menu.add(0, 11, 0, "關閉軌跡圖").setIcon(android.R.drawable.ic_media_play); //20210226adds menu_trace_off 關閉軌跡圖
////                m_traceon.setVisible(false);
////                m_traceoff.setVisible(true);
//                break;
//            case 11:  //關閉軌跡圖
//                tracesel = 0;
//                menu.removeItem(11);
//                menu.add(0, 10, 0, "開啟軌跡圖").setIcon(android.R.drawable.ic_media_play); //20210226adds menu_trace_on 開啟軌跡圖
////                m_traceon.setVisible(true);
////                m_traceoff.setVisible(false);
//                break;
//            case 12:  //清除軌跡圖
//                arrayList.clear();
////                mytrace.clear();
//                map.clear();
////                showloc();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private Runnable friend_Location=new Runnable() {
        @Override
        public void run() {
            dbmysqlFriendLocation();


//            Toast.makeText(getApplicationContext(),"XXX",Toast.LENGTH_LONG).show();
            handler.postDelayed(friend_Location,10000);
        }
    };
    @Override
    public void onInfoWindowClick(Marker marker) {
//        Toast.makeText(getApplicationContext(), "onInfoWindowClick", Toast.LENGTH_LONG).show();
        Findroutes(new LatLng(lat_I, lng_I), marker.getPosition());
        LatLng abc = marker.getPosition();
        int a = 0;

    }

// ===================main class end ===========================

    //20210125O---------------------------
    //===========自訂義Infowindow class===========
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
            infoWindow.setAlpha(0.8f);
// ----------------------------------------------
// 顯示地標title
            TextView title = ((TextView) infoWindow.findViewById(R.id.title));
            String[] ss = marker.getTitle().split("#"); //ex.台中火車站#圖片號碼
            title.setText(ss[0]);
// 顯示地標snippet
            TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
            snippet.setText(marker.getSnippet());

// //--test
// TextView tel = ((TextView) infoWindow.findViewById(R.id.tel));
// TextView addr = ((TextView) infoWindow.findViewById(R.id.addr));
// tel.setText(ss[2]);
// addr.setText(ss[3]);
// 顯示圖片
//            ImageView imageview = ((ImageView) infoWindow.findViewById(R.id.content_ico));
//            imageview.setImageResource(Integer.parseInt(ss[1]));

            return infoWindow;
        }


        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

    }


}
