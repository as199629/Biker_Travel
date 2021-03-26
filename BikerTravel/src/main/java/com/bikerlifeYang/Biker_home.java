    package com.bikerlifeYang;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Biker_home extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private List<String> permissionsList=new ArrayList<>();
    private int DBversion;//版本號
    private static final String[] permissionarray = new String[]
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };

    private static final int RC_SIGN_IN = 9001;
    //SQLite
    private static final String DB_FILE = "bikerlife.db";
//    private static final String DB_TABLE = "home";

    private static final String DB_TABLE_I100 = "I100";
    private String U_ID;

    private ListView routelistLYT;
    private int jsonArray1_length;
    private Uri uri;
    private Intent intent=new Intent();
    private String[] Map_url;
    private String[] Website_url;
    private Uri User_IMAGE;
    private CircleImgView img;
    private Button b002;
    private GoogleSignInClient mGoogleSignInClient;
    private Uri noiconimg;
    private Menu menu;
    private MenuItem m_logout;
    private MenuItem m_login;
    private MenuItem m_register;
    private MenuItem m_forgetpad;
    private MenuItem m_action;
    private Dialog AlertDig;
    private Button alertBtnOK;
    private Button alertBtnCancel;
    private TextView Dig_tarin_waring;
    private TextView Dig_train_title;
    private GoogleSignInAccount account;
    private String TAG="tcnr2902";
    private FriendDbHelper29h dbHper;
    private ArrayList<String> recSet_list;
    private List<Map<String, Object>> mList;
    private LinearLayout LL_goprofileLYT,LL_gotrainLYT,LL_goplanLYT,LL_gofindLYT
            ,LL_gomapLYT,LL_gostopwatchLYT,LL_gojoinLYT,LL_gorawardLYT;
    private int a;
    private Button go_loginLYT;
    private MenuItem m_registered;
    private TextView TextView00;
    private Button RecCountTEST;
    private String m_Respone="";
    private ProgressDialog progDlg;
    private String sqlctl;
    private String ser_msg;
    private ArrayList<String>  addname_opda;
    private Geocoder geocoder;
    private double list_latitude;
    private double list_longitude;

    //天氣
    public static String BaseUrl = "https://api.openweathermap.org/";
    public static String AppId = "fdc4017e4b347f6fb6c30881430e2e20";
    public static String lat = "24.170768202109116";
    public static String lon = "120.61011226844714";
    public static String lang = "zh_tw";
    private String iconurl;
    private TextView weatherLat;
    private TextView weatherLon;
    private TextView weatherData;
    private TextView weatherPic;
    private Button b001;
    private ImageView weatherimg;
    private TextView weatherName;
    private String list_city;
    private ProgressDialog pd;
    private Dialog weatherDlg;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout laySwipe;
    private int nowposition;
    private ViewPager mViewPager;
    private TextView route_textLYT;
    private TextView attractions_textLYT;
    private int TABLE_ID=2;//預設是
    private Biker_home_RecyclerAdapter adapter;
    private  ArrayList<Post> mData =new ArrayList<>();
    private  ArrayList<Post> mData_route= new ArrayList<>();
    private TextView route_text2LYT;
    private TextView attractions_text2LYT;
    public static int phone_height_img;
    public static int phone_width_img;
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;
    private String list_Add;
    private String home_Add="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkRequiredPermission(this);
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_home);
        setUpViewComponent();
        DBversion = Integer.parseInt(getString(R.string.SQLite_version));
        initDB();
    }

    public static void enableStrictMode(Context context) {
        StrictMode.setThreadPolicy(
//                -------------抓取遠端資料庫設定執行續------------------------------
                new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()
                        .penaltyLog()
                        .build());
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .penaltyLog()
                        .build());
    }



    private void setUpViewComponent() {
        // 動態調整高度 抓取使用裝置尺寸
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newscrollheight = displayMetrics.heightPixels * 60 / 100; // 設定ScrollView使用尺寸的4/5
        phone_height_img=displayMetrics.heightPixels* 25 / 100;
        phone_width_img=displayMetrics.widthPixels* 25 / 100;

        //
        routelistLYT = (ListView) findViewById(R.id.route_list);
        routelistLYT.getLayoutParams().height = newscrollheight;

        routelistLYT.setLayoutParams(routelistLYT.getLayoutParams()); // 重定ScrollView大小

        //--google 登入
        validateServerClientID();
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        go_loginLYT=(Button)findViewById(R.id.go_login);
        b002=(Button)findViewById(R.id.b002);

        LL_goprofileLYT=(LinearLayout)findViewById(R.id.LL_goprofile);
        LL_gotrainLYT=(LinearLayout)findViewById(R.id.LL_gotrain);
        LL_goplanLYT=(LinearLayout)findViewById(R.id.LL_goplan);
        LL_gofindLYT=(LinearLayout)findViewById(R.id.LL_gofind);

        LL_gomapLYT=(LinearLayout)findViewById(R.id.LL_gomap);
        LL_gostopwatchLYT=(LinearLayout)findViewById(R.id.LL_gostopwatch);
        LL_gojoinLYT=(LinearLayout)findViewById(R.id.LL_goraward);
        LL_gorawardLYT=(LinearLayout)findViewById(R.id.LL_gojoin);


        route_textLYT=(TextView)findViewById(R.id.route_text);
        attractions_textLYT=(TextView)findViewById(R.id.attractions_text);
        route_text2LYT=(TextView)findViewById(R.id.route_text2);
        attractions_text2LYT=(TextView)findViewById(R.id.attractions_text2);

        go_loginLYT.setOnClickListener(this);
        b002.setOnClickListener(this);
        LL_goprofileLYT.setOnClickListener(this);
        LL_gotrainLYT.setOnClickListener(this);
        LL_goplanLYT.setOnClickListener(this);
        LL_gofindLYT.setOnClickListener(this);
        LL_gomapLYT.setOnClickListener(this);
        LL_gostopwatchLYT.setOnClickListener(this);
        LL_gojoinLYT.setOnClickListener(this);
        LL_gorawardLYT.setOnClickListener(this);
        attractions_textLYT.setOnClickListener(this);
        route_textLYT.setOnClickListener(this);
        attractions_text2LYT.setOnClickListener(this);
        route_text2LYT.setOnClickListener(this);


        //TextView測試用按鈕
        TextView00 = (TextView) findViewById(R.id.TextView);
        TextView00.setOnClickListener(this);
        RecCountTEST=(Button)findViewById(R.id.RecCountTEST);
        RecCountTEST.setOnClickListener(this);

        // 建立Geocoder物件
        geocoder  =new Geocoder(this,Locale.TAIWAN);//地址轉換函數轉換哪個國家的


        //recyclerView
//        li01 = (LinearLayout) findViewById(R.id.li01);
//        li01.setVisibility(View.GONE);//平時不顯示
//        mTxtResult = findViewById(R.id.m2206_name);
//        mDesc = findViewById(R.id.m2206_descr);
        //textview 滑動回到最左上角
//        mDesc.setMovementMethod(ScrollingMovementMethod.getInstance());//可以滑動
//        mDesc.scrollTo(0, 0);//textview 回頂端
        recyclerView = findViewById(R.id.recyclerView);

//        t_count = findViewById(R.id.count);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //滑動recyclerView 做什麼
//                li01.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(),"XX",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        //--------------設定下載中-----------
//        u_loading = (TextView)findViewById(R.id.u_loading);//白底紅字那條
//        u_loading.setVisibility(View.GONE);
        //-------------------------------------
        //下面那塊

        laySwipe = (SwipeRefreshLayout)findViewById(R.id.laySwipe);
        laySwipe.setVisibility(View.INVISIBLE);
        laySwipe.setOnRefreshListener(onSwipeToRefresh);//下拉的監聽
        laySwipe.setSize(SwipeRefreshLayout.LARGE);//圈圈的大小
        // 設置下拉多少距離之後開始刷新數據
        laySwipe.setDistanceToTriggerSync(100);
        // 設置進度條背景顏色
        laySwipe.setProgressBackgroundColorSchemeColor(getColor(android.R.color.background_light));
        // 設置刷新動畫的顏色，可以設置1或者更多
        laySwipe.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_purple,
                android.R.color.holo_orange_dark);

