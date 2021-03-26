package com.bikerlifeYang;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Toast.makeText(getApplicationContext(),"吐司測試機A型",Toast.LENGTH_LONG).show();
public class Biker_reward extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_CODE = 1; //地址
    private Button reward_change;
    private Intent intent = new Intent();;
    private ListView reward_List;
    private TextView reward_title;
   // private String[] reward_ListStringArray;
    private ArrayList<String> reward_ListStringArray;
//    private String[] rewardDes;
    private TextView rewardanim;
    private int rewardfinish_goal=0;
    private Menu menu;
    private ArrayList<Map<String, Object>> mList;
//0114
    private static final String DB_FILE = "bikerlife.db"; //資料庫名稱
    private static final String DB_TABLE = "F100";//資料表名稱
//    private static final int DBversion = 1;//資料庫版本
    private Biker_Life_RewardDbHelper30 BdbHper;  //宣告DbHelper的類別
    private TextView reward_goalschedule;
    private TextView reward_user_name;
    private String u_id;
    private TextView myname;
    private String sqlctl;
    private String achievement_name;
    private ProgressDialog progDlg;
    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_reward);
        SharedPreferences xxx=getSharedPreferences("USER_ID",0);
        u_id=xxx.getString("USER_ID","");
        initDB();
        setupViewCompontent();
        //讀取資料並渲染至layout------------------
        show_ProgDlg();//啟動進度條
        handler.postDelayed(readSQL, 1000);
    //-----------------------------

        //下面畢卡索
