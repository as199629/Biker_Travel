package com.bikerlifeYang;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Thread.sleep;

public class Biker_profile extends AppCompatActivity implements ViewSwitcher.ViewFactory {

    private Intent intent01 = new Intent();
    private TextView t002, t004, t016, t005, t055, t028, t29, t43, t32, t31, t37;
    private Spinner a01;
    private String society,sqlctl;
    private TableRow ta01;
    private String[] listarr;
    private ListView lv;
    private ArrayList<Map<String, Object>> mList;
    private Dialog mLoginDlg;
    private Menu menu;
    private int profile_modify = 0,profile_society=0;
    private int weight = 0, city_position = 0, area_position = 0, gender_position = 0;
    private ArrayList societyArr = new ArrayList<String>();
    RecyclerView mRecyclerView;//for recyclerview
    MyListAdapter myListAdapter;//for recyclerview
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();//for recyclerview
    private HashMap<String, String> hashMap;//for recyclerview
    private int picminus;//固定的-號圖案
    private Dialog profile_dialog;
    private FriendDbHelper28 dbHper;
    private static final String DB_TABLE = "C100";    // 資料庫物件，固定的欄位變數
    private static final String DB_FILE = "bikerlife.db";
//    private static final int DBversion = 1;
    private int DBversion=0;
    private ArrayList<String> recSet;
    private String u_id;//只顯示使用者的社團與紀錄
    private ProgressDialog progDlg;
    private Handler handler=new Handler();
    private String member_uid;
    private CircleImgView image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        check_internet();//檢查連線
        enableStrictMode(this);//用到mySQL必加
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_profile);
        initDB();//有用到DB一定要加***放setupViewcomponent前面
        setupViewcomponent();

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
        // 連結ImageView
        image_view = (CircleImgView) findViewById(R.id.circleImgView);
        // 讀取圖片
        try{//抓使用者頭貼跟姓名
//            dbHper.insertRec_login();//測試用

            SharedPreferences xxx=getSharedPreferences("USER_ID",0);
            u_id=xxx.getString("USER_ID","");//"1"為測試用


            String file=dbHper.getUserFile(u_id);//取得登入者檔案
            String[] fld = file.split("#");//一條字串用#分割(M1405browse)
            String httpname=fld[1];
//        String httpname = "https://lh3.googleusercontent.com/a-/AOh14Ggs37WURY38V0Y4h39uIqBcpwd-XKtavp_kMi5fjw";
            Picasso.get()
                    .load(httpname)
                    .into(image_view);

//        Intent intent01 = this.getIntent();
//        String name = intent01.getStringExtra("name");//取得modify擺過來的"name"
//        if (name != null)//從修改跳過來才會改
//            t002.setText(name);
            t002 = (TextView) findViewById(R.id.profile_t002);//姓名
            t002.setText(fld[0]);//使用者姓名
        }catch (Exception e){}

        t004 = (TextView) findViewById(R.id.profile_t004);//修改個資按鈕
        t43 = (TextView) findViewById(R.id.profile_t43);//歷史紀錄按鈕
        t37 = (TextView) findViewById(R.id.profile_t37);// 歷史紀錄(bar)
        t31 = (TextView) findViewById(R.id.profile_t31);//正在追蹤(bar)
        a01 = (Spinner) findViewById(R.id.profile_a01);//社團列表
        t016 = (TextView) findViewById(R.id.profile_t016);//找新車友
        t005 = (TextView) findViewById(R.id.profile_t005);//建立社團按鈕
        t055 = (TextView) findViewById(R.id.profile_t055);//社團資訊(eye)
        t028 = (TextView) findViewById(R.id.profile_t28);//同意加入好友+
        t29 = (TextView) findViewById(R.id.profile_t29);//拒絕加入好友-
        ta01 = (TableRow) findViewById(R.id.profile_ta01);//申請加入好友列表
        t32 = (TextView) findViewById(R.id.profile_t32);//正在追蹤按鈕
        t004.setOnClickListener(t004on);//修改個資
        t016.setOnClickListener(t004on);//找新車友
        t005.setOnClickListener(t004on);//建立社團
        t055.setOnClickListener(t004on);//社團資訊
        t43.setOnClickListener(t005on);//歷史紀錄按鈕
        t32.setOnClickListener(t005on);//正在追蹤按鈕
        t028.setOnClickListener(t005on);//同意加入好友
        t29.setOnClickListener(t005on);//拒絕加入好友
