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
    private int DBversion;//?????????
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

    //??????
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
    private int TABLE_ID=2;//?????????
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
//                -------------????????????????????????????????????------------------------------
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
        // ?????????????????? ????????????????????????
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newscrollheight = displayMetrics.heightPixels * 60 / 100; // ??????ScrollView???????????????4/5
        phone_height_img=displayMetrics.heightPixels* 25 / 100;
        phone_width_img=displayMetrics.widthPixels* 25 / 100;

        //
        routelistLYT = (ListView) findViewById(R.id.route_list);
        routelistLYT.getLayoutParams().height = newscrollheight;

        routelistLYT.setLayoutParams(routelistLYT.getLayoutParams()); // ??????ScrollView??????

        //--google ??????
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


        //TextView???????????????
        TextView00 = (TextView) findViewById(R.id.TextView);
        TextView00.setOnClickListener(this);
        RecCountTEST=(Button)findViewById(R.id.RecCountTEST);
        RecCountTEST.setOnClickListener(this);

        // ??????Geocoder??????
        geocoder  =new Geocoder(this,Locale.TAIWAN);//???????????????????????????????????????


        //recyclerView
//        li01 = (LinearLayout) findViewById(R.id.li01);
//        li01.setVisibility(View.GONE);//???????????????
//        mTxtResult = findViewById(R.id.m2206_name);
//        mDesc = findViewById(R.id.m2206_descr);
        //textview ????????????????????????
//        mDesc.setMovementMethod(ScrollingMovementMethod.getInstance());//????????????
//        mDesc.scrollTo(0, 0);//textview ?????????
        recyclerView = findViewById(R.id.recyclerView);

//        t_count = findViewById(R.id.count);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //??????recyclerView ?????????
//                li01.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(),"XX",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        //--------------???????????????-----------
//        u_loading = (TextView)findViewById(R.id.u_loading);//??????????????????
//        u_loading.setVisibility(View.GONE);
        //-------------------------------------
        //????????????

        laySwipe = (SwipeRefreshLayout)findViewById(R.id.laySwipe);
        laySwipe.setVisibility(View.INVISIBLE);
        laySwipe.setOnRefreshListener(onSwipeToRefresh);//???????????????
        laySwipe.setSize(SwipeRefreshLayout.LARGE);//???????????????
        // ????????????????????????????????????????????????
        laySwipe.setDistanceToTriggerSync(100);
        // ???????????????????????????
        laySwipe.setProgressBackgroundColorSchemeColor(getColor(android.R.color.background_light));
        // ??????????????????????????????????????????1????????????
        laySwipe.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_purple,
                android.R.color.holo_orange_dark);

/*        setProgressViewOffset : ?????????????????????????????????
        ?????????????????????????????????????????????
        ??????????????????????????????????????????????????????????????????
        ??????????????????????????????????????????????????????????????????*/
        laySwipe.setProgressViewOffset(true, 0, 50);
        laySwipe.getLayoutParams().height=newscrollheight;
//=====================
//        onSwipeToRefresh.onRefresh();  //????????????????????????
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
                save_uid();//??????ID?????????0
                save_gpx_id();//??????gpx?????????
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
                onSwipeToRefresh.onRefresh();  //????????????????????????
//                attractions_textLYT.setVisibility(View.GONE);
//                attractions_text2LYT.setVisibility(View.VISIBLE);
                break;
            case R.id.route_text:
                TABLE_ID=2;
                onSwipeToRefresh.onRefresh();  //????????????????????????
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
                    //------------??????USER_ID-------------------
                    SharedPreferences xxx=getSharedPreferences("USER_ID",0);
                    U_ID=xxx.getString("USER_ID","");

                    FriendDbHelper23 dbHper23 = null;
                    if (dbHper23 == null) {
                        dbHper23 = new FriendDbHelper23(this, DB_FILE, null, DBversion);
                    }
                    dbHper23.FindRec(U_ID);

                    //??????SQLite?????????
                    SQLiteDatabase database= dbHper23.getReadableDatabase();
                    Cursor I100 = database.query(       //??????????????????
                            true,
                            DB_TABLE_I100,
                            new String[]{"I103", "I104", "I105","I106","I107"},//?????????
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
                    if (I100 == null || I100.getCount() == 0){       //???????????????????????????????????????
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

                    AlertDig.setCancelable(false);//?????????????????????
                    AlertDig.setContentView(R.layout.alert_dialog);//??????layout

                    alertBtnOK = (Button)AlertDig.findViewById(R.id.train_btnOK);
                    alertBtnCancel = (Button)AlertDig.findViewById(R.id.train_btnCancel);
                    Dig_tarin_waring = (TextView) AlertDig.findViewById(R.id.tarin_waring);
                    Dig_train_title=(TextView)AlertDig.findViewById(R.id.train_title);
                    Dig_train_title.setText("??????");
                    Dig_tarin_waring.setText("????????????????????????????????????");
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

//                    Toast.makeText(getApplicationContext(),"????????????",Toast.LENGTH_SHORT).show();
                    break;

            }
        }

    }
    private final SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh=
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //??????????????? ???????????????
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
                            //-----------------??????????????????----------------
                            laySwipe.setRefreshing(true);//setRefreshing(false) ??????????????????????????????
