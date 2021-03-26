package com.bikerlifeYang;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class Biker_profile_society extends AppCompatActivity implements View.OnClickListener, ViewSwitcher.ViewFactory {

    private String mode_title, gone,user_name;
    private Intent intent01 = new Intent();
    private EditText e030,e053;
    private TextView t30, t035, t33,t036,t058,t059,t039,t040,t041,t01,t02,t032,t40,t41,t38,t37,t054,t056,t061;
//    private String[] listarr;
    private RelativeLayout r_layout;
    private Dialog mLoginDlg,profile_dialog;//有實體不用new
    RecyclerView mRecyclerView;//for recyclerview-社員
    MyListAdapter myListAdapter;//for recyclerview-社員
    RecyclerView mRecyclerView1;//for recyclerview-待審核
    MyListAdapter1 myListAdapter1;//for recyclerview-待審核
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();//for recyclerview-社員
    ArrayList<HashMap<String,String>> arrayList1 = new ArrayList<>();//for recyclerview-待審核
    private HashMap<String, String> hashMap;//for recyclerview-社員
    private HashMap<String, String> hashMap1;//for recyclerview-待審核
    private int id,picminus,picplus;//固定的-&+號圖案，id=cur_list.getInt(0);//刪除用
    private Menu menu;
    private FriendDbHelper28 dbHper;
    private static final String DB_TABLE = "C100";    // 資料庫物件，固定的欄位變數
    private LinearLayout llayout_info;//社團資訊
    private static final String DB_FILE = "bikerlife.db";
    private int DBversion = 1;
    private ArrayList<String> recSet;
    private String sqlctl;
    private ArrayList<String> mList;
    private String leaderUID;
    private String leadername;//取得團長姓名
    private ArrayList<String> compare;//放快慢長短的使用者
    private ArrayList<String> G100_data;
    private int max_member;
    private int number;//目前人數
//    private String leader_name;//取得團長UID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);//上架必加，用到mySQL必加
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_profile_society);
        initDB();//有用到DB一定要加，放setupViewcomponent前面***
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
        e030 = (EditText) findViewById(R.id.profile_e030);//社團名稱
        e053 = (EditText) findViewById(R.id.profile_e053);//社團公告
        t032=(TextView)findViewById(R.id.profile_t032);//團長
        t054 = (TextView) findViewById(R.id.profile_t054);//社團公告(按鈕)
        t056 = (TextView) findViewById(R.id.profile_t056);//最近揪團
        t061 = (TextView) findViewById(R.id.profile_t061);//出發時間
        t058=(TextView)findViewById(R.id.profile_t058);//縣市名稱
        t059=(TextView)findViewById(R.id.profile_t059);//鄉鎮市區名稱
        t30 = (TextView) findViewById(R.id.profile_t30);//確認修改
        t33 = (TextView) findViewById(R.id.profile_t33);//社團人數
        t035 = (TextView) findViewById(R.id.profile_t035);//8/50人
        t036=(TextView)findViewById(R.id.profile_t036);//刪除社團(按鈕)
        t039=(TextView)findViewById(R.id.profile_t039);//社團資訊(按鈕)
        t040=(TextView)findViewById(R.id.profile_t040);//社員(按鈕)
        t041=(TextView)findViewById(R.id.profile_t041);//待審核(按鈕)
        llayout_info=(LinearLayout)findViewById(R.id.llayout_info);//社團資訊
        t01=(TextView)findViewById(R.id.profile_t01);//社員(bar)
        t02=(TextView)findViewById(R.id.profile_t02);//待審核(bar)
        t40=(TextView)findViewById(R.id.profile_t40);//最快
        t41=(TextView)findViewById(R.id.profile_t41);//慢
        t38=(TextView)findViewById(R.id.profile_t38);//長
        t37=(TextView)findViewById(R.id.profile_t37);//短



        //        *** implements View.OnClickListener 之後，每個要動作的物件都要有這行啟動監聽事件***
        t30.setOnClickListener(this);//確認修改
        t036.setOnClickListener(this);//刪除社團
        t039.setOnClickListener(this);//社團資訊(按鈕)
        t040.setOnClickListener(this);//社員(按鈕)
        t041.setOnClickListener(this);//待審核(按鈕)
        t054.setOnClickListener(this);//更新社團公告

        Bundle bundle = this.getIntent().getExtras();//**********

        //---設定class標題
        intent01 = this.getIntent();
