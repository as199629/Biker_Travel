package com.bikerlifeYang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView list;
    private ArrayList<Map<String, Object>> mList;
    private ArrayList<Map<String, Object>> mList_already;
    private String sqlctl;
    private ArrayList<String> recSet;
    private static final String DB_TABLE = "C100";    // 資料庫物件，固定的欄位變數
    private static final String DB_FILE = "bikerlife.db";
    //private static final int DBversion = 2;
    private FriendDbHelper21 dbHper;
    private TextView t000;
    ArrayList<String> str;
//    private Button b000;
    private ListView listalready;
    private Spinner s001;
    private Button b001;
    private ArrayList<String> ClubIDArray;//紀錄Spinner 每個item代表的ClubID
    private ArrayList<String> Applying;//申請中(ID)作為Lisetview position的參考依據
    private int index=0;//作為選擇後 儲存ClubID的變數
    private boolean join_recommend_bool=true;
    private TextView biker_recommend_club;
    private TextView biker_Application_club;
    private String u_id;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);//用到mySQL必加
        super.onCreate(savedInstanceState);
        SharedPreferences xxx=getSharedPreferences("USER_ID",0);
        u_id=xxx.getString("USER_ID","");
        setContentView(R.layout.activity_main);
        initDB();
        setupViewComponent();
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
        list=(ListView)findViewById(R.id.list);
        listalready=(ListView)findViewById(R.id.list_alreay);
        mList = new ArrayList<Map<String, Object>>();
        mList_already= new ArrayList<Map<String, Object>>();
        t000=(TextView)findViewById(R.id.t000);
        t000.setOnClickListener(this);
//        b000=(Button)findViewById(R.id.b000);
//        b000.setOnClickListener(this);
        s001=(Spinner)findViewById(R.id.s001);
        s001.setOnItemSelectedListener(mSpnNameOnItemSelLis);
        b001=(Button)findViewById(R.id.b001);
        biker_recommend_club=(TextView)findViewById(R.id.biker_recommend_club);
        biker_Application_club=(TextView)findViewById(R.id.biker_Application_club);
        biker_recommend_club.setOnClickListener(this);
        biker_Application_club.setOnClickListener(this);
        viewchange();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private Dialog delete_Dlg;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(join_recommend_bool==true)
                {
                    s001.setSelection(position, true); //spinner 小窗跳到第幾筆
                }
                else{
                    delete_Dlg = new Dialog(MainActivity.this);
                    delete_Dlg.setCancelable(false);
                    delete_Dlg.setContentView(R.layout.biker_alert_dialog);
                    TextView d_title=(TextView)delete_Dlg.findViewById(R.id.alert_title);
                    TextView d_msg=(TextView)delete_Dlg.findViewById(R.id.alert_msg);
                    Button d_BtnOK = (Button) delete_Dlg.findViewById(R.id.alert_btnOK);
                    Button d_BtnCancel = (Button) delete_Dlg.findViewById(R.id.alert_btnCancel);

                    d_title.setText("刪除!!");
                    d_msg.setText("確定要刪除");
                    d_BtnOK.setText("是");
                    d_BtnCancel.setText("否");


                    d_BtnOK.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v)
                        {

                            ArrayList<String> data=new ArrayList<String>();
                            data.add( Applying.get(position));
                            data.add(u_id);//暫時寫死(userid
                            DBConnector21.executeDeletClubApplying(data);
                            dbmysql();
                            dbmysql2();
                            viewchange();
                            delete_Dlg.cancel();
                        }
                    });
                    d_BtnCancel.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            delete_Dlg.cancel();
                        }
                    });
                    delete_Dlg.show();
                }
            }
        });
        listalready.setOnItemClickListener(new AdapterView.OnItemClickListener() {//取消加入
            private Dialog delete_Dlg;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                delete_Dlg = new Dialog(MainActivity.this);
//                delete_Dlg.setCancelable(false);
//                delete_Dlg.setContentView(R.layout.biker_alert_dialog);
//                TextView d_title=(TextView)delete_Dlg.findViewById(R.id.alert_title);
//                TextView d_msg=(TextView)delete_Dlg.findViewById(R.id.alert_msg);
//                Button d_BtnOK = (Button) delete_Dlg.findViewById(R.id.alert_btnOK);
//                Button d_BtnCancel = (Button) delete_Dlg.findViewById(R.id.alert_btnCancel);
//
//                d_title.setText("刪除!!");
//                d_msg.setText("確定要刪除");
//                d_BtnOK.setText("是");
//                d_BtnCancel.setText("否");
//
//
//                d_BtnOK.setOnClickListener(new View.OnClickListener(){
//
//                    @Override
//                    public void onClick(View v)
//                    {
//
//                        ArrayList<String> data=new ArrayList<String>();
//                        data.add( Applying.get(position));
//                        data.add("78");//暫時寫死(userid
//                        DBConnector28.executeDeletClubApplying(data);
//                        dbmysql();
//                        dbmysql2();
//                        viewchange();
//                        delete_Dlg.cancel();
//                    }
//                });
//                d_BtnCancel.setOnClickListener(new View.OnClickListener()
//                {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        delete_Dlg.cancel();
//                    }
//                });
//                delete_Dlg.show();
            }
        });
        b001.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try {//延遲Thread 睡眠0.5秒
                    Thread.sleep(500);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }

