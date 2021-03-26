package com.bikerlifeYang;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;

// Toast.makeText(getApplicationContext(),"吐司測試機四號",Toast.LENGTH_LONG).show();
public class Biker_Join_NewTeam extends AppCompatActivity
{
    private static final String DB_FILE = "bikerlife.db"; //資料庫名稱
    private static final String DB_TABLE = "G100";//資料表名稱
   // private static final int DBversion = 1;//資料庫版本
    private Biker_Life_JoinDbHelper BdbHper;  //宣告DbHelper的類別

    private static final int REQUEST_CODE2 = 2; //地址
    private TextView new_des;
    private Button Check;
    private Intent intent;
    private EditText new_title,new_poeple,new_address;
    private TextView new_date,new_time;
    private Menu menu;
    private int Nowyear;
    private int Nowmonth;
    private int Nowday;
    private int Nowhour;
    private int Nowminute;
    private Calendar calendar;
    private long endtime;
    private long spentTime;
    private Spinner join_spinner;
    private ArrayList<String> array_clubid; //spinner中postion中對應的clubID 作為判斷
    private String spinner_index;
    private String u_id;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        SharedPreferences xxx=getSharedPreferences("USER_ID",0);
        u_id=xxx.getString("USER_ID","");
        setContentView(R.layout.biker_join_new_team);
        initDB();
        setupViewComponent();
        SpinnerText();
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
    private void setupViewComponent()
    {
        Check = (Button) findViewById(R.id.join_new_Check);
        Check.setOnClickListener(On);
        new_title = (EditText) findViewById(R.id.join_new_title);
        new_poeple = (EditText) findViewById(R.id.join_new_people);
        new_poeple.addTextChangedListener(new TextWatcher()
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
                        new_poeple.setText("50");
                        new_poeple.setSelection(s.length());
                    }else if (getInput<=0){
                        new_poeple.setText("1");
                        new_poeple.setSelection(s.length());
                    }

                    if (s.toString().length() > 1 && s.toString().startsWith("0")) {
                        s.replace(0,1,"");
                    }

                }catch (Exception e){
                    new_poeple.setText("");
                    new_poeple.setText("");
                    new_poeple.setText("");
                }
            }
        });
        new_address = (EditText) findViewById(R.id.join_new_address);
        new_des=(TextView)findViewById(R.id.join_new_des);
        new_date=(TextView)findViewById(R.id.join_new_date);
        new_time=(TextView)findViewById(R.id.join_new_time);
        //Date_new.setInputType(InputType.TYPE_NULL);//關閉Edit顯示鍵盤
        //20210206
        join_spinner=(Spinner)findViewById(R.id.join_new_club_spinner);
        join_spinner.setOnItemSelectedListener(mSpnNameOnItemSelLis);
        array_clubid=new ArrayList<String>();
