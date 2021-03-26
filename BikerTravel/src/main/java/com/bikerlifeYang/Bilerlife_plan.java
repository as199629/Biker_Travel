package com.bikerlifeYang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Bilerlife_plan extends AppCompatActivity {
    //weather的opendata用=======================================
    public static String BaseUrl = "https://api.openweathermap.org/";
    public static String AppId = "cbb6948c25ec9787282051bc6b1395c0";
    public static String lat = "24.170758413879188";
    public static String lon = "120.61005862426565";
    public static String lang = "zh_tw";
    //=======================================
    private static final String DB_FILE = "friends.db";
    private static final String DB_TABLE = "member";
    private static int DBversion = 1;

    private FriendDbHelper20 dbHper;
    private ArrayList<String> recSet;//資料Array
    //==========================================================
    private Button b001,b002,b003;
    private Intent intent=new Intent();
    private Menu menu;
    private int index=0;

    private Dialog mLoginDlg;
    private Calendar now;
    private String u_date="";
    private EditText e002, e001;
    private Spinner s001;
    private Button write_newplan, cancel_newplan;
    private String planname, planday, planplace;
    private String msg;
    private String ser_msg;
    private String sqlctl;
    private String TAG="";
    private TableRow tb002,tb003,tb004,tb005,tb006,tb007, tb008;
    private TextView no002;
    private Spinner s001planname;
    private TextView t002, t004;
    private TextView dayweather, nightweather;
    private TextView daytemp_mx, daytemp_lo;
    private TextView nighttemp_mx, nighttemp_lo;
    private ImageView img01;
    private Button nowweather;
    private SwipeRefreshLayout laySwipe;
    private ArrayList<Map<String, Object>> roadList;
    private int total;
    private String U_ID;
    private ArrayList<String> spinner_index=new ArrayList<String>();
    private Dialog delDlg;//確認刪除對話盒
    private Button Dlg_delplan;
    private Button cancelDlg_delplan;
    private TextView t_alert;
    private TextView del_msg;
    private int old_index;
    private Dialog editDlg;
    private Button writeedit_plan;
    private Button canceledit_plan;
    private String planID;
    private int iSelect;
    private String now_planID;
    private TextView edittitle;
    private ImageView clothe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_plan);

        //------------取得USER_ID-------------------
        SharedPreferences xxx=getSharedPreferences("USER_ID",0);
        U_ID=xxx.getString("USER_ID","");
        setupViewCompoent();