/*        setProgressViewOffset : 設置進度圓圈的偏移量。
        第一個參數表示進度圈是否縮放，
        第二個參數表示進度圈開始出現時距頂端的偏移，
        第三個參數表示進度圈拉到最大時距頂端的偏移。*/
        laySwipe.setProgressViewOffset(true, 0, 50);
        laySwipe.getLayoutParams().height=newscrollheight;
//=====================
//        onSwipeToRefresh.onRefresh();  //開始轉圈下載資料
        //-------------------------
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.go_login:
                intent.setClass(Biker_home.this, Biker_login.class);
                startActivity(intent);
                save_uid();//儲存ID目前為0
                save_gpx_id();//儲存gpx流水號
                break;
            case R.id.TextView:


//                dbHper.RecCount();
                break;
            case R.id.RecCountTEST:
//            Biker_login aww = Biker_login.enableStrictMode();
//            dbmysql_A100();
                dbHper.RecCountTEST();
                break;
            case R.id.attractions_text:
                TABLE_ID=1;
                onSwipeToRefresh.onRefresh();  //開始轉圈下載資料
//                attractions_textLYT.setVisibility(View.GONE);
//                attractions_text2LYT.setVisibility(View.VISIBLE);
                break;
            case R.id.route_text:
                TABLE_ID=2;
                onSwipeToRefresh.onRefresh();  //開始轉圈下載資料
//                route_textLYT.setVisibility(View.GONE);
//                route_text2LYT.setVisibility(View.VISIBLE);
                break;
            case R.id.attractions_text2:
                TABLE_ID=1;
                adapter = new Biker_home_RecyclerAdapter(TABLE_ID,this, mData);
                adapter_click_setAdap();
                break;
            case R.id.route_text2:
                TABLE_ID=2;
                adapter = new Biker_home_RecyclerAdapter(TABLE_ID,this, mData_route);
                adapter_click_setAdap();
                break;

        }

        if(a!=0)
        {
            switch (v.getId())
            {
                case R.id.b002:
                    signOut();
                    break;
                case R.id.LL_goprofile:
                    intent.setClass(Biker_home.this,Biker_profile.class);
                    startActivity(intent);
                    break;
                case R.id.LL_gotrain:
                    //------------取得USER_ID-------------------
                    SharedPreferences xxx=getSharedPreferences("USER_ID",0);
                    U_ID=xxx.getString("USER_ID","");

                    FriendDbHelper23 dbHper23 = null;
                    if (dbHper23 == null) {
                        dbHper23 = new FriendDbHelper23(this, DB_FILE, null, DBversion);
                    }
                    dbHper23.FindRec(U_ID);

                    //讀取SQLite資料表
                    SQLiteDatabase database= dbHper23.getReadableDatabase();
                    Cursor I100 = database.query(       //目標的資料庫
                            true,
                            DB_TABLE_I100,
                            new String[]{"I103", "I104", "I105","I106","I107"},//無意義
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
                    if (I100 == null || I100.getCount() == 0){       //沒有資料表的話就去設定頁面
                        intent.setClass(Biker_home.this, Biker_train1.class);
                        startActivity(intent);
                    }
                    else{
                        intent.setClass(Biker_home.this, Biker_train.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.LL_goplan:
                    intent.setClass(Biker_home.this,Bilerlife_plan.class);
                    startActivity(intent);
                    break;
                case R.id.LL_gofind:
                    intent.setClass(Biker_home.this,MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.LL_gomap:
                    intent.setClass(Biker_home.this,Biker_time_map_plan.class);
                    startActivity(intent);
                    break;


                case R.id.LL_gojoin:
                    intent.setClass(Biker_home.this,Biker_Join.class);
                    startActivity(intent);
                    break;
                case R.id.LL_goraward:
                    intent.setClass(Biker_home.this,Biker_reward.class);
                    startActivity(intent);
                    break;
                case R.id.LL_gostopwatch:
                    intent.setClass(Biker_home.this, Biker_time_Stopwatch.class);
                    startActivity(intent);

                    break;

            }
        }
        else
        {
            switch (v.getId())
            {
                case R.id.LL_goprofile:
                case R.id.LL_gotrain:
                case R.id.LL_goplan:
                case R.id.LL_gofind:
                case R.id.LL_gomap:
                case R.id.LL_goraward:
                case R.id.LL_gojoin:
                case R.id.LL_gostopwatch:

                    AlertDig = new Dialog(Biker_home.this);

                    AlertDig.setCancelable(false);//不能按其他地方
                    AlertDig.setContentView(R.layout.alert_dialog);//選擇layout

                    alertBtnOK = (Button)AlertDig.findViewById(R.id.train_btnOK);
                    alertBtnCancel = (Button)AlertDig.findViewById(R.id.train_btnCancel);
                    Dig_tarin_waring = (TextView) AlertDig.findViewById(R.id.tarin_waring);
                    Dig_train_title=(TextView)AlertDig.findViewById(R.id.train_title);
                    Dig_train_title.setText("說明");
                    Dig_tarin_waring.setText("此功能必須先登入才能使用");
                    alertBtnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            go_loginLYT.callOnClick();

                            AlertDig.cancel();
                        }
                    });
                    alertBtnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDig.cancel();
                        }
                    });
                    AlertDig.show();