//        t002.setOnClickListener(test);


        //for recyclerview
        mRecyclerView = findViewById(R.id.follow);//相對應的Layout的recycleviewID
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        myListAdapter = new MyListAdapter();
        mRecyclerView.setAdapter(myListAdapter);
        //for recyclerview

//        DecimalFormat jaFormat=new DecimalFormat("0.00");//規定格式:小數點後兩位，0改#代表不補零
//        String ja=jaFormat.format(Double.parseDouble(avg))+"km";

        //設定spinner item選項(這種寫法陣列是固定的)
//        ArrayAdapter<CharSequence> adapterSociety = ArrayAdapter.createFromResource(this, R.array.profile_a01, R.layout.profile_spinner);
//        adapterSociety.setDropDownViewResource(R.layout.profile_spinner);//設置下拉列表的風格
//        a01.setOnItemSelectedListener(a01on);
//        a01.setAdapter(adapterSociety);

            //--------------------------------------------巨集---正在追蹤 recyclerview
            try {
                listarr = getResources().getStringArray(R.array.profile_friend);//取得陣列內容
                picminus = getResources().getIdentifier("ic_delete_profile", "drawable", getPackageName());
                for (int i = 49; i < 140; i++) {

                    // %02d執行十進制整數轉換d，格式化補零，寬度為2。 因此，一個int參數，它的值是7，將被格式化為"07"作為一個String
                    // 取得陣列profile_friend並丟進textview
                    String microNO = String.format("%03d", i);//抓drawable用

//                取得圖片
                    int picId = getResources().getIdentifier("img" + microNO, "drawable", getPackageName());
//               取得要放圖片的id
//                int picNo = i - 46;
//                int circleImg = getResources().getIdentifier("circleImgView" + picNo, "id", getPackageName());
//                ImageView pic = (ImageView) findViewById(circleImg);
//                將圖片放在相對應的id
//                pic.setImageResource(picId);

                    hashMap = new HashMap<>();
                    //第一個參數對應layout，第二個參數告訴電腦去哪找資料
                    hashMap.put("friendPic", Integer.toString(picId));//頭像
                    hashMap.put("friendName", listarr[i - 49]);//姓名
                    hashMap.put("friendSymbol", Integer.toString(picminus));//X

                    arrayList.add(hashMap);

                    if (picId == 0) {
                        break;//撈不到值(=傳回0)就跳出迴圈
                    }
                }
            } catch (Exception e) {
                return;
            }
        }

    private void check_internet() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // do something you want
        } else {
//            Toast.makeText(getApplicationContext(), "make sure you have connected the internet", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(Biker_profile.this);
            dialog.setTitle(getString(R.string.no_internet));
            dialog.setIcon(R.drawable.ic_dialog_warning);
            dialog.setCancelable(false);
//                dialog.setMessage("基本訊息對話功能介紹");
//                dialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        // TODO Auto-generated method stub
//                        Toast.makeText(Biker_profile.this, "我還尚未了解",Toast.LENGTH_SHORT).show();
//                    }
//
//                });
            dialog.setPositiveButton(getString(R.string.leave),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    finish();
                }

            });
//                dialog.setNeutralButton("取消",new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        // TODO Auto-generated method stub
//                        Toast.makeText(Biker_profile.this, "取消",Toast.LENGTH_SHORT).show();
//                    }
//
//                });
            dialog.show();
        }

    }

    // 讀取MySQL 資料-社團
    private void dbmysql() {
        sqlctl = "SELECT * FROM C100 ORDER BY id ASC";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            //--------------------------------------------------------
            int rowsAffected = dbHper.clearRec();                 // 匯入前,刪除[社團]的SQLite資料
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
                    long rowID = dbHper.insertRec_m(newRow);

                }
//                Toast.makeText(getApplicationContext(), "共有 " + Integer.toString(jsonArray.length()) + " 個社團", Toast.LENGTH_SHORT).show();
int a=0;
                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
