package com.bikerlifeYang;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

// Toast.makeText(getApplicationContext(),"吐司測試機號",Toa五st.LENGTH_LONG).show();
public class Biker_Join_now extends AppCompatActivity {

    private static final String DB_FILE = "bikerlife.db"; //資料庫名稱
    private static final String DB_TABLE = "G100";//資料表名稱
//    private static final int DBversion = 1;//資料庫版本
    private Biker_Life_JoinDbHelper BdbHper;  //宣告DbHelper的類別



    //private Intent intent=new Intent();
    private Button now_Delete_btn,now_Join_btn,now_Edit_btn;
    private static final int REQUEST_CODE = 1;
    private EditText now_Title,now_Des,now_People,now_Address;
    private TextView now_Date,now_Time,now_Name;
    private Menu menu;
    private boolean editMode=false;
    private Button now_Editmode_btn;
    private TextView join_now_editing;
    private String[] db_array;//此畫面的資料
    private Calendar calendar;
    private int Nowyear;
    private int Nowmonth;
    private int Nowday;
    private int Nowhour;
    private int Nowminute;
    private long endtime;
    private long spentTime;
    private TextView aa;
    private int people_count=0;
    private String sqlctl;
    private boolean addMode=false;
    private ArrayList<String> joindata;
    private Button now_member_btn;
    private String u_id;
    private ListView members_list;
    private Button join_now_cancel_member;
    private RelativeLayout R1,R2;
    private ArrayList<Map<String, Object>> mList;
    private RecyclerView mRecyclerView;
    MyListAdapter myListAdapter;
    ArrayList<HashMap<String, String>> RecyclerView_arrayList = new ArrayList<>();
    private Context mContext=this;
    private ImageView join_now_img;
    private ProgressDialog progDlg;
    private int server_error;
    private Handler handler=new Handler();
    private String ser_msg="";
    private RequestOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_join_now);
        SharedPreferences xxx=getSharedPreferences("USER_ID",0);
        u_id=xxx.getString("USER_ID","");
        initDB();
        setUpViewConponent();
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
    private void setUpViewConponent()
    {
        aa=(TextView)findViewById(R.id.myname5);//測試用
        mRecyclerView = findViewById(R.id.RecyclerView_members);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        now_Title=(EditText)findViewById(R.id.join_now_title); //標題
        now_Name=(TextView)findViewById(R.id.join_now_name); //主辦人
        now_People=(EditText)findViewById(R.id.join_now_people); //人數
        now_Date=(TextView)findViewById(R.id.join_now_date); //日期
        now_Time=(TextView)findViewById(R.id.join_now_time);
        now_Address=(EditText)findViewById(R.id.join_now_address); //地點
        now_Des=(EditText)findViewById(R.id.join_now_des); //敘述
        join_now_img=(ImageView)findViewById(R.id.join_now_img);
        join_now_editing=(TextView)findViewById(R.id.join_now_editing);//顯示編輯中的文字
//        測試用aa
        aa.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BdbHper.getWritableDatabase();
            }
        });