//                    Toast.makeText(getApplicationContext(),"請先登入",Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    }
    private final SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh=
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //不小心拉到 要不要重讀
                    //-------------------------------------
//                    mTxtResult.setText("");
                    MyAlertDialog myAltDlg = new MyAlertDialog(Biker_home.this);
                    myAltDlg.setTitle(getString(R.string.home_dialog_title));
                    if(TABLE_ID==2)
                        myAltDlg.setTitle(getString(R.string.home_table_title_route));

                    myAltDlg.setMessage(getString(R.string.home_dialog_t001) + getString(R.string.home_dialog_b001));
                    myAltDlg.setIcon(android.R.drawable.ic_menu_rotate);
                    myAltDlg.setCancelable(false);
                    myAltDlg.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.home_dialog_positive), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TextView select_text=(TextView)findViewById(R.id.select_text);
                            select_text.setVisibility(View.GONE);
                            laySwipe.setVisibility(View.VISIBLE);
                            show_ProgDlg();
                            //-----------------開始執行下載----------------
                            laySwipe.setRefreshing(true);//setRefreshing(false) 進圖條取消後則消失。
//                            u_loading.setVisibility(View.VISIBLE);
//                            mTxtResult.setText(getString(R.string.m2206_name) + "");
//                            mDesc.setText("");
//                            mDesc.scrollTo(0, 0);//textview 回頂端
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //                    =================================
                                    setDatatolist();
                                    //                  =================================
                                    //----------SwipeLayout 結束 --------
                                    //可改放到最終位置 u_importopendata()
//                                    u_loading.setVisibility(View.GONE);
                                    laySwipe.setRefreshing(false);
                                    Toast.makeText(getApplicationContext(), getString(R.string.home_loadover), Toast.LENGTH_SHORT).show();
                                    if(TABLE_ID==1)
                                    {
                                        attractions_textLYT.setVisibility(View.GONE);
                                        attractions_text2LYT.setVisibility(View.VISIBLE);
                                    }
                                    else if(TABLE_ID==2)
                                    {
                                        route_textLYT.setVisibility(View.GONE);
                                        route_text2LYT.setVisibility(View.VISIBLE);
                                    }
                                    hander.sendEmptyMessage(1); // 下載完成後發送處理消息
                                }
                            }, 1000);  //10秒
                        }
                    });
                    //不要重讀
                    myAltDlg.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.home_dialog_neutral), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(TABLE_ID==1)
                            {
                                TABLE_ID=2;
                            }
                            else if(TABLE_ID==2)
                            {
                                TABLE_ID=1;
                            }
                            //取消 不小心案到重讀
//                            u_loading.setVisibility(View.GONE);
                            laySwipe.setRefreshing(false);
                        }
                    });
                    myAltDlg.show();
//------------------------------------
//                    Toast.makeText(getApplicationContext(),"XX",Toast.LENGTH_SHORT).show();
                }
            };
    private void show_ProgDlg() {
        progDlg = new ProgressDialog(this);
        progDlg.setTitle("請稍後");
        progDlg.setMessage("載入資料中");
        progDlg.setIcon(android.R.drawable.presence_away);
        progDlg.setCancelable(false);
        progDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDlg.setMax(100);
        progDlg.show();
    }

    private void setDatatolist() {
        //==================================
        u_importopendata();  //下載Opendata
//        u_importopendata_biker();
        //抓mysql部分
        //==================================
        //設定Adapter to 任何的 view recyleview scrollview 等等


        for (Map<String, Object> m : mList) {
            if (m != null) {

                if(TABLE_ID==1)
                {
                    //=================觀光景點=====================
                String Name = m.get("Name").toString().trim(); //名稱
                String Add = m.get("Add").toString().trim(); //住址
                String Picture1 = m.get("Picture1").toString().trim(); //圖片
                if (Picture1.isEmpty() || Picture1.length() < 1) { //沒有圖片的
                    Picture1 = "https://bklifetw.com/img/nopic1.jpg";
                }
                String Description = m.get("Description").toString().trim(); //描述
                String Zipcode = m.get("Zipcode").toString().trim(); //描述

                String Latitude = m.get("Latitude").toString().trim();;//緯度
                String Longitude= m.get("Longitude").toString().trim();;//經度
                String Ticketinfo= m.get("Ticketinfo").toString().trim();//票價訊息
                if (Ticketinfo.isEmpty() || Ticketinfo.length() < 1) { //沒有寫的
                    Ticketinfo = "無";
                }
                String Opentime= m.get("Opentime").toString().trim();;//開放時間
                if (Opentime.isEmpty() || Opentime.length() < 1) { //沒有寫的
                    Opentime = "無";
                }


                String Picdescribe1= m.get("Picdescribe1").toString().trim();;//圖片說明
                String Tel= m.get("Tel").toString().trim();;//電話


                mData.add(new Post(Name, Picture1, Add, Description, Zipcode,
                        Latitude,Longitude,Ticketinfo,Opentime,Picdescribe1,Tel));
                    //=================觀光景點=====================
                }
                else if(TABLE_ID==2)
                {
                    String Name =m.get("Name").toString().trim();
                    String S_PlaceDes = m.get("S_PlaceDes").toString().trim();
                    String Bike_length = m.get("Bike_length").toString().trim();
                    String Toldescribe = m.get("Toldescribe").toString().trim();
                    String Add=m.get("Add").toString().trim();


                    mData_route.add(new Post(Name, S_PlaceDes, Bike_length, Toldescribe,Add));
                }

            } else {
                return;
            }
        }

        if(TABLE_ID==1)
        {
            adapter = new Biker_home_RecyclerAdapter(TABLE_ID,this, mData);
        }
        else if(TABLE_ID==2)
        {
            adapter = new Biker_home_RecyclerAdapter(TABLE_ID,this, mData_route);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
// ************************************
        adapter_click_setAdap();
    }
    //太多重複 所以 寫方法
    private void adapter_click_setAdap()
    {
        //案到哪比 顯示
        adapter.setOnItemClickListener(new Biker_home_RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                li01.setVisibility(View.VISIBLE);
//                Toast.makeText(M2205.this, "onclick" + mData.get(position).hotelName.toString(), Toast.LENGTH_SHORT).show();
//                mTxtResult.setText(getString(R.string.m2206_name) + mData.get(position).Name);
//                mDesc.setText(mData.get(position).Content);
//                mDesc.scrollTo(0, 0); //textview 回頂端
                nowposition = position;
//                mData.clear();
//                Toast.makeText(getApplicationContext(),nowposition+"",Toast.LENGTH_SHORT).show();
//                t_count.setText(getString(R.string.ncount) + total + "/" + t_total + "   (" + (nowposition + 1) + ")");
                if(isFastClick())
                {
                    show_weather(nowposition);//秀天氣
                }
            }
        });
