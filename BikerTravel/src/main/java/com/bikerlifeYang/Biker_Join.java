package com.bikerlifeYang;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

// Toast.makeText(getApplicationContext(),"吐司測試機一號",Toast.LENGTH_LONG).show();
public class Biker_Join extends AppCompatActivity implements View.OnClickListener
{

//    private Intent intent=new Intent();
//0109
    private static final String DB_FILE = "bikerlife.db"; //資料庫名稱
    private static final String DB_TABLE = "G100";//資料表名稱
    //private static final int DBversion = 1;//資料庫版本
    private Biker_Life_JoinDbHelper BdbHper;  //宣告DbHelper的類別
    String joinDb_strArray[][]; //儲存從資料表中取得的資料
//0129
    private ArrayList<Integer> correct_Position; //儲存清單中實際的position

  //====================
    private static final int REQUEST_CODE = 1;
   // private ListView join_List;
    private ArrayList<String> join_strListArray;
    private TextView join_Nowteam;
    private TextView join_Historyteam;
    private boolean join_change_bool;
    private Button join_NewTeam_btn;
    private ImageSwitcher imgSwi;
    private TextView join_joinanim;
    private Menu menu;
    RecyclerView mRecyclerView;
    MemberListAdapter myListAdapter;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    private boolean bool_listview_item=false;//listview中是否有實際有用的資料
    private TextView aaaaa;//隱藏按鈕的變數
    private String sqlctl;
    private Spinner join_spinner;
    private ArrayList<String> array_clubid; //spinner中postion中對應的clubID 作為判斷
    private String spinner_index;//存放spinner中資訊的ID
    private String u_id;
    private ProgressDialog progDlg;
    private Handler handler=new Handler();
    private String ser_msg;
    private Dialog AlertDig;
    private Button alertBtnOK;
    private Button alertBtnCancel;
    private TextView Dig_tarin_waring;
    private TextView Dig_train_title;
    private ImageView btnfrmLy;
    private ImageView btnupmysqlLY;
    private int server_error=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_join);
        SharedPreferences xxx=getSharedPreferences("USER_ID",0);
        u_id=xxx.getString("USER_ID","");
        initDB();
        SetupViewComponent();
        show_ProgDlg();//啟動進度條
        handler.postDelayed(readSQL, 1000);

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
    private void SetupViewComponent()
    {
        //隱藏資料庫開啟選項
        aaaaa=(TextView)findViewById(R.id.myname);
        aaaaa.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SQLiteDatabase db = BdbHper.getWritableDatabase();
            }
        });
        //
      //  join_List = (ListView) findViewById(R.id.ListView);//清單
        join_Nowteam=(TextView)findViewById(R.id.biker_join_Recruiting);
        join_Historyteam=(TextView)findViewById(R.id.biker_join_Historyteam);
        join_NewTeam_btn=(Button)findViewById(R.id.biker_join_newteam_btn);
        join_Nowteam.setOnClickListener(this);
        join_NewTeam_btn.setOnClickListener(this);
        join_Historyteam.setOnClickListener(this);
        join_change_bool=true;//預設點擊的是開團中
        join_strListArray=new ArrayList<String>();
        mRecyclerView = findViewById(R.id.recyclerview);
        //20210129
        correct_Position=new ArrayList<Integer>();
        //20210206
        join_spinner=(Spinner)findViewById(R.id.biker_join_spinner);
        join_spinner.setOnItemSelectedListener(mSpnNameOnItemSelLis);
        array_clubid=new ArrayList<String>();
        // 動態調整高度 抓取使用裝置尺寸
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newscrollheight = displayMetrics.heightPixels * 50 / 100; // 設定ScrollView使用尺寸的4/5
        mRecyclerView.getLayoutParams().height = newscrollheight;
        mRecyclerView.setLayoutParams(mRecyclerView.getLayoutParams()); // 重定ScrollView大小
        //-----
    }
    private void Anim(){
        //動畫
        join_joinanim = (TextView) findViewById(R.id.joinanim);
        join_joinanim.setVisibility(View.VISIBLE);
        join_joinanim.setAnimation(null);
        join_joinanim.clearAnimation();
        join_joinanim.setAnimation(AnimationUtils.loadAnimation(this, R.anim.join_anim_goto_right));
    }



    private void initDB() {
        int DBversion =Integer.parseInt(getString(R.string.SQLite_version));
        if (BdbHper == null) {  //如果沒有連線資料庫 就開啟
            BdbHper = new Biker_Life_JoinDbHelper(this, DB_FILE, null, DBversion);
        }
        queryDB();//抓取資料表資料
//        BdbHper.is_end();
    }
    private void show_ProgDlg () {
        progDlg = new ProgressDialog(this);
        progDlg.setTitle(getString(R.string.join_dialog_1));
        progDlg.setMessage(getString(R.string.join_dialog_2));
        progDlg.setIcon(android.R.drawable.presence_away);
        progDlg.setCancelable(false);
        progDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDlg.setMax(100);
        progDlg.show();
    }
    private Runnable readSQL = new Runnable() {
        @Override
        public void run() {
            server_error=0;
            dbmysql_G100();//載入mysql
            dbmysql_G200();//現在放到 進入編輯加入頁面前的 click
            dbmysql_C100();
            dbmysql_C200();
            RecycleViewChange();//變更顯示RecycleView的方法
            SpinnerText();//顯示spinner所抓到的所屬社團
            Anim();
            progDlg.cancel();
            if(server_error>0){
                show_dialog();
            }
            handler.removeCallbacks(readSQL);// 結束緒
        }
    };
    private void queryDB() {//抓取Sqlite資料
        joinDb_strArray=BdbHper.FindNowJoin(spinner_index);
    }

    public void dbmysql_G100() {// 讀取MySQL資料=>sqlite（G100）使用者ID所屬社團中擁有的揪團資料
        sqlctl = "SELECT * FROM G100 WHERE G108 IN (SELECT DISTINCT C201 FROM C200 WHERE C202="+u_id+")";//sql 78測試用userID
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            int rowsAffected = BdbHper.clearRec_G100();                 // 匯入前,刪除G100SQLite資料
            String result = DBConnector.executeQuery(nameValuePairs);
            chk_httpstate();
//            Toast.makeText(getApplicationContext(),"a:"+result,Toast.LENGTH_LONG).show();
            /**************************************************************************
             * SQL 結果有多筆資料時使用JSONArray
             * 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            JSONArray jsonArray = new JSONArray(result);//抓到的所有資料

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
                    // -------------------加入SQLite---------------------------------------
                    long rowID = BdbHper.insertRec_G100(newRow);
                }
                // ---------------------------
            } else {
//                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), getString(R.string.join_mysql_error1), Toast.LENGTH_LONG).show();
            }
            queryDB();  //重新載入SQLite
            BdbHper.is_end();//判斷是否已經結束揪團
            //RecycleViewChange();//變更Listview的內容
            // --------------------------------------------------------
        } catch (Exception e) {
        }
    }
    public void dbmysql_G200() {// 讀取MySQL資料=>sqlite（G200）使用者ID所加入的\\揪團//
        sqlctl = "SELECT * FROM G200 WHERE G203 IN(SELECT G101 FROM G100 WHERE G108 IN (SELECT DISTINCT C201 FROM C200 WHERE C202="+u_id+"))";//sql 78測試用userID
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            int rowsAffected = BdbHper.clearRec_G200();// 匯入前,刪除G200 SQLite資料
            String result = DBConnector.executeQuery(nameValuePairs);
//            Toast.makeText(getApplicationContext(),"a:"+result,Toast.LENGTH_LONG).show();
            /**************************************************************************
             * SQL 結果有多筆資料時使用JSONArray
             * 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            JSONArray jsonArray = new JSONArray(result);//抓到的所有資料
                // 匯入前,刪除所有SQLite資料
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
                    // -------------------加入SQLite---------------------------------------
                    long rowID = BdbHper.insertRec_G200(newRow);
                }
                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
          //  queryDB();  //重新載入SQLite
            //BdbHper.is_end();//判斷是否已經結束揪團
            //RecycleViewChange();//變更Listview的內容
            // --------------------------------------------------------
        } catch (Exception e) {
        }
    }

    //－－－－－－－抓取已加入的社團!! C100 C200－－－－－－－－－－－－－－－－－－－－－－－－
    private void dbmysql_C100() {//mySQL=>SQLite
        sqlctl="SELECT * FROM C100 WHERE id IN (SELECT C201 FROM C200 WHERE C202="+u_id+" AND (C203=1 OR C203=2)) "; //78測試用userID
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            //--------------------------------------------------------
            int rowsAffected = BdbHper.clearRec_C100();                 // 匯入前,刪除[社團]的SQLite資料
            //--------------------------------------------------------
            String result = DBConnector28.executeQuery(nameValuePairs);
            chk_httpstate();
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
                    long rowID = BdbHper.insertRec_C100(newRow);
                }
//                Toast.makeText(getApplicationContext(), "共有 " + Integer.toString(jsonArray.length()) + " 個社團", Toast.LENGTH_SHORT).show();

                // ---------------------------
            } else {
//                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), getString(R.string.join_mysql_error1), Toast.LENGTH_LONG).show();
            }
            //recSet = dbHper.getRecSet();  //重新載入SQLite

            // --------------------------------------------------------
        } catch (Exception e) {}
    }
    private void dbmysql_C200() { //mySQL=>SQLite
        sqlctl = "SELECT * FROM C200  WHERE C202="+u_id;//假設78
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            //--------------------------------------------------------
            int rowsAffected = BdbHper.clearRec_C200();                 // 匯入前,刪除[社團]的SQLite資料
            //--------------------------------------------------------
            String result = DBConnector28.executeQuery(nameValuePairs);
            chk_httpstate();
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
                    // -------------------加入SQLite---------------------------------------
                    long rowID = BdbHper.insertRec_C200(newRow);
                }
//                Toast.makeText(getApplicationContext(), "共有 " + Integer.toString(jsonArray.length()) + " 個社團", Toast.LENGTH_SHORT).show();

                // ---------------------------
            } else {
//                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), getString(R.string.join_mysql_error1), Toast.LENGTH_LONG).show();
            }
            // recSet = dbHper.getRecSet();  //重新載入SQLite

            // --------------------------------------------------------
        } catch (Exception e) {}
    }

    //--------------------
    @Override
    public void onClick(View v)
    {
        Drawable drawable=getResources().getDrawable(R.drawable.button_check);
        Drawable drawable2=getResources().getDrawable(R.drawable.button_blue1);
        switch (v.getId()){
            case R.id.biker_join_Recruiting://當點擊開團中時

                join_change_bool=true;//切換不同layout判定
                join_Nowteam.setBackground(drawable2);
                join_Historyteam.setBackground(drawable);
                RecycleViewChange();//設定Listview的item;
                break;
            case R.id.biker_join_Historyteam://當點擊成團紀錄時

                join_change_bool=false;//切換不同layout判定
                join_Nowteam.setBackground(drawable);
                join_Historyteam.setBackground(drawable2);
                RecycleViewChange();//設定Listview的item;
                break;
            case R.id.biker_join_newteam_btn://當點擊創新啾團時
                if(join_spinner.getCount()!=0){
                    Intent intent = new Intent(Biker_Join.this, Biker_Join_NewTeam.class);
                    startActivityForResult(intent,REQUEST_CODE);
                }
                else{
//                    Toast.makeText(getApplicationContext(),"你目前沒有加入任何社團，不可開新揪團",Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),getString(R.string.join_add_error1),Toast.LENGTH_SHORT).show();
                }


//
//                intent.setClass(Biker_Join.this, Biker_Join_NewTeam.class);
//                startActivity(intent);//跳至新增頁面
                break;
        }
    }
    private Spinner.OnItemSelectedListener mSpnNameOnItemSelLis = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView parent, View view, int position,
                                   long id) {
            int iSelect = join_spinner.getSelectedItemPosition(); //找到按何項
            spinner_index=array_clubid.get(iSelect);
            queryDB();//從sqlite抓取指定資料
            RecycleViewChange();//更新RecycelView的畫面
           // Toast.makeText(getApplicationContext(),correct_Position+"",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };
    private void SpinnerText()//顯示特定spinner的文字
    {
        array_clubid.clear();//重置(不一定要)
        ArrayAdapter<String> adap_spinner =new ArrayAdapter<String>(this, R.layout.join_spinner);
        ArrayList<String> array_c100=BdbHper.getRecSet_C100();
        for (int i = 0; i < array_c100.size(); i++)//未加入
        {
            String[] fld = array_c100.get(i).split("#");
            //spinner
            adap_spinner.add(fld[1]);
            array_clubid.add(fld[0]);
            //--------
        }
        adap_spinner.setDropDownViewResource(R.layout.join_spinner);
        join_spinner.setAdapter(adap_spinner);
    }
    public void RecycleViewChange(){//切換RecycleView內容的方法
        arrayList.clear();
        correct_Position.clear();
        if(join_change_bool==true)//按下開團中
        {
            bool_listview_item=true;//預設有資料
            for (int i = 0; i < joinDb_strArray.length; i++)
            {
                //String aa=joinDb_strArray[i][8];
                if(joinDb_strArray[i][9].equals("0")){//如果G109(是否已結束)的值為0(還沒結束)
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("title", joinDb_strArray[i][1]);
                    hashMap.put("date", joinDb_strArray[i][2]+"---");
                    arrayList.add(hashMap);
                    correct_Position.add(i);//加入實際的position
                }

            }
            if(arrayList.size()==0){//如果開團資訊沒資料
                bool_listview_item=false;//沒資料
                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("title", "尚無開團中資訊");
                hashMap.put("title", getString(R.string.join_prompt1));
                hashMap.put("date", "");
                arrayList.add(hashMap);
            }
        }
        else{//按下歷史團
            bool_listview_item=true;//假設有資料
            for (int i = 0; i < joinDb_strArray.length; i++)
            {
             //   String a=joinDb_strArray[i][8];
                if (joinDb_strArray[i][9] .equals("1") )//如果G109(是否已結束)的值為1(已經結束)
                {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("title", joinDb_strArray[i][1]);
                    hashMap.put("date", joinDb_strArray[i][2]+"---");
                    arrayList.add(hashMap);
                    correct_Position.add(i);//加入實際的position
                }
            }
            if(arrayList.size()==0){//如果歷史資訊沒資料
                bool_listview_item=false;//沒資料
                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("title", "尚無歷史團隊資訊");
                hashMap.put("title", getString(R.string.join_prompt2));
                hashMap.put("date", "");
                arrayList.add(hashMap);
            }

        }
         mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
      //  mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        myListAdapter = new MemberListAdapter();
        mRecyclerView.setAdapter(myListAdapter);
    }


    //1420新增的方法(伺服器狀態)-----
    private void chk_httpstate() {
        ////-------------------------------
        //存取類別成員 DBConnector01.httpstate 判定是否回應 200(連線要求成功)
        if (DBConnector.httpstate == 200) {
//            ser_msg = "伺服器匯入資料(code:" + DBConnector.httpstate + ") ";
            ser_msg = getString(R.string.join_okHttp_error0) + DBConnector.httpstate + ") ";
        } else {
            int checkcode = DBConnector.httpstate / 100;
            switch (checkcode) {
                case 1:
//                    ser_msg = "資訊回應(code:" + DBConnector.httpstate + ") ";
                    ser_msg = getString(R.string.join_okHttp_error1) + DBConnector.httpstate + ") ";
                    break;
                case 2:
//                    ser_msg = "已經完成由伺服器匯入資料(code:" + DBConnector.httpstate + ") ";
                    ser_msg = getString(R.string.join_okHttp_error2) + DBConnector.httpstate + ") ";
                    break;
                case 3:
//                    ser_msg = "伺服器重定向訊息，請稍後在試(code:" + DBConnector.httpstate + ") ";
                    ser_msg = getString(R.string.join_okHttp_error3) + DBConnector.httpstate + ") ";
                    break;
                case 4:
//                    ser_msg = "用戶端錯誤回應，請稍後在試(code:" + DBConnector.httpstate + ") ";
                    ser_msg = getString(R.string.join_okHttp_error4) + DBConnector.httpstate + ") ";
                    break;
                case 5:
//                    ser_msg = "伺服器error responses，請稍後在試(code:" + DBConnector.httpstate + ") ";
                    ser_msg = getString(R.string.join_okHttp_error5) + DBConnector.httpstate + ") ";
                    break;
            }
//                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        }
        if (DBConnector.httpstate == 0) {
//            ser_msg = "遠端資料庫異常(code:" + DBConnector.httpstate + ") ";
            ser_msg = getString(R.string.join_okHttp_error6) + DBConnector.httpstate + ") ";
            server_error++;
        }
//        Toast.makeText(getBaseContext(), ser_msg, Toast.LENGTH_SHORT).show();

        //-------------------------------------------------------------------
    }
    private void show_dialog()
    {
        AlertDig = new Dialog(Biker_Join.this);

        AlertDig.setCancelable(false);//不能按其他地方
        AlertDig.setContentView(R.layout.alert_dialog);//選擇layout

        alertBtnOK = (Button)AlertDig.findViewById(R.id.train_btnOK);
        alertBtnCancel = (Button)AlertDig.findViewById(R.id.train_btnCancel);
        Dig_tarin_waring = (TextView) AlertDig.findViewById(R.id.tarin_waring);
        Dig_train_title=(TextView)AlertDig.findViewById(R.id.train_title);
//        Dig_train_title.setText("請稍後再試");
        Dig_train_title.setText(getString(R.string.join_dialog_3));
        Dig_train_title.setTextSize(16);
        Dig_tarin_waring.setText(ser_msg);
        Dig_tarin_waring.setTextSize(12);
        alertBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                btnfrmLy.setVisibility(View.GONE);
//                btnupmysqlLY.setVisibility(View.VISIBLE);
                show_ProgDlg();//啟動進度條
                handler.postDelayed(readSQL, 1000);
                AlertDig.cancel();
            }
        });
        alertBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                btnfrmLy.setVisibility(View.GONE);
//                btnupmysqlLY.setVisibility(View.VISIBLE);
                AlertDig.cancel();
            }
        });
        AlertDig.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)//返回跑完後接onresume
    {
        super.onActivityResult(requestCode, resultCode, data);
        initDB();
//-----嘗試用這個
        show_ProgDlg();//啟動進度條
        handler.postDelayed(readSQL, 1000);
    //--------------
        //---暫時嘗試註解
//        dbmysql_G100();//載入mysql 目前放在initDB裡面
//        dbmysql_C100();
//        dbmysql_C200();
//---





//        RecycleViewChange();//變更顯示RecycleView的方法
//        SpinnerText();//顯示spinner所抓到的所屬社團
       // Toast.makeText(getApplicationContext(),"onActivityResult",Toast.LENGTH_SHORT).show();
    }
//生命週期
    @Override
    protected void onPause() {
        super.onPause();
        if (BdbHper != null) { //如果資料庫連線中，就關閉
            BdbHper.close();
            BdbHper = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (BdbHper != null) { //如果資料庫連線中，就關閉
            BdbHper.close();
            BdbHper = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDB();//如果沒有連線資料庫 就開啟,並且將str_JoinDb陣列的值更新為資料表的值
        RecycleViewChange();//更新清單的畫面
        //Toast.makeText(getApplicationContext(),"onResume",Toast.LENGTH_SHORT).show();
    }













    ////----------------------------------以下是menu用的方法----------------
    private void Signin_menu(){//登入時顯示的menu
        menu.setGroupVisible(R.id.g01,false);
        menu.setGroupVisible(R.id.g02,true);
    }
    //↓↓↓↓↓除了亮瑜使用此方法外，其他人原則上都使用Signin_menu↑↑的方法顯示menu item（167行已經寫好(亮瑜的部分改167行)）
    private void Signout_menu(){//登出時顯示的menu
        menu.setGroupVisible(R.id.g01,true);
        menu.setGroupVisible(R.id.g02,false);
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.biker_menu,menu);
        this.menu=menu;
        menu.setGroupVisible(R.id.g01,false);
        menu.setGroupVisible(R.id.g02,false);
//        menu.add(0,1,0,"開新揪團").setIcon(android.R.drawable.ic_media_play);
        menu.add(0,1,0,getString(R.string.biker_join_newteam)).setIcon(android.R.drawable.ic_media_play);
        return true; //返回值為“true”,表示菜單可見，即顯示菜單
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()){

            case 1:
                join_NewTeam_btn.callOnClick();
                break;
            case R.id.action_settings: //原則上不用修改
                //Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView list_title,list_Details;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                list_title = itemView.findViewById(R.id.list_title);
                list_Details = itemView.findViewById(R.id.list_Details);
                itemView.setOnClickListener(new View.OnClickListener()//點擊相關格子
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(join_change_bool==true)//開團中
                        {
                            if(bool_listview_item){//如果開團中有實際
                                // 資料的話 才可以點擊
                                //dbmysql_G200();
//                                Toast.makeText(getApplicationContext(),arrayList.get(getAdapterPosition()).get("title"),Toast.LENGTH_SHORT).show();
//                                Toast.makeText(getApplicationContext(),correct_Position.get(getAdapterPosition())+"",Toast.LENGTH_SHORT).show();
                                String[] db_array = new String[joinDb_strArray[0].length];//陣列長度=TB欄位數量
                                //     Toast.makeText(getApplicationContext(),joinDb_strArray[correct_Position.get(getAdapterPosition())][0],Toast.LENGTH_SHORT).show();
                                db_array = joinDb_strArray[correct_Position.get(getAdapterPosition()) ];
                                Intent intent = new Intent(Biker_Join.this, Biker_Join_now.class);
                                Bundle bundle = new Bundle();
                                bundle.putStringArray("now_information", db_array);
                                intent.putExtras(bundle);
                                startActivityForResult(intent,REQUEST_CODE);


//                                Intent it = new Intent();
//                                it.setClass(Biker_Join.this, Biker_Join_now.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putStringArray("now_information", db_array);
//                                it.putExtras(bundle);
//                                startActivity(it);
//


                            }

                        }
                        else{//成團紀錄
                            if(bool_listview_item){//如果開團中有實際資料的話 才可以點擊

                                String[] db_array = new String[joinDb_strArray[0].length];//陣列長度=TB欄位數量
                                db_array = joinDb_strArray[correct_Position.get(getAdapterPosition()) ];
                                Intent intent = new Intent(Biker_Join.this, Biker_Join_history.class);
                                Bundle bundle = new Bundle();
                                bundle.putStringArray("now_information", db_array);
                                intent.putExtras(bundle);
                                startActivityForResult(intent,REQUEST_CODE);


//                                Intent it = new Intent();
//                                it.setClass(Biker_Join.this, Biker_Join_now.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putStringArray("now_information", db_array);
//                                it.putExtras(bundle);
//                                startActivity(it);
//


                            }
                        }
                    }
                });
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //:連接剛才寫的layout檔案，return一個View
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.biker_join_recycle_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {//在這裡取得元件的控制(每個item內的控制)

            if(join_change_bool==true)//開團中
            {
                if(bool_listview_item){//如果開團中有實際資料的話 才顯示詳細資訊的textview
                    holder.list_Details.setVisibility(View.VISIBLE);
                }
                else{
                    holder.list_Details.setVisibility(View.INVISIBLE);
                }
                holder.list_title.setText(arrayList.get(position).get("date")  + arrayList.get(position).get("title"));

            }
            else//成團紀錄
            {
                if(bool_listview_item){//如果成團紀錄中有實際資料的話 才顯示詳細資訊的textview
                    holder.list_Details.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.list_Details.setVisibility(View.INVISIBLE);
                }
                holder.list_title.setText(arrayList.get(position).get("date") +arrayList.get(position).get("title"));

            }
        }

        @Override
        public int getItemCount() {//取得顯示數量，return一個int，通常都會return陣列長度(arrayList.size)
            return arrayList.size();
        }

    }
}