//        mode_title = bundle.getString("societyName");
        mode_title=intent01.getStringExtra("societyName");//取得社團名稱
        user_name=intent01.getStringExtra("u_id");//取得會員UID
//        this.setTitle(this.getResources().getIdentifier(mode_title,"string",getPackageName()));
        e030.setText(mode_title);//將使用者於個人檔案選擇的社團名稱丟進更改社團名稱
        //---
        //從create或新創的社團過來，物件全gone
//        gone = bundle.getString("create");//**********
//        if (mode_title.equals("社團1") || mode_title.equals("社團2") || mode_title.equals("社團3")){
//            gone ="";//為了不讓物件全gone設的判斷式
//        }

        //設定縣市及鄉鎮市區(textview)撈取資料庫全部欄位
        DBversion=Integer.parseInt(getString(R.string.SQLite_version));
        Cursor cur_list=dbHper.getDatabase(getApplicationContext(),DBversion).query(true,
                DB_TABLE,
                new String[]{"id","C101","C102","C103","C104","C105","C106","C108"},
                "C101=" + "\"" + mode_title+ "\"",//全部相同
                null,
                null,
                null,
                null,
                null );
        if (cur_list==null){//沒東西
            return;
        }
        if (cur_list.getCount()==0){
//            Toast.makeText(getApplicationContext(),getString(R.string.nosociety),Toast.LENGTH_SHORT).show();
        }else{
            cur_list.moveToFirst();//移到第一筆，一定要加
            id=cur_list.getInt(0);//刪除用，對應到上面的new String[]{"id","C101","C102","C103","C104","C105","C106"},0是id
            t33.setText(cur_list.getString(2));//對應到上面的new String[]{"id","C101","C102","C103","C104","C105","C106"},2是目前人數
            t035.setText("/"+cur_list.getString(3)+"人");//對應到上面的new String[]{"id","C101","C102","C103","C104","C105","C106"},3是人數上限
            t058.setText(cur_list.getString(4));//對應到上面的new String[]{"id","C101","C102","C103","C104","C105","C106"},4是city
            t059.setText(cur_list.getString(5));//對應到上面的new String[]{"id","C101","C102","C103","C104","C105","C106"},5是area
            leaderUID=cur_list.getString(6);//對應到上面的new String[]{"id","C101","C102","C103","C104","C105","C106"},6是leaderUID
            leadername=dbHper.getLeaderName(leaderUID);
            t032.setText(leadername);
            max_member=cur_list.getInt(3);//人數上限(滿人不給加
            String societyPost=cur_list.getString(7);//社團公告
            e053.setText(societyPost);
        }
        cur_list.close();//記得關
        //設定縣市及鄉鎮市區(textview)

        //開機動畫
//        r_layout = (RelativeLayout) findViewById(R.id.profile_r02);
//        r_layout.setBackgroundResource(R.drawable.background_color);
//        r_layout.setAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_scale_rotate_start));

        //for recyclerview--社員
        mRecyclerView = findViewById(R.id.recycleview);//相對應的Layout的recycleviewID
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        myListAdapter = new MyListAdapter();
        mRecyclerView.setAdapter(myListAdapter);
        //for recyclerview--社員
        //for recyclerview--待審核
        mRecyclerView1 = findViewById(R.id.recycleview1);//相對應的Layout的recycleviewID
        mRecyclerView1.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView1.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        myListAdapter1 = new MyListAdapter1();
        mRecyclerView1.setAdapter(myListAdapter1);
        //for recyclerview--待審核