//********************************* ****
        recyclerView.setAdapter(adapter);
    }
    //防止連點
    public static boolean isFastClick() {
        boolean flag = false;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME ) {
            flag = true;//大於1000時間 true
        }
        lastClickTime = currentClickTime;
        return flag;
    }


    private void show_weather(int position) {
        //把地名轉成座標
        if(TABLE_ID==1)
        {
            list_city=mData.get(position).Name;
            list_latitude = Double.parseDouble(mData.get(position).Latitude);
            list_longitude = Double.parseDouble(mData.get(position).Longitude);
            home_Add=mData.get(position).Add;
        }
        else if(TABLE_ID==2)
        {
            list_city=mData_route.get(position).Name;
//            address_to_location(list_city);
            address_to_location(mData_route.get(position).Add);
            home_Add=mData_route.get(position).Add;
//            String aaa="";
        }

//                            String aa=location_to_address(list_latitude,list_longitude);
        weatherDlg = new Dialog(Biker_home.this);
        weatherDlg.setTitle("test");
        weatherDlg.setCancelable(true);
        weatherDlg.setContentView(R.layout.home_weather_dlg);
        weatherLat = (TextView)weatherDlg.findViewById(R.id.weather_lat);
        weatherLon = (TextView)weatherDlg.findViewById(R.id.weather_lon);
        weatherData = (TextView)weatherDlg.findViewById(R.id.weather_status);
        weatherPic = (TextView)weatherDlg.findViewById(R.id.show_pic);
        b001 = (Button)weatherDlg.findViewById(R.id.button);
        Button gomap=(Button)weatherDlg.findViewById(R.id.home_dlg_go_map);
        gomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //=====================下面留著用
                if(a!=0)
                {
                    Intent it = new Intent();
                    it.setClass(Biker_home.this, Biker_time_map_plan.class);

                    Bundle bundle = new Bundle();

                    bundle.putString("home_Add", home_Add);

                    it.putExtras(bundle);

                    startActivity(it);
                }
                else
                {
                    AlertDig = new Dialog(Biker_home.this);

                    AlertDig.setCancelable(false);//不能按其他地方
                    AlertDig.setContentView(R.layout.alert_dialog);//選擇layout

                    alertBtnOK = (Button)AlertDig.findViewById(R.id.train_btnOK);
                    alertBtnCancel = (Button)AlertDig.findViewById(R.id.train_btnCancel);
                    Dig_tarin_waring = (TextView) AlertDig.findViewById(R.id.tarin_waring);
                    Dig_train_title=(TextView)AlertDig.findViewById(R.id.train_title);
                    Dig_train_title.setText("說明");
                    Dig_tarin_waring.setText("此功能必須先登入才能使用");
                    alertBtnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            go_loginLYT.callOnClick();

                            AlertDig.cancel();
                        }
                    });
                    alertBtnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDig.cancel();
                        }
                    });
                    AlertDig.show();
                }


            }
        });

        Button close = (Button) weatherDlg.findViewById(R.id.home_dlg_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherDlg.cancel();
//                Intent it = new Intent();
//                it.setClass(Biker_home.this, M1908open.class);
//
//                Bundle bundle = new Bundle();
//                String lat[] =new String[mData.size()];
//                String lon[] =new String[mData.size()];
//                String url[] =new String[mData.size()];
//                for(int i=0;i<mData.size();i++)
//                {
//                    lat[i]=mData.get(i).Latitude;
//                    lon[i]=mData.get(i).Longitude;
//                    url[i]=mData.get(i).posterThumbnailUrl;
//                }
//
//                bundle.putStringArray("Array_lat", lat);
//                bundle.putStringArray("Array_lon", lon);
//                bundle.putStringArray("Array_url",url);
//
//                it.putExtras(bundle);
//
//                startActivity(it);
            }
        });
        b001.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"bb",Toast.LENGTH_SHORT).show();
            }
        });
        weatherimg = (ImageView)weatherDlg.findViewById(R.id.weather_img);
        weatherName = (TextView)weatherDlg.findViewById(R.id.weather_name);

        getCurrentData();
    }


    private void u_importopendata() { //下載Opendata
        try {
            String Task_opendata="";
            if(TABLE_ID==1)
            {
                Task_opendata
                        = new TransTask().execute("https://gis.taiwan.net.tw/XMLReleaseALL_public/scenic_spot_C_f.json").get();   //旅館民宿 - 觀 光資訊資料庫
            }
            else if(TABLE_ID==2)
            {
                Task_opendata
                        = new TransTask().execute("https://gis.taiwan.net.tw/XMLReleaseALL_public/Bike_f.json").get();   //車道路線
            }


//-------解析 json   帶有多層結構-------------
            mList = new ArrayList<Map<String, Object>>();
            JSONObject json_obj1 = new JSONObject(Task_opendata);
            JSONObject json_obj2 = json_obj1.getJSONObject("XML_Head");
            JSONObject infos = json_obj2.getJSONObject("Infos");
            JSONArray info = infos.getJSONArray("Info");
//            total = 0;
//            t_total = info.length(); //總筆數
//------JSON 排序----------------------------------------
            if(TABLE_ID==1)
              info = sortJsonArray(info);
//            total = info.length(); //有效筆數
//            t_count.setText(getString(R.string.ncount) + total + "/" + t_total);
//----------------------------------------------------------
            //-----開始逐筆轉換-----
//            total = info.length();
//            t_count.setText(getString(R.string.ncount) + total);

            for (int i = 0; i < info.length(); i++) {
                Map<String, Object> item = new HashMap<String, Object>();

                if(TABLE_ID==1)
                {
                    //=========================觀光景點=================================
                    String Name = info.getJSONObject(i).getString("Name");
                    String Description;
                    if(info.getJSONObject(i).getString("Toldescribe").trim()==info.getJSONObject(i).getString("Description"))
                    {
                        Description = info.getJSONObject(i).getString("Toldescribe");
                    }
                    else
                    {
                        Description = info.getJSONObject(i).getString("Toldescribe")+info.getJSONObject(i).getString("Description");
                    }
    //                String Description = info.getJSONObject(i).getString("Description");
                    String Add = info.getJSONObject(i).getString("Add");//地址
                    String Picture1 = info.getJSONObject(i).getString("Picture1");//圖片
                    String Zipcode = info.getJSONObject(i).getString("Zipcode"); //郵遞區號,
                    String Latitude =info.getJSONObject(i).getString("Py");//緯度
                    String Longitude=info.getJSONObject(i).getString("Px");//經度
                    String Ticketinfo=info.getJSONObject(i).getString("Ticketinfo");//票價訊息
                    String Opentime=info.getJSONObject(i).getString("Opentime");//開放時間
                    String Picdescribe1=info.getJSONObject(i).getString("Picdescribe1");//圖片說明
                    String Tel=info.getJSONObject(i).getString("Tel");//電話


                    item.put("Name", Name);
                    item.put("Description", Description);
                    item.put("Add", Add);
                    item.put("Picture1", Picture1);
                    item.put("Zipcode", Zipcode);

                    item.put("Latitude", Latitude);
                    item.put("Longitude", Longitude);
                    item.put("Ticketinfo", Ticketinfo);
                    item.put("Opentime", Opentime);
                    item.put("Picdescribe1", Picdescribe1);
                    item.put("Tel", Tel);


                    //=========================觀光景點=================================
                }
                else if(TABLE_ID==2)
                {
                    //==========================腳踏車路線===============================

                    String Name = info.getJSONObject(i).getString("Name");
                    String S_PlaceDes = info.getJSONObject(i).getString("S_PlaceDes");
                    String Bike_length = info.getJSONObject(i).getString("Bike_length") + "公尺";
                    String Toldescribe = info.getJSONObject(i).getString("Toldescribe");
                    String Add = info.getJSONObject(i).getString("Add");

                    item.put("Name", Name);
                    item.put("S_PlaceDes", S_PlaceDes);
                    item.put("Bike_length", Bike_length);
                    item.put("Toldescribe", Toldescribe);
                    item.put("Add",Add);
                    //===============================================================
                }

                mList.add(item);
//-------------------
            }
//            t_count.setText(getString(R.string.ncount) + total + "/" + t_total);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//----------SwipeLayout 結束 --------
    }

    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in google_sign.xml, must end with " + suffix;

            Log.w(TAG, message);
//            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //--START_EXCLUDE--
                        updateUI(null);
                        // [END_EXCLUDE]
                        img.setImageResource(R.drawable.home_user); //還原圖示
                    }
                });
    }

    private Handler hander=new Handler()
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case  0:
                        //---設定ListView
                        SimpleAdapter adapter = new SimpleAdapter(
                                Biker_home.this, mList, R.layout.home_routelist,
                                new String[]{"H101", "H102", "H103", "H104"},
                                new int[]{R.id.route_Name, R.id.route_S_PlaceDes, R.id.route_Bike_length, R.id.route_Toldescribe});
                        checkRequiredPermission(Biker_home.this);
                        routelistLYT.setAdapter(adapter);
                        routelistLYT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //把地名轉成座標
                            list_city=addname_opda.get(position);
                            address_to_location(list_city);