//-----------------------------------------------//實際執行新增資料至mysql
                ArrayList<String> sql=new ArrayList<String>();
                sql.add("INSERT INTO C200 (C201,C202,C203)");
                String cid=ClubIDArray.get(index);
//                Toast.makeText(getApplicationContext(),cid,Toast.LENGTH_SHORT).show();
                String result = DBConnector21.executeInsert_C200(sql,cid,u_id); //2暫時寫死的
//                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                dbmysql();
                dbmysql2();
                viewchange();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        Drawable drawable=getResources().getDrawable(R.drawable.button_check);
        Drawable drawable2=getResources().getDrawable(R.drawable.button_blue1);
        switch (v.getId()){
            case R.id.t000:
                dbHper.getReadableDatabase();
                break;
            case  R.id.biker_recommend_club:
                join_recommend_bool=true;//切換不同layout判定
                biker_recommend_club.setBackground(drawable2);
                biker_Application_club.setBackground(drawable);
                s001.setVisibility(View.VISIBLE);
                b001.setVisibility(View.VISIBLE);
                viewchange();
                break;
            case  R.id.biker_Application_club:
                join_recommend_bool=false;//切換不同layout判定
                biker_recommend_club.setBackground(drawable);
                biker_Application_club.setBackground(drawable2);
                s001.setVisibility(View.INVISIBLE);
                b001.setVisibility(View.INVISIBLE);
                viewchange();
                break;
//            case R.id.b000:
//                mysql_insert();
//                break;
        }
    }


    private void initDB () {
        int DBversion =Integer.parseInt(getString(R.string.SQLite_version));
        if (dbHper == null) {      //dbHper=SQLite
            dbHper = new FriendDbHelper21(this, DB_FILE, null, DBversion);
        }
        dbmysql();//帶入社團MySQL資料
        dbmysql2();
        // recSet = dbHper.getRecSet();//重新載入SQLite
    }
    private void dbmysql() {
        sqlctl = "SELECT * FROM C100 ORDER BY id ASC";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            //--------------------------------------------------------
            int rowsAffected = dbHper.clearRec();                 // 匯入前,刪除[社團]的SQLite資料
            //--------------------------------------------------------
            String result = DBConnector21.executeQuery(nameValuePairs);

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

                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
            //recSet = dbHper.getRecSet();  //重新載入SQLite

            // --------------------------------------------------------
        } catch (Exception e) {}
    }
    private void dbmysql2() {
        sqlctl = "SELECT * FROM C200  WHERE C202="+u_id;
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            //--------------------------------------------------------
            int rowsAffected = dbHper.clearRec_C200();                 // 匯入前,刪除[社團]的SQLite資料
            //--------------------------------------------------------
            String result = DBConnector21.executeQuery(nameValuePairs);

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
                    long rowID = dbHper.insertRec_C200(newRow);
                }
//                Toast.makeText(getApplicationContext(), "共有 " + Integer.toString(jsonArray.length()) + " 個社團", Toast.LENGTH_SHORT).show();

                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
            // recSet = dbHper.getRecSet();  //重新載入SQLite

            // --------------------------------------------------------
        } catch (Exception e) {}
    }