//        CircleImgView s=(CircleImgView)findViewById(R.id.circleImgView3);
//        String httpname = "https://lh3.googleusercontent.com/a-/AOh14Ggs37WURY38V0Y4h39uIqBcpwd-XKtavp_kMi5fjw";
////            ImageView showimg = (ImageView) findViewById(R.id.imageView);
//        Picasso.get().load(httpname).into(s);


       // BdbHper.insertreward();測試用
        myname=(TextView)findViewById(R.id.myname);
        myname.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SQLiteDatabase db = BdbHper.getWritableDatabase(); //測試用
            }
        });


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
    private void setupViewCompontent() {
        reward_user_name=(TextView)findViewById(R.id.reward_name);
        reward_goalschedule=(TextView)findViewById(R.id.reward_schedule);
        reward_change = (Button) findViewById(R.id.reward_Change);  //交換按鈕
        reward_change.setOnClickListener(this);//按鈕監聽
        //------------
        reward_title=(TextView)findViewById(R.id.reward_designation);//稱號
        reward_List=(ListView)findViewById(R.id.listview); //Listview
        //reward_ListStringArray=getResources().getStringArray(R.array.reward); //Listview用的假字陣列
        reward_ListStringArray=new ArrayList<String>(); //Listview用的資料
//        rewardDes=getResources().getStringArray(R.array.rewardDes);//Listview點擊後用給dialog的假字陣列
//--------------------------------------------------------
        rewardanim = (TextView) findViewById(R.id.rewardanim);//獎杯顯示der圖
        rewardanim.setAnimation(null);
        rewardanim.clearAnimation();
        rewardanim.setAnimation(AnimationUtils.loadAnimation(this, R.anim.reward_anim_alpha_go));
        //以上是動畫

        // 動態調整高度 抓取使用裝置尺寸
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newscrollheight = displayMetrics.heightPixels * 50 / 100; // 設定ScrollView使用尺寸的4/5
        reward_List.getLayoutParams().height = newscrollheight;
        reward_List.setLayoutParams(reward_List.getLayoutParams()); // 重定ScrollView大小
        //-----

    }

    private void initDB() {
        int DBversion =Integer.parseInt(getString(R.string.SQLite_version));
        if (BdbHper == null) {  //如果沒有連線資料庫 就開啟
            BdbHper = new Biker_Life_RewardDbHelper30(this, DB_FILE, null, DBversion);
        }
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
            dbmysql_F300();//讀取mysql資料 並放入sqlite
            dbmysql_B100();
            reward_check();//更新清單
            reward_user_name.setText(BdbHper.FinduserName(u_id));//從資料庫讀取姓名
//            reward_user_name.setText(BdbHper.FinduserName(u_id));//從資料庫讀取姓名
            reward_title.setText(BdbHper.FinduserRewardName(u_id));
            //下面畢卡索
            CircleImgView s=(CircleImgView)findViewById(R.id.circleImgView3);
            String httpname = BdbHper.FinduserUrl(u_id);
//            ImageView showimg = (ImageView) findViewById(R.id.imageView);
            Picasso.get().load(httpname).into(s);
            //====歷史紀錄done=======================================
            progDlg.cancel();
            handler.removeCallbacks(readSQL);// 結束緒
        }
    };
    // 讀取MySQL 資料F300
    private void dbmysql_F300() {
//        sqlctl = "SELECT * FROM F300 ";//sql
//        ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
        try {
            DBConnector_reward30.connect_ip="https://bklifetw.com/T30/webtest/bikerlife/bikerlife/reward_r_api.php";
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

                int rowsAffected = BdbHper.clearRec_F("F300"); // 匯入前,刪除所有SQLite資料

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
                    long rowID = BdbHper.insertRec_F300(newRow);//將mysql中的資料 寫入sqlite
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
    // 讀取MySQL 資料-紀錄
    private void dbmysql_B100() {
        //接續dbmysql2()的撈法，得到與使用者相同社團的所有會員UID後，去B100撈出歷史紀錄
//        sqlctl = "SELECT * FROM B100 WHERE B101="+u_id;
        sqlctl ="SELECT * FROM B100";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            //--------------------------------------------------------
            int rowsAffected = BdbHper.clearRec_B100();                 // 匯入前,刪除[紀錄]的SQLite資料
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
                    long rowID = BdbHper.insertRec_B100(newRow);
                }
//                Toast.makeText(getApplicationContext(), "共有 " + Integer.toString(jsonArray.length()) + " 個社團", Toast.LENGTH_SHORT).show();

                // ---------------------------
            } else {
//                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), getString(R.string.reward_mysql_error1), Toast.LENGTH_LONG).show();
            }
//            recSet = dbHper.getRecSet();  //重新載入SQLite
            // --------------------------------------------------------
        } catch (Exception e) {}
    }
     private void reward_check()
     {//檢查成就是否達成（並顯示）
         //以下是listview顯示的code
         // ListAdapter List = new ArrayAdapter<String>(this, R.layout.join_listitem,reward_ListStringArray);
         mList = new ArrayList<Map<String, Object>>();
         reward_ListStringArray = BdbHper.FindF302();
         if (reward_ListStringArray != null) //如果有成就的相關資料才新增至ｌｉｓｔｖｉｅｗ　不然會閃退
         {
             for (int i = 0; i < reward_ListStringArray.size(); i++)
             {
//                 boolean isfocus = BdbHper.Findreward(i + 1, 1); //成就名稱的ID 比陣列index多一 &&userid  整合時註解這裡
             boolean isfocus=BdbHper.Findreward(i+1,Integer.parseInt(u_id)); //成就名稱的ID 比陣列index多一 &&userid  整合時開啟這裡
                 if (isfocus)
                 {
                     rewardfinish_goal++;
                 }
                 Map<String, Object> item = new HashMap<String, Object>();
                 item.put("txtView", reward_ListStringArray.get(i));
                 item.put("isfocus", isfocus);
                 mList.add(item);
             }
             reward_goalschedule.setText(rewardfinish_goal + "/" + reward_ListStringArray.size());//顯示成就達成數量

             SimpleAdapter mAdapter = new SimpleAdapter(this, mList, R.layout.join_list_item, new String[]{"txtView", "isfocus"}, new int[]{R.id.txtView})
             {
                 @Override   //成就變顏色
                 public View getView(int position, View convertView, ViewGroup parent)
                 {

                     View view = super.getView(position, convertView, parent);
                     //Toast.makeText(getApplicationContext(),position+"",Toast.LENGTH_SHORT).show();


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

             };


             reward_List.setAdapter(mAdapter);
             reward_List.setSelector(R.drawable.button_check);//點擊變色 //須配合下方的方法(暫定)
             reward_List.setOnItemClickListener(new AdapterView.OnItemClickListener()
             {
                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                 {
//                     if (BdbHper.Findreward(position + 1, 1))
//                     {  //整合時註解這裡
                 if(BdbHper.Findreward(position+1,Integer.parseInt(u_id))){  //整合時解開此處註解
                         new AlertDialog.Builder(Biker_reward.this)
                                 .setIcon(android.R.drawable.star_big_on)
//                                 .setTitle(reward_ListStringArray.get(position) + "(成就已達成)")
                                 .setTitle(reward_ListStringArray.get(position) + getString(R.string.reward_get1))
//                                 .setMessage(BdbHper.Findreward2(position, 1))//整合時註解
                             .setMessage(BdbHper.Findreward2(position,Integer.parseInt(u_id)))// //整合時解開此處註解
                                 .show();
                     } else
                     {
                         new AlertDialog.Builder(Biker_reward.this)
                                 .setIcon(android.R.drawable.star_big_on)
                                 .setTitle(reward_ListStringArray.get(position))
//                                 .setMessage(BdbHper.Findreward2(position, 1))//整合時註解
                             .setMessage(BdbHper.Findreward2(position,Integer.parseInt(u_id)))//整合時解開此處註解
                                 .show();
                     }
                 }
             });
         }
     }




    @Override//稱號返回
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case REQUEST_CODE:
                initDB();
                reward_title.setText(BdbHper.FinduserRewardName(u_id));
//                String result = data.getStringExtra("Title");
//                try {
//                    //Intent intent = this.getIntent();
//                    reward_title.setText(result);
//                } catch (Exception e) {
//                }
                break;
        }
    }



    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.reward_Change:
                intent.setClass(Biker_reward.this, Biker_reward_Change.class);
                Bundle bundle = new Bundle();
                bundle.putInt("rewardfinish_goal", rewardfinish_goal);
                intent.putExtras(bundle);
                startActivityForResult(intent,REQUEST_CODE);
                //startActivity(intent);
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
        initDB();//如果沒有連線資料庫 就開啟,並且將str_JoinDb陣列的值更新為資料表的值
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
//        menu.add(0,1,0,"更換稱號").setIcon(android.R.drawable.ic_media_play);
        menu.add(0,1,0,getString(R.string.rewardchange_btn)).setIcon(android.R.drawable.ic_media_play);
        return true; //返回值為“true”,表示菜單可見，即顯示菜單
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()){

            case 1:
                reward_change.callOnClick();
                break;
            case R.id.action_settings: //原則上不用修改
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}