//                            String aa=location_to_address(list_latitude,list_longitude);
                            weatherDlg = new Dialog(Biker_home.this);
                            weatherDlg.setTitle("test");
                            weatherDlg.setCancelable(false);
                            weatherDlg.setContentView(R.layout.home_weather_dlg);
                            weatherLat = (TextView)weatherDlg.findViewById(R.id.weather_lat);
                            weatherLon = (TextView)weatherDlg.findViewById(R.id.weather_lon);
                            weatherData = (TextView)weatherDlg.findViewById(R.id.weather_status);
                            weatherPic = (TextView)weatherDlg.findViewById(R.id.show_pic);
                            b001 = (Button)weatherDlg.findViewById(R.id.button);
                            Button close = (Button) weatherDlg.findViewById(R.id.home_dlg_close);
                            close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    weatherDlg.cancel();
                                }
                            });
                            b001.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getApplicationContext(),"bb",Toast.LENGTH_SHORT).show();
                                }
                            });
                            weatherimg = (ImageView)weatherDlg.findViewById(R.id.weather_img);
                            weatherName = (TextView)weatherDlg.findViewById(R.id.weather_name);
//                            Button loginBtnOK = (Button) mLoginDlg.findViewById(R.id.m0905_btnOK);
//                            Button loginBtnCancel = (Button) mLoginDlg.findViewById(R.id.m0905_btnCancel);
//                            loginBtnOK.setOnClickListener(loginDlgBtnOKOnClkLis);
//                            loginBtnCancel.setOnClickListener(loginDlgBtnCancelOnClkLis);

                            getCurrentData();