//        lv = (ListView) findViewById(R.id.member);//準備一個要放社員的ListView
//        mList=new ArrayList<Map<String, Object>>();//準備一個要放社員資料的ArrayList
//        listarr = getResources().getStringArray(R.array.profile_friend);//取得社員陣列內容
        //固定的-號圖案
        picminus = getResources().getIdentifier("ic_delete_profile", "drawable", getPackageName());
        //固定的+號圖案
        picplus = getResources().getIdentifier("ic_add_profile", "drawable", getPackageName());
        getleader();//取得團長UID
        //--------------------------------------------巨集---社員資訊
        //for recyclerview-社員資訊
        member_list();

//        //--------------------------------------------巨集---待審核
        //for recyclerview-待審核
        verify_list();

        //騎乘快慢長短
        competition();

        //揪團
        get_G100();
    }

    private void get_G100() {
        G100_data=dbHper.getG100(id);//id為社團UID
        try{//G100_data可能傳回null
            t056.setText(getString(R.string.profile_t054)+G100_data.get(0));//揪團名稱
            t061.setText(getString(R.string.profile_t057)+G100_data.get(1)+"/"+G100_data.get(2));//揪團日期時間
        }catch (Exception e){}
    }

    private void competition() {
        compare=dbHper.competition(id);//id為社團UID
        try {
            t40.setText(compare.get(0));//最快
            t41.setText(compare.get(1));//最慢
            t38.setText(compare.get(2));//距離最長
            t37.setText(compare.get(3));//距離最短
        }catch (Exception e){}

    }

    private void getleader() {
        //團長
        try {
//            leader_name=dbHper.getC200_leader(leaderUID);//取得團長的會員UID

                if(!user_name.equals(leaderUID)){//判斷使用者是否為團長，不是的話垃圾桶跟確認修改&待審核&更新社團公告要消失
                    t036.setVisibility(View.INVISIBLE);
                    t30.setVisibility(View.INVISIBLE);
                    t041.setVisibility(View.INVISIBLE);
                    t054.setVisibility(View.INVISIBLE);
                    e030.setFocusable(false);
                    e030.setEnabled(false);//不可編輯
                    e053.setFocusable(false);
                    e053.setEnabled(false);
                }else{
                    t036.setVisibility(View.VISIBLE);//是的話垃圾桶跟確認修改&待審核&更新社團公告要出現
                    t30.setVisibility(View.VISIBLE);
                    t041.setVisibility(View.VISIBLE);
                    t054.setVisibility(View.VISIBLE);
                    e030.setFocusable(true);
                    e030.setEnabled(true);
                    e053.setFocusable(true);
                    e053.setEnabled(true);
                }
            }
         catch (Exception e) {
            return;
        }
    }


    private void member_list() {//社員
        try {
            mList=dbHper.getC200_member(id);//取得所有社團人員明細
            for (int i = 0;i<mList.size();i++){
                // %02d執行十進制整數轉換d，格式化補零，寬度為2。 因此，一個int參數，它的值是7，將被格式化為"07"作為一個String
//                String microNO = String.format("%03d", i);//抓drawable用
//                取得圖片
//                int picId = getResources().getIdentifier("img" + microNO, "drawable", getPackageName());
//                if(picId==0){
//                    picId=2131230852;//後面沒頭像的用這個
//                }
                String file=mList.get(i);//姓名|頭貼|會員UID
                String[] fld = file.split("#");//一條字串用|分割似乎會出錯，每個字都被割(M1405browse)
//                String httpname=fld[1];
//                Picasso.get()
//                        .load(httpname)
//                        .into(image_view);

                hashMap = new HashMap<>();
                //第一個參數對應layout，第二個參數告訴電腦去哪找資料
//                hashMap.put("friendPic",Integer.toString(picId));//頭像
//                String a=Picasso.get().load(fld[1]).toString();
                hashMap.put("friendPic",fld[1]);//頭像
//                hashMap.put("friendName",listarr[i-49]);//姓名
                hashMap.put("friendName",fld[0]);//姓名
                hashMap.put("friendSymbol",Integer.toString(picminus));//X
                hashMap.put("friendID1",fld[2]);//會員UID

                arrayList.add(hashMap);

//                if (picId == 0) {
//                    break;//撈不到值(=傳回0)就跳出迴圈
//                }
            }
        } catch (Exception e) {
            return;
        }
    }

    private void verify_list() {//待審核
                try {
            mList=dbHper.getC200(id);//取得所有社團人員明細
            for (int i = 0;i<mList.size();i++){
                // %02d執行十進制整數轉換d，格式化補零，寬度為2。 因此，一個int參數，它的值是7，將被格式化為"07"作為一個String
//                String microNO = String.format("%03d", i);//抓drawable用
//                取得圖片
//                int picId = getResources().getIdentifier("img" + microNO, "drawable", getPackageName());
//                if(picId==0){
//                    picId=2131230852;//後面沒頭像的用這個
//                }
                String file=mList.get(i);//姓名|頭貼|會員UID
                String[] fld = file.split("#");//一條字串用#分割(M1405browse)
//                String httpname=fld[1];
//                Picasso.get()
//                        .load(httpname)
//                        .into(image_view);

//                Map<String,Object> item=new HashMap<String, Object>();
//                HashMap<String,String>
                hashMap1 = new HashMap<>();
                //第一個參數對應layout，第二個參數告訴電腦去哪找資料
                hashMap1.put("friendPic",fld[1]);//頭像
                hashMap1.put("friendSymbol_add",Integer.toString(picplus));//+
//                hashMap.put("friendName",listarr[i-49]);//姓名
                hashMap1.put("friendName",fld[0]);//姓名
                hashMap1.put("friendSymbol",Integer.toString(picminus));//X
                hashMap1.put("friendID",fld[2]);//會員UID

                arrayList1.add(hashMap1);

//                if (picId == 0) {
//                    break;//撈不到值(=傳回0)就跳出迴圈
//                }
            }
        } catch (Exception e) {
            return;
        }
    }


    // 讀取MySQL 資料-社團人員明細
    private void dbmysql_C200() {
        sqlctl = "SELECT * FROM C200 ";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            //--------------------------------------------------------
            int rowsAffected = dbHper.clearRec_C200();                 // 匯入前,刪除[社團人員明細]的SQLite資料
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
            recSet = dbHper.getRecSet();  //重新載入SQLite
            // --------------------------------------------------------
        } catch (Exception e) {}
    }

            //有用到DB一定要加***
    private void initDB() {
        DBversion=Integer.parseInt(getString(R.string.SQLite_version));
        if (dbHper == null) {      //dbHper=SQLite
            dbHper = new FriendDbHelper28(this, DB_FILE, null, DBversion);
        }
//        dbmysql_C200();//帶入社團C200--MySQL資料
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_t30://更改社團名稱
                if (e030.getText().toString().trim().equals(""))//記得修剪空白
                {
                    Toast.makeText(getApplicationContext(),getString(R.string.profile_t056),Toast.LENGTH_SHORT).show();
                }else{
                    String societyName=e030.getText().toString().trim();//trim()去掉空白
                    int a=dbHper.FindRec(societyName);//看社團名稱有沒有人取過了(0-有；1-沒有)

                    if(a==1){//社團名稱沒人取過
//                        dbHper.updateSocietyName(id,societyName);//14-9
                        mysql_update(1,societyName);//執行MySQL更新

                        this.finish();
                    }else{//社團名稱已存在
                        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.nameexist), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP|Gravity.START,300,350);
                        toast.show();
                    }

                }
                break;
            case R.id.profile_t054://更新公告
                String societyPost=e053.getText().toString().trim();//trim()去掉空白