//                            u_loading.setVisibility(View.VISIBLE);
//                            mTxtResult.setText(getString(R.string.m2206_name) + "");
//                            mDesc.setText("");
//                            mDesc.scrollTo(0, 0);//textview ?????????
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //                    =================================
                                    setDatatolist();
                                    //                  =================================
                                    //----------SwipeLayout ?????? --------
                                    //???????????????????????? u_importopendata()
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
                                    hander.sendEmptyMessage(1); // ?????????????????????????????????
                                }
                            }, 1000);  //10???
                        }
                    });
                    //????????????
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
                            //?????? ?????????????????????
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
        progDlg.setTitle("?????????");
        progDlg.setMessage("???????????????");
        progDlg.setIcon(android.R.drawable.presence_away);
        progDlg.setCancelable(false);
        progDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDlg.setMax(100);
        progDlg.show();
    }

    private void setDatatolist() {
        //==================================
        u_importopendata();  //??????Opendata
//        u_importopendata_biker();
        //???mysql??????
        //==================================
        //??????Adapter to ????????? view recyleview scrollview ??????


        for (Map<String, Object> m : mList) {
            if (m != null) {

                if(TABLE_ID==1)
                {
                    //=================????????????=====================
                String Name = m.get("Name").toString().trim(); //??????
                String Add = m.get("Add").toString().trim(); //??????
                String Picture1 = m.get("Picture1").toString().trim(); //??????
                if (Picture1.isEmpty() || Picture1.length() < 1) { //???????????????
                    Picture1 = "https://bklifetw.com/img/nopic1.jpg";
                }
                String Description = m.get("Description").toString().trim(); //??????
                String Zipcode = m.get("Zipcode").toString().trim(); //??????

                String Latitude = m.get("Latitude").toString().trim();;//??????
                String Longitude= m.get("Longitude").toString().trim();;//??????
                String Ticketinfo= m.get("Ticketinfo").toString().trim();//????????????
                if (Ticketinfo.isEmpty() || Ticketinfo.length() < 1) { //????????????
                    Ticketinfo = "???";
                }
                String Opentime= m.get("Opentime").toString().trim();;//????????????
                if (Opentime.isEmpty() || Opentime.length() < 1) { //????????????
                    Opentime = "???";
                }


                String Picdescribe1= m.get("Picdescribe1").toString().trim();;//????????????
                String Tel= m.get("Tel").toString().trim();;//??????


                mData.add(new Post(Name, Picture1, Add, Description, Zipcode,
                        Latitude,Longitude,Ticketinfo,Opentime,Picdescribe1,Tel));
                    //=================????????????=====================
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
    //???????????? ?????? ?????????
    private void adapter_click_setAdap()
    {
        //???????????? ??????
        adapter.setOnItemClickListener(new Biker_home_RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                li01.setVisibility(View.VISIBLE);
//                Toast.makeText(M2205.this, "onclick" + mData.get(position).hotelName.toString(), Toast.LENGTH_SHORT).show();
//                mTxtResult.setText(getString(R.string.m2206_name) + mData.get(position).Name);
//                mDesc.setText(mData.get(position).Content);
//                mDesc.scrollTo(0, 0); //textview ?????????
                nowposition = position;
//                mData.clear();
//                Toast.makeText(getApplicationContext(),nowposition+"",Toast.LENGTH_SHORT).show();
//                t_count.setText(getString(R.string.ncount) + total + "/" + t_total + "   (" + (nowposition + 1) + ")");
                if(isFastClick())
                {
                    show_weather(nowposition);//?????????
                }
            }
        });
//********************************* ****
        recyclerView.setAdapter(adapter);
    }
    //????????????
    public static boolean isFastClick() {
        boolean flag = false;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME ) {
            flag = true;//??????1000?????? true
        }
        lastClickTime = currentClickTime;
        return flag;
    }


    private void show_weather(int position) {
        //?????????????????????
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
                //=====================???????????????
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

                    AlertDig.setCancelable(false);//?????????????????????
                    AlertDig.setContentView(R.layout.alert_dialog);//??????layout

                    alertBtnOK = (Button)AlertDig.findViewById(R.id.train_btnOK);
                    alertBtnCancel = (Button)AlertDig.findViewById(R.id.train_btnCancel);
                    Dig_tarin_waring = (TextView) AlertDig.findViewById(R.id.tarin_waring);
                    Dig_train_title=(TextView)AlertDig.findViewById(R.id.train_title);
                    Dig_train_title.setText("??????");
                    Dig_tarin_waring.setText("????????????????????????????????????");
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


    private void u_importopendata() { //??????Opendata
        try {
            String Task_opendata="";
            if(TABLE_ID==1)
            {
                Task_opendata
                        = new TransTask().execute("https://gis.taiwan.net.tw/XMLReleaseALL_public/scenic_spot_C_f.json").get();   //???????????? - ??? ??????????????????
            }
            else if(TABLE_ID==2)
            {
                Task_opendata
                        = new TransTask().execute("https://gis.taiwan.net.tw/XMLReleaseALL_public/Bike_f.json").get();   //????????????
            }


//-------?????? json   ??????????????????-------------
            mList = new ArrayList<Map<String, Object>>();
            JSONObject json_obj1 = new JSONObject(Task_opendata);
            JSONObject json_obj2 = json_obj1.getJSONObject("XML_Head");
            JSONObject infos = json_obj2.getJSONObject("Infos");
            JSONArray info = infos.getJSONArray("Info");
//            total = 0;
//            t_total = info.length(); //?????????
//------JSON ??????----------------------------------------
            if(TABLE_ID==1)
              info = sortJsonArray(info);
//            total = info.length(); //????????????
//            t_count.setText(getString(R.string.ncount) + total + "/" + t_total);
//----------------------------------------------------------
            //-----??????????????????-----
//            total = info.length();
//            t_count.setText(getString(R.string.ncount) + total);

            for (int i = 0; i < info.length(); i++) {
                Map<String, Object> item = new HashMap<String, Object>();

                if(TABLE_ID==1)
                {
                    //=========================????????????=================================
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
                    String Add = info.getJSONObject(i).getString("Add");//??????
                    String Picture1 = info.getJSONObject(i).getString("Picture1");//??????
                    String Zipcode = info.getJSONObject(i).getString("Zipcode"); //????????????,
                    String Latitude =info.getJSONObject(i).getString("Py");//??????
                    String Longitude=info.getJSONObject(i).getString("Px");//??????
                    String Ticketinfo=info.getJSONObject(i).getString("Ticketinfo");//????????????
                    String Opentime=info.getJSONObject(i).getString("Opentime");//????????????
                    String Picdescribe1=info.getJSONObject(i).getString("Picdescribe1");//????????????
                    String Tel=info.getJSONObject(i).getString("Tel");//??????


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


                    //=========================????????????=================================
                }
                else if(TABLE_ID==2)
                {
                    //==========================???????????????===============================

                    String Name = info.getJSONObject(i).getString("Name");
                    String S_PlaceDes = info.getJSONObject(i).getString("S_PlaceDes");
                    String Bike_length = info.getJSONObject(i).getString("Bike_length") + "??????";
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
//----------SwipeLayout ?????? --------
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
                        img.setImageResource(R.drawable.home_user); //????????????
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
                        //---??????ListView
                        SimpleAdapter adapter = new SimpleAdapter(
                                Biker_home.this, mList, R.layout.home_routelist,
                                new String[]{"H101", "H102", "H103", "H104"},
                                new int[]{R.id.route_Name, R.id.route_S_PlaceDes, R.id.route_Bike_length, R.id.route_Toldescribe});
                        checkRequiredPermission(Biker_home.this);
                        routelistLYT.setAdapter(adapter);
                        routelistLYT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //?????????????????????
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
                    //?????????????????????
                    break;
            }
        }
    };

    private void address_to_location(String name) {

        try {
            // ??????????????????????????????List??????
            List<Address> listGPSAddress = geocoder.getFromLocationName(name, 1);
            // ????????????????????????
            if (listGPSAddress != null) {

                list_latitude = listGPSAddress.get(0).getLatitude();
                list_longitude = listGPSAddress.get(0).getLongitude();
            }
        } catch (Exception ex) {
//            output.setText("??????:" + ex.toString());
        }
    }
    private String location_to_address(Double latitude,Double longitude)
    {
        try {
            // ?????????????????????List??????
            List<Address> listAddress = geocoder.getFromLocation(latitude, longitude, 1);//maxResult ?????????
            // ?????????????????????
            if (listAddress != null){
                Address findAddress=listAddress.get(0);
//                // ??????StringBuilder??????
                StringBuilder strAddress=new StringBuilder();
                // ?????????????????????
                return findAddress.getAddressLine(0);
//                 strAddress.append(str).append("\n");


                //---------------------------------------------------;
            }else{
//                output.setText("????????????!");
            }

        }catch (Exception ex){
//            output.setText("??????:"+ex.toString());
        }
        return "????????????!";
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
Retrofit ???????????? Square ????????????????????? RESTful API ???????????????????????????????????????
???????????????????????? okHttp ???okHttp ???????????? okHttp ?????????
Retrofit ???????????????????????????????????? API ???????????? JSON?????????????????????????????????????????? Converter???
?????????????????? Server ????????????????????????????????? okHttpClient ?????? Interceptor???
??? Retrofit 1.9.0 ??? Interceptor ?????????????????????
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
                    //********??????????????????????????????*****************************

                    weatherDlg.show();
                    WeatherResponse weatherResponse = response.body();
                    assert weatherResponse != null;
                    String stringBuilder =
                            getString(R.string.home_weather_name) +list_city+
                            "\n" +
//                            getString(R.string.areaname) + weatherResponse.name +
//                            "\n" +
                            getString(R.string.home_Temperature) +
// --------------- K?????????????????C??-------------------
                            (int) (Float.parseFloat("" + weatherResponse.main.temp) - 273.15) + "C??" +
                            "\n" +
                            getString(R.string.home_Temperature_Max) + (int) (Float.parseFloat("" + weatherResponse.main.temp_max) - 273.15) + "C??" +
                            "\n" +
                            getString(R.string.home_Temperature_Min) + (int) (Float.parseFloat("" + weatherResponse.main.temp_min) - 273.15) + "C??" +
                            "\n" +
                            getString(R.string.home_Humidity) +
                            weatherResponse.main.humidity +
                            "\n" +
                            getString(R.string.home_Pressure) +
                            weatherResponse.main.pressure;
                    weatherData.setText(stringBuilder); //??????
////====????????????==============
                    weatherLat.setText(getString(R.string.home_weather_lat) + (list_latitude));
                    weatherLon.setText(getString(R.string.home_weather_lon) + (list_longitude));
                    //====??????????????????==============

//                    weatherName.setText(getString(R.string.weather_name) + tranlocationName(lat, lon));
//======?????? Internet ??????==================
                    int b_id = weatherResponse.weather.get(0).id;
                    String b_main = weatherResponse.weather.get(0).main;
                    String b_description = weatherResponse.weather.get(0).description;
                    String b_icon = weatherResponse.weather.get(0).icon;
                    iconurl = "https://openweathermap.org/img/wn/" + b_icon + "@2x.png";  //icon?????????
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
+ ??????Picasso???????????? +
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
        recSet_list=dbHper.getRecSet_list();//0112????????????  ??????????????????
    }



    // ??????A100MySQL ?????? ??????sqlite
    private void dbmysql_A100() {
        sqlctl = "SELECT * FROM A100 ORDER BY id ASC";
//        sqlctl = "SELECT * FROM A100 WHERE A101='100504042909700846019'";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            String result = DBConnector29G.executeQuery(nameValuePairs);
            //===========================================
            chk_httpstate();//??????????????????
            //===============================

            /**************************************************************************
             * SQL ??????????????????????????????JSONArray
             * ?????????????????????????????????JSONObject?????? JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            JSONArray jsonArray = new JSONArray(result);
            // -------------------------------------------------------
            if (jsonArray.length() > 0) { // MySQL ?????????????????????
//--------------------------------------------------------
                FriendDbHelper29G dbHper_LOGIN = new FriendDbHelper29G(this, DB_FILE, null, DBversion);
                int rowsAffected = dbHper_LOGIN.clearRec();                 // ?????????,????????????SQLite??????
                dbHper_LOGIN.close();
//--------------------------------------------------------
//                // ??????JASON ????????????????????????
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    ContentValues newRow = new ContentValues();
////                    // --(1) ?????????????????? --?????? jsonObject ????????????("key","value")-----------------------
//                    Iterator itt = jsonData.keys();
//                    while (itt.hasNext()) {
//                        String key = itt.next().toString();
//                        String value = jsonData.getString(key); // ??????????????????
//                        if (value == null) {
//                            continue;
//                        } else if ("".equals(value.trim())) {
//                            continue;
//                        } else {
//                            jsonData.put(key, value.trim());
//                        }
//                        // ------------------------------------------------------------------
//                        newRow.put(key, value.toString()); // ???????????????????????????
//                        // -------------------------------------------------------------------
//                    }
//                    // ---(2) ????????????????????????---------------------------
                    newRow.put("id",jsonData.getString("id").toString());
                    newRow.put("A101",jsonData.getString("A101").toString());
                    newRow.put("A102",jsonData.getString("A102").toString());
                    newRow.put("A103",jsonData.getString("A103").toString());
                    newRow.put("A104",jsonData.getString("A104").toString());
                    newRow.put("A105",jsonData.getString("A105").toString());
                    newRow.put("A106",jsonData.getString("A106").toString());
                    newRow.put("A107",jsonData.getString("A107").toString());
                    newRow.put("A108",jsonData.getString("A108").toString());
                    // -------------------??????SQLite---------------------------------------
                    long rowID = dbHper.insertRec_A100(newRow);
//                        Toast.makeText(getApplicationContext(), "????????? " + Integer.toString(jsonArray.length()) + " ?????????", Toast.LENGTH_SHORT).show();

                }
                // ---------------------------
            } else {
//                    Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_LONG).show();
            }
//            recSet = dbHper.getRecSet();  //????????????SQLite
//            u_setspinner();
            // --------------------------------------------------------
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

    }

    private void chk_httpstate()
    {
        ////-------------------------------
        //?????????????????? DBConnector01.httpstate ?????????????????? 200(??????????????????)
        if (DBConnector29G.httpstate == 200) {
            ser_msg = "?????????????????????(code:" + DBConnector29G.httpstate + ") ";
//            servermsgcolor = ContextCompat.getColor(this, R.color.Navy);
//                Toast.makeText(getBaseContext(), "???????????????????????? ",
//                        Toast.LENGTH_SHORT).show();


        } else {
            int checkcode = DBConnector29G.httpstate / 100;
            switch (checkcode) {
                case 1:
                    ser_msg = "????????????(code:" + DBConnector29G.httpstate + ") ";
                    break;
                case 2:
                    ser_msg = "????????????????????????????????????(code:" + DBConnector29G.httpstate + ") ";
                    break;
                case 3:
                    ser_msg = "??????????????????????????????????????????(code:" + DBConnector29G.httpstate + ") ";
//                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
                case 4:
                    ser_msg = "???????????????????????????????????????(code:" + DBConnector29G.httpstate + ") ";
//                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
                case 5:
                    ser_msg = "?????????error responses??????????????????(code:" + DBConnector29G.httpstate + ") ";
//                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
            }
//                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        }
        if (DBConnector29G.httpstate == 0) {
            ser_msg = "?????????????????????(code:" + DBConnector29G.httpstate + ") ";
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
        // ????????????????????????
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // ????????????????????????
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
    private class TransTask extends AsyncTask<String, Void, String> {//????????????????????????(????????????)
        String ans;                                             //?????????Veiw?????????

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
    private JSONArray sortJsonArray(JSONArray jsonArray) {//County??????????????????Method
        final ArrayList<JSONObject> json = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {  //???????????????ArrayList json???
            try {
                //-----------------------------------
                //??????????????????????????????
//                Zipcode????????????,Picture1??????????????????
                if (//?????????????????????????????????
                        jsonArray.getJSONObject(i).getString("Zipcode").trim().length() > 0 //????????????
                                &&    jsonArray.getJSONObject(i).getString("Picture1").trim().length() > 0  //??????
                                &&    !jsonArray.getJSONObject(i).getString("Picture1").trim().trim().equals("null") //??????
                                &&    !jsonArray.getJSONObject(i).getString("Picture1").trim().trim().contains("192.168") //??????
                                &&    jsonArray.getJSONObject(i).getString("Picture1").trim().trim().contains("https") //??????

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

        return new JSONArray(json);//????????????????????????array
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

//-------????????????--------------
            User_IMAGE = account.getPhotoUrl();
            //??????????????????
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

    private void save_uid() //??????ID
    {
        //????????????   ???????????????????????????
        SharedPreferences gameresult =getSharedPreferences("USER_ID",0);

        gameresult
                .edit()
                .putString("USER_ID", "0")
                .commit();
//        Toast.makeText(getApplicationContext(),"????????????",Toast.LENGTH_SHORT).show();
    }
    private void save_gpx_id() //??????gpx???????????????????????????
    {
        //????????????   ???????????????????????????
        SharedPreferences gpx_id =getSharedPreferences("GPX_ID",0);

        gpx_id
                .edit()
                .putString("GPX_ID", "1")
                .commit();
//        Toast.makeText(getApplicationContext(),"????????????",Toast.LENGTH_SHORT).show();
    }
    public boolean onCreateOptionsMenu(Menu menu) {//????????????layout?????????
        getMenuInflater().inflate(R.menu.biker_menu,menu);
        this.menu = menu;
        menu.setGroupVisible(R.id.g01, false);
        menu.setGroupVisible(R.id.g02, false);
        account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        m_login = menu.findItem(R.id.Item01);//??????
        m_registered = menu.findItem(R.id.Item02);//??????
        m_forgetpad = menu.findItem(R.id.Item03);//????????????



        m_logout = menu.findItem(R.id.Item04); //????????????

        m_action = menu.findItem(R.id.action_settings); //??????
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
//            m_registered.setVisible(false);//??????
//            m_forgetpad.setVisible(false);//????????????
        }
        else
        {
            m_logout.setVisible(false);

            menu.setGroupVisible(R.id.g01, true);
//            m_login.setVisible(true);
//            m_registered.setVisible(true);//??????
//            m_forgetpad.setVisible(true);//????????????
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
//            m_registered.setVisible(false);//??????
//            m_forgetpad.setVisible(false);//????????????
        }
        else
        {
            m_logout.setVisible(false);

            menu.setGroupVisible(R.id.g01, true);
//            m_login.setVisible(true);
//            m_registered.setVisible(true);//??????
//            m_forgetpad.setVisible(true);//????????????
        }
        return super.onPrepareOptionsMenu(menu);
//        onPrepareOptionsMenu
//??????onPrepareOptionsMenu????????????display menu???????????????????????????
//?????????????????????menu??????????????????????????????
//?????????????????????????????????????????????menu?????????
//??????menu??????????????????????????????????????????????????????
//?????????????????????onPrepareOptionsMenu??????update menu???????????????
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {

            case R.id.Item01://????????????
//                intent.setClass(Biker_home.this,Biker_login.class);
//                startActivity(intent);
                go_loginLYT.callOnClick();
                break;
            case R.id.Item02://??????
                uri = Uri.parse("https://accounts.google.com/signup/v2/webcreateaccount?continue=https%3A%2F%2Fwww.google.com.tw%2F&hl=zh-TW&dsh=S251889732%3A1610874616537298&gmb=exp&biz=false&flowName=GlifWebSignIn&flowEntry=SignUp");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.Item03://????????????
                uri = Uri.parse("https://accounts.google.com/signin/v2/usernamerecovery?hl=zh-TW&passive=true&continue=https%3A%2F%2Fwww.google.com.tw%2F&ec=GAZAAQ&flowName=GlifWebSignIn&flowEntry=ServiceLogin");
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.Item04://??????
                AlertDig = new Dialog(Biker_home.this);

                AlertDig.setCancelable(false);//?????????????????????
                AlertDig.setContentView(R.layout.alert_dialog);//??????layout

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
                        save_uid();//??????ID?????????0
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

                AlertDig.setCancelable(false);//?????????????????????
                AlertDig.setContentView(R.layout.alert_dialog);//??????layout

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