//            recSet = dbHper.getRecSet();  //重新載入SQLite
            // --------------------------------------------------------
        } catch (Exception e) {}
    }
    // 讀取MySQL 資料-紀錄
    private void dbmysql1() {
        //接續dbmysql2()的撈法，得到與使用者相同社團的所有會員UID後，去B100撈出歷史紀錄
//        sqlctl = "SELECT * FROM B100 WHERE B101="+u_id;
        sqlctl ="SELECT * FROM B100";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            //--------------------------------------------------------
            int rowsAffected = dbHper.clearRec1();                 // 匯入前,刪除[紀錄]的SQLite資料
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
                    long rowID = dbHper.insertRec_record(newRow);
                }
//                Toast.makeText(getApplicationContext(), "共有 " + Integer.toString(jsonArray.length()) + " 個社團", Toast.LENGTH_SHORT).show();

                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
//            recSet = dbHper.getRecSet();  //重新載入SQLite
            // --------------------------------------------------------
        } catch (Exception e) {}
    }

    // 讀取MySQL 資料-C200社團人員明細
    private void dbmysql2() {
        //step2.撈C200全部欄位，社團UID是step1.撈到的(step1.撈社團UID，C202=會員UID))
        sqlctl ="SELECT * FROM `C200` WHERE C201 IN (SELECT C201 FROM C200 WHERE C202="+u_id+")";
//        sqlctl =" SELECT * FROM `C200` WHERE C201 IN (SELECT id FROM C100 WHERE id IN(SELECT C201 FROM C200 WHERE C202="+u_id+"))";
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
int a=0;
                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
//            recSet = dbHper.getRecSet();  //重新載入SQLite
            // --------------------------------------------------------
        } catch (Exception e) {}
    }
    // 讀取MySQL 資料-G100社團人員明細
    private void dbmysql3() {
        sqlctl ="SELECT * FROM `G100` WHERE  G110=0";//G110=0未結束
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            //--------------------------------------------------------
            int rowsAffected = dbHper.clearRec_G100();                 // 匯入前,刪除[C200]的SQLite資料
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
                    long rowID = dbHper.insertRec_G100(newRow);
                }
//                Toast.makeText(getApplicationContext(), "共有 " + Integer.toString(jsonArray.length()) + " 個社團", Toast.LENGTH_SHORT).show();
                int a=0;
                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
