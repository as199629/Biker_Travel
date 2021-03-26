package com.bikerlifeYang;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class  Biker_train extends AppCompatActivity implements View.OnClickListener {

    private Button b02;
    private Intent intent = new Intent();
    private TextView t01;
    private ProgressBar p01,p02,p03,p04;
    private Dialog AlertDig;
    private Menu menu;
    private static final String DB_FILE = "bikerlife.db",DB_TABLE_I100 = "I100";
    private int DBversion;
    private FriendDbHelper23 dbHper;
    private SQLiteDatabase database;
    private RelativeLayout RtL;
    private int index = 0;
    Calendar overtime_All = Calendar.getInstance();
    Calendar rightnow = Calendar.getInstance();
    private String U_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_train);
        enableStrictMode(this);//加到MYSQL一定要加的方法
        setupViewComponent();
        initDB();

        //------------取得USER_ID-------------------
        SharedPreferences xxx=getSharedPreferences("USER_ID",0);
        U_ID=xxx.getString("USER_ID","");
    }

    public static void enableStrictMode(Context context) {
        StrictMode.setThreadPolicy(
        //-------------抓取遠端資料庫設定執行續------------------------------
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

    private void setupViewComponent() {
        b02=(Button)findViewById(R.id.train_b002);
        t01=(TextView)findViewById(R.id.train_t001);
        p01=(ProgressBar)findViewById(R.id.train_p001);
        p02=(ProgressBar)findViewById(R.id.train_p002);
        p03=(ProgressBar)findViewById(R.id.train_p003);
        p04=(ProgressBar)findViewById(R.id.train_p004);
        RtL=(RelativeLayout)findViewById(R.id.RelativeLay);

        b02.setOnClickListener(this);
        t01.setOnClickListener(this);//測試新增假資料按鈕

        DBversion=Integer.parseInt(getString(R.string.SQLite_version));
        initDB();//開啟資料庫連線

        // 動態調整高度 抓取使用裝置尺寸
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newscrollheight = displayMetrics.heightPixels * 60 / 100; // 設定ScrollView使用尺寸的4/5
        RtL.getLayoutParams().height = newscrollheight;
        RtL.setLayoutParams(RtL.getLayoutParams()); // 重定ScrollView大小
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.train_b002:         //---------Alertdialog--------------
                AlertDig=new Dialog(Biker_train.this);
                AlertDig.setCancelable(false);//不能按其他地方
                AlertDig.setContentView(R.layout.alert_dialog);//選擇layout

                Button alertBtnOK = (Button)AlertDig.findViewById(R.id.train_btnOK);
                Button alertBtnCancel = (Button)AlertDig.findViewById(R.id.train_btnCancel);

                alertBtnOK.setOnClickListener(this);
                alertBtnCancel.setOnClickListener(this);
                AlertDig.show();
                break;

            case R.id.train_btnOK://按下確定就刪除資料庫裡的Table
                dbHper.clearRec();
                mysql_del();//執行MySQL刪除
                if (index == dbHper.RecCount()) {
                    index--;
                }
                //-------------------------------------
                intent = new Intent();
                intent.setClass(Biker_train.this,Biker_train1.class);
                startActivity(intent);
                finish();
                break;

            case  R.id.train_btnCancel://按下取消按鈕就取消此程式
                AlertDig.cancel();
                break;
        }
    }

    //=============SQLite方法===================
    private void initDB() {
        if (dbHper == null) {
            dbHper = new FriendDbHelper23(this, DB_FILE, null, DBversion);
        }
        dbHper.FindRec(U_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

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
        initDB();//開啟資料庫連線
        DB_AutoUpdatePage();//刷新頁面
        DB_CheckSystemTime();//超過使用者設定時間，就刪除資料庫
    }

    //-----------------------取得資料庫內容並更改進度條-------------------------
    private void DB_AutoUpdatePage() {
        // -------執行匯入MySQL
        dbmysql();
        dbHper.FindRec(U_ID);  //重新載入SQLite
        // -------執行匯入MySQL

        //讀取SQLite資料表
        database= dbHper.getReadableDatabase();
        Cursor I100 = database.query(//目標的資料庫
                true,
                DB_TABLE_I100,
                new String[]{"I103", "I104", "I105","I106","I107"},
                null,   //I103 FLOAT 預計騎乘里程 ， I104 FLOAT 預計爬升高度 ，
                null, //I105 INTEGER 預計騎乘時間，I106 TEXT 訓練結束日期，I107 TEXT 訓練開始日期
                null,
                null,
                null,
                null);
        if (I100 == null || I100.getCount() == 0) {
            return;//沒有資料表的話就回去
        }
        else {
            //----------------------------------測試  符合日期才執行---------------------------------------
            I100.moveToFirst(); //撈取的資料會是第一筆

            //----------------取出符合訓練時間範圍的資料表---------------
            String start_time =(I100.getString(4));
            String end_time =(I100.getString(3));

            ArrayList<Float> mList = new ArrayList<Float>();
            mList = dbHper.getRecord_B100(U_ID, start_time, end_time);//取得B103 B104 B104


            if(mList == null){
                p01.setProgress(0);     //累積里程
                p02.setProgress(0);     //爬升高度
                p03.setProgress(0);     //騎乘時間
                p04.setProgress(0);     //整體進度
            }else{
                Float B103 =mList.get(0);
                Float B104 =mList.get(1);
                Float B105 =mList.get(2);
                float pg1 = (B103 / I100.getInt(0) ) * 100;    //累積里程百分比
                if(pg1>=100) pg1=100;

                float pg2 = (B104 / I100.getInt(1) ) * 100;     //爬升高度百分比
                if(pg2>=100) pg2=100;

                float pg3a = I100.getInt(2);
                float pg3 = (B105 / pg3a) * 100;  //騎乘時間百分比
                if(pg3>=100) pg3=100;

                float pg4 = ( ((int) (pg1)) + ((int) (pg2)) + ((int) (pg3)) ) / 3;    //整體進度百分比

                p01.setProgress((int) (pg1));   //累積里程
                p02.setProgress((int) (pg2));   //爬升高度
                p03.setProgress((int) (pg3));   //騎乘時間
                p04.setProgress((int) (pg4));   //整體進度

                if(pg4==100){
                    //==========測試中==============
                    //完成訓練-Dialog"訓練挑戰成功"!!，再跳轉到設定訓練
                    AlertDig=new Dialog(Biker_train.this);
                    AlertDig.setCancelable(false);//不能按其他地方
                    AlertDig.setContentView(R.layout.train_dialog);//選擇layout
                    AlertDig.setTitle(getString(R.string.train_dialog_title));
                    TextView warning = (TextView) AlertDig.findViewById(R.id.tarin_waring);//更改字
                    warning.setText(getString(R.string.train_dialog_text));//更改dialog的字
                    TextView warning2 = (TextView) AlertDig.findViewById(R.id.tarin_waring2);//更改字
                    warning2.setText(getString(R.string.train_dialog_text2));//更改dialog的字

                    Button over_BtnOK = (Button)AlertDig.findViewById(R.id.train_btnOK);

                    over_BtnOK.setOnClickListener(complete_or_fail);
                    AlertDig.show();
                    //==========測試中==============
                }
            }
            t01.setText("於" + I100.getString(3) + "完成");
        }
        //database.close(); //關閉資料庫
    }

    //----------------超過使用者設定時間，就刪除資料庫功能------------
    private void DB_CheckSystemTime() {

        //讀取SQLite資料表
        database= dbHper.getReadableDatabase();
        Cursor I100 = database.query(
                true,
                DB_TABLE_I100,
                new String[]{"I106"},//之後需要更改為已做運算的資料表名稱
                null,
                null,
                null,
                null,
                null,
                null);
        if (I100 == null) {
            return;//沒有資料表的話就回去
        } if (I100.getCount() == 0) {//有資料表但裡面沒資料的話
            return;//沒有資料表的話就回去
        } else {
            Calendar calendar = Calendar.getInstance();//获取系统的日期
            int year = calendar.get(Calendar.YEAR);//年
            int month = calendar.get(Calendar.MONTH)+1;//月
            int day = calendar.get(Calendar.DAY_OF_MONTH);//日
            rightnow.set(year,month,day);

            //訓練結束日期
            I100.moveToFirst();
            String[] overtime =(I100.getString(0)).split("-");
            String over_year = overtime[0];
            String over_month = overtime[1];
            String over_date = overtime[2];
            int overtime_y = Integer.parseInt(over_year);
            int overtime_m = Integer.parseInt(over_month);
            int overtime_d = Integer.parseInt(over_date);
            overtime_All.set(overtime_y,overtime_m,overtime_d);

            if (rightnow.after(overtime_All)) {

                //==========測試中==============
                //超過目標時間跳出 Dialog"訓練挑戰不成功"!!，再跳轉到設定訓練
                AlertDig=new Dialog(Biker_train.this);
                AlertDig.setCancelable(false);//不能按其他地方
                AlertDig.setContentView(R.layout.train_dialog);//選擇layout

                Button over_BtnOK = (Button)AlertDig.findViewById(R.id.train_btnOK);

                over_BtnOK.setOnClickListener(complete_or_fail);
                AlertDig.show();
            }
        }
    }
    //----------------超過使用者設定時間，就刪除資料庫功能------------

    //---------------完成or失敗-刪除資料庫監聽------------
    private View.OnClickListener complete_or_fail = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                dbHper.delete();//刪除SQLITE(方法在TrainDbHelper)
                mysql_del();//刪除MYSQL
                intent = new Intent();
                intent.setClass(Biker_train.this,Biker_train1.class);
                startActivity(intent);
                finish();
        }};
    //----------------完成or失敗-刪除資料庫監聽------------

    //==============MYSQL=================
    // 讀取MySQL 資料
    private void dbmysql() {

        try {
            String result = DBConnector23.executeQuery(U_ID);

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

    private void mysql_del() {
        try {
            Thread.sleep(100); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DBConnector23.executeDelet(U_ID);//刪除使用者自己的訓練計劃
    }

    //==============MYSQL=================

    @Override
    protected void onStop() {
        super.onStop();
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }
    }

    //==============menu方法==============
    private void Signin_menu(){//登入時顯示的menu

        menu.setGroupVisible(R.id.g01,false);
        menu.setGroupVisible(R.id.g02,false);
    }

    @Override//註解的話可以鎖定返回鍵，無法使用
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.biker_menu,menu);
        this.menu=menu;
        Signin_menu(); //登入menu的選項
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)//menu返回設定
    {
        intent.setClass(Biker_train.this, Biker_home.class);//統整時打開
        startActivity(intent);//統整時打開
        finish();
        return super.onOptionsItemSelected(item);
    }
    //==============menu方法==============
}