//--------------------------------------

        now_Date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) //設定日期
            {
                if(editMode==true){
                    calendar = Calendar.getInstance(); //單例模式
                    //下面三行暫時註解(暫時沒用)
//                    Nowyear= calendar.get(Calendar.YEAR);//今天日期
//                    Nowmonth= calendar.get(Calendar.MONTH);//今天日期
//                    Nowday= calendar.get(Calendar.DAY_OF_MONTH);//今天日期
                    //-------
                    DatePickerDialog datePicDlg = new DatePickerDialog(Biker_Join_now.this,
                            new DatePickerDialog.OnDateSetListener()
                            {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                                {
                                    calendar.set(year,month,dayOfMonth+1);
                                    //.after會不包含今天( 所以需+1)=>雖然月尾或年尾會超出日期格式但不影響(12/31變成12/32)
                                    if (calendar.after(Calendar.getInstance())) {//選擇的時間是否為現在時間之後(不含今天)
                                        calendar.set(year,month,dayOfMonth);//復原成正常日期(否則後續判斷會出錯)
                                        String dateTime = String.valueOf(year) + "-" + String.format("%02d",month + 1)+ "-" + String.format("%02d",dayOfMonth);//轉換
                                        now_Date.setText(dateTime);
                                        Nowyear= calendar.get(Calendar.YEAR);//設定的日期（後續給時間的欄位判斷）
                                        Nowmonth= calendar.get(Calendar.MONTH);//設定的日期（後續給時間的欄位判斷）
                                        Nowday= calendar.get(Calendar.DAY_OF_MONTH);//設定的日期（後續給時間的欄位判斷）
                                        now_Time.setText("");//選擇日期後 時間變成空字串，避免使用者透過換日期而產生不正確時間的Bug
                                    }
                                    else{
                                        // Toast.makeText(getApplicationContext(),"吐司測試機一號",Toast.LENGTH_LONG).show();
                                        new AlertDialog.Builder(Biker_Join_now.this)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setTitle(getString(R.string.join_error1))
                                                .setMessage(getString(R.string.join_error2))
                                                .show();
                                    }

                                }
                            }, Nowyear, Nowmonth, Nowday);
                    datePicDlg.setTitle(getString(R.string.join_hint2));
                    datePicDlg.setIcon(android.R.drawable.ic_lock_idle_alarm);
                    datePicDlg.setCancelable(false);
                    datePicDlg.show();
                }

            }
        });

        now_Time.setOnClickListener(new View.OnClickListener()//設定時間
        {
            @Override
            public void onClick(View v)
            {
                if(editMode==true){
                calendar = Calendar.getInstance();
                Nowhour = calendar.get(Calendar.HOUR_OF_DAY);
                Nowminute = calendar.get(Calendar.MINUTE);
                if(now_Date.getText().toString()==""){ //沒先選擇日期的話會跳提醒
                    new AlertDialog.Builder(Biker_Join_now.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getString(R.string.join_error3))
                            .setMessage(getString(R.string.join_error4))
                            .show();
                    return;
                }

                TimePickerDialog timePicDlg = new TimePickerDialog(Biker_Join_now.this,//輸出選擇時間
                        new TimePickerDialog.OnTimeSetListener()
                        {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                            {
                                calendar.set(Nowyear,Nowmonth,Nowday,hourOfDay,minute); //選擇的日期時間
                                endtime = calendar.getTimeInMillis();
                                spentTime = endtime - System.currentTimeMillis(); //抓取剩餘的毫秒數;
                                int a=0;
                                if(spentTime<0){
                                    new AlertDialog.Builder(Biker_Join_now.this)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle(getString(R.string.join_error5))
                                            .setMessage(getString(R.string.join_error2))
                                            .show();
                                }
                                else{
//                                    String dateTime = String.valueOf(hourOfDay) + ":" + String.format("%02d", minute) + "出發";//轉換
                                    String dateTime = String.valueOf(hourOfDay) + ":" + String.format("%02d", minute) ;//轉換
                                    now_Time.setText(dateTime);
                                }
                            }
                        }, Nowhour, Nowminute, false);
                timePicDlg.setTitle(getString(R.string.join_hint3));
                timePicDlg.setIcon(android.R.drawable.ic_lock_idle_alarm);
                timePicDlg.setCancelable(false);
                timePicDlg.show();
            }

            }
        });
        R1=(RelativeLayout)findViewById(R.id.now_R1);
        R2=(RelativeLayout)findViewById(R.id.now_R2);
        now_Join_btn=(Button)findViewById(R.id.join_now_add);
        now_Join_btn.setOnClickListener(On);
        now_Delete_btn=(Button)findViewById(R.id.join_now_Delete);
        now_Delete_btn.setOnClickListener(On);
        now_Edit_btn=(Button)findViewById(R.id.join_now_Edit);
        now_Edit_btn.setOnClickListener(On);
        now_Editmode_btn=(Button)findViewById(R.id.join_now_Editmode);
        now_Editmode_btn.setOnClickListener(On);
        now_member_btn=(Button)findViewById(R.id.join_now_member);//成員名單按鈕
        now_member_btn.setOnClickListener(On);
        join_now_cancel_member=(Button)findViewById(R.id.join_now_cancel_member);//成員名單按鈕
        join_now_cancel_member.setOnClickListener(On);

        //******成員名單----------------------
        // 動態調整高度 抓取使用裝置尺寸
        members_list=(ListView)findViewById(R.id.members_list);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newscrollheight = displayMetrics.heightPixels * 50 / 100; // 設定ScrollView使用尺寸的4/5
        members_list.getLayoutParams().height = newscrollheight;
        members_list.setLayoutParams(members_list.getLayoutParams()); // 重定ScrollView大小

        mRecyclerView.getLayoutParams().height = newscrollheight;
        mRecyclerView.setLayoutParams(mRecyclerView.getLayoutParams()); // 重定ScrollView大小
        //-----*********----------------------------
    }
    private void initDB() {
        int DBversion =Integer.parseInt(getString(R.string.SQLite_version));
        if (BdbHper == null) {  //如果沒有連線資料庫 就開啟
            BdbHper = new Biker_Life_JoinDbHelper(this, DB_FILE, null, DBversion);
            //Toast.makeText(getApplicationContext(),"yaa",Toast.LENGTH_SHORT).show();
        }

    }
    private void showResult()//取得從join頁面中的資料
    {
        Bundle bundle=this.getIntent().getExtras();
        db_array=bundle.getStringArray("now_information");//JOIN傳過來 G100的特定資料
        //-----------MYSQL資料=>SQLITE
        dbmysql_G200();
        dbmysql_Specify_G100();
        dbmysql_B100();//抓取記錄的資料
        //----------
        joindata= BdbHper.FindNow_Specify_Join(db_array[0]);//db_array[0]=joinid
        if(joindata.size()>0){//有資料才跳這裡
            now_Title.setText(joindata.get(1)); //標題
            ArrayList<String> member_ar=BdbHper.getRecSet_A100(Integer.parseInt(joindata.get(8)));
            String str_member[]=member_ar.get(0).split("#");
            now_Name.setText(str_member[2]); //主辦人
            people_count=BdbHper.RecG200_Count(db_array[0]);//目前人數
            now_People.setText(people_count+"/"+joindata.get(4)); //人數
            now_Date.setText(joindata.get(2)); //日期　ＸＸ－ＸＸ－ＸＸ
            now_Time.setText(joindata.get(3));
            now_Address.setText(joindata.get(5)); //地點
            now_Des.setText(joindata.get(6)); //敘述

            Glide.with(mContext)
                    .load(str_member[4])
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .override(100, 75)
//                    .transition(withCrossFade())
                    .error(
                            Glide.with(mContext)
                                    .load("https://bklifetw.com//img/nopic1.jpg"))
                    .into(join_now_img);
            // now_Title.setText(db_array[1]); //標題
            //  now_Name.setText("沒資料"); //主辦人
//        Toast.makeText(getApplicationContext(),db_array[0],Toast.LENGTH_SHORT).show();
//        people_count=BdbHper.RecG200_Count(db_array[0]);//目前人數
//        now_People.setText(people_count+"/"+db_array[4]); //人數
//        now_Date.setText(db_array[2]); //日期　ＸＸ－ＸＸ－ＸＸ
//        now_Time.setText(db_array[3]);
//        now_Address.setText(db_array[5]); //地點
//        now_Des.setText(db_array[6]); //敘述
            String[] datearray =now_Date.getText().toString().split("-");
            Nowyear= Integer.valueOf(datearray[0]);//(編輯需透過字串切割預設日期，作為選擇時間的基準，否則是0年0月0日)
            Nowmonth= Integer.valueOf(datearray[1])-1;//(編輯需透過字串切割預設日期，作為選擇時間的基準，否則是0年0月0日)
            Nowday=Integer.valueOf(datearray[2]);//(編輯需透過字串切割預設日期，作為選擇時間的基準，否則是0年0月0日)

            if(BdbHper.adding(db_array[0],u_id)){//78暫時寫死　　　//如果有參團資料的話
                now_Join_btn.setSelected(true);
//                now_Join_btn.setText("退出");
                now_Join_btn.setText(getString(R.string.join_quit));
                now_People.setTextColor(0xFFf12f1f);
            }
            else{
                now_Join_btn.setSelected(false);
//                now_Join_btn.setText("加入");
                now_Join_btn.setText(getString(R.string.join_add));
                now_People.setTextColor(0xff000000);
            }
        }

    }

    private void show_ProgDlg () {
        progDlg = new ProgressDialog(this);
//        progDlg.setTitle("請稍後");
//        progDlg.setMessage("載入資料中");
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
            showResult();//取得並顯示從Join中取得的資料
            progDlg.cancel();
            if(server_error>0){
                show_dialog();
            }
            handler.removeCallbacks(readSQL);// 結束緒
        }
    };
    private void show_dialog()
    {
        Dialog AlertDig = new Dialog(Biker_Join_now.this);

        AlertDig.setCancelable(false);//不能按其他地方
        AlertDig.setContentView(R.layout.alert_dialog);//選擇layout

        Button alertBtnOK = (Button) AlertDig.findViewById(R.id.train_btnOK);
        Button alertBtnCancel = (Button) AlertDig.findViewById(R.id.train_btnCancel);
        TextView Dig_tarin_waring = (TextView) AlertDig.findViewById(R.id.tarin_waring);
        TextView Dig_train_title = (TextView) AlertDig.findViewById(R.id.train_title);
//            Dig_train_title.setText("請稍後再試");
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
                AlertDig.cancel();
                finish();
            }
        });
        AlertDig.show();
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
    private View.OnClickListener On=new View.OnClickListener() {
        private Dialog delete_Dlg;

        @Override
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.join_now_add://點擊加入退出按鈕

                    if (now_Join_btn.isSelected())
                    {
                        server_error=0;
                        if(db_array[8].equals(u_id))  //db_array[8]==主辦人id
                        {
//                            Toast.makeText(getApplicationContext(),"主辦人不得退出社團",Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(),getString(R.string.join_quit2),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ArrayList<String> arr = new ArrayList<String>();
                        arr.add(u_id);
                        arr.add(db_array[0]);
                        DBConnector.executeDeletJoinTeam(arr);
                        now_Join_btn.setSelected(false);
//                        now_Join_btn.setText("加入");
                        now_Join_btn.setText(getString(R.string.join_add));
                        now_People.setTextColor(0xff000000);
                        //----------
                        dbmysql_G200();//更新SQLITE資料
                        if(server_error==0){ //連線成功
                            people_count=BdbHper.RecG200_Count(db_array[0]);//目前人數
//                        now_People.setText(people_count+"/"+db_array[4]); //人數
                            now_People.setText(people_count+"/"+joindata.get(4)); //人數
                        }
                        else{
//                            Toast.makeText(getApplicationContext(),"網路不穩定，請注意網路訊號",Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(),getString(R.string.join_network_error),Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        //---------

                    }
                    else
                    {
                        //第一次判定網路狀況
                        server_error=0;
                        //加入前先更新一次資料確定沒超過人數上限才insert進sqlite
                        dbmysql_G200();
                        dbmysql_Specify_G100();
                        if(server_error==0)//連線成功
                        {
                            people_count=BdbHper.RecG200_Count(db_array[0]);//目前人數
                            joindata= BdbHper.FindNow_Specify_Join(db_array[0]);
                            now_People.setText(people_count+"/"+joindata.get(4)); //人數
                            if(people_count>=Integer.valueOf(joindata.get(4))){
//                                Toast.makeText(getApplicationContext(),"人數已達上限，不可加入",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),getString(R.string.join_max_error),Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //-------********

                            //開始新增↓
                            ArrayList<String> arr = new ArrayList<String>();
                            arr.add(u_id);//暫時寫死78
                            arr.add(db_array[0]);//社團id
                            arr.add("0");//加入者是0(權限)
                            DBConnector.executeInsert_G200(arr);
                            //如果新增成功↓
                            //第二次判定網路狀況
                            server_error=0;
                            dbmysql_G200();//更新SQLITE資料
                        }
                        else{
//                            Toast.makeText(getApplicationContext(),"網路不穩定，請注意網路訊號",Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(),getString(R.string.join_network_error),Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        if(server_error==0){
                            people_count=BdbHper.RecG200_Count(db_array[0]);//目前人數
//                        now_People.setText(people_count+"/"+db_array[4]); //人數
                            now_People.setText(people_count+"/"+ joindata.get(4)); //人數
                            //-------
                            now_Join_btn.setSelected(true);
//                            now_Join_btn.setText("退出");
                            now_Join_btn.setText(getString(R.string.join_quit));
                            now_People.setTextColor(0xFFf12f1f);
                        }
                        else {
//                            Toast.makeText(getApplicationContext(),"網路不穩定，請注意網路訊號",Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(),getString(R.string.join_network_error),Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
//                    if (now_Join_btn.isSelected()) {
//                        now_Join_btn.setSelected(false);
//                        now_Join_btn.setText("加入");
//                        now_People.setText("0/"+db_array[4]);
//                        now_People.setTextColor(0xff000000);
//                    } else {
//                        now_Join_btn.setSelected(true);
//                        now_Join_btn.setText("退出");
//                        now_People.setText("1/"+db_array[4]);
//                        now_People.setTextColor(0xFFf12f1f);
//                    }
                    break;

                case R.id.join_now_Delete://點擊刪除按鈕
                    delete_Dlg = new Dialog(Biker_Join_now.this);
                    delete_Dlg.setCancelable(false);
                    delete_Dlg.setContentView(R.layout.biker_alert_dialog);
                    TextView d_title=(TextView)delete_Dlg.findViewById(R.id.alert_title);
                    TextView d_msg=(TextView)delete_Dlg.findViewById(R.id.alert_msg);
                    Button d_BtnOK = (Button) delete_Dlg.findViewById(R.id.alert_btnOK);
                    Button d_BtnCancel = (Button) delete_Dlg.findViewById(R.id.alert_btnCancel);

                    d_title.setText(getString(R.string.warning_title));
                    d_msg.setText(getString(R.string.deleteteam_warning_msg));
                    d_BtnOK.setText(getString(R.string.deleteteam_warning_okbtn));
                    d_BtnCancel.setText(getString(R.string.deleteteam_warning_cancelbtn));


                    d_BtnOK.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v)
                        {
                            server_error=0;
                            ArrayList<String> arr = new ArrayList<String>();
                            arr.add(db_array[0]);
                            DBConnector.executeDeletJoin(arr);//刪除MYSQL_G100特定資料
                            //----------

                            dbmysql_G200();//更新SQLITE資料　　　G100的sqlite會在finish後 由Biker_Join那邊的方法更新
                            //---------
                            if(server_error==0)//連線成功
                            {
//                                Toast.makeText(getApplicationContext(),"刪除揪團成功",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),getString(R.string.join_del_success),Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
//                                Toast.makeText(getApplicationContext(),"網路不穩定，請注意網路訊號",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),getString(R.string.join_network_error),Toast.LENGTH_SHORT).show();
                                finish();
                            }
//                            BdbHper.DelectTeam(db_array[0]);

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

                    break;
                case R.id.join_now_Edit://點擊編輯按鈕
                    server_error=0;
                    String i_title=now_Title.getText().toString().trim(); //標題
                    String i_date=now_Date.getText().toString().trim(); //日期
                    String i_time =now_Time.getText().toString().trim();//時間
                    String i_poeple=now_People.getText().toString().trim(); //人數
                    String i_address=now_Address.getText().toString().trim(); //地點
                    String i_des=now_Des.getText().toString().trim(); //敘述

                    if(i_title.equals("") || i_date.equals("") || i_time.equals("") || i_address.equals("") || i_des.equals("")||i_poeple.equals("")){
                        Toast.makeText(getApplicationContext(),getString(R.string.join_now_error),Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    //加入前先更新一次資料確定沒超過人數上限才insert進sqlite
                    dbmysql_G200();
//                    dbmysql_Specify_G100();
                    people_count=BdbHper.RecG200_Count(db_array[0]);//目前人數

                    if(Integer.valueOf(i_poeple)<people_count){
//                        Toast.makeText(getApplicationContext(),"人數上限小於參加人數，無法更新",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),getString(R.string.join_max_error2),Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    joindata= BdbHper.FindNow_Specify_Join(db_array[0]);
//                    if(people_count>=Integer.valueOf(joindata.get(4))){
//                        Toast.makeText(getApplicationContext(),"人數已達上限，不可加入",Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    BdbHper.updateTeam(db_array[0],i_title,i_date,i_time,i_poeple,i_address,i_des);
                    ArrayList<String> arr = new ArrayList<String>();
                    arr.add(db_array[0]);
                    arr.add(i_title);
                    arr.add(i_date);
                    arr.add(i_time);
                    arr.add(i_poeple);
                    arr.add(i_address);
                    arr.add(i_des);
                    DBConnector.executeUpdate_G100(arr);
                    //---更新新資料
                    dbmysql_Specify_G100();
                    if(server_error==0){//連線成功

                        joindata= BdbHper.FindNow_Specify_Join(db_array[0]);
                        //--------
//                        Toast.makeText(getApplicationContext(), "資料已經更新完成!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), getString(R.string.join_update_success), Toast.LENGTH_SHORT).show();

                        if(BdbHper.adding(db_array[0],u_id)){//78暫時寫死　　　更新中 人數的字體顏色
                            now_People.setTextColor(0xFFf12f1f);
                        }
                        else{
                            now_People.setTextColor(0xff000000);
                        }

                        editMode=false;
                        now_Title.setEnabled(false);
                        now_Des.setEnabled(false);
                        now_People.setEnabled(false);
                        now_Address.setEnabled(false);


                        now_Join_btn.setVisibility(View.VISIBLE);//參加鍵開啟
                        now_Editmode_btn.setVisibility(View.VISIBLE);
                        join_now_editing.setVisibility(View.INVISIBLE);
                        now_Edit_btn.setVisibility(View.INVISIBLE);
                        now_Delete_btn.setVisibility(View.INVISIBLE);
                        now_People.removeTextChangedListener(textWatcher);//關閉EDITTEXT 數字監聽
                        people_count=BdbHper.RecG200_Count(db_array[0]);//目前人數

//                    now_People.setText(people_count+"/"+db_array[4]); //人數
                        now_People.setText(people_count+"/"+ joindata.get(4)); //人數
//                    now_People.setText("0/"+i_poeple);
                    }
                    else{
//                        Toast.makeText(getApplicationContext(),"網路不穩定，請注意網路訊號",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),getString(R.string.join_network_error),Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.join_now_Editmode://開啟編輯模式
     //--------------------------------------------
        now_People.addTextChangedListener(textWatcher);
                //開啟EDITTEXT 數字監聽

//------------------------------------------
                    String bossid=BdbHper.getRecSet_C106(db_array[7]);//db_array[7]==社團ID
//                    Toast.makeText(getApplicationContext(),bossid,Toast.LENGTH_SHORT).show();
                    if(!db_array[8].equals(u_id)&&!bossid.equals(u_id))  //db_array[8]==主辦人id
                    {
//                        Toast.makeText(getApplicationContext(),"權限不足(非團長或主辦人)",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),getString(R.string.join_authority_error),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    editMode=true;
//                    now_Title.setFocusableInTouchMode(true);
//                    now_Des.setFocusableInTouchMode(true);
//                    now_People.setFocusableInTouchMode(true);
//                    now_Address.setFocusableInTouchMode(true);
                    now_Title.setEnabled(true);
                    now_Des.setEnabled(true);
                    now_People.setEnabled(true);  //    目前開起編輯人數上限              先註解起來(禁止編輯人數上限)
                    now_Address.setEnabled(true);

                    now_Join_btn.setVisibility(View.INVISIBLE);//參加鍵關閉
                    now_Editmode_btn.setVisibility(View.INVISIBLE);

//                    now_People.setText(db_array[4]);
                    now_People.setText(joindata.get(4));
                    join_now_editing.setVisibility(View.VISIBLE);
                    now_Edit_btn.setVisibility(View.VISIBLE);
                    now_Delete_btn.setVisibility(View.VISIBLE);
                    break;
                case R.id.join_now_member:

                    R1.setVisibility(View.INVISIBLE);
                    R2.setVisibility(View.VISIBLE);
                    RecyclerView_arrayList.clear();//避免重複
                    mList = new ArrayList<Map<String, Object>>();
                    ArrayList<String> arr_member_userid=new ArrayList<String>();
                    arr_member_userid=BdbHper.getRecSet_G202(db_array[0]);//db_array[0]=joinid
                    if(arr_member_userid.size()>0){
                        for(int i=0;i<arr_member_userid.size();i++){
                            String club_member_id[]=arr_member_userid.get(i).split("#"); //club_member_G200[0]是ID
                            String members_data_A100[]=BdbHper.getRecSet_A100(Integer.valueOf(club_member_id[0])).get(0).split("#");
                            String member_B100=BdbHper.getRecSet_B104_total(String.valueOf(club_member_id[0]));
//                            Map<String, Object> item = new HashMap<String, Object>();
//                            item.put("name", members[2]);
//                            mList.add(item);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("name",  members_data_A100[2]);
                            hashMap.put("reward",  members_data_A100[6]);
                            hashMap.put("distance", member_B100);
                            hashMap.put("img_url",  members_data_A100[4]);
                            RecyclerView_arrayList.add(hashMap);
                        }
//                        SimpleAdapter mAdapter = new SimpleAdapter(Biker_Join_now.this, mList, R.layout.members_list, new String[]{"name"},
//                                new int[]{R.id.member_name});
//                        members_list.setAdapter(mAdapter);

                        myListAdapter = new MyListAdapter();
                        mRecyclerView.setAdapter(myListAdapter);
                    }

                    else
                    {
                        mRecyclerView.setAdapter(null);
//                        Toast.makeText(getApplicationContext(),"沒有成員",Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),getString(R.string.join_n_member),Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.join_now_cancel_member:
                    R2.setVisibility(View.INVISIBLE);
                    R1.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    //---------------EditText 即時監聽
    private TextWatcher  textWatcher= new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            try {
                if(s.length()==0){
                    return;
                }
                int getInput = Integer.parseInt(String.valueOf(s));
                if (getInput>50){
                    now_People.setText("50");
                    now_People.setSelection(s.length());
                }else if (getInput<=0){
                    now_People.setText("1");
                    now_People.setSelection(s.length());
                }

                if (s.toString().length() > 1 && s.toString().startsWith("0")) {
                    s.replace(0,1,"");
                }

            }catch (Exception e){
                now_People.setText("");
                now_People.setText("");
                now_People.setText("");
            }
        }
    };
    //---
    public void dbmysql_G200() {// 讀取MySQL資料=>sqlite（G200）使用者ID所加入的\\揪團//
        sqlctl = "SELECT * FROM G200 WHERE G203 IN(SELECT G101 FROM G100 WHERE G108 IN (SELECT DISTINCT C201 FROM C200 WHERE C202="+u_id+"))";//sql 78測試用userID
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            int rowsAffected = BdbHper.clearRec_G200();// 匯入前,刪除G200 SQLite資料
            String result = DBConnector.executeQuery(nameValuePairs);
            chk_httpstate();
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
//                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), getString(R.string.join_mysql_error1), Toast.LENGTH_LONG).show();
            }
            // --------------------------------------------------------
        } catch (Exception e) {
        }
    }
    public void dbmysql_Specify_G100() {
        sqlctl = "SELECT * FROM G100 WHERE G101="+db_array[0];
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
//                    Toast.makeText(getApplicationContext(),rowID+"",Toast.LENGTH_SHORT).show();
                }
                // ---------------------------
            } else {
//                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), getString(R.string.join_mysql_error1), Toast.LENGTH_LONG).show();
            }
            // --------------------------------------------------------
        } catch (Exception e) {
        }
    }
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
                    long rowID = BdbHper.insertRec_B100(newRow);
                }
//                Toast.makeText(getApplicationContext(), "共有 " + Integer.toString(jsonArray.length()) + " 個社團", Toast.LENGTH_SHORT).show();

                // ---------------------------
            } else {
//                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), getString(R.string.join_mysql_error1), Toast.LENGTH_LONG).show();
            }
//            recSet = dbHper.getRecSet();  //重新載入SQLite
            // --------------------------------------------------------
        } catch (Exception e) {}
    }
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
        menu.setGroupVisible(R.id.g01,false);//此頁只能返回
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
             //   Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /////-----R View
    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>
    {


        public class ViewHolder extends RecyclerView.ViewHolder
        {
            private final TextView member_name;
            private final TextView member_reward;
            private final TextView member_distance;
            private final ImageView circleImg_member;

            public ViewHolder(@NonNull View itemView)
            {
                super(itemView);
                member_name = itemView.findViewById(R.id.member_name);
                member_reward = itemView.findViewById(R.id.member_reward);
                member_distance = itemView.findViewById(R.id.member_distance);
                circleImg_member = itemView.findViewById(R.id.circleImg_member);
            }
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.members_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position)
        {
            holder.member_name.setText(RecyclerView_arrayList.get(position).get("name"));
            if(RecyclerView_arrayList.get(position).get("reward")==null
                    ||RecyclerView_arrayList.get(position).get("reward").equals("")||RecyclerView_arrayList.get(position).get("reward").equals("null"))
            {
//                holder.member_reward.setText("未設定");
                holder.member_reward.setText(getString(R.string.join_setting));
            }
            else{
                holder.member_reward.setText(RecyclerView_arrayList.get(position).get("reward"));
            }

            holder.member_distance.setText(RecyclerView_arrayList.get(position).get("distance"));

            //變圓形
            options = new RequestOptions()
                    .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(50)))
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .priority(Priority.NORMAL);

//            Picasso.get().load(RecyclerView_arrayList.get(position).get("img_url")).into(holder.circleImg_member);//頭像
            Glide.with(mContext)
                    .load(RecyclerView_arrayList.get(position).get("img_url"))
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(options)
                    .override(100, 100)
//                    .transition(withCrossFade())
                    .error(
                            Glide.with(mContext)
                                    .load("https://bklifetw.com/img/nopic1.jpg"))
                    .into(holder.circleImg_member);
        }

        @Override
        public int getItemCount()
        {
            return RecyclerView_arrayList.size();
        }


    }
    //--------------------
}