//            recSet = dbHper.getRecSet();  //重新載入SQLite
            // --------------------------------------------------------
        } catch (Exception e) {}
    }
    private View.OnClickListener test = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent01.setClass(Biker_profile.this, Biker_time_Stopwatch.class);
                startActivity(intent01);
            }
        };

        private void  spinner_society () {//設定spinner item選項(社團)
            ArrayList<String> society_list = dbHper.society(u_id);//取得使用者(其為社員或團長)的社團
            int a=0;
            if (society_list == null) {//沒東西
                return;
            }
            if (society_list.size() == 0) {
//            Toast.makeText(getApplicationContext(),getString(R.string.nosociety),Toast.LENGTH_SHORT).show();
                t055.setVisibility(View.INVISIBLE);//隱藏眼睛
                a01.setVisibility(View.INVISIBLE);//隱藏社團(spinner)
            } else {
                for(int i =0;i<society_list.size();i++){
                    societyArr.add(society_list.get(i));
                }
                t055.setVisibility(View.VISIBLE);//顯示眼睛
                a01.setVisibility(View.VISIBLE);//顯示社團(spinner)
            }
            ArrayAdapter adapterSociety = new ArrayAdapter(this
                    , R.layout.profile_spinner, societyArr);
            a01.setOnItemSelectedListener(a01on);
            a01.setAdapter(adapterSociety);
        }

        //有用到DB一定要加***
        private void initDB () {
            DBversion=Integer.parseInt(getString(R.string.SQLite_version));
            if (dbHper == null) {      //dbHper=SQLite
                dbHper = new FriendDbHelper28(this, DB_FILE, null, DBversion);
            }
//            dbmysql();//帶入社團MySQL資料
//            dbmysql1();//帶入紀錄MySQL資料
//            dbmysql2();//帶入社團人員明細MySQL資料
//            recSet = dbHper.getRecSet();//重新載入SQLite
        }

        @Override
        protected void onPause () {
            super.onPause();
            if (dbHper != null) {
                dbHper.close();
                dbHper = null;
            }
        }

        @Override
        protected void onResume () {
            super.onResume();
            //check internet
            check_internet();

            if (dbHper == null) {      //dbHper=SQLite
                dbHper = new FriendDbHelper28(this, DB_FILE, null, DBversion);
            }
            societyArr.clear();//清空spinner

            try {
                Bundle bundle = getIntent().getExtras();
                member_uid = bundle.getString("member_profile");//取得society擺過來的"member_profile"
            }catch (Exception e){}

            //readSQL=工作命令單，後面設1000或500(毫秒)，設太小會程式衝突，handler=跑腿者
            if (member_uid == null){
                show_ProgDlg();//先跑進度條
                handler.postDelayed(readSQL, 1000);//有postDelayed要movecallback
            }else{//其他社員歷史紀錄
                String file = dbHper.getUserFile(member_uid);
                String[] fld = file.split("#");//一條字串用#分割(M1405browse)
                String httpname = fld[1];
                Picasso.get()
                        .load(httpname)
                        .into(image_view);
                t002.setText(fld[0]);//使用者姓名
                //====歷史紀錄==================================
                lv = (ListView) findViewById(R.id.record);//準備一個要放歷史紀錄的ListView
                mList = new ArrayList<Map<String, Object>>();//準備一個要放歷史紀錄的ArrayList
                try {
                    mList = dbHper.getAllRecord(member_uid);//取得使用者的所有歷史紀錄
                    //-----------------------------------------------------------------------------------
                    SimpleAdapter adapter = new SimpleAdapter(
                            Biker_profile.this,
                            mList,
                            R.layout.profile_record_lv,
                            new String[]{"year", "distance", "time", "climb", "date", "distance_data", "time_data",
                                    "climb_data", "id"},
                            new int[]{R.id.year, R.id.distance, R.id.time, R.id.climb, R.id.date, R.id.distance_data,
                                    R.id.time_data, R.id.climb_data, R.id.record_id});
                    lv.setAdapter(adapter);//將adapter撈到的資料放進listview
                    lv.setOnItemClickListener(record_detail);//點歷史紀錄後開啟詳細資料的layout

                    //-----------------------------------------------------------------------------------------
//            }
                } catch (Exception e) {
//            return;
                }
                //====歷史紀錄done=======================================
                t055.setVisibility(View.GONE);//隱藏眼睛
                a01.setVisibility(View.GONE);//隱藏社團(spinner)
                t016.setVisibility(View.GONE);//找新車友(按鈕)
                t005.setVisibility(View.GONE);//建立社團(按鈕)
                t32.setVisibility(View.GONE);//正在追蹤(按鈕)
                t43.setVisibility(View.GONE);//歷史紀錄(按鈕)
            }

//            try
//            {
//                Thread.sleep(1000);//睡一下
//            }catch (InterruptedException e)
//            {e.printStackTrace();}
            }

    private void show_ProgDlg () {
                progDlg = new ProgressDialog(this);
                progDlg.setTitle("請稍後");
                progDlg.setMessage("載入資料中");
                progDlg.setIcon(android.R.drawable.presence_away);
                progDlg.setCancelable(false);
                progDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDlg.setMax(100);
                progDlg.show();
            }

            private Runnable readSQL = new Runnable() {
                @Override
                public void run() {
                    dbmysql();// 讀取MySQL 資料-社團
                    dbmysql1();//帶入紀錄MySQL資料
                    dbmysql2();//帶入社團人員明細MySQL資料
                    dbmysql3();//帶入揪團MySQL資料
                    spinner_society();//設定spinner item選項(社團)
                    //====歷史紀錄==================================
                    lv = (ListView) findViewById(R.id.record);//準備一個要放歷史紀錄的ListView
                    mList = new ArrayList<Map<String, Object>>();//準備一個要放歷史紀錄的ArrayList
                    try {
                        mList = dbHper.getAllRecord(u_id);//取得使用者的所有歷史紀錄
                        //-----------------------------------------------------------------------------------
                        SimpleAdapter adapter = new SimpleAdapter(
                                Biker_profile.this,
                                mList,
                                R.layout.profile_record_lv,
                                new String[]{"year", "distance", "time", "climb", "date", "distance_data", "time_data",
                                        "climb_data", "id"},
                                new int[]{R.id.year, R.id.distance, R.id.time, R.id.climb, R.id.date, R.id.distance_data,
                                        R.id.time_data, R.id.climb_data, R.id.record_id});
                        lv.setAdapter(adapter);//將adapter撈到的資料放進listview
                        lv.setOnItemClickListener(record_detail);//點歷史紀錄後開啟詳細資料的layout

                        //-----------------------------------------------------------------------------------------
//            }
                    } catch (Exception e) {
//            return;
                    }
                    //====歷史紀錄done=======================================
                    progDlg.cancel();
                    handler.removeCallbacks(readSQL);//關掉緒
                }
            };

            @Override
            protected void onStop () {
                super.onStop();
                if (dbHper != null) {
                    dbHper.close();
                    dbHper = null;
                }
            }
            //有用到DB一定要加 done***


            @Override
            protected void onRestart () {
                super.onRestart();
//            this.recreate();//其他class finish的時候會重生此class，但會閃屏
            }

            private AdapterView.OnItemClickListener record_detail = new AdapterView.OnItemClickListener() {
                @Override//點歷史紀錄後開啟詳細資料的layout
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),"這是第"+position+"筆",Toast.LENGTH_SHORT).show();
                    String r_id = ((TextView) view.findViewById(R.id.record_id)).getText().toString().trim();
                    intent01.putExtra("record_id", r_id);//此方式可以放所有基本型別
                    intent01.putExtra("member_uid", member_uid);//此方式可以放所有基本型別
                    intent01.setClass(Biker_profile.this, Biker_profile_record_detail.class);
                    startActivity(intent01);
                }
            };
            private AdapterView.OnItemSelectedListener a01on = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    society = parent.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
            private View.OnClickListener t005on = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.profile_t43://歷史紀錄內容
                            lv.setVisibility(View.VISIBLE); // 歷史紀錄
                            t37.setVisibility(View.VISIBLE); // 歷史紀錄(bar)
                            ta01.setVisibility(View.GONE); // 申請加入好友列表
                            t31.setVisibility(View.GONE);//正在追蹤(bar)
                            mRecyclerView.setVisibility(View.GONE);//正在追蹤
                            break;
                        case R.id.profile_t32://點擊正在追蹤
                            lv.setVisibility(View.GONE); // 隱藏歷史紀錄
                            t37.setVisibility(View.GONE); // 隱藏歷史紀錄(bar)
                            ta01.setVisibility(View.VISIBLE); // 顯示申請加入好友列表
                            t31.setVisibility(View.VISIBLE);//顯示正在追蹤(bar)
                            mRecyclerView.setVisibility(View.VISIBLE);//正在追蹤
                            break;
                        case R.id.profile_t28:
                            ta01.setVisibility(View.GONE); // 隱藏申請加入好友列表
                            break;
                        case R.id.profile_t29://拒絕加入好友
                            mLoginDlg = new Dialog(Biker_profile.this);
                            mLoginDlg.setTitle(getString(R.string.profile_t056));
                            mLoginDlg.setCancelable(false);//一定要給我選，true的話可以按旁邊躲掉

                            //R.layout.profile_dialog當作Dialog的畫面
                            mLoginDlg.setContentView(R.layout.alert_dialog);

                            Button btnOK = (Button) mLoginDlg.findViewById(R.id.train_btnOK);
                            Button btnCancel = (Button) mLoginDlg.findViewById(R.id.train_btnCancel);
                            TextView warning = (TextView) mLoginDlg.findViewById(R.id.tarin_waring);
                            warning.setText(getString(R.string.deny_add));//更改dialog的字

                            btnCancel.setOnClickListener(t005on);
                            btnOK.setOnClickListener(t005on);

                            mLoginDlg.show();
                            break;
                        case R.id.train_btnOK:
                            ta01.setVisibility(View.GONE); // 隱藏申請加入好友列表
                            mLoginDlg.cancel();
                        case R.id.train_btnCancel:
                            mLoginDlg.cancel();
                            break;
                    }


                }
            };

            private View.OnClickListener t004on = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