//    private void  mysql_insert(){
//        try {//延遲Thread 睡眠0.5秒
//            Thread.sleep(500);
//        }
//        catch (InterruptedException e){
//            e.printStackTrace();
//        }
//
////-----------------------------------------------//實際執行新增資料至mysql
//        ArrayList<String> sql=new ArrayList<String>();
//        sql.add("INSERT INTO C200 (C201,C202,C203)");
//        String result = DBConnector28.executeInsert_C200(sql,"10","78"); //暫時寫死的
//    }
    private void viewchange()//變更listview && spinner  顯示的內容
    {
        mList.clear();//重置資料
        mList_already.clear();//重置資料
        ArrayList<String> ArrayList  = dbHper.getRecSet(1,Integer.parseInt(u_id));
        ArrayList<String> ArrayList_already  = dbHper.getRecSet(3,Integer.parseInt(u_id));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.join_spinner);
        ClubIDArray=new ArrayList<String>();//重置
        Applying=new ArrayList<String>();//重置
        if(join_recommend_bool==true){//按下推薦社團
            for (int i = 0; i < ArrayList.size(); i++)//未加入
            {
                String[] fld = ArrayList.get(i).split("#");//一條字串用#分割(M1405browse)
                //------Listview
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("txtView", fld[1]);
                item.put("txtView2",fld[2]+"/"+fld[3]);
                item.put("txtView3",fld[4]+fld[5]);
                item.put("txtView4", fld[7]);
                mList.add(item);
                //------------------
                //spinner
                adapter.add(fld[1]);
                ClubIDArray.add(fld[0]); //紀錄每個未加入ID的item
                //----
            }
            //listview
            SimpleAdapter mAdapter = new SimpleAdapter
                    (this, mList, R.layout.list_item,
                            new String[]{"txtView","txtView2","txtView3","txtView4"}, new int[]{R.id.t002,R.id.t001,R.id.t003,R.id.t004});
            list.setAdapter(mAdapter);
        }
        else{//按下申請中社團
            for (int i = 0; i < ArrayList_already.size(); i++)//已經加入(含申請中
            {
                String[] fld = ArrayList_already.get(i).split("#");//一條字串用#分割(M1405browse)
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("txtView", fld[1]+"(申請中)");
                item.put("txtView2",fld[2]+"/"+fld[3]);
                item.put("txtView3",fld[4]+fld[5]);
                item.put("txtView5", "(點擊取消加入社團申請)");
                mList_already.add(item);
                Applying.add(fld[0]); //紀錄每個申請中ID的item
            }
            SimpleAdapter mAdapter2 = new SimpleAdapter
                    (this, mList_already, R.layout.list_item,
                            new String[]{"txtView","txtView2","txtView3","txtView5"}, new int[]{R.id.t002,R.id.t001,R.id.t003,R.id.t3});
            list.setAdapter(mAdapter2);
        }
        adapter.setDropDownViewResource(R.layout.join_spinner);
        s001.setAdapter(adapter);



//        mList.clear();//重置資料
//        mList_already.clear();//重置資料
//        //listivew
//        ArrayList<String> ArrayList  = dbHper.getRecSet(1,78);
//        ArrayList<String> ArrayList_already  = dbHper.getRecSet(3,78);
        //spinner
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item);
//        ClubIDArray=new ArrayList<String>();//重置
//        Applying=new ArrayList<String>();//重置
//        for (int i = 0; i < ArrayList.size(); i++)//未加入
//        {
//            String[] fld = ArrayList.get(i).split("#");//一條字串用#分割(M1405browse)
//            //------Listview
//            Map<String, Object> item = new HashMap<String, Object>();
//            item.put("txtView", fld[0]);
//            item.put("txtView2", fld[1]);
//            item.put("txtView3", fld[2]);
//            mList.add(item);
//            //------------------
//            //spinner
//            adapter.add(fld[1]);
//            ClubIDArray.add(fld[0]); //紀錄每個未加入ID的item
//            //----
//        }
//        //listview
//        SimpleAdapter mAdapter = new SimpleAdapter
//                (this, mList, R.layout.list_item,
//                        new String[]{"txtView","txtView2","txtView3"}, new int[]{R.id.t000,R.id.t002,R.id.t003});
//        list.setAdapter(mAdapter);
//
//        for (int i = 0; i < ArrayList_already.size(); i++)//已經加入(含申請中
//        {
//            String[] fld = ArrayList_already.get(i).split("#");//一條字串用#分割(M1405browse)
//            Map<String, Object> item = new HashMap<String, Object>();
//            item.put("txtView", fld[0]+"(申請中)");
//            item.put("txtView2", fld[1]);
//            item.put("txtView3", fld[2]);
//            mList_already.add(item);
//            Applying.add(fld[0]); //紀錄每個申請中ID的item
//        }
//        SimpleAdapter mAdapter2 = new SimpleAdapter
//                (this, mList_already, R.layout.list_item,
//                        new String[]{"txtView","txtView2","txtView3"}, new int[]{R.id.t000,R.id.t002,R.id.t003});
//        listalready.setAdapter(mAdapter2);
//        //spinner
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        s001.setAdapter(adapter);
    }
    private Spinner.OnItemSelectedListener mSpnNameOnItemSelLis = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView parent, View view, int position,
                                   long id) {
            int iSelect = s001.getSelectedItemPosition(); //找到按何項
            index=position;

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };
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
        return true; //返回值為“true”,表示菜單可見，即顯示菜單
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()){


            case R.id.action_settings: //原則上不用修改
                //Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}