//        int a=0;
    }
    //有使用到mysql(or遠端的資料庫)都需要這個方法==============================
    public static void enableStrictMode(Context context) {
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
    //===================================================================
    private void setupViewCompoent() {
        //---測試用
        ImageView img_goplan=(ImageView)findViewById(R.id.img_goplan);
        img_goplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHper.getReadableDatabase();
            }
        });
        //--
        b001 = (Button) findViewById(R.id.plan_gonewplan);//前往寫入計畫按鈕
        b001.setOnClickListener(go_newplan);
        nowweather =(Button)findViewById(R.id.plan_nowweather); //取得即時天氣(不寫入資料)
        nowweather.setOnClickListener(get_now);
        //如果mysql有資料才有的欄位===================
        tb002=(TableRow)findViewById(R.id.tableRow02);
        tb003=(TableRow)findViewById(R.id.tableRow03);
        tb004=(TableRow)findViewById(R.id.tableRow04);
        tb005=(TableRow)findViewById(R.id.tableRow05);
        tb006=(TableRow)findViewById(R.id.tableRow06);
        tb007=(TableRow)findViewById(R.id.tableRow07);

        t002 = (TextView) findViewById(R.id.plan_t002); //計畫日期
        t004 = (TextView) findViewById(R.id.plan_t004);//計畫地區

        b002=(Button)findViewById(R.id.plan_delplan);//刪除計畫按鈕
        b002.setOnClickListener(del_plan);
        b003 = (Button)findViewById(R.id.plan_edit); //編輯目前計畫按鈕
        b003.setOnClickListener(edit_plan);

        s001planname=(Spinner)findViewById(R.id.plan_planname);//計畫名稱(選單)

        //        =============opendata==============================
        dayweather=(TextView)findViewById(R.id.plan_day);//白天天氣
        nightweather=(TextView)findViewById(R.id.plan_night);//晚上天氣
        daytemp_mx=(TextView)findViewById(R.id.plan_day_mx);//白天高溫
        daytemp_lo=(TextView)findViewById(R.id.plan_day_lo);//白天低溫
        nighttemp_mx=(TextView)findViewById(R.id.plan_night_mx);//晚上高溫
        nighttemp_lo=(TextView)findViewById(R.id.plan_night_lo);//晚上低溫
        clothe=(ImageView)findViewById(R.id.plan_i001);//建議服裝
        //如果mysql沒資料的欄位===================
        tb008=(TableRow)findViewById(R.id.tableRow08);
        no002=(TextView)findViewById(R.id.plan_noplan02);

        initDB();//開啟資料庫並讀取資料
        dbmysql();
        showRec(index);//更新畫面資料(除了spinner)


        Button b001test = (Button) findViewById(R.id.b001test);
        b001test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHper.clearRec();
            }
        });
    }
    //滑動刷新推薦自行車道(如果有建立資料)========================================================
    private void loadRoad(){
        laySwipe = (SwipeRefreshLayout)findViewById(R.id.laySwipe);//推薦自行車道欄位
        laySwipe.setOnRefreshListener(onSwipeToRefresh);
        laySwipe.setSize(SwipeRefreshLayout.LARGE);
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
        onSwipeToRefresh.onRefresh();  //開始轉圈下載資料
    }
    //準備資料庫================================================
    private void initDB() {
        DBversion = Integer.parseInt(getString(R.string.SQLite_version));
        if (dbHper == null){
            dbHper = new FriendDbHelper20(this, DB_FILE, null, DBversion);
//        recSet=dbHper.getRecSet();
        }
//        dbHper.FindRec(U_ID);
    }

    private void showRec(int index) {
        recSet=dbHper.getRecSet();
        if (recSet.size() != 0) {
            planVISIBLE();
            //有資料就跳轉到已創好的計畫頁面
            alreadyplan();
            String[] fld = recSet.get(index).split("#");
//            b_id.setText(fld[0]);
            t002.setText(fld[2]);
            t004.setText(fld[3]);
//            s001.setText(fld[3]);
            s001planname.setSelection(index, true); //spinner 小窗跳到第幾筆
        } else {
            noplan();
        }
    }
    //如果mysql有資料=============================================
    private  void alreadyplan(){
        tb008.setVisibility(View.INVISIBLE);no002.setVisibility(View.INVISIBLE);
        //下拉選單物件========================================================================
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        spinner_index.clear();
        for (int i = 0; i < recSet.size(); i++) {
            String[] fld = recSet.get(i).split("#");
            spinner_index.add(fld[0]);
            adapter.add(fld[4]);
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s001planname.setAdapter(adapter);
        s001planname.setOnItemSelectedListener(select_plan);
        //溫馨提醒圖片秀==================================
        img01=(ImageView)findViewById(R.id.plan_i002);
        Bikerlife_plan_imgshow point=new Bikerlife_plan_imgshow(this);
        point.start();
    }
    Handler point=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    img01.setImageResource(R.drawable.kagi);
                    break;
                case 2:
                    img01.setImageResource(R.drawable.saifu);
                    break;
                case 3:
                    img01.setImageResource(R.drawable.smartphone);
                    break;
            }
        }
    };
    //如果mysql沒資料=============================================
    private void noplan(){
        tb002.setVisibility(View.INVISIBLE);tb003.setVisibility(View.INVISIBLE);tb004.setVisibility(View.INVISIBLE);
        tb005.setVisibility(View.INVISIBLE);tb006.setVisibility(View.INVISIBLE);tb007.setVisibility(View.INVISIBLE);
        b002.setVisibility(View.INVISIBLE);;s001planname.setVisibility(View.INVISIBLE);
//        laySwipe.setVisibility(View.INVISIBLE);
    }
    private void planVISIBLE(){
        tb002.setVisibility(View.VISIBLE);tb003.setVisibility(View.VISIBLE);tb004.setVisibility(View.VISIBLE);
        tb005.setVisibility(View.VISIBLE);tb006.setVisibility(View.VISIBLE);tb007.setVisibility(View.VISIBLE);
        b002.setVisibility(View.VISIBLE);;s001planname.setVisibility(View.VISIBLE);
//        laySwipe.setVisibility(View.INVISIBLE);
    }

    //打開新增計畫頁面視窗=================================
    private View.OnClickListener go_newplan = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            u_login_dig();
            //輸入日期時取消輸入鍵盤==============================================
            e002.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int inType = e002.getInputType(); // backup the input type
                    e002.setInputType(InputType.TYPE_NULL); // disable soft input
                    e002.onTouchEvent(event); // call native handler
                    e002.setInputType(inType); // restore input type
                    e002.setSelection(e002.getText().length());
                    return true;
                }
            });
            //轉移視窗由編輯文字變成日曆================================================
            e002.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        showdatelog();
                        int inType = e002.getInputType(); // backup the input type
                        e002.setInputType(InputType.TYPE_NULL); // disable soft input
                        e002.setInputType(inType); // restore input type
                        e002.setSelection(e002.getText().length());
                    }
                }
            });
        }
    };
        //呼叫新增計畫dialog==========================================
    private void u_login_dig() {
        mLoginDlg = new Dialog(Bilerlife_plan.this);
        mLoginDlg.setTitle(getString(R.string.plan_newplan));
        mLoginDlg.setCancelable(false);
        mLoginDlg.setContentView(R.layout.biker_plan_newplan);  //選擇layout
        e001 = (EditText) mLoginDlg.findViewById(R.id.newplan_e001);//計畫名稱
        e002 = (EditText) mLoginDlg.findViewById(R.id.newplan_e002);//計畫日期
        s001 = (Spinner) mLoginDlg.findViewById(R.id.newplan_s001); //選擇地區
        write_newplan = (Button) mLoginDlg.findViewById(R.id.newplan_b003);//確定寫入新計畫
        cancel_newplan = (Button) mLoginDlg.findViewById(R.id.newplan_b002);//取消輸入新計畫
        write_newplan.setOnClickListener(write_newplanOK);
        cancel_newplan.setOnClickListener(write_newplanNO);
        mLoginDlg.show();
    }
    //寫入新計畫到mysql===========================================
    private View.OnClickListener write_newplanOK= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            planname=e001.getText().toString();
            planday=e002.getText().toString();
            planplace=s001.getSelectedItem().toString();
            if ( !("".equals(e001.getText().toString())
                    || "".equals(e002.getText().toString())
                    || "".equals(s001.getSelectedItem().toString())) ) //判斷名稱日期地點都有輸入
            {
                mysql_insert();
                dbmysql();

                msg = null;
                long rowID = dbHper.RecCount();
                if (rowID != -1) {
                    dbmysql();
                    index = dbHper.RecCount() - 1;
                    showRec(index);
                    ctlLast();  //成功跳到最後一筆
                    msg = "新增計畫成功 ! ";
//                    Intent intent=new Intent();
//                    intent.setClass(Bilerlife_plan.this,Bilerlife_plan.class);
//                    startActivity(intent);
//                    finish();
                } else {
                    msg = "新增計畫失敗 !";
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),getString(R.string.plan_waringkeyin),Toast.LENGTH_SHORT).show();
            }
            mLoginDlg.cancel();
        }
    };
    //取消寫入新計畫================================================
    private View.OnClickListener write_newplanNO=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mLoginDlg.cancel();
        }
    };
    //刪除計畫=================================================
    private View.OnClickListener del_plan = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        //呼叫對話盒===================================
            delDlg = new Dialog(Bilerlife_plan.this);
            delDlg.setTitle(getString(R.string.plan_newplan));
            delDlg.setCancelable(false);
            delDlg.setContentView(R.layout.biker_alert_dialog);  //選擇layout

            t_alert =(TextView)delDlg.findViewById(R.id.alert_title);
            t_alert.setText(R.string.plan_del);
            t_alert.setTextSize(16);
            del_msg = (TextView)delDlg.findViewById(R.id.alert_msg);
            del_msg.setText(R.string.plan_waring_del);

            Dlg_delplan = (Button) delDlg.findViewById(R.id.alert_btnOK);//確定刪除
            Dlg_delplan.setText(R.string.plan_OK);
            Dlg_delplan.setOnClickListener(del_planOK);

            cancelDlg_delplan = (Button) delDlg.findViewById(R.id.alert_btnCancel);//取消刪除
            cancelDlg_delplan.setText(R.string.plan_cel);
            cancelDlg_delplan.setOnClickListener(del_planNO);
            delDlg.show();
        }
        private View.OnClickListener del_planOK= new View.OnClickListener() { //確認刪除
            @Override
            public void onClick(View v) {
                // 執行MySQL刪除
//                old_index=index;
//                ArrayList<String> planIDs = new ArrayList<>();
//                planIDs.add(planID);
//                try {
//                    Thread.sleep(100); //  延遲Thread 睡眠0.5秒
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//-----------------------------------------------
                    String result = DBConnector20.executeDelet(now_planID);

//                    Log.d(TAG, "Delete result:" + result);
                // ---------------------------
                dbmysql();
                recSet=dbHper.getRecSet();
                alreadyplan();
                if(recSet.size()==0)
                {
                    noplan();
                }
                // ---------------------------
//                index=old_index;
//                u_setspinner();
//                if (index == dbHper.RecCount()) {
//                    index--;
//                }
//                showRec(index);
////                    mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆
////                }
//                msg = "資料已刪除" ;
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                delDlg.cancel();
            }
        };
        private View.OnClickListener del_planNO= new View.OnClickListener() { //取消刪除
            @Override
            public void onClick(View v) {
                delDlg.cancel();
            }
        };
    };
    private void u_setspinner() {//重設計畫名稱小窗
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        for (int i = 0; i < recSet.size(); i++) {
            String[] fld = recSet.get(i).split("#");
            adapter.add(fld[0] + " " + fld[1] + " " + fld[2] + " " + fld[3]);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s001planname.setAdapter(adapter);
        s001planname.setOnItemSelectedListener(select_plan);
        //        mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆
    }
    //編輯已經建立的計畫=======================================
    private View.OnClickListener edit_plan = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(recSet.size()==0)
            {
                Toast.makeText(getApplicationContext(),"無計畫，請先新增計畫",Toast.LENGTH_LONG).show();
                return;
            }

            editDlg = new Dialog(Bilerlife_plan.this);
            editDlg.setTitle(getString(R.string.plan_editplan));
            editDlg.setCancelable(false);
            editDlg.setContentView(R.layout.biker_plan_newplan);  //選擇layout
            edittitle = (TextView) editDlg.findViewById(R.id.newplan_new);
            edittitle.setText(R.string.plan_editplan);
            e001 = (EditText) editDlg.findViewById(R.id.newplan_e001);//計畫名稱

            e001.setText(s001planname.getSelectedItem().toString());
            e002 = (EditText) editDlg.findViewById(R.id.newplan_e002);//計畫日期
//            e002.setText(planday);
            e002.setOnFocusChangeListener(editplan_clander);
            s001 = (Spinner) editDlg.findViewById(R.id.newplan_s001); //選擇地區

            writeedit_plan = (Button) editDlg.findViewById(R.id.newplan_b003);//確定更新計畫內容
            canceledit_plan = (Button) editDlg.findViewById(R.id.newplan_b002);//取消更新計畫
            writeedit_plan.setOnClickListener(edit_planOK);
            canceledit_plan.setOnClickListener(edit_planNO);
            editDlg.show();
        }
        private View.OnFocusChangeListener editplan_clander = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showdatelog();
            }
        };
        private View.OnClickListener edit_planOK = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                old_index=index;
                mysql_update();
                dbmysql(); //清空sqlite 重新匯入mysql資料至sqlite
                //------------------
                recSet=dbHper.getRecSet();