//                    case R.id.profile_t004://修改個資
//                        //跳去修改，並把姓名，生日，體重、地區、性別丟過去
//                        Bundle bundle_file = new Bundle();//包裹
//                        bundle_file.putString("name", name);
//                        bundle_file.putString("birth", birth);
//                        bundle_file.putInt("weight", weight);
//                        bundle_file.putInt("city", city_position);
//                        bundle_file.putInt("area", area_position);
//                        bundle_file.putInt("gender", gender_position);
//
//                        intent01.putExtras(bundle_file);
////                    setResult(RESULT_OK,intent01);//回傳值
//                        //------------------------------------------------------------------------------------
//                        //跳轉頁面
//                        intent01.setClass(Biker_profile.this, Biker_profile_modify.class);
//
//                        //掛號，呼叫程式有回傳值，下面的profile_modify接收setResult(RESULT_OK,it);給onActivityResult用
//                        startActivityForResult(intent01, profile_modify);
//
//                        break;
                        case R.id.profile_t016:
                            intent01.putExtra("subname", getString(R.string.profile_t016));
                            intent01.setClass(Biker_profile.this, Biker_profile_addfriend.class);
                            startActivity(intent01);
                            break;
                        case R.id.profile_t005://建立社團
//                        intent01.putExtra("name", t002.getText().toString().trim());//團長
                            intent01.putExtra("C202", u_id);//C202會員UID
                            intent01.setClass(Biker_profile.this, Biker_profile_create.class);
                            startActivity(intent01);
                            break;
                        case R.id.profile_t055:
                            //設定選擇哪個社團
                            for (int i = 0; i < societyArr.toArray().length; i++) {
                                if (society.equals(societyArr.get(i).toString())) {//社團123
                                    intent01.putExtra("societyName", societyArr.get(i).toString());//給Biker_profile_society的profile_e030
                                    intent01.putExtra("u_id", u_id);//給Biker_profile_society用來判斷是不是團長
                                    intent01.setClass(Biker_profile.this, Biker_profile_society.class);
                                }
                            }
                            //掛號，呼叫程式有回傳值，下面的profile_society接收setResult(RESULT_OK,it);給onActivityResult用
