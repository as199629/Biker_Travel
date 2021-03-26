package com.bikerlifeYang;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

public class Biker_profile_create extends AppCompatActivity implements View.OnClickListener {

    private TextView t057;
    private Intent intent01=new Intent();
    private Intent intent=new Intent();
    private EditText e05;
    private Bundle bundle = new Bundle(); //new一個Bundle物件，並將要傳遞的資料傳入
    private Spinner a02,a03;
    private Menu menu;
    private String country,area,societyName;
    private FriendDbHelper28 dbHper;
    private static final String DB_FILE = "bikerlife.db";
    private int DBversion = 1;
    private ArrayList<String> recSet;
    private String sqlctl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);//用到mySQL必加
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_profile_create);
        setupViewcomponent();
        initDB();//有用到DB一定要加***
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

    private void setupViewcomponent() {
        //---設定class標題
//        Intent intent01 = this.getIntent();
//        mode_title=intent01.getStringExtra("subname");
//        this.setTitle(this.getResources().getIdentifier(mode_title,"string",getPackageName()));
//        this.setTitle(getString(R.string.profile_t005));
        //---
        t057=(TextView)findViewById(R.id.profile_t057);
        t057.setOnClickListener(this);
        e05=(EditText)findViewById(R.id.profile_e05);//社團名稱
        a02=(Spinner)findViewById(R.id.profile_a02);//縣市
        a03=(Spinner)findViewById(R.id.profile_a03);//鄉鎮市區

        // 設定 spinner item 選項------------
        ArrayAdapter<CharSequence> city = ArrayAdapter
                .createFromResource(this, R.array.profile_city,
                        R.layout.profile_spinner);//將可選内容與ArrayAdapter連接起來
        city.setDropDownViewResource(R.layout.profile_spinner);//設置下拉列表的風格
        // 準備 Listener a001Lis 需再設定 Listener
        a02.setAdapter(city);
        a02.setOnItemSelectedListener(district);
    }

    //有用到DB一定要加***
    private void initDB() {
        DBversion=Integer.parseInt(getString(R.string.SQLite_version));
        if (dbHper == null) {      //dbHper=SQLite
            dbHper = new FriendDbHelper28(this, DB_FILE, null, DBversion);
        }
//        dbmysql2();// 讀取MySQL 資料-C200社團人員明細(這行打開，跳回主頁社團spinner會不見)
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
        if (dbHper == null) {      //dbHper=SQLite
            dbHper = new FriendDbHelper28(this, DB_FILE, null, DBversion);
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
    //有用到DB一定要加 done***

    private AdapterView.OnItemSelectedListener district=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            country = parent.getSelectedItem().toString();//紀錄選擇哪個縣市
            ArrayAdapter<CharSequence> a_district;
            switch (country){
                case "臺北市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area01, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "基隆市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area02, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "新北市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area03, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "連江縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area04, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "宜蘭縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area05, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "新竹市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area06, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "新竹縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area07, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "桃園市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area08, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "苗栗縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area09, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "臺中市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area10, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "彰化縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area11, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "南投縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area12, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "嘉義市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area13, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "嘉義縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area14, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "雲林縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area15, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "臺南市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area16, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "高雄市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area17, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "南海島":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area18, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "澎湖縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area19, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "金門縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area20, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "屏東縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area21, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "臺東縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area22, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
                case "花蓮縣":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_area23, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a03.setAdapter(a_district);
                    break;
            }
            a03.setOnItemSelectedListener(area_select);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private AdapterView.OnItemSelectedListener area_select=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            area = parent.getSelectedItem().toString();//紀錄選擇哪個鄉鎮市區
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    // 讀取MySQL 資料-C200社團人員明細
    private void dbmysql2() {
        sqlctl = "SELECT * FROM C200 ORDER BY id ASC";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            //--------------------------------------------------------
            int rowsAffected = dbHper.clearRec2();                 // 匯入前,刪除[C200]的SQLite資料
            //--------------------------------------------------------
            String result = DBConnector28.executeQuery(nameValuePairs);

            /**************************************************************************
             * SQL 結果有多筆資料時使用JSONArray
             * 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            JSONArray jsonArray = new JSONArray(result);
            // -------------------------------------------------------
            if (jsonArray.length() > 0) { // MySQL 連結成功有資料

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
                    long rowID = dbHper.insertRec_C200(newRow);
                }
//                Toast.makeText(getApplicationContext(), "共有 " + Integer.toString(jsonArray.length()) + " 個社團", Toast.LENGTH_SHORT).show();

                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
//            recSet = dbHper.getRecSet_C200();  //重新載入SQLite
            // --------------------------------------------------------
        } catch (Exception e) {}
    }

    @Override
    public void onClick(View v) {
        //建立社團
        if (e05.getText().toString().trim().equals(""))//記得修剪空白
        {
            Toast.makeText(getApplicationContext(),getString(R.string.profile_t056),Toast.LENGTH_SHORT).show();
        }else{
            societyName=e05.getText().toString().trim();//trim()去掉空白
            int a=dbHper.FindRec(societyName);//看社團名稱有沒有人取過了(0-有；1-沒有)

            //跳去個人檔案，並把社團名稱丟過去
//            intent01.putExtra("societyName",societyName);//應該跟下面那行用意一樣
//            bundle.putString("newSocietyName",societyName);
            if(a==1){//社團名稱沒人取過
                Intent intent01 = this.getIntent();
//                String name=intent01.getStringExtra("name");//團長
                String C202=intent01.getStringExtra("C202");//C202會員UID=C106團長UID
//                long rowID=dbHper.insertRec(societyName,country,area,C202);//真正執行SQLite
                //-----直接增加到Mysql------------------------------
                mysql_insert(C202);
                //-------------------------------------
                //從這去社團，物件全gone
                e05.setText("");//建好後把社團名稱清空
//            bundle.putString("create","gone");
//            intent01.putExtras(bundle);//將Bundle物件傳給intent
//            intent01.setClass(Biker_profile_create.this,Biker_profile.class);
//            startActivity(intent01);

                this.finish();
            }else{//社團名稱已存在
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.nameexist), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.START,300,350);
                toast.show();
            }

        }
    }

    private void mysql_insert(String leader)
    {
        ArrayList<String> nameValuePairs =new ArrayList<>();

        nameValuePairs.add(societyName);
        nameValuePairs.add(country);
        nameValuePairs.add(area);
        nameValuePairs.add(leader);
        nameValuePairs.add("2");//團長
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }


        //----------------------------------------------- 真正執行mySQL 是這行
        String result = DBConnector28.executeInsert(nameValuePairs);
//        String result1 = DBConnector28.executeInsert_C200(nameValuePairs);
        //-----------------------------------------------
        //==============================================
        chk_httpstate();//檢查連結狀態
        //==============================================

    }
    private void chk_httpstate() {
        ////-------------------------------
        //存取類別成員 DBConnector01.httpstate 判定是否回應 200(連線要求成功)
        if (DBConnector28.httpstate != 200) {
            Toast.makeText(getApplicationContext(),getString(R.string.no_internet),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();//讓返回鍵失效
    }
    ////----------------------------------以下是menu用的方法----------------
    private void Signin_menu(){//登入時顯示的menu
        menu.setGroupVisible(R.id.g01,false);
        menu.setGroupVisible(R.id.g02,false);
    }
    //↓↓↓↓↓除了亮瑜使用此方法外，其他人原則上都使用Signin_menu↑↑的方法顯示menu item（167行已經寫好(亮瑜的部分改167行)）
    private void Signout_menu(){//登出時顯示的menu
        menu.setGroupVisible(R.id.g01,true);
        menu.setGroupVisible(R.id.g02,false);
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.biker_menu,menu);
        this.menu=menu;
        Signin_menu(); //登入menu的選項
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)//請自行inten至各個畫面(未整合前先註解起來)　item01~03亮瑜改就好
    {
        switch (item.getItemId()){
            case R.id.action_settings: //原則上不用修改
//                Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}