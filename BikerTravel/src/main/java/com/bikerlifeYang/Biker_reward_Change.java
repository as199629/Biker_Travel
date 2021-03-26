package com.bikerlifeYang;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Toast.makeText(getApplicationContext(),"吐司測試機B型",Toast.LENGTH_LONG).show();
public class Biker_reward_Change extends AppCompatActivity implements View.OnClickListener
{

    private Intent intent = new Intent();
    private static final int REQUEST_CODE = 1;//地址
    private ListView change_List;
    private String[] change_strArray;
    private Button change_check;
    private Menu menu;
    private ArrayList<Map<String, Object>> mList;

    private String sqlctl;
    private static final String DB_FILE = "bikerlife.db"; //資料庫名稱
    private static final String DB_TABLE = "F100";//資料表名稱
//    private static final int DBversion = 1;//資料庫版本
    private Biker_Life_RewardDbHelper30 BdbHper;//宣告DbHelper的類別
    private ArrayList<String> reward_ListStringArray_F402;
    private ArrayList<String> reward_ListStringArray_F403;
    private int rewardfinish_goal;//成就達成數
    private Boolean Is_usable=false; //選擇的稱號是否可用(給更換稱號的按鈕判斷)
    private String u_id;
    private String achievement_name;//選擇後準備更新的稱號名
    private String ser_msg;//server訊息
    private int DBversion;
    private Handler handler=new Handler();
    private ProgressDialog progDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_reward_change);
        SharedPreferences xxx=getSharedPreferences("USER_ID",0);
        u_id=xxx.getString("USER_ID","");
        initDB();
        setUpViewCompoent();
        //讀取資料並渲染至layout------------------
        show_ProgDlg();//啟動進度條
        handler.postDelayed(readSQL, 1000);
        //-----------------------------
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
    private void setUpViewCompoent()
    {
        change_check=(Button)findViewById(R.id.reward_change_check);
        change_check.setOnClickListener(this);
        change_List = (ListView) findViewById(R.id.listview);
        //change_strArray=getResources().getStringArray(R.array.title_name_List);
        //0131
        reward_ListStringArray_F402=new ArrayList<String>();
        reward_ListStringArray_F403=new ArrayList<String>();

        //----------------------
//        ListAdapter mAdapter = new ArrayAdapter<String>(this,//將稱號放進listview(可使用)
//                R.layout.join_listitem,
//                change_strArray);

        // 動態調整高度 抓取使用裝置尺寸
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newscrollheight = displayMetrics.heightPixels * 65/ 100; // 設定ScrollView使用尺寸的4/5
        change_List.getLayoutParams().height = newscrollheight;
        change_List.setLayoutParams(change_List.getLayoutParams()); // 重定ScrollView大小
        //-----
        showResult();
    }
    private void showResult()//取得從成就頁面中取得成就的達成數量
    {
        Bundle bundle=this.getIntent().getExtras();
        rewardfinish_goal=bundle.getInt("rewardfinish_goal");
//        Toast.makeText(getApplicationContext(),rewardfinish_goal+"",Toast.LENGTH_SHORT).show();
    }
    private void initDB() {
         DBversion =Integer.parseInt(getString(R.string.SQLite_version));
        if (BdbHper == null) {  //如果沒有連線資料庫 就開啟
            BdbHper = new Biker_Life_RewardDbHelper30(this, DB_FILE, null, DBversion);
        }
        dbmysql();//讀取mysql資料 並放入sqlite

    }
    private void show_ProgDlg () {
        progDlg = new ProgressDialog(this);
//        progDlg.setTitle("請稍後");
//        progDlg.setMessage("載入資料中");
        progDlg.setTitle(getString(R.string.reward_dialog_1));
        progDlg.setMessage(getString(R.string.reward_dialog_2));
        progDlg.setIcon(android.R.drawable.presence_away);
        progDlg.setCancelable(false);
        progDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDlg.setMax(100);
        progDlg.show();
    }
    private Runnable readSQL = new Runnable() {
        @Override
        public void run() {
            read_achievementname();//更新清單
            progDlg.cancel();
            handler.removeCallbacks(readSQL);// 結束緒
        }
    };
    // 讀取MySQL 資料
    private void dbmysql() {
//        sqlctl = "SELECT * FROM F400 ";//sql
//        ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
        try {
            DBConnector_reward30.connect_ip="https://bklifetw.com/T30/webtest/bikerlife/bikerlife/reward_achievement_r_api.php";
            String result = DBConnector_reward30.executeQuery();
//            Toast.makeText(getApplicationContext(),"a:"+result,Toast.LENGTH_LONG).show();
            /**************************************************************************
             * SQL 結果有多筆資料時使用JSONArray
             * 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            JSONArray jsonArray = new JSONArray(result);//抓到的所有資料
            // -------------------------------------------------------
            if (jsonArray.length() > 0) { // MySQL 連結成功有資料

                int rowsAffected = BdbHper.clearRec_F("F400");                 // 匯入前,刪除所有SQLite資料

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
                    long rowID = BdbHper.insertRec_F400(newRow);//將mysql中的資料 寫入sqlite
                }
                // ---------------------------
            } else {
//                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), getString(R.string.reward_mysql_error1), Toast.LENGTH_LONG).show();
            }
            // --------------------------------------------------------
        } catch (Exception e) {
        }
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
                int rowsAffected = BdbHper.clearRec_A100();                 // 匯入前,刪除所有SQLite資料;
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
                    long rowID = BdbHper.insertRec_A100(newRow);
//                        Toast.makeText(getApplicationContext(), "共匯入 " + Integer.toString(jsonArray.length()) + " 筆資料", Toast.LENGTH_SHORT).show();

                }
                // ---------------------------
            } else {
//                    Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), getString(R.string.reward_mysql_error1), Toast.LENGTH_LONG).show();
            }
//            recSet = dbHper.getRecSet();  //重新載入SQLite
//            u_setspinner();
            // --------------------------------------------------------
        } catch (Exception e) {
//            Log.d(TAG, e.toString());
        }

    }
    private void chk_httpstate()
    {
        ////-------------------------------
        //存取類別成員 DBConnector01.httpstate 判定是否回應 200(連線要求成功)
        if (DBConnector29G.httpstate == 200) {
//            ser_msg = "伺服器匯入資料(code:" + DBConnector29G.httpstate + ") ";
            ser_msg = getString(R.string.reward_okHttp_error0) + DBConnector29G.httpstate + ") ";
//            servermsgcolor = ContextCompat.getColor(this, R.color.Navy);
//                Toast.makeText(getBaseContext(), "由伺服器匯入資料 ",
//                        Toast.LENGTH_SHORT).show();


        } else {
            int checkcode = DBConnector29G.httpstate / 100;
            switch (checkcode) {
                case 1:
//                    ser_msg = "資訊回應(code:" + DBConnector29G.httpstate + ") ";
                    ser_msg = getString(R.string.reward_okHttp_error1) + DBConnector29G.httpstate + ") ";
                    break;
                case 2:
//                    ser_msg = "已經完成由伺服器會入資料(code:" + DBConnector29G.httpstate + ") ";
                    ser_msg = getString(R.string.reward_okHttp_error2) + DBConnector29G.httpstate + ") ";
                    break;
                case 3:
//                    ser_msg = "伺服器重定向訊息，請稍後在試(code:" + DBConnector29G.httpstate + ") ";
                    ser_msg = getString(R.string.reward_okHttp_error3) + DBConnector29G.httpstate + ") ";
//                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
                case 4:
//                    ser_msg = "用戶端錯誤回應，請稍後在試(code:" + DBConnector29G.httpstate + ") ";
                    ser_msg = getString(R.string.reward_okHttp_error4) + DBConnector29G.httpstate + ") ";
//                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
                case 5:
//                    ser_msg = "伺服器error responses，請稍後在試(code:" + DBConnector29G.httpstate + ") ";
                    ser_msg = getString(R.string.reward_okHttp_error5) + DBConnector29G.httpstate + ") ";
//                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
            }
//                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        }
        if (DBConnector29G.httpstate == 0) {
//            ser_msg = "遠端資料庫異常(code:" + DBConnector29G.httpstate + ") ";
            ser_msg = getString(R.string.reward_okHttp_error6) + DBConnector29G.httpstate + ") ";
        }
//        b_servermsg.setText(ser_msg);
//        b_servermsg.setTextColor(servermsgcolor);

        //-------------------------------------------------------------------
    }
  public void  read_achievementname(){//讀取稱號名稱及條件
      mList= new ArrayList<Map<String,Object>>();
      reward_ListStringArray_F402 = BdbHper.FindF402();
      reward_ListStringArray_F403= BdbHper.FindF403();
      int a=0;
      if(reward_ListStringArray_F402!=null)//如果mysql有稱號名稱的相關資料才新增至ｌｉｓｔｖｉｅｗ　不然會閃退
      {
          for(int i=0;i<reward_ListStringArray_F402.size();i++){
              Map<String,Object> item=new HashMap<String,Object>();

              if(rewardfinish_goal>=Integer.valueOf(reward_ListStringArray_F403.get(i))){
                  item.put("txtView",reward_ListStringArray_F402.get(i)+"(GET!!)");
                  item.put("isfocus",true);
              }
              else{
                  item.put("txtView",reward_ListStringArray_F402.get(i));
                  item.put("isfocus",false);
              }


              mList.add(item);
          }
      }

      SimpleAdapter mAdapter=new SimpleAdapter(this,mList,R.layout.join_list_item,new String[]{"txtView"},new int[]{R.id.txtView}){

          @Override
          public View getView(int position, View convertView, ViewGroup parent)
          {
              View view = super.getView(position, convertView, parent);


              Boolean isFocus = (Boolean) mList.get(position).get("isfocus");//是否達成
              if (isFocus)
              {
                  //view.setBackgroundColor(Color.BLUE);
                  view.setSelected(false);
                  view.setAlpha(1);
                  view.setBackgroundResource(0);//移除背景，包括：背景颜色，圖片等等

              } else
              {
                  // view.setBackgroundColor(Color.GRAY);
                  view.setAlpha(0.3f);
                  view.setBackgroundResource(R.drawable.button_gray);
              }
              return view;

          }

          @Override
             public boolean isEnabled(int position)//關閉點擊效果
             {
//
//                 if(position==2){
//                     return false ; // 表示第2行不可以點擊
//                 }
                 return super.isEnabled(position);
             }
      };

      change_List.setAdapter(mAdapter);
      change_List.setSelector(R.drawable.button_check);//https://pvencs.blogspot.com/2014/09/scrollview-listview.html  //點擊時變更背景色
      change_List.setOnItemClickListener(new AdapterView.OnItemClickListener() //判斷選擇哪一個ListView的Item
      {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id)
          {
              HashMap<String, String> map = (HashMap<String, String>) parent//取得listview指定的值  (SimpleAdapter版本
                      .getItemAtPosition(position);
            if( (Boolean) mList.get(position).get("isfocus")){
//                new AlertDialog.Builder(Biker_reward_Change.this)
//                        .setIcon(android.R.drawable.star_big_on)
//                        .setTitle(reward_ListStringArray_F402.get(position) + "(Get!)")
//                        .setMessage("你已達成"+rewardfinish_goal+"個成就\n"+"本稱號取得須達成"+reward_ListStringArray_F403.get(position)+"個成就")
//                        .show();
                Is_usable=true;
            }
            else{
                new AlertDialog.Builder(Biker_reward_Change.this)
                        .setIcon(android.R.drawable.star_big_on)
                        .setTitle(reward_ListStringArray_F402.get(position)  )
//                        .setMessage("你已達成"+rewardfinish_goal+"個成就\n"+"本稱號取得須達成"+reward_ListStringArray_F403.get(position)+"個成就")// //整合時解開此處註解
                        .setMessage(getString(R.string.rewardtext1)+rewardfinish_goal+getString(R.string.rewardtext2)+"\n"+
                                getString(R.string.rewardtext3)+reward_ListStringArray_F403.get(position)+getString(R.string.rewardtext2))
                        .show();
                Is_usable=false;
            }



//                Toast.makeText(view.getContext(), map.get("txtView"),
//                        Toast.LENGTH_SHORT).show();

//                String s=change_List.getItemAtPosition(position).toString();//取得listview指定的值  (ListAdapter版本
//                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
//              intent.putExtra("Title",  reward_ListStringArray_F402.get(position));//存入指定item的值
              achievement_name=reward_ListStringArray_F402.get(position);
          }
      });
//      //-程式觸發點擊事件
//      try
//      {
////          change_List.performItemClick(change_List.getChildAt(rewardfinish_goal-1),
////              rewardfinish_goal-1, change_List.getItemIdAtPosition((rewardfinish_goal-1)));
////              这种方法可以在listview 点击监听里面改变该item的颜色：
////
//          change_List.performItemClick(change_List.getAdapter().getView(rewardfinish_goal-1, null, null),
//                  rewardfinish_goal-1, change_List.getItemIdAtPosition(rewardfinish_goal-1));
//          change_List.getChildAt(rewardfinish_goal-1).setBackgroundResource(R.drawable.button_check);
//      }
//    catch (Exception e){
//
//    }
      //---
  }





    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.reward_change_check:
                // intent = getIntent();
                if(Is_usable){
                    DBConnector29G.Update_A106(u_id,achievement_name);//更新資料庫稱號
                    dbmysql_A100();//MYSQL=>SQLITE A100
                    setResult(REQUEST_CODE, intent); //REQUEST_CODE 需跟AActivity.class的一樣
                    finish();
                }
                else
                {
                    new AlertDialog.Builder(Biker_reward_Change.this)
                            .setIcon(android.R.drawable.star_big_on)
//                            .setTitle("稱號未啟用")
//                            .setMessage("請另選稱號")// //整合時解開此處註解
                            .setTitle(getString(R.string.rewardtext4))
                            .setMessage(getString(R.string.rewardtext5))// //整合時解開此處註解
                            .show();
                }
                break;
        }

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
        initDB();//如果沒有連線資料庫 就開啟
    }



    ////----------------------------------以下是menu用的方法----------------
    private void Signin_menu(){//登入時顯示的menu
        menu.setGroupVisible(R.id.g01,false);  //內側選單頁面(例如)只能返回
        menu.setGroupVisible(R.id.g02,false);
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
        Signin_menu(); //登入menu的選項
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)//請自行inten至各個畫面(未整合前先註解起來)　item01~03亮瑜改就好
    {
        switch (item.getItemId()){

            case R.id.action_settings: //原則上不用修改
         //       Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}