//                u_setspinner();
                index=old_index;
                showRec(index);
                alreadyplan();
                editDlg.cancel();
            }
        };
        private View.OnClickListener edit_planNO=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDlg.cancel();
            }
        };
    };

    //確定寫入編輯已建立的計畫======================================================
    private void mysql_update() {
        planname=e001.getText().toString().trim();
        planday=e002.getText().toString().trim();
        planplace=s001.getSelectedItem().toString().trim();

        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(now_planID);
        nameValuePairs.add(U_ID);
        nameValuePairs.add(planday);
        nameValuePairs.add(planname);
        nameValuePairs.add(planplace);


        try {
            Thread.sleep(100); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //-----------------------------------------------
        String result = DBConnector20.executeUpdate( nameValuePairs);
        //-----------------------------------------------

    }

    //點選即時天氣從openweather獲得資料(不做寫入所以不管有沒有建立mysql都可使用)================================
    private View.OnClickListener get_now = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(), getString(R.string.loadover), Toast.LENGTH_SHORT).show();
            //從openweather抓資料(抓天氣資料&icon)==================================================================

//        //********設定轉圈圈進度對話盒*****************************
//        final ProgressDialog pd = new ProgressDialog(Bilerlife_plan.this);
//
//        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pd.setTitle("Internet");
//        pd.setMessage("Loading.........");
//        pd.setIndeterminate(false);
//        pd.show();
//        //**********************************************************
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            com.bikerlifeYang.WeatherService service = retrofit.create(com.bikerlifeYang.WeatherService.class);

            Call<WeatherResponse> call = service.getCurrentWeatherData(lat, lon, lang, AppId);
            call.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                    if (response.code() == 200) {
                        WeatherResponse weatherResponse = response.body();
                        assert weatherResponse != null;
//                    String stringBuilder = getString(R.string.country) +
//                            weatherResponse.sys.country +
//                            "\n" +
//                            getString(R.string.areaname) +
//                            weatherResponse.name +
//                            "\n" +
//                            getString(R.string.Temperature) +
//// --------------- K°凱氏轉攝氏C°-------------------
//                            (int) (Float.parseFloat("" + weatherResponse.main.temp) - 273.15) +"C°"+
//                            "\n" +
//                            getString(R.string.Temperature_Min) +
//                            (int) (Float.parseFloat("" + weatherResponse.main.temp_min) - 273.15) +"C°"+
//                            "\n" +
//                            getString(R.string.Temperature_Max) +
//                            (int) (Float.parseFloat("" + weatherResponse.main.temp_max) - 273.15) +"C°"+
//                            "\n" +
//                            getString(R.string.Humidity) +
//                            weatherResponse.main.humidity +
//                            "\n" +
//                            getString(R.string.Pressure) +
//                            weatherResponse.main.pressure;
//                    weatherData.setText(stringBuilder); //描述
////====填入座標==============
//                    weatherLat.setText(getString(R.string.weather_lat) + (lat));
//                    weatherLon.setText(getString(R.string.weather_lon) + (lon));
////======抓取 Internet 圖片==================
//                    int b_id = weatherResponse.weather.get(0).id;
//                    String b_main = weatherResponse.weather.get(0).main;
//                    String b_description = weatherResponse.weather.get(0).description;
//                    String b_icon = weatherResponse.weather.get(0).icon;
//                    iconurl = "https://openweathermap.org/img/wn/" + b_icon + "@2x.png";  //icon兩倍大
//// iconurl = "https://openweathermap.org/img/wn/" + b_icon + "@2x.png";
//// https://openweathermap.org/img/wn/50n@2x.png
//                    int cc = 1;
//                    String weather = "\n" +
//                            getString(R.string.w_description) +
//                            b_description +
//                            "\n" +
//                            getString(R.string.w_icon) +
//                            "\n" +
//                            iconurl;
////=========================
//                    weatherData.append(weather);
//*****************使用 AyncTask 非同步執行續**********************************
// AsyncTask非同步任務，或稱異步任務，是一個相當常用的類別，是專門用來處理背景任務與UI的類別。
// Android 4.0 之後，有明文規定所有的網路行為都不能在主執行緒(Main Thread)執行，
// 主執行緒又稱UI執行緒(UI Thread)，任何有關UI的東西都在主執行緒中執行，若是你的程式佔據主執行緒很久，使用者體驗會非常的差。
// 想像一下，按了一個按鈕後，整個App停住五秒會是怎樣的感覺，因此許多耗時的程式建議寫在背景執行，而其中最常見的就是網路的功能。
// AsyncTask<Params, Progress, Result>，這是基本的架構，使用泛型來定義參數，
// 泛型意思是，你可以定義任意的資料型態給他。
// Params ： 參數，你要餵什麼樣的參數給它。
// Progress ： 進度條，進度條的資料型態要用哪種
// Result ： 結果，你希望這個背景任務最後會有什麼樣的結果回傳給你。
// 此外，AsyncTask會有四個步驟。
//
// onPreExecute ： 執行前，一些基本設定可以在這邊做。
// doInBackground ： 執行中，在背景做任務。
// onProgressUpdate ： 執行中，當你呼叫publishProgress的時候會到這邊，可以告知使用者進度。
// onPostExecute ： 執行後，最後的結果會在這邊。
// https://developer.android.com/reference/android/os/AsyncTask
////This class was deprecated in API level 30.
////Use the standard java.util.concurrent or Kotlin concurrency utilities instead.

//                    new AsyncTask<String, Void, Bitmap>() {
//
//                        @Override
//                        protected Bitmap doInBackground(String... strings) {
//                            String url = iconurl;
//                            return getBitmapFromURL(url);
//                        }
//
//                        @Override
//                        protected void onPostExecute(Bitmap result) //當doinbackground完成後
//                        {
//                            weatherimg.setImageBitmap(result);
//                            pd.cancel();
//                            super.onPostExecute(result);
//                        }
//                    }.execute(iconurl);
// ***************************************************************************
/*+++++++++++++++++++++
+ 使用Picasso網路照片 +
+++++++++++++++++++++*/
////----------- implementation 'com.squareup.picasso:picasso:2.71828'
// Picasso.get()
// .load(iconurl)
// .into(weatherimg);
// pd.cancel();
//////-----------------------------------------------------------
// **********************************************************************************
                    }
                }
                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) { }
            });
        }
    };
    //滑動更新自行車道===============================================
    private final SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            //-----------------滑動就直接開始執行下載----------------
            laySwipe.setRefreshing(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    =================================
                    setDatatolist();
//                  =================================
                    //----------SwipeLayout 結束 --------
                    //可改放到最終位置 u_importopendata()
                    laySwipe.setRefreshing(false);
                }
            }, 10000);  //10秒
        }
    };
    //下載自行車道opendata============================================
    private void setDatatolist() {
//        //==================================
        u_importopendata();  //下載Opendata
//        //==================================
        //設定Adapter
        final ArrayList<Post20> mData = new ArrayList<>();
        for (Map<String, Object> m : roadList) {
            if (m != null) {
                String Name = m.get("Name").toString().trim(); //名稱
                String Add = m.get("Add").toString().trim(); //住址
                String Description = m.get("Description").toString().trim(); //描述
//************************************************************
                mData.add(new Post20(Name , Add, Description));
//************************************************************
            } else {
                return;
            }
        }
        RoadAdapter adapter = new RoadAdapter(this, mData);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//// ************************************
//        //===按取第幾筆就在上方顯示內容=================
//        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                li01.setVisibility(View.VISIBLE);
////                Toast.makeText(M2205.this, "onclick" + mData.get(position).hotelName.toString(), Toast.LENGTH_SHORT).show();
//                mTxtResult.setText(getString(R.string.m2206_name) + mData.get(position).Name);
//                mDesc.setText(mData.get(position).Content);
//                mDesc.scrollTo(0, 0); //textview 回頂端
//                nowposition = position;
//                t_count.setText(getString(R.string.ncount) + total + "/" + t_total + "   (" + (nowposition + 1) + ")");
//            }
//        });
////********************************* ****
//        recyclerView.setAdapter(adapter);
    }