//                            startActivityForResult(intent01, profile_society);
                        startActivity(intent01);
                            break;
                    }
                }
            };

//            @Override//接收society傳來的資料(看其他社員的歷史紀錄)
//            protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data)
//            {
//                super.onActivityResult(requestCode, resultCode, data);
//                if (requestCode != profile_society) {//不是從society過來的話就return
//                    return;
//                }
//                switch (resultCode) {
//                    //接收包裹(從society傳來的會員UID)
//                    case RESULT_OK:
//                        Bundle bundle = data.getExtras();
//                        member_uid = bundle.getString("member_profile");//取得society擺過來的"member_profile"
//                        break;
//                    case RESULT_CANCELED://沒按鈕...就沒有setResult(RESULT_CANCELED);//回傳值
//                        break;
//                }
//            }

            //        @Override//接收modify傳來的資料
//        protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
//            super.onActivityResult(requestCode, resultCode, data);
//            if (requestCode != profile_modify) {//不是從modify過來的話就return
//                return;
//            }
//            switch (resultCode) {
//                //接收包裹(從modify傳來的修改資料-姓名，生日，體重、地區、性別)
//                case RESULT_OK:
//                    Bundle bundle = data.getExtras();
//
//                    name = bundle.getString("name");
//                    birth = bundle.getString("birth");
//                    weight = bundle.getInt("weight");
//                    city_position = bundle.getInt("city");
//                    area_position = bundle.getInt("area");
//                    gender_position = bundle.getInt("gender");
//
//                    String s = name;
//                    t002.setText(s);
//                    break;
//                case RESULT_CANCELED://沒按鈕...就沒有setResult(RESULT_CANCELED);//回傳值
//                    break;
//            }
//        }

            @Override
            public View makeView () {
                ImageView v = new ImageView(this);
//        v.setBackgroundColor(0xFF000000);
                v.setLayoutParams(new ImageSwitcher.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                return v;
            }

            @Override
            public void onBackPressed () {
//        super.onBackPressed();//讓返回鍵失效
            }

            ////----------------------------------以下是menu用的方法----------------
            private void Signin_menu () {//登入時顯示的menu
                menu.setGroupVisible(R.id.g01, false);
                menu.setGroupVisible(R.id.g02, false);
            }

            //↓↓↓↓↓除了亮瑜使用此方法外，其他人原則上都使用Signin_menu↑↑的方法顯示menu item（167行已經寫好(亮瑜的部分改167行)）
            private void Signout_menu () {//登出時顯示的menu
                menu.setGroupVisible(R.id.g01, true);
                menu.setGroupVisible(R.id.g02, false);
            }

            @Override//顯示出menu　item選項的icon
            public boolean onMenuOpened ( int featureId, Menu menu){
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
            public boolean onCreateOptionsMenu (Menu menu){
                getMenuInflater().inflate(R.menu.biker_menu, menu);
                this.menu = menu;
                Signin_menu(); //登入menu的選項
                return true;
            }


            @Override
            public boolean onOptionsItemSelected (@NonNull MenuItem item)
            {
                switch (item.getItemId()) {
                    case R.id.action_settings: //原則上不用修改
                        this.finish();
                        break;
                }
                return super.onOptionsItemSelected(item);
            }

    //------------正在追蹤-------------------------副程式for recyclerview---------------------------------------------------------------------
            private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {

                class ViewHolder extends RecyclerView.ViewHolder {
                    private TextView tvfriendName;
                    private ImageView tvfriendPic, tvfriendSymbol;
                    private View mView;

                    public ViewHolder(@NonNull View itemView) {//設置recyclerview的內容
                        super(itemView);
                        tvfriendPic = itemView.findViewById(R.id.friendPic);
                        tvfriendName = itemView.findViewById(R.id.friendName);
                        tvfriendSymbol = itemView.findViewById(R.id.friendSymbol);
                        mView = itemView;
                    }
                }

                @NonNull
                @Override//選擇用哪個layout放recyclerview
                public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.profile_member_lv, parent, false);
                    return new ViewHolder(view);
                }

                @Override//生成recyclerview列表
                public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                    holder.tvfriendPic.setImageResource(Integer.parseInt(arrayList.get(position).get("friendPic")));
                    holder.tvfriendName.setText(arrayList.get(position).get("friendName"));
                    holder.tvfriendSymbol.setImageResource(Integer.parseInt(arrayList.get(position).get("friendSymbol")));

                    holder.tvfriendSymbol.setOnClickListener((v) -> {//按X開啟確認刪除的對話盒
                        profile_dialog = new Dialog(Biker_profile.this);
                        profile_dialog.setContentView(R.layout.alert_dialog);
                        profile_dialog.setCancelable(false);//一定要給我選
                        Button btnOK = (Button) profile_dialog.findViewById(R.id.train_btnOK);
                        Button btnCancel = (Button) profile_dialog.findViewById(R.id.train_btnCancel);

                        TextView warning = (TextView) profile_dialog.findViewById(R.id.tarin_waring);
                        warning.setText(getString(R.string.warn_cancel_follow) + arrayList.get(position).get("friendName") + "?");//更改dialog的字

                        btnCancel.setOnClickListener(new View.OnClickListener() {//取消刪除社員
                            @Override
                            public void onClick(View v) {
                                profile_dialog.cancel();//關閉對話盒
                            }
                        });
                        btnOK.setOnClickListener(new View.OnClickListener() {//確認刪除社員
                            @Override
                            public void onClick(View v) {
                                arrayList.remove(position);
                                notifyDataSetChanged();//整體刷新
                                profile_dialog.cancel();//關閉對話盒
                            }
                        });

                        profile_dialog.show();
                    });
                    //對話盒--------------------------------------------------------------------------------
                    holder.tvfriendName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), arrayList.get(position).get("friendName"), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public int getItemCount() {
                    return arrayList.size();//決定顯示數量，這裡的寫法是顯示全部
                }
            }

        }