//                            Toast.makeText(getApplicationContext(),"aa",Toast.LENGTH_SHORT).show();

                        }
                    });
                    break;
                case 1:
                    progDlg.cancel();
                    break;
                default:
                    //其他想做的事情
                    break;
            }
        }
    };

    private void address_to_location(String name) {

        try {
            // 取得經緯度座標清單的List物件
            List<Address> listGPSAddress = geocoder.getFromLocationName(name, 1);
            // 有找到經緯度座標
            if (listGPSAddress != null) {

                list_latitude = listGPSAddress.get(0).getLatitude();
                list_longitude = listGPSAddress.get(0).getLongitude();
            }
        } catch (Exception ex) {
//            output.setText("錯誤:" + ex.toString());
        }
    }
    private String location_to_address(Double latitude,Double longitude)
    {
        try {
            // 取得地址清單的List物件
            List<Address> listAddress = geocoder.getFromLocation(latitude, longitude, 1);//maxResult 選幾個
            // 是否有取得地址
            if (listAddress != null){
                Address findAddress=listAddress.get(0);
//                // 建立StringBuilder物件
                StringBuilder strAddress=new StringBuilder();
                // 取得地址的內容
                return findAddress.getAddressLine(0);
//                 strAddress.append(str).append("\n");


                //---------------------------------------------------;
            }else{
//                output.setText("查無地址!");
            }

        }catch (Exception ex){
//            output.setText("錯誤:"+ex.toString());
        }
        return "查無地址!";
    }
    private void getCurrentData() {
        pd = new ProgressDialog(Biker_home.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Internet");
        pd.setMessage("Loading.........");
        pd.setIndeterminate(false);
        pd.show();
//***************************************************************
/*
Retrofit 是一套由 Square 所開發維護，將 RESTful API 寫法規範和模組化的函式庫。
底層也使用他們的 okHttp ，okHttp 用法參考 okHttp 章節。
Retrofit 預設回傳的處理器是現代化 API 最流行的 JSON，如果你要處理別的要另外實作 Converter。
如果需要實作 Server 驗證，建議做好另外接上 okHttpClient 去設 Interceptor。
在 Retrofit 1.9.0 的 Interceptor 中能做的有限。
*/
//***************************************************************
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);

//        retrofit2.Call<WeatherResponse> call = service.getCurrentWeatherData(lat, lon, lang, AppId);
        retrofit2.Call<WeatherResponse> call = service.getCurrentWeatherData(list_latitude+"", list_longitude+"", lang, AppId);

        call.enqueue(new retrofit2.Callback<WeatherResponse>() {
            @Override
            public void onResponse(retrofit2.Call<WeatherResponse> call, retrofit2.Response<WeatherResponse> response) {
                if (response.code() == 200) {
                    //********設定轉圈圈進度對話盒*****************************

                    weatherDlg.show();
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;
                    String stringBuilder =
                            getString(R.string.home_weather_name) +list_city+
                            "\n" +
//                            getString(R.string.areaname) + weatherResponse.name +
//                            "\n" +
                            getString(R.string.home_Temperature) +
// --------------- K°凱氏轉攝氏C°-------------------
                            (int) (Float.parseFloat("" + weatherResponse.main.temp) - 273.15) + "C°" +
                            "\n" +
                            getString(R.string.home_Temperature_Max) + (int) (Float.parseFloat("" + weatherResponse.main.temp_max) - 273.15) + "C°" +
                            "\n" +
                            getString(R.string.home_Temperature_Min) + (int) (Float.parseFloat("" + weatherResponse.main.temp_min) - 273.15) + "C°" +
                            "\n" +
                            getString(R.string.home_Humidity) +
                            weatherResponse.main.humidity +
                            "\n" +
                            getString(R.string.home_Pressure) +
                            weatherResponse.main.pressure;
                    weatherData.setText(stringBuilder); //描述
////====填入座標==============
                    weatherLat.setText(getString(R.string.home_weather_lat) + (list_latitude));
                    weatherLon.setText(getString(R.string.home_weather_lon) + (list_longitude));
                    //====轉換中文地名==============

//                    weatherName.setText(getString(R.string.weather_name) + tranlocationName(lat, lon));
//======抓取 Internet 圖片==================
                    int b_id = weatherResponse.weather.get(0).id;
                    String b_main = weatherResponse.weather.get(0).main;
                    String b_description = weatherResponse.weather.get(0).description;
                    String b_icon = weatherResponse.weather.get(0).icon;
                    iconurl = "https://openweathermap.org/img/wn/" + b_icon + "@2x.png";  //icon兩倍大
// iconurl = "https://openweathermap.org/img/wn/" + b_icon + "@2x.png";
// https://openweathermap.org/img/wn/50n@2x.png
                    int cc = 1;
                    String weather = "\n" +
                            getString(R.string.w_description) + b_description;
//                            "\n" ;
//                            getString(R.string.w_icon) +
//                            "\n" +
//                            iconurl;
//=========================
                    weatherData.append(weather);


/*+++++++++++++++++++++
+ 使用Picasso網路照片 +
+++++++++++++++++++++*/
////----------- implementation 'com.squareup.picasso:picasso:2.71828'
                    Picasso.get()
                            .load(iconurl)
                            .into(weatherimg);
                    pd.cancel();
//////-----------------------------------------------------------
// **********************************************************************************
                }
                else
                {
                    Toast.makeText(getApplicationContext(),getString(R.string.home_server_error),Toast.LENGTH_SHORT).show();

                }
            }


            @Override
            public void onFailure(retrofit2.Call<WeatherResponse> call, Throwable t) {

            }
        });


    }

    private void checkRequiredPermission(Biker_home biker_home)
    {
        for (String permission : permissionarray) {
            if (ContextCompat.checkSelfPermission(biker_home, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size()!=0) {
            ActivityCompat.requestPermissions(biker_home, permissionsList.toArray(new
                    String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        }

    }



    private void initDB()
    {
        if (dbHper == null)
        {
            dbHper = new FriendDbHelper29h(this, DB_FILE, null, DBversion);
        }
        recSet_list=dbHper.getRecSet_list();//0112多了這行  選擇所有資料
    }



    // 讀取A100MySQL 資料 寫進sqlite
    private void dbmysql_A100() {
        sqlctl = "SELECT * FROM A100 ORDER BY id ASC";
//        sqlctl = "SELECT * FROM A100 WHERE A101='100504042909700846019'";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            String result = DBConnector29G.executeQuery(nameValuePairs);
            //===========================================
            chk_httpstate();//檢查連接狀態
            //===============================

            /**************************************************************************
             * SQL 結果有多筆資料時使用JSONArray
             * 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            JSONArray jsonArray = new JSONArray(result);
            // -------------------------------------------------------
            if (jsonArray.length() > 0) { // MySQL 連結成功有資料
//--------------------------------------------------------
                FriendDbHelper29G dbHper_LOGIN = new FriendDbHelper29G(this, DB_FILE, null, DBversion);
                int rowsAffected = dbHper_LOGIN.clearRec();                 // 匯入前,刪除所有SQLite資料
                dbHper_LOGIN.close();
//--------------------------------------------------------
//                // 處理JASON 傳回來的每筆資料
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    ContentValues newRow = new ContentValues();
////                    // --(1) 自動取的欄位 --取出 jsonObject 每個欄位("key","value")-----------------------
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
                    newRow.put("id",jsonData.getString("id").toString());
                    newRow.put("A101",jsonData.getString("A101").toString());
                    newRow.put("A102",jsonData.getString("A102").toString());
                    newRow.put("A103",jsonData.getString("A103").toString());
                    newRow.put("A104",jsonData.getString("A104").toString());
                    newRow.put("A105",jsonData.getString("A105").toString());
                    newRow.put("A106",jsonData.getString("A106").toString());
                    newRow.put("A107",jsonData.getString("A107").toString());
                    newRow.put("A108",jsonData.getString("A108").toString());
                    // -------------------加入SQLite---------------------------------------
                    long rowID = dbHper.insertRec_A100(newRow);
//                        Toast.makeText(getApplicationContext(), "共匯入 " + Integer.toString(jsonArray.length()) + " 筆資料", Toast.LENGTH_SHORT).show();

                }
                // ---------------------------
            } else {
//                    Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
//            recSet = dbHper.getRecSet();  //重新載入SQLite
//            u_setspinner();
            // --------------------------------------------------------
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

    }

    private void chk_httpstate()
    {
        ////-------------------------------
        //存取類別成員 DBConnector01.httpstate 判定是否回應 200(連線要求成功)
        if (DBConnector29G.httpstate == 200) {
            ser_msg = "伺服器匯入資料(code:" + DBConnector29G.httpstate + ") ";
//            servermsgcolor = ContextCompat.getColor(this, R.color.Navy);
//                Toast.makeText(getBaseContext(), "由伺服器匯入資料 ",
//                        Toast.LENGTH_SHORT).show();


        } else {
            int checkcode = DBConnector29G.httpstate / 100;
            switch (checkcode) {
                case 1:
                    ser_msg = "資訊回應(code:" + DBConnector29G.httpstate + ") ";
                    break;
                case 2:
                    ser_msg = "已經完成由伺服器會入資料(code:" + DBConnector29G.httpstate + ") ";
                    break;
                case 3:
                    ser_msg = "伺服器重定向訊息，請稍後在試(code:" + DBConnector29G.httpstate + ") ";
//                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
                case 4:
                    ser_msg = "用戶端錯誤回應，請稍後在試(code:" + DBConnector29G.httpstate + ") ";
//                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
                case 5:
                    ser_msg = "伺服器error responses，請稍後在試(code:" + DBConnector29G.httpstate + ") ";
//                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
            }
//                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        }
        if (DBConnector29G.httpstate == 0) {
            ser_msg = "遠端資料庫異常(code:" + DBConnector29G.httpstate + ") ";
        }
//        b_servermsg.setText(ser_msg);
//        b_servermsg.setTextColor(servermsgcolor);

        //-------------------------------------------------------------------
    }
    @Override
    public void onStart() {
        super.onStart();
//        dbmysql_A100();

    }

    @Override
    protected void onStop() {
        super.onStop();
        // 開啟中的話就關掉
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // 開啟中的話就關掉
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbHper == null) {
            dbHper = new FriendDbHelper29h(this, DB_FILE, null, DBversion);
        }
        Log.d(TAG,"onResume");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        dbmysql_A100();



    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }



    //==========================subclass=========================================
    private class TransTask extends AsyncTask<String, Void, String> {//下方理論上不用改(萬用格式)
        String ans;                                             //▲一個Veiw是網址

        @Override
        protected String doInBackground(String... params) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(params[0]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String line = in.readLine();
                while (line != null) {
                    Log.d("HTTP", line);
                    sb.append(line);
                    line = in.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ans = sb.toString();
            //------------
            return ans;
        }
    }
    private JSONArray sortJsonArray(JSONArray jsonArray) {//County自定義的排序Method
        final ArrayList<JSONObject> json = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {  //將資料存入ArrayList json中
            try {
                //-----------------------------------
                //可以刪除不想要的資料
//                Zipcode郵遞區號,Picture1照片不可為空
                if (//沒有該資料的話就不顯示
                        jsonArray.getJSONObject(i).getString("Zipcode").trim().length() > 0 //郵遞區號
                                &&    jsonArray.getJSONObject(i).getString("Picture1").trim().length() > 0  //照片
                                &&    !jsonArray.getJSONObject(i).getString("Picture1").trim().trim().equals("null") //照片
                                &&    !jsonArray.getJSONObject(i).getString("Picture1").trim().trim().contains("192.168") //照片
                                &&    jsonArray.getJSONObject(i).getString("Picture1").trim().trim().contains("https") //照片

                )
                {
                    json.add(jsonArray.getJSONObject(i));
                }
//                json.add(jsonArray.getJSONObject(i));
            } catch (JSONException jsone) {
                jsone.printStackTrace();
            }
        }
        //---------------------------------------------------------------
        Collections.sort(json, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject jsonOb1, JSONObject jsonOb2) {
                        String lidZipcode = "", ridZipcode = "";
                        try {
                            lidZipcode = jsonOb1.getString("Zipcode");
                            ridZipcode = jsonOb2.getString("Zipcode");
                        } catch (JSONException jsone) {
                            jsone.printStackTrace();
                        }
                        return lidZipcode.compareTo(ridZipcode);
                    }
                }
        );

        return new JSONArray(json);//回傳排序縣市後的array
    }

    //---------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }


    }
    //--END onActivityResult--

    // --TART handleSignInResult--
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }

    }
    // --END handleSignInResult--
    private void updateUI(GoogleSignInAccount account) {
        GoogleSignInAccount aa = account;
        int aaa=1;
        if (account != null) {

//-------改變圖像--------------
            User_IMAGE = account.getPhotoUrl();
            //沒有照片的話
            if(User_IMAGE==null){
//                noiconimg=Uri.parse("https://lh3.googleusercontent.com/pw/ACtC-3f7ifqOfGrkeKoxWel1YUubvk1UzdlwSpsIY_Wfxa3jCYE75R1aYZlFtZd-jvFPzp5aUNfJksNAtXYj0OhzV-brFWU7E81L8H5td0SZTDgeWDp7PdVcBwKYxChccjyhUsTjVb2L8Zrqh7xJEGBIuhyK=w200-h192-no?authuser=0");
//                User_IMAGE=noiconimg;
                return;
            }
            img = (CircleImgView) findViewById(R.id.google_icon);


            new AsyncTask<String,Void, Bitmap>() {
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

            a=1;


        } else
        {

            noiconimg=Uri.parse("https://lh3.googleusercontent.com/pw/ACtC-3fLoadb5GZ_TdsuBwjBPtI07ThdSzRl9lxXFt0sRTelpeR6xnKDsYqg_4i2A8rj5tOf_YnAkJp51WGCsaHMj0Ivmi14auhPywSkXRj_DxLF2lpO_CF81FRiPYL88Ntr_m8u53rL3y6hFmXElAjxLdL-=s144-no?authuser=0");
            User_IMAGE=noiconimg;
//                return;
            a=0;
            img = (CircleImgView) findViewById(R.id.google_icon);

            new AsyncTask<String,Void, Bitmap>() {
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



        }

    }
    //--------------------------------------------
    public static Bitmap getBitmapFromURL(String imageUrl) {
        try{
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }  catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void save_uid() //儲存ID
    {
        //儲存資料   後面開啟儲存的檔案
        SharedPreferences gameresult =getSharedPreferences("USER_ID",0);

        gameresult
                .edit()
                .putString("USER_ID", "0")
                .commit();
//        Toast.makeText(getApplicationContext(),"儲存成功",Toast.LENGTH_SHORT).show();
    }
    private void save_gpx_id() //儲存gpx流水號避免蓋掉檔案
    {
        //儲存資料   後面開啟儲存的檔案
        SharedPreferences gpx_id =getSharedPreferences("GPX_ID",0);

        gpx_id
                .edit()
                .putString("GPX_ID", "1")
                .commit();
//        Toast.makeText(getApplicationContext(),"儲存成功",Toast.LENGTH_SHORT).show();
    }
    public boolean onCreateOptionsMenu(Menu menu) {//選擇哪個layout的檔名
        getMenuInflater().inflate(R.menu.biker_menu,menu);
        this.menu = menu;
        menu.setGroupVisible(R.id.g01, false);
        menu.setGroupVisible(R.id.g02, false);
        account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        m_login = menu.findItem(R.id.Item01);//登入
        m_registered = menu.findItem(R.id.Item02);//註冊
        m_forgetpad = menu.findItem(R.id.Item03);//忘記密碼



        m_logout = menu.findItem(R.id.Item04); //登出按鈕

        m_action = menu.findItem(R.id.action_settings); //結束
        m_action.setIcon(android.R.drawable.ic_menu_close_clear_cancel);

        m_login.setTitle(getString(R.string.home_goLogin));

//        if(account!=null){
//            m_logout.setVisible(true);
////            m_logout.setVisible(true);
////            m_logout.setVisible(true);
//
//            m_login.setVisible(false);
//        }
//
//        if(account==null){
//            m_logout.setVisible(false);
//            m_login.setVisible(true);
//        }
        if(a!=0)
        {
            m_logout.setVisible(true);



            menu.setGroupVisible(R.id.g01, false);
//            m_login.setVisible(false);
//            m_registered.setVisible(false);//註冊
//            m_forgetpad.setVisible(false);//忘記密碼
        }
        else
        {
            m_logout.setVisible(false);

            menu.setGroupVisible(R.id.g01, true);
//            m_login.setVisible(true);
//            m_registered.setVisible(true);//註冊
//            m_forgetpad.setVisible(true);//忘記密碼
        }
        Log.d(TAG, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {//
        if(a!=0)
        {
            m_logout.setVisible(true);
            menu.setGroupVisible(R.id.g01, false);
//            m_login.setVisible(false);
//            m_registered.setVisible(false);//註冊
//            m_forgetpad.setVisible(false);//忘記密碼
        }
        else
        {
            m_logout.setVisible(false);

            menu.setGroupVisible(R.id.g01, true);
//            m_login.setVisible(true);
//            m_registered.setVisible(true);//註冊
//            m_forgetpad.setVisible(true);//忘記密碼
        }
        return super.onPrepareOptionsMenu(menu);
//        onPrepareOptionsMenu
//　　onPrepareOptionsMenu是每次在display menu之前，都會去呼叫，
//　　只要按一次menu按鍵，就會呼叫一次。
//　　所以你會發現每次只要按一次menu按鍵，
//　　menu選單就會多兩個（因為我們預設是兩個）
//　　你可以利用onPrepareOptionsMenu來做update menu狀態的動作
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {

            case R.id.Item01://前往登入
//                intent.setClass(Biker_home.this,Biker_login.class);
//                startActivity(intent);
                go_loginLYT.callOnClick();
                break;
            case R.id.Item02://註冊
                uri = Uri.parse("https://accounts.google.com/signup/v2/webcreateaccount?continue=https%3A%2F%2Fwww.google.com.tw%2F&hl=zh-TW&dsh=S251889732%3A1610874616537298&gmb=exp&biz=false&flowName=GlifWebSignIn&flowEntry=SignUp");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.Item03://忘記密碼
                uri = Uri.parse("https://accounts.google.com/signin/v2/usernamerecovery?hl=zh-TW&passive=true&continue=https%3A%2F%2Fwww.google.com.tw%2F&ec=GAZAAQ&flowName=GlifWebSignIn&flowEntry=ServiceLogin");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.Item04://登出
                AlertDig = new Dialog(Biker_home.this);

                AlertDig.setCancelable(false);//不能按其他地方
                AlertDig.setContentView(R.layout.alert_dialog);//選擇layout

                alertBtnOK = (Button)AlertDig.findViewById(R.id.train_btnOK);
                alertBtnCancel = (Button)AlertDig.findViewById(R.id.train_btnCancel);
                Dig_tarin_waring = (TextView) AlertDig.findViewById(R.id.tarin_waring);
                Dig_train_title=(TextView)AlertDig.findViewById(R.id.train_title);
                Dig_train_title.setText(getString(R.string.home_logout_text));
                Dig_tarin_waring.setText(getString(R.string.home_logout_ok));
                alertBtnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signOut();
                        save_uid();//儲存ID目前為0
                        if(account!=null){
                            m_logout.setVisible(false);
                            m_login.setVisible(true);
                        }
                        AlertDig.cancel();
                    }
                });
                alertBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDig.cancel();
                    }
                });
                AlertDig.show();


                break;
            case R.id.action_settings:
                AlertDig = new Dialog(Biker_home.this);

                AlertDig.setCancelable(false);//不能按其他地方
                AlertDig.setContentView(R.layout.alert_dialog);//選擇layout

                alertBtnOK = (Button)AlertDig.findViewById(R.id.train_btnOK);
                alertBtnCancel = (Button)AlertDig.findViewById(R.id.train_btnCancel);
                Dig_tarin_waring = (TextView) AlertDig.findViewById(R.id.tarin_waring);
                Dig_train_title=(TextView)AlertDig.findViewById(R.id.train_title);
                Dig_train_title.setText(getString(R.string.home_action_title));
                Dig_tarin_waring.setText(getString(R.string.home_action_text));
                alertBtnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Biker_time_Stopwatch.Biker_time_Stopwatch_class.finish();
                        }catch (Exception e)
                        {
                        }
                        finish();
                        AlertDig.cancel();

                    }
                });
                alertBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDig.cancel();
                    }
                });
                AlertDig.show();

                break;

        }
        return super.onOptionsItemSelected(item);
    }



}
