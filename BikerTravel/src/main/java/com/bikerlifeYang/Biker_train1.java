package com.bikerlifeYang;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class Biker_train1 extends AppCompatActivity implements View.OnClickListener{

    private static final int FAST_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;
    private Spinner s001,s002,s003,s004;
    private Intent intent = new Intent();
    private Button ok_btn;
    private RelativeLayout r_layout;
    private CheckBox chb01;
    private EditText cus01,cus02,cus03,cus04;
    private Menu menu;
    private Dialog giveupDlg;
    private String t_day,t_km,t_high,t_totaltime;
    private static final String DB_FILE = "bikerlife.db";
    private int DBversion;
    private FriendDbHelper23 dbHper;
    private RelativeLayout RtL;
    private String format1;
    private String format;
    private String U_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_train1);
        enableStrictMode(this);//加到MYSQL一定要加的方法
        setupViewComponent();
        //------------取得USER_ID-------------------
        SharedPreferences xxx=getSharedPreferences("USER_ID",0);
        U_ID=xxx.getString("USER_ID","");
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

    private void setupViewComponent() {
        s001 = (Spinner) findViewById(R.id.train_s001);
        s002 = (Spinner) findViewById(R.id.train_s002);
        s003 = (Spinner) findViewById(R.id.train_s003);
        s004 = (Spinner) findViewById(R.id.train_s004);
        ok_btn = (Button) findViewById(R.id.train_b003);
        chb01 = (CheckBox) findViewById(R.id.train_chb001);
        cus01 = (EditText) findViewById(R.id.train_cus01);
        cus02 = (EditText) findViewById(R.id.train_cus02);
        cus03 = (EditText) findViewById(R.id.train_cus03);
        cus04 = (EditText) findViewById(R.id.train_cus04);
        RtL=(RelativeLayout)findViewById(R.id.RelativeLay);

        s001.setOnItemSelectedListener(s001On);
        s002.setOnItemSelectedListener(s001On);
        s003.setOnItemSelectedListener(s001On);
        s004.setOnItemSelectedListener(s001On);
        ok_btn.setOnClickListener(this);

        DBversion=Integer.parseInt(getString(R.string.SQLite_version));
        initDB();//開啟資料庫連線

        // 動態調整高度 抓取使用裝置尺寸
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newscrollheight = displayMetrics.heightPixels * 60 / 100; // 設定ScrollView使用尺寸的4/5
        RtL.getLayoutParams().height = newscrollheight;
        RtL.setLayoutParams(RtL.getLayoutParams()); // 重定ScrollView大小
        //-----

        //        ----開機動畫----
        r_layout=findViewById(R.id.train_r002);
        r_layout.setBackgroundResource(R.drawable.background_color);
        r_layout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_alpha_in));
        r_layout.setBackgroundResource(R.drawable.background_color);

        //-----使用自訂讓spinner消失，editText出現--------
        chb01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //---------------checkbox--------------------
                if (chb01.isChecked()){
                    s001.setVisibility(View.INVISIBLE);
                    s002.setVisibility(View.INVISIBLE);
                    s003.setVisibility(View.INVISIBLE);
                    s004.setVisibility(View.INVISIBLE);
                    cus01.setVisibility(View.VISIBLE);
                    cus02.setVisibility(View.VISIBLE);
                    cus03.setVisibility(View.VISIBLE);
                    cus04.setVisibility(View.VISIBLE);
                }
                else {
                    s001.setVisibility(View.VISIBLE);
                    s002.setVisibility(View.VISIBLE);
                    s003.setVisibility(View.VISIBLE);
                    s004.setVisibility(View.VISIBLE);
                    cus01.setVisibility(View.INVISIBLE);
                    cus02.setVisibility(View.INVISIBLE);
                    cus03.setVisibility(View.INVISIBLE);
                    cus04.setVisibility(View.INVISIBLE);
                    cus01.setText("");//如果自訂選項沒被選取的話就變空字串
                    cus02.setText("");
                    cus03.setText("");
                    cus04.setText("");
                }
            }
        });

        //------------設定 spinner  選項------------
        ArrayAdapter<CharSequence> adapSexList1 = ArrayAdapter
                .createFromResource(this, R.array.time,
                        R.layout.train_spinner);//將可選内容與ArrayAdapter連接起來
        adapSexList1.setDropDownViewResource(R.layout.train_spinner);//設置下拉列表的風格
        s001.setAdapter(adapSexList1);

        ArrayAdapter<CharSequence> adapSexList2 = ArrayAdapter
                .createFromResource(this, R.array.km,
                        R.layout.train_spinner);
        adapSexList1.setDropDownViewResource(R.layout.train_spinner);
        s002.setAdapter(adapSexList2);

        ArrayAdapter<CharSequence> adapSexList3 = ArrayAdapter
                .createFromResource(this, R.array.high,
                        R.layout.train_spinner);
        adapSexList1.setDropDownViewResource(R.layout.train_spinner);
        s003.setAdapter(adapSexList3);

        ArrayAdapter<CharSequence> adapSexList4 = ArrayAdapter
                .createFromResource(this, R.array.totaltime,
                        R.layout.train_spinner);
        adapSexList1.setDropDownViewResource(R.layout.train_spinner);
        s004.setAdapter(adapSexList4);
    }

    @Override
    public void onClick(View v) {
        //========傳值進入資料庫==================
        if(isFastClick()){
            if (chb01.isChecked()) {
                t_day = cus01.getText().toString().trim();//trim前後空白處剪掉
                t_km = cus02.getText().toString().trim();
                t_high = cus03.getText().toString().trim();
                t_totaltime = cus04.getText().toString().trim();
                if (t_day.equals("") || t_km.equals("") || t_high.equals("") || t_totaltime.equals("")) {
                    Toast.makeText(Biker_train1.this, getText(R.string.toast1), Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    //------------------設定結束時間----------------------------
                    // 獲取Calendar的實例，預設為當前系統時間
                    Calendar rightNow = Calendar.getInstance();
                    //轉型使用者輸入的值t_day
                    int tt_day = Integer.parseInt(t_day);
                    //將目前時間加上使用者輸入的值
                    rightNow.add(Calendar.DAY_OF_YEAR, tt_day);
                    //new   SimpleDateFormat 進行格式化
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
                    //利用Calendar的getTime方法，將時間轉為Date對象
                    Date date = rightNow.getTime();
                    //利用SimpleDateFormat對象 把Date對象格式化
                    format = sdf.format(date);//String型態

                    //------------------設定開始時間----------------------------
                    //獲取Calendar的實例，預設為當前系統時間
                    Calendar rightNow1 = Calendar.getInstance();
                    //new   SimpleDateFormat 進行格式化
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
                    //利用Calendar的getTime方法，將時間轉為Date對象
                    Date date1 = rightNow1.getTime();
                    //利用SimpleDateFormat對象 把Date對象格式化
                    format1 = sdf1.format(date1);//String型態

                    //-----傳值進入Mysql------------------------------
                    dbHper.clearRec();
                    mysql_del();//執行MySQL刪除();//執行MySQL刪除
                    mysql_insert();
                    dbmysql();
                    //-------------------------------------
                    long rowID = dbHper.RecCount();
                    if (rowID != -1) {
                        cus01.setText("");
                        cus02.setText("");
                        cus03.setText("");
                        cus04.setText("");
                    }
                }
            }else{
                //------------------設定結束時間----------------------------
                //獲取Calendar的實例，預設為當前系統時間
                Calendar rightNow = Calendar.getInstance();
                //轉型使用者輸入的值t_day
                int tt_day = Integer.parseInt(t_day);
                //將目前時間加上使用者輸入的值
                rightNow.add(Calendar.DAY_OF_YEAR, tt_day);
                //new SimgpleDateFormat 進行格式化
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
                //利用Calendar的getTime方法，將時間轉為Date對象
                Date date = rightNow.getTime();
                //利用SimpleDateFormat對象 把Date對象格式化
                format = sdf.format(date);//String型態
                //------------------設定結束時間----------------------------

                //------------------設定開始時間----------------------------
                //獲取Calendar的實例，預設為當前系統時間
                Calendar rightNow1 = Calendar.getInstance();
                //new   SimpleDateFormat 進行格式化
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.TAIWAN);
                //利用Calendar的getTime方法，將時間轉為Date對象
                Date date1 = rightNow1.getTime();
                //利用SimpleDateFormat對象 把Date對象格式化
                format1 = sdf1.format(date1);//String型態
                //------------------設定開始時間----------------------------

                //-----傳值進入Mysql------------------------------
                dbHper.clearRec();
                mysql_del();//執行MySQL刪除();//執行MySQL刪除
                mysql_insert();
                dbmysql();
                //-------------------------------------
                long rowID = dbHper.RecCount();
                if (rowID != -1) {
                    cus01.setText("");
                    cus02.setText("");
                    cus03.setText("");
                }
            }
            intent.setClass(Biker_train1.this,Biker_train.class);
            startActivity(intent); //執行指定的class
            finish();//跳轉過去後結束此layout
        }
    }

    //    ------------Spinner下拉選單開始------------
    private AdapterView.OnItemSelectedListener s001On=new AdapterView.OnItemSelectedListener() {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!chb01.isChecked()){
                switch(parent.getId()) {
                    case R.id.train_s001:
                        t_day = String.valueOf(parent.getSelectedItem());
                        break;
                    case R.id.train_s002:
                        t_km = String.valueOf(parent.getSelectedItem());
                        break;
                    case R.id.train_s003:
                        t_high = String.valueOf(parent.getSelectedItem());
                        break;
                    case R.id.train_s004:
                        t_totaltime = String.valueOf(parent.getSelectedItem());
                        break;
                }

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            cus01.setText("");
            cus02.setText("");
            cus03.setText("");
            cus04.setText("");
        }
    };

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

    //==============MYSQL=================
    private void mysql_insert()
    {
        ArrayList<String> nameValuePairs =new ArrayList<>();
        nameValuePairs.add(U_ID);//U_ID
        nameValuePairs.add(t_day);
        nameValuePairs.add(t_km);
        nameValuePairs.add(t_high);
        nameValuePairs.add(t_totaltime);
        nameValuePairs.add(format);
        nameValuePairs.add(format1);
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        //----------------------------------------------- 真正執行mySQL 是這行
        DBConnector23.executeInsert(nameValuePairs);
        //-----------------------------------------------

    }
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
                // 匯入前,刪除所有SQLite資料
                dbHper.clearRec();
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
                    dbHper.insertRec_m(newRow);
                }
            } else {
                //Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
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
    private void initDB() {
        if (dbHper == null) {
            dbHper = new FriendDbHelper23(this, DB_FILE, null, DBversion);
            dbHper.FindRec(U_ID); //重新載入SQLite
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (dbHper != null) {
//            //dbHper.close();
//            dbHper = null;
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDB();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dbHper != null) {
            //  dbHper.close();
            dbHper = null;
        }
    }

    ////------------menu方法----------------
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        giveupDlg = new Dialog(this);//確認是否放棄編輯
        giveupDlg.setCancelable(false);//false可以讓除了dialog的畫面都不能被點選

        //R.layout.alert_dialog設為Dialog的畫面
        giveupDlg.setContentView(R.layout.alert_dialog);

        Button btnOK = (Button) giveupDlg.findViewById(R.id.train_btnOK);
        Button btnCancel = (Button) giveupDlg.findViewById(R.id.train_btnCancel);
        TextView giveup=(TextView)giveupDlg.findViewById(R.id.tarin_waring);

        giveup.setText(getString(R.string.train_giveup));//更改dialog的字
        btnOK.setText(getString(R.string.train_yes));
        btnCancel.setText(getString(R.string.train_no));
        giveupDlg.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveupDlg.cancel();//關閉對話盒
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveupDlg.cancel();
                finish();
            }
        });
        return super.onOptionsItemSelected(item);
    }
}