//    Id: "C7_315080500H_000001",
//    IOTId: null,
//    RoadId: "",
//    RoadNo: "",
//    Systype: "0",
//    Name: "三仙臺-成功鎮自行車道",
//    S_PlaceDes: "台東縣成功鎮",
//    E_PlaceDes: "台東縣成功鎮",
//    Toldescribe: "由新港漁港沿海濱公園往三仙台方向，1500公尺長的三仙台自行車道環繞三仙台至基翬路段的海岸林中。騎乘此路線，成功鎮有趣的魚市交易與新鮮的海產您一定不能錯過，而當穿越礫石灘珊瑚礁區時，豐富的生態環境，很適合親子共同騎乘。",
//    Description: "由新港漁港沿海濱公園往三仙台方向，1500公尺長的三仙台自行車道環繞三仙台至基翬路段的海岸林中。騎乘此路線，成功鎮有趣的魚市交易與新鮮的海產您一定不能錯過，而當穿越礫石灘珊瑚礁區時，豐富的生態環境，很適合親子共同騎乘。",
//    Bike_length: "8460",
//    Bike_width: "3",
//    Road_width: "15",
//    Hight: "25",
//    Slope: "3",
//    Lamp: "1",
//    Direction: "2",
//    RoadType: "",
//    Pave: "1",
//    Class1: "2",
//    Class2: "0",
//    Class3: "0",
//    STRUCTURE: "4",
//    CS: "1",
//    Add: "台東縣成功鎮三仙里基翬路74號",
//    Tel: "886-89-841520",
//    Region: "台東縣",
//    town: "成功鎮",
//    Travellinginfo: "起點東17線109k， 終點東17線114k",
//    Picture1: "",
//    Picdescribe1: "",
//    Picture2: "",
//    Picdescribe2: "",
//    Picture3: "",
//    Picdescribe3: "",
//    Map: "http://www.eastcoast-nsa.gov.tw/zh-tw/Travel/Cycling/ClosePacificOcean",
//    Gov: "315080500H",
//    Website: "",
//    Remarks: "",

    private void u_importopendata() { //下載Opendata
        try {
            String Task_opendata
                    //交通部觀光局自行車道開放資料
                    = new TransTask().execute("https://gis.taiwan.net.tw/XMLReleaseALL_public/Bike_f.json").get();
            //-------解析 json   帶有多層結構-------------
            roadList = new ArrayList<Map<String, Object>>();
            JSONObject json_obj1 = new JSONObject(Task_opendata);
            JSONObject json_obj2 = json_obj1.getJSONObject("XML_Head");
            JSONObject infos = json_obj2.getJSONObject("Infos");
            JSONArray info = infos.getJSONArray("Info");
            total = 0;
            //-----開始逐筆轉換-----
            for (int i = 0; i < info.length(); i++) {
                Map<String, Object> item = new HashMap<String, Object>();
                String Name = info.getJSONObject(i).getString("Name");
//                String Description = info.getJSONObject(i).getString("Description");
                String Description = info.getJSONObject(i).getString("Toldescribe");
                String Add = info.getJSONObject(i).getString("Add");
                String Picture1 = info.getJSONObject(i).getString("Picture1");

                item.put("Name", Name);
                item.put("Description", Description);
                item.put("Add", Add);
                item.put("Picture1", Picture1);
                roadList.add(item);
//-------------------
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//----------SwipeLayout 結束 --------
    }

    //選取spinner並顯示======================================================
    private AdapterView.OnItemSelectedListener select_plan=new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView parent, View view, int position, long id) {
            iSelect = s001planname.getSelectedItemPosition(); //找到項目
            now_planID=spinner_index.get(iSelect);
            //項目的id
            String[] fld = recSet.get(iSelect).split("#");
            t002.setText(fld[2]);
            t004.setText(fld[3]);
//            Toast.makeText(getApplicationContext(),s001planname.getSelectedItem().toString(),Toast.LENGTH_LONG).show();

            opendata();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    //跳到最後一筆=================================================
    private void ctlLast() {
        index=recSet.size()-1;
        showRec(index);
    }
    //讀取mysql==================================================
    private void dbmysql() {

        try {
            int rowsAffected = dbHper.clearRec();                 // 匯入前,刪除所有SQLite資料
            String result = DBConnector20.executeQuery(U_ID);

            /**************************************************************************
             * SQL 結果有多筆資料時使用JSONArray
             * 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            JSONArray jsonArray = new JSONArray(result);
            // -------------------------------------------------------
            if (jsonArray.length() > 0) { // MySQL 連結成功有資料
//--------------------------------------------------------

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
                    // -------------------加入SQLite---------------------------------------
                    long rowID = dbHper.insertRec_m(newRow);
                }
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
            dbHper.FindRec(U_ID);  //重新載入SQLite
            // --------------------------------------------------------
        } catch (Exception e) {
        }
    }
    //新增資料到mysql===============================================
    private void mysql_insert() {
        ArrayList<String> nameValuePairs=new ArrayList<>();
        nameValuePairs.add(U_ID);
        nameValuePairs.add(planday);
        nameValuePairs.add(planplace);
        nameValuePairs.add(planname);
        try {
            Thread.sleep(100); // 延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //真正執行新增mysql-----------------------------------------------
        String result = DBConnector20.executeInsert(nameValuePairs);
        //-----------------------------------------------
    }
    //日曆物件============================================================
    private void showdatelog() {
        now= Calendar.getInstance();
        DatePickerDialog datePickDog=new DatePickerDialog(
                this,showdatelog,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        datePickDog.setTitle(getString(R.string.plan_datetitle));
        datePickDog.setMessage(getString(R.string.plan_datemessage));
        datePickDog.setIcon(getDrawable(R.drawable.clockblack));
        datePickDog.setCancelable(false);
        datePickDog.show();
    }
    private DatePickerDialog.OnDateSetListener showdatelog = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            u_date=
                    (year) + getString(R.string.plan_m)+
                            String.format("%02d",month + 1) + getString(R.string.plan_m) +
                            String.format("%02d",dayOfMonth) ;
            //鎖住過去日期======================================
            if(month== now.get(Calendar.MONTH) && dayOfMonth<now.get(Calendar.DAY_OF_MONTH) &&year<=now.get(Calendar.YEAR)){
                Toast.makeText(getApplicationContext(),getString(R.string.plan_waring_time),Toast.LENGTH_LONG).show();
                showdatelog();
            }
            else if(month<=now.get(Calendar.MONTH) && dayOfMonth<=now.get(Calendar.DAY_OF_MONTH) && year<now.get(Calendar.YEAR))
            {
                Toast.makeText(getApplicationContext(),getString(R.string.plan_waring_time),Toast.LENGTH_LONG).show();
                showdatelog();
            }else
            {
                e002.setText(u_date);
            }
        }
    };
    //抓取天氣資料==================================================================
    private void  opendata(){
        //********設定轉圈圈進度對話盒*****************************
        final ProgressDialog pd = new ProgressDialog(Bilerlife_plan.this);

        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Internet");
        pd.setMessage("Loading.........");
        pd.setIndeterminate(false);
        pd.show();

        try
        {
            String Task_opendata
                    = new TransTask().execute("https://opendata.cwb.gov.tw/fileapi/v1/opendataapi/F-C0032-005?Authorization=CWB-44F24E37-702E-4002-A2CD-5ECAD2C1B938&format=JSON").get();
            JSONObject jsonData=new JSONObject(Task_opendata);

            String cwbopen= jsonData.getString("cwbopendata");
            JSONObject o_cwbopen=new JSONObject(cwbopen);

            String dataset=o_cwbopen.getString("dataset");
            JSONObject o_dataset=new JSONObject(dataset);

            String location=o_dataset.getString("location");
            JSONArray locationArray=new JSONArray(location);

            //===============逐筆轉換=========================
            for (int i = 0; i < locationArray.length(); i++) {
                JSONObject o_locationName=locationArray.getJSONObject(i);
                String locationName=o_locationName.getString("locationName");

                if(locationName.equals(t004.getText().toString())){
                    //==================比對寫入的縣市
                    String weatherElement=o_locationName.getString("weatherElement");
                    JSONArray weatherElementArray=new JSONArray(weatherElement);

                    //天氣狀況=0
                    JSONObject weatherElement_0=weatherElementArray.getJSONObject(0);
                    String time_0=weatherElement_0.getString("time");
                    JSONArray timeArray0=new JSONArray(time_0);

                    //日最高溫=1
                    JSONObject weatherElement_1=weatherElementArray.getJSONObject(1);
                    String time_1 = weatherElement_1.getString("time");
                    JSONArray timeArray1=new JSONArray(time_1);

                    //日最低溫=2
                    JSONObject weatherElement_2=weatherElementArray.getJSONObject(2);
                    String time_2 = weatherElement_2.getString("time");
                    JSONArray timeArray2=new JSONArray(time_2);

                    int data_count=14;//計算是否沒有資料
                    int q=0;//區分白天晚上兩組天氣
                    //===================比對寫入的時間抓出天氣time{0}
                    for (int ii = 0; ii < timeArray0.length(); ii++){
                        JSONObject time_0_ii=timeArray0.getJSONObject(ii);
                        JSONObject time_1_ii=timeArray1.getJSONObject(ii);
                        JSONObject time_2_ii=timeArray2.getJSONObject(ii);

                        String startTime = time_0_ii.getString("startTime").substring(0, 10);

                        if (t002.getText().toString().equals(startTime)){ //如果寫入時間符合 抓天氣
                            String parameter_0 = time_0_ii.getString("parameter");
                            String parameter_1 = time_1_ii.getString("parameter");
                            String parameter_2 = time_2_ii.getString("parameter");

                            JSONObject o_parameter_0=new JSONObject(parameter_0);
                            JSONObject o_parameter_1=new JSONObject(parameter_1);
                            JSONObject o_parameter_2=new JSONObject(parameter_2);

                            String weather= o_parameter_0.getString("parameterName");//天氣
                            String tempMAX=o_parameter_1.getString("parameterName");//高溫
                            String tempLOW=o_parameter_2.getString("parameterName");//低溫

                            int weathercount=2;
                            //======會抓出白天晚上兩筆資料 =====分成兩組
                            if(q==0){ //白天組
                                dayweather.setText(weather);//天氣
                                daytemp_mx.setText(getString(R.string.plan_t003_01)+tempMAX+getString(R.string.plan_do));//高溫
                                daytemp_lo.setText(getString(R.string.plan_t003_02)+tempLOW+getString(R.string.plan_do));//低溫
                                q++;
                            }
                            else if(q==1){ //晚上組
                                nightweather.setText(weather);//天氣
                                nighttemp_mx.setText(getString(R.string.plan_t003_01)+tempMAX+getString(R.string.plan_do));//高溫
                                nighttemp_lo.setText(getString(R.string.plan_t003_02)+tempLOW+getString(R.string.plan_do));//低溫
                            }
                            //設定建議穿著 白天最高溫25度以上設置短袖
                            int mx = Integer.valueOf(tempMAX);
                            if(mx>=25){
                                clothe.setImageResource(R.drawable.sports_run_syoumen_man);
// clothe.setImageDrawable(getResources().getDrawable(R.drawable.sports_run_syoumen_man));
                            }else if(mx<=24){
                                clothe.setImageResource(R.drawable.sports_run_syoumen_man2);
// clothe.setImageDrawable(getResources().getDrawable(R.drawable.sports_run_syoumen_man2));
                            }else {
                                clothe.setVisibility(View.INVISIBLE);
                            }
//                        if (weathercount==1){//晚上6點後會剩下晚上資料，白天組要顯示沒有資訊
//                            weathercount--;
//                            dayweather.setText(weather);
//                            daytemp_mx.setText("");
//                            daytemp_lo.setText("");
//                        }

                        }else{  //超出opendata可統計範圍
                            data_count--;
                        }
                        if(data_count==0){
                            dayweather.setText(R.string.plan_nodata);
                            nightweather.setText(R.string.plan_nodata);
                            daytemp_mx.setText("");
                            daytemp_lo.setText("");
                            nighttemp_mx.setText("");
                            nighttemp_lo.setText("");
                        }
                    }
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        pd.cancel();
    }
    //===========JSON固定方法=====================
    private class TransTask extends AsyncTask<String, Void, String> {
        String ans;
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
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("s", "s:" + s);
            parseJson(s);
        }
        private void parseJson(String s) {
        }
    }
    //生命週期===============================
    @Override
    protected void onRestart() {
        super.onRestart();
        if (dbHper == null)
            dbHper = new FriendDbHelper20(this, DB_FILE, null, DBversion);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent it = new Intent();
        if (dbHper == null)
            dbHper = new FriendDbHelper20(this, DB_FILE, null, DBversion);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (dbHper == null)
            dbHper = new FriendDbHelper20(this, DB_FILE, null, DBversion);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (dbHper != null)
        {
            dbHper.close();
            dbHper = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dbHper != null)
        {
            dbHper.close();
            dbHper = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHper != null)
        {
            dbHper.close();
            dbHper = null;
        }
    }
    //===================================================
    private void Signin_menu() {//登入時顯示的menu
        menu.setGroupVisible(R.id.g01, false);
        menu.setGroupVisible(R.id.g02, false);
    }

    //↓↓↓↓↓除了亮瑜使用此方法外，其他人原則上都使用Signin_menu↑↑的方法顯示menu item（167行已經寫好(亮瑜的部分改167行)）
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
        Signin_menu(); //登入menu的選項
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)//請自行inten至各個畫面(未整合前先註解起來)　item01~03亮瑜改就好
    {
        switch (item.getItemId()) {
            case R.id.Item04:
                //   Toast.makeText(getApplicationContext(), getString(R.string.menu_signout), Toast.LENGTH_LONG).show();
//                intent.setClass(Biker_reward.this, Biker_login.class);
//                startActivity(intent);
                //                this.finish();
                break;

            case R.id.Item05:
                //   Toast.makeText(getApplicationContext(), getString(R.string.menu_management), Toast.LENGTH_LONG).show();//此處先不用
                break;
            case R.id.Item06:
                //   Toast.makeText(getApplicationContext(), getString(R.string.menu_connection), Toast.LENGTH_LONG).show();//intent至Biker_team_information
                intent.setClass(Bilerlife_plan.this, Biker_team_information.class);
                startActivity(intent);
                this.finish();
                break;
            case R.id.Item07:
                //   Toast.makeText(getApplicationContext(), getString(R.string.menu_home), Toast.LENGTH_LONG).show();
//                intent.setClass(Biker_reward.this, Biker_home.class);
//                startActivity(intent);
//                this.finish();
                break;
            case R.id.Item08:
                //   Toast.makeText(getApplicationContext(), getString(R.string.menu_train), Toast.LENGTH_LONG).show();
//                intent.setClass(Biker_reward.this,  Biker_train.class);
//                startActivity(intent);
//                this.finish();
                break;
            case R.id.Item09:
                //   Toast.makeText(getApplicationContext(), getString(R.string.menu_plan), Toast.LENGTH_LONG).show();
//                intent.setClass(Biker_reward.this,  Bilerlife_plan.class);
//                startActivity(intent);
//                this.finish();
                break;
            case R.id.Item10:
                //    Toast.makeText(getApplicationContext(), getString(R.string.menu_find), Toast.LENGTH_LONG).show();
//                intent.setClass(Biker_reward.this,  Biker_find.class);
//                startActivity(intent);
//                this.finish();
                break;
            case R.id.Item11:
                //    Toast.makeText(getApplicationContext(), getString(R.string.menu_profile), Toast.LENGTH_LONG).show();
//                intent.setClass(Biker_reward.this,  Biker_profile.class);
//                startActivity(intent);
//                this.finish();
                break;
            case R.id.Item12:
                //   Toast.makeText(getApplicationContext(), getString(R.string.menu_time_stopwatch), Toast.LENGTH_LONG).show();
//                intent.setClass(Biker_reward.this,  Biker_time_Stopwatch.class);
//                startActivity(intent);
//                this.finish();
                break;
            case R.id.Item13:
                //   Toast.makeText(getApplicationContext(), getString(R.string.menu_time_map), Toast.LENGTH_LONG).show();
//                intent.setClass(Biker_reward.this,  Biker_time_map.class);
//                startActivity(intent);
//                this.finish();
                break;
            case R.id.Item14:
                //   Toast.makeText(getApplicationContext(), getString(R.string.menu_join), Toast.LENGTH_LONG).show();
//                intent.setClass(Biker_reward.this,  Biker_Join.class);
//                startActivity(intent);
//                this.finish();
                break;
            case R.id.Item15:
                //    Toast.makeText(getApplicationContext(), getString(R.string.menu_reward), Toast.LENGTH_LONG).show();
                break;


            case R.id.action_settings: //原則上不用修改
                //    Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}