//                dbHper.updateSocietyName(id,societyPost);//14-9
                mysql_update(2,societyPost);//執行MySQL更新

                this.finish();
                break;
//            case R.id.profile_t056://拒絕入社申請
//                mLoginDlg=new Dialog(this);
//                mLoginDlg.setTitle(getString(R.string.profile_t056));
//                mLoginDlg.setCancelable(false);//一定要給我選，true的話可以按旁邊躲掉
//
//                //R.layout.profile_dialog當作Dialog的畫面
//                mLoginDlg.setContentView(R.layout.alert_dialog);
//
//                Button btnOK1 = (Button) mLoginDlg.findViewById(R.id.train_btnOK);
//                Button btnCancel1 = (Button) mLoginDlg.findViewById(R.id.train_btnCancel);
//                TextView warning1=(TextView)mLoginDlg.findViewById(R.id.tarin_waring);
//                warning1.setText(getString(R.string.warn_stock));//更改dialog的字
//
//                btnCancel1.setOnClickListener(new View.OnClickListener() {
//                                                  @Override
//                                                  public void onClick(View v) {
//                                                      mLoginDlg.cancel();
//                                                  }
//                                              });
//                        btnOK1.setOnClickListener(new View.OnClickListener() {
//                                                      @Override
//                                                      public void onClick(View v) {
//                                                          ta01.setVisibility(View.GONE); // 物件隱藏
//                                                          mLoginDlg.cancel();
//                                                      }
//                                                  });
//
//                mLoginDlg.show();
//                break;
//            case R.id.profile_t66://同意入社申請
//                //第一個參數對應layout，第二個參數告訴電腦去哪找資料
//                int picId = getResources().getIdentifier("img049", "drawable", getPackageName());
//                hashMap.put("friendPic",Integer.toString(picId));//頭像
//                hashMap.put("friendName",getString(R.string.profile_t049));//姓名
//                hashMap.put("friendSymbol",Integer.toString(picminus));//X
//                arrayList.add(hashMap);
//                myListAdapter.notifyDataSetChanged();
//                member++;
//                t33.setText(Integer.toString(member));//社員多一人
//                ta01.setVisibility(View.GONE); // 物件隱藏
//
//                break;
            case R.id.profile_t036://刪除社團
                mLoginDlg=new Dialog(this);
                mLoginDlg.setTitle(getString(R.string.profile_t056));
                mLoginDlg.setCancelable(false);//一定要給我選，true的話可以按旁邊躲掉

                //R.layout.profile_dialog當作Dialog的畫面
                mLoginDlg.setContentView(R.layout.alert_dialog);

                Button btnOK = (Button) mLoginDlg.findViewById(R.id.train_btnOK);
                Button btnCancel = (Button) mLoginDlg.findViewById(R.id.train_btnCancel);
                TextView warning=(TextView)mLoginDlg.findViewById(R.id.tarin_waring);

                btnCancel.setOnClickListener(this);
                btnOK.setOnClickListener(this);
                warning.setText(getString(R.string.warn));//更改dialog的字
                mLoginDlg.show();
                break;
            case R.id.train_btnCancel://取消刪除社團
                mLoginDlg.cancel();
                break;
            case R.id.train_btnOK://確定刪除社團
               dbHper.deleteSociety(id);//14-9
                mysql_del();// 執行MySQL刪除
                mLoginDlg.cancel();
                this.finish();
                break;
            case R.id.profile_t039://社團資訊(按鈕)
                t01.setVisibility(View.INVISIBLE);
                t02.setVisibility(View.INVISIBLE);
                mRecyclerView1.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                llayout_info.setVisibility(View.VISIBLE);
                if(user_name.equals(leaderUID)){
                    t054.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.profile_t040://社員(按鈕)
                t01.setVisibility(View.VISIBLE);
                t02.setVisibility(View.INVISIBLE);
                mRecyclerView1.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                llayout_info.setVisibility(View.INVISIBLE);
                t054.setVisibility(View.INVISIBLE);
                break;
            case R.id.profile_t041://待審核(按鈕)
                t01.setVisibility(View.INVISIBLE);
                t02.setVisibility(View.VISIBLE);
                mRecyclerView1.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                llayout_info.setVisibility(View.INVISIBLE);
                t054.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void mysql_del() {//---刪除社團-
        ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
        nameValuePairs.add(""+id);
        try {
            Thread.sleep(100); //  延遲Thread 睡眠0.1秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = DBConnector28.executeDelet(nameValuePairs);
//-----------------------------------------------
    }

    private void mysql_del_member(String b_member) {//---刪除社員-
        ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
        nameValuePairs.add(""+id);//社團UID
        nameValuePairs.add(b_member);//社員UID
        try {
            Thread.sleep(100); //  延遲Thread 睡眠0.1秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = DBConnector28.executeDelete_member(nameValuePairs);
//-----------------------------------------------
    }

    private void mysql_update(int name_or_post,String b_name) {//更新1.社團名稱或2.公告
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(""+id);
        nameValuePairs.add(b_name);
        nameValuePairs.add(""+name_or_post);
        try {
            Thread.sleep(500); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = DBConnector28.executeUpdate( nameValuePairs);
//-----------------------------------------------
    }
    private void mysql_update_C200(String b_name) {//審核中->社員
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(""+id);//C201,要改哪筆(主鍵是C201+C202)
        nameValuePairs.add(b_name);//待審核之人的UID(C202)
        try {
            Thread.sleep(500); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = DBConnector28.executeUpdate_C200( nameValuePairs);
//-----------------------------------------------
    }

    private void mysql_delete_C200(String b_name) {//不給過審核&刪除社員
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(""+id);//C201,要改哪筆(主鍵是C201+C202)
        nameValuePairs.add(b_name);//被刪除之人的UID(C202)
        try {
            Thread.sleep(500); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = DBConnector28.executeDelete_C200( nameValuePairs);
//-----------------------------------------------
    }

    @Override
    public View makeView() {
        ImageView v = new ImageView(this);
//        v.setBackgroundColor(0xFF000000);
        v.setLayoutParams(new ImageSwitcher.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return v;
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

    //----------------------------------------------------副程式for recyclerview--社員----------------------------------------------------------------------------
    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{


        class ViewHolder extends RecyclerView.ViewHolder{
            private TextView tvfriendName,tvfriendID1;
            private ImageView tvfriendPic,tvfriendSymbol;
            private View mView;

            public ViewHolder(@NonNull View itemView) {//設置recyclerview的內容
                super(itemView);
                tvfriendPic = itemView.findViewById(R.id.friendPic);
                tvfriendName = itemView.findViewById(R.id.friendName);
                tvfriendSymbol = itemView.findViewById(R.id.friendSymbol);
                tvfriendID1 = itemView.findViewById(R.id.friendID1);
                mView  = itemView;
            }
        }

        @NonNull
        @Override//選擇用哪個layout放recyclerview
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_member_lv,parent,false);
            return new ViewHolder(view);
        }

        @Override//生成recyclerview列表
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            holder.tvfriendPic.setImageResource(Integer.parseInt(arrayList.get(position).get("friendPic")));
            Picasso.get().load(arrayList.get(position).get("friendPic")).into(holder.tvfriendPic);
            holder.tvfriendName.setText(arrayList.get(position).get("friendName"));
            holder.tvfriendSymbol.setImageResource(Integer.parseInt(arrayList.get(position).get("friendSymbol")));
            holder.tvfriendID1.setText(arrayList.get(position).get("friendID1"));//會員UID


//            holder.label.setText(mDataset.get(position).getmText1());
//            Picasso.with(context).load(mDataset.get(position).getImageUrl()).into(holder.imgViewIcon);
//            holder.description.setText(mDataset.get(position).getmText2());

            String del_self=arrayList.get(position).get("friendID1");//刪自己
            if(user_name.equals(leaderUID) || user_name.equals(del_self)){//判斷使用者是否為團長，是的話開啟刪除功能，不是的話只能刪自己
                holder.tvfriendSymbol.setOnClickListener((v)->{//按X開啟確認刪除的對話盒
                    profile_dialog = new Dialog(Biker_profile_society.this);
                    profile_dialog.setContentView(R.layout.alert_dialog);
                    profile_dialog.setCancelable(false);//一定要給我選
                    Button btnOK = (Button) profile_dialog.findViewById(R.id.train_btnOK);
                    Button btnCancel = (Button) profile_dialog.findViewById(R.id.train_btnCancel);

                    TextView warning=(TextView)profile_dialog.findViewById(R.id.tarin_waring);
                    warning.setText(getString(R.string.profile_t011)+arrayList.get(position).get("friendName")+"?");//更改dialog的字

                    btnCancel.setOnClickListener(new View.OnClickListener() {//取消刪除社員
                        @Override
                        public void onClick(View v) {
                            profile_dialog.cancel();//關閉對話盒
                        }
                    });
                    btnOK.setOnClickListener(new View.OnClickListener() {//確認刪除社員
                        @Override
                        public void onClick(View v) {
                            String member_name=arrayList.get(position).get("friendID1");//取得社員的UID
                            mysql_del_member(member_name);
                            arrayList.remove(position);
                            number=Integer.parseInt(t33.getText().toString().trim())-1;//社員少一人
                            t33.setText(Integer.toString(number));//社員少一人
                            notifyDataSetChanged();//整體刷新
                            myListAdapter1.notifyDataSetChanged();//這裡要刷新才會進number!=max_member的判別式
                            profile_dialog.cancel();//關閉對話盒
                        }
                    });

                    profile_dialog.show();
                });
            }else{
                holder.tvfriendSymbol.setOnClickListener((v)->{//不是團長只能跳吐司
                    Toast.makeText(getApplicationContext(),getString(R.string.not_leader),Toast.LENGTH_LONG).show();
                     });
            }

            //對話盒-------其他社員的個人檔案------------------------------------------------------------------
            holder.tvfriendName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getApplicationContext(),arrayList.get(position).get("friendName"),Toast.LENGTH_SHORT).show();
                    String member_uid=arrayList.get(position).get("friendID1");//取得社員的UID
                    Bundle bundle_file = new Bundle();//包裹
                    bundle_file.putString("member_profile", member_uid);
                    intent01.putExtras(bundle_file);
                    intent01.setClass(Biker_profile_society.this,Biker_profile.class);
                    startActivity(intent01);
//                    setResult(RESULT_OK, intent01);//回傳值
//                    finish();
                }
            });
        }
        @Override
        public int getItemCount() {
            return arrayList.size();//決定顯示數量，這裡的寫法是顯示全部
        }
    }
    //----------------------------------------------------副程式for recyclerview--待審核----------------------------------------------------------------------------
    private class MyListAdapter1 extends RecyclerView.Adapter<MyListAdapter1.ViewHolder1>{

        class ViewHolder1 extends RecyclerView.ViewHolder{
            private TextView tvfriendName1,tvfriendID;
            private ImageView tvfriendPic1,tvfriendSymbol1,tvfriendSymbol_add1;
            private View mView;

            public ViewHolder1(@NonNull View itemView) {//設置recyclerview的內容
                super(itemView);
                tvfriendPic1 = itemView.findViewById(R.id.friendPic);
                tvfriendName1 = itemView.findViewById(R.id.friendName);
                tvfriendSymbol1 = itemView.findViewById(R.id.friendSymbol);//X
                tvfriendSymbol_add1 = itemView.findViewById(R.id.friendSymbol_add);
                tvfriendID = itemView.findViewById(R.id.friendID);
                mView  = itemView;
            }
        }

        @NonNull
        @Override//選擇用哪個layout放recyclerview
        public ViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_wait_verify_lv,parent,false);
            return new ViewHolder1(view);
        }

        @Override//生成recyclerview列表
        public void onBindViewHolder(@NonNull ViewHolder1 holder, int position) {
//            holder.tvfriendPic1.setImageResource(Integer.parseInt(arrayList1.get(position).get("friendPic")));
            Picasso.get().load(arrayList1.get(position).get("friendPic")).into(holder.tvfriendPic1);//頭像
            holder.tvfriendName1.setText(arrayList1.get(position).get("friendName"));
            holder.tvfriendSymbol1.setImageResource(Integer.parseInt(arrayList1.get(position).get("friendSymbol")));
            holder.tvfriendSymbol_add1.setImageResource(Integer.parseInt(arrayList1.get(position).get("friendSymbol_add")));
            holder.tvfriendID.setText(arrayList1.get(position).get("friendID"));//會員UID

            holder.tvfriendSymbol1.setOnClickListener((v)->{//按X開啟拒絕加入的對話盒
                profile_dialog = new Dialog(Biker_profile_society.this);
                profile_dialog.setContentView(R.layout.alert_dialog);
                profile_dialog.setCancelable(false);//一定要給我選
                Button btnOK = (Button) profile_dialog.findViewById(R.id.train_btnOK);
                Button btnCancel = (Button) profile_dialog.findViewById(R.id.train_btnCancel);

                TextView warning=(TextView)profile_dialog.findViewById(R.id.tarin_waring);
                warning.setText(getString(R.string.deny_add)+arrayList1.get(position).get("friendName")+getString(R.string.profile_t012)+"?");//更改dialog的字

                btnCancel.setOnClickListener(new View.OnClickListener() {//取消拒絕加入
                    @Override
                    public void onClick(View v) {
                        profile_dialog.cancel();//關閉對話盒
                    }
                });
                btnOK.setOnClickListener(new View.OnClickListener() {//審核不給過
                    @Override
                    public void onClick(View v) {
                        String member_name=arrayList1.get(position).get("friendID");//取得待審核之人的UID
                        mysql_delete_C200(member_name);
                        arrayList1.remove(position);
                        notifyDataSetChanged();//整體刷新

                        profile_dialog.cancel();//關閉對話盒
                    }
                });
                profile_dialog.show();
            });
            //=====================================================
            if(number!=max_member){
                holder.tvfriendSymbol_add1.setOnClickListener((v)-> {//按+同意加入
                    String member_name=arrayList1.get(position).get("friendID");//取得待審核之人的UID
                    String name=arrayList1.get(position).get("friendName");//取得待審核之人的姓名
                    String head=arrayList1.get(position).get("friendPic");//取得待審核之人的頭像
                    mysql_update_C200(member_name);
//                dbmysql_C200();// 讀取MySQL 資料-社團人員明細
//                verify_list();//待審核，刷新列表
                    //加到社員列表===============================
                    hashMap = new HashMap<>();
                    //第一個參數對應layout，第二個參數告訴電腦去哪找資料
//                int picId=2131230852;//後面沒頭像的用這個
//                int picId = getResources().getIdentifier("img050", "drawable", getPackageName());
                    hashMap.put("friendPic",head);//頭像
                    hashMap.put("friendName",name);//姓名
                    hashMap.put("friendSymbol",Integer.toString(picminus));//X
                    hashMap.put("friendID1",member_name);//會員UID

                    arrayList.add(hashMap);
                    myListAdapter.notifyDataSetChanged();//adapter整體刷新
                    //加到社員列表 done===========================
                    arrayList1.remove(position);//把待審核之人的姓名從列表中刪掉
                    number=Integer.parseInt(t33.getText().toString().trim())+1;//社員多一人
                    t33.setText(Integer.toString(number));//社員多一人
                    notifyDataSetChanged();//整體刷新
                });
            }else{
                holder.tvfriendSymbol_add1.setOnClickListener((v)-> {//社團已滿
                    Toast.makeText(getApplicationContext(), getString(R.string.fullmember), Toast.LENGTH_SHORT).show();
                });
            }


            //對話盒--------------------------------------------------------------------------------
            holder.tvfriendName1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),arrayList1.get(position).get("friendName"),Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public int getItemCount() {
            return arrayList1.size();//決定顯示數量，這裡的寫法是顯示全部
        }
    }
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------
}