//-------------------------------------
        new_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)//輸出選擇日期
            {   calendar = Calendar.getInstance(); //單例模式
                Nowyear= calendar.get(Calendar.YEAR);//今天日期
                Nowmonth= calendar.get(Calendar.MONTH);//今天日期
                Nowday= calendar.get(Calendar.DAY_OF_MONTH);//今天日期
                DatePickerDialog datePicDlg = new DatePickerDialog(Biker_Join_NewTeam.this,
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
                                    new_date.setText(dateTime);
                                    Nowyear= calendar.get(Calendar.YEAR);//設定的日期（後續給時間的欄位判斷）
                                    Nowmonth= calendar.get(Calendar.MONTH);//設定的日期（後續給時間的欄位判斷）
                                    Nowday= calendar.get(Calendar.DAY_OF_MONTH);//設定的日期（後續給時間的欄位判斷）
                                }
                                else{
                                    // Toast.makeText(getApplicationContext(),"吐司測試機一號",Toast.LENGTH_LONG).show();
                                    new AlertDialog.Builder(Biker_Join_NewTeam.this)
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
        });

        new_time.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)//輸出選擇時間
            {   calendar = Calendar.getInstance();
                Nowhour = calendar.get(Calendar.HOUR_OF_DAY);
                Nowminute = calendar.get(Calendar.MINUTE);
                if(new_date.getText().toString()==""){ //沒先選擇日期的話會跳提醒
                    new AlertDialog.Builder(Biker_Join_NewTeam.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getString(R.string.join_error3))
                            .setMessage(getString(R.string.join_error4))
                            .show();
                    return;
                }

                TimePickerDialog timePicDlg = new TimePickerDialog(Biker_Join_NewTeam.this,//輸出選擇時間
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
                                    new AlertDialog.Builder(Biker_Join_NewTeam.this)
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setTitle(getString(R.string.join_error5))
                                            .setMessage(getString(R.string.join_error2))
                                            .show();
                                }
                                else{
                                    String dateTime = String.valueOf(hourOfDay) + ":" + String.format("%02d", minute) ;//轉換
                                    new_time.setText(dateTime);
                                }
                            }
                        }, Nowhour, Nowminute, false);
                timePicDlg.setTitle(getString(R.string.join_hint3));
                timePicDlg.setIcon(android.R.drawable.ic_lock_idle_alarm);
                timePicDlg.setCancelable(false);
                timePicDlg.show();
            }
        });
    }

    private void initDB()
    {
        int DBversion =Integer.parseInt(getString(R.string.SQLite_version));
        if (BdbHper == null) {  //如果沒有連線資料庫 就開啟
            BdbHper = new Biker_Life_JoinDbHelper(this, DB_FILE, null, DBversion);
        }

    }

    private View.OnClickListener On=new View.OnClickListener(){

        private String i_title,i_date,i_time,i_poeple,i_des,i_address;

        @Override
        public void onClick(View v)
        {
            i_title=new_title.getText().toString().trim();//標題
            i_date=new_date.getText().toString().trim();//日期
            i_time=new_time.getText().toString().trim();//時間
            i_poeple=new_poeple.getText().toString().trim();//人數
            i_address=new_address.getText().toString().trim();//地點
            i_des=new_des.getText().toString().trim();//備註
            if(join_spinner.getCount()==0){//如果spinner沒有東西(第二道防線)
//                Toast.makeText(getApplicationContext(),"目前沒有社團，無法新增",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),getString(R.string.join_add_error2),Toast.LENGTH_SHORT).show();
                return;
            }
            if(i_title.equals("") || i_date.equals("")|| i_poeple.equals("") || i_time.equals("") || i_address.equals("") || i_des.equals("")){
                Toast.makeText(getApplicationContext(),getString(R.string.join_new_error),Toast.LENGTH_SHORT).show();
                return;
            }
//
            //-------------建立揪團
            try{ //(第三道防線)防止建立中被刪除社團(尚無功能)
                ArrayList<String> arr=new ArrayList<String>();
                arr.add(i_title);//名稱
                arr.add(i_date);//日期
                arr.add(i_time);//時間
                arr.add(i_poeple);//人數
                arr.add(i_address);//地點
                arr.add(i_des);//描述
                arr.add(spinner_index);//社團ID
                arr.add(u_id); //主辦人ID(整合時要改 A100的內容 =>78是記號(這裡不需考慮78num))

                String result = DBConnector.executeInsert_G100(arr);
                String nowid="0";
                try
                {//抓取剛建立的G100 社團ID(立即性的)
                    JSONObject jsonData = new JSONObject(result);
                    nowid=jsonData.getString("id");
                }
                catch (Exception e){
//                    Toast.makeText(getApplicationContext(),"錯誤訊息(json抓取失敗)",Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),getString(R.string.join_mysql_error2),Toast.LENGTH_SHORT).show();
                }

                ///------------------
                ///------------讓主辦者加入揪團
                ArrayList<String> arr2=new ArrayList<String>();
                arr2.add(u_id);//暫時寫死78
                arr2.add(nowid);//剛建立的社團ＩＤ
                arr2.add("1");//主辦人是一
                DBConnector.executeInsert_G200(arr2);
                finish();
            }
            catch (Exception e){
//                Toast.makeText(getApplicationContext(),"發生異常(社團已不存在)",Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),getString(R.string.join_mysql_error3),Toast.LENGTH_SHORT).show();
            }



//-------------------------------------------
            //下方是原本sqlite版本的做法
//            String msg=null;
//            long rowID=BdbHper.InsertNewTeam(i_title,i_date,i_time,i_poeple,i_address,i_des,spinner_index,"1"); //新增資料(到外部類別方法)
//            if(rowID!=-1){ //新增成功
//                msg=getString(R.string.join_new_success);
//                finish();
//            }
//            else{
//                new_title.setText("");
//                new_date.setText("");
//                new_time.setText("");
//                new_address.setText("");
//                new_des.setText("");
//                msg=getString(R.string.join_new_error2);
//            }
//            Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
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
            //----
        }
        adap_spinner.setDropDownViewResource(R.layout.join_spinner);
        join_spinner.setAdapter(adap_spinner);
    }

    private Spinner.OnItemSelectedListener mSpnNameOnItemSelLis = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView parent, View view, int position,
                                   long id) {
            int iSelect = join_spinner.getSelectedItemPosition(); //找到按何項
            spinner_index=array_clubid.get(iSelect);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };


//---------------------------

    @Override
    protected void onStart()
    {
        super.onStart();
//        SharedPreferences xxx=getSharedPreferences("USER_ID",0);
//        u_id=xxx.getString("USER_ID","");
//        reward_user_name.setText(BdbHper.FinduserName(u_id));//從資料庫讀取姓名
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
        initDB();
//        if (BdbHper == null) {  //如果沒有連線資料庫 就開啟
//            BdbHper = new Biker_Life_JoinDbHelper(this, DB_FILE, null, DBversion);
//        }
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
}