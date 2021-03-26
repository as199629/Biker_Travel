package com.bikerlifeYang;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.getExternalStorageDirectory;


public class Biker_time_Stopwatch extends AppCompatActivity implements View.OnClickListener {
    //SQLite
    private static final String DB_FILE = "bikerlife.db";
    private static final String DB_TABLE = "stopwatch";
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    private int DBversion;//版本號
    public static Biker_time_Stopwatch Biker_time_Stopwatch_class=null;


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private static final String TAG = "29trnc";
    // 更新位置頻率的條件
    int minTime= 5000; // 毫秒 202101/31更新0秒看看
    float minDistance = 0; // 公尺




    private Handler handler = new Handler();


    //傳進SQL的值
    int t_timing=0;//紀錄時間
    int t_ridingtiming=0;//騎乘時間
    Float t_distance=0.0f;//距離
    Float t_uphillSum=0.0f;//爬升距離
    Float t_downhillSum=0.0f;//下坡距離
    Float t_altitudeUp=0.0f;//最高海拔
    Float t_altitudeDown=0.0f;//最低海拔
    Float t_avgSpeed=0.0f;//平均速度
    Float t_fastestSpeed=0.0f;//最快速度
    String t_startTime="";//開始時間
    String t_endTime="";//結束時間
    String t_year="";//年
    String t_date="";//月以及日


    private int timeSum;//紀錄時間加總時間用
    private int timeRecordSum;//騎乘時間加總時間用
    private int hour;
    private int hourRecord;//騎乘時間小時
    private int minute;
    private int minuteRecord;//騎乘時間分鐘
    private int seconds;
    private int secondsRecord;//騎乘時間小時秒數
    private Calendar begin;//開始時間
    private TextView startLYT,startTimeLYT,endTimeLYT,fastestSpeedLYT,highestAltitudeLYT;
    private TextView timingLYT,ridingtimingLYT,avgSpeedLYT,goHomeLYT,goMapLYT,prompt_pauseLYT
            ,distanceSumLYT,altitudeUpLYT,altitudeDownLYT,uphillSumLYT,downhillSumLYT;

    private TextView output,XXXX;//範例測試



    private Location currentLocation;//當前位置
    private LocationManager manager;// 取得系統服務的LocationManager物件


    private String best;




    private Float maxSpeed;//最快速度
    private double maxAltitude;//最大海拔
    private double minAltitude;//最低海拔


    //19-7
    private int index = 0, count = 0;
    private final int MAX_RECORDS = 10;
    private double[] Lats = new double[MAX_RECORDS];
    private double[] Lngs = new double[MAX_RECORDS];
    private double[] altitudeArray =new double[2];

    private double totalDistance=0.0;

    boolean LocationInfoTouch=false;
    private boolean startTouch=false;

    private AlertDialog.Builder completeDialog;//完成後跳出確定按鈕
    private boolean isSave=false;
    private float upAltitude=0;
    private float downAltitude=0;
    private float distance=0;

    private DecimalFormat oneDPFormat =new DecimalFormat("0.0");//oneDecimal Places
    private Button btnstopLYT;
    private Button btnstartLYT;
    private TextView counterLYT;
    private Context mContext;

    private Bundle bundle=new Bundle();
    private Intent intent=new Intent();
    private String action;
    private Menu menu;
    private TextView completeLYT;
    private TextView gosettingLYT;
    private LinearLayout btnfrmLy;
    private FrameLayout gosettinLy;
    private FriendDbHelper29S dbHper;
    private DatePicker date;
    private String u_id;
    private TextView myname;
    private String ser_msg;
    private Dialog AlertDig;
    private Button alertBtnOK;
    private Button alertBtnCancel;
    private TextView Dig_tarin_waring;
    private TextView Dig_train_title;
    private String sqlctl;
    private LinearLayout btnupmysqlLY;
    private Button upmysqlLYT;
    private Button testupdatebtn;
    private Button testdelbtn;
    private ArrayList<String> recSetB100;
    private List<Map<String, Object>> mList;
    private boolean B100_mode=false;
    private String B100_id="";
    private String t_mode="0";
    private boolean completeLYT_Flag=false;
    private String seleteB100_id;
    private String b100_id_mode;
    private String end_time;
    private String aa;
    private File file;
    private List<Location> allMusicNum = new ArrayList<Location>();
    private Location loc;
    private int index_location=0;
    private String header;
    private String gpx_name="";
    private String segments="";
    private boolean creat_gpx_ok=false;


    //通知
    private static final int notificationManager_id = 134; //給訊息一個id
    private int gpx_id;
    private NotificationManager notificationManager;
    Context context=this;
    private Location gpx_location;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkRequiredPermission(this);
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_time_stopwatch);
        Biker_time_Stopwatch_class=this;
        u_checkgps();
        setupViewComponent();
//        String a=String.format("%.6f", 23.94131); //小数点后两位。
        Log.d(TAG,"onCreate");
        DBversion = Integer.parseInt(getString(R.string.SQLite_version));
        initDB();
        u_load_uid();
        mysql_del_save_keep();
        //製作GPX
        //如果同意就創建GPX
        creat_gpx_Dilog();





    }

    private void creat_gpx_Dilog() {
        LayoutInflater inflater = LayoutInflater.from(Biker_time_Stopwatch.this);
        final View v = inflater.inflate(R.layout.gpx_name, null);
        EditText gpx_e001 = (EditText) (v.findViewById(R.id.gpx_e001));
        gpx_id=load_gpx_id();
        gpx_e001.setHint(getString(R.string.stopwatch_FileName));
        gpx_e001.setText("bikerlife"+gpx_id);
        completeDialog=new AlertDialog.Builder(Biker_time_Stopwatch.this);
        completeDialog.setTitle(getString(R.string.stopwatch_Precautions));
        completeDialog.setView(v);
        completeDialog.setMessage(getString(R.string.stopwatch_Precautions_text01));

        completeDialog.setCancelable(false);

        completeDialog.setPositiveButton(getString(R.string.stopwatch_assent), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                save_gpx_id((gpx_id+1)+"");
                creat_gpx_ok=true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.stopwatch_userjoin_FileName)+gpx_e001.getText().toString(),Toast.LENGTH_SHORT).show();
                    creat_gpx(gpx_e001.getText().toString());
                    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                    {
                        File dirfile = new File(getExternalStorageDirectory().toString() + "/Download/");
                        if(!dirfile.exists()){//如果資料夾不存在

                            dirfile.mkdir();//建立資料夾
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),getString(R.string.stopwatch_SD_CantSave),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        completeDialog.setNegativeButton(getString(R.string.stopwatch_disagree), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                creat_gpx_ok=false;
            }
        });


        completeDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void creat_gpx(String name) {
        loc = new Location("");
        //this.getDataDir()路徑在DATA下
        String a=this.getDataDir()+"";
        String b=this.getCacheDir()+"";
        String c=this.getFilesDir()+"";
        String d=this.getDataDir()+"";
//"/storage/emulated/0/1",

//        file = new File(this.getFilesDir(), "0003.gpx");
//        file = new File(Environment.getExternalStorageDirectory().toString()+"/Download/", "123456.gpx");
        file = new File(Environment.getExternalStorageDirectory().toString()+"/Download/", name+".gpx");
        header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
//        gpx_name="test000";
        gpx_name=name;
        gpx_name = "<name>" + gpx_name + "</name><trkseg>\n";



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
        startLYT=(TextView)findViewById(R.id.start);
        startTimeLYT=(TextView)findViewById(R.id.startTime);
        endTimeLYT=(TextView)findViewById(R.id.endTime);
        fastestSpeedLYT=(TextView)findViewById(R.id.fastestSpeed);
        goHomeLYT=(TextView)findViewById(R.id.goHome);
        goMapLYT=(TextView)findViewById(R.id.goMap);
        prompt_pauseLYT=(TextView)findViewById(R.id.prompt_pause);
        altitudeUpLYT=(TextView)findViewById(R.id.altitudeUp);
        altitudeDownLYT=(TextView)findViewById(R.id.altitudeDown);
        timingLYT=(TextView)findViewById(R.id.timing);//紀錄時間
        ridingtimingLYT=(TextView)findViewById(R.id.ridingtiming);//騎乘時間
        highestAltitudeLYT=(TextView)findViewById(R.id.altitudeUp);//最高海拔
        distanceSumLYT=(TextView)findViewById(R.id.distanceSum);
        avgSpeedLYT=(TextView)findViewById(R.id.avgSpeed);//平均速度
        completeLYT=(TextView)findViewById(R.id.complete);
        gosettingLYT=(TextView)findViewById(R.id.gosetting);
        btnfrmLy=(LinearLayout)findViewById(R.id.btnfrmLy);
        gosettinLy=(FrameLayout)findViewById(R.id.gosettinLy);
        date=(DatePicker)findViewById(R.id.date);//抓取系統日期
        myname=(TextView)findViewById(R.id.myname_29);
        btnupmysqlLY=(LinearLayout)findViewById(R.id.btnupmysqlLY);
        upmysqlLYT=(Button)findViewById(R.id.upmysql);
        uphillSumLYT=(TextView)findViewById(R.id.uphillSum);
        downhillSumLYT=(TextView)findViewById(R.id.downhillSum);

        startLYT.setOnClickListener(this);
        goHomeLYT.setOnClickListener(this);
        goMapLYT.setOnClickListener(this);
        prompt_pauseLYT.setOnClickListener(this);
        completeLYT.setOnClickListener(this);
        gosettingLYT.setOnClickListener(this);
        myname.setOnClickListener(this);
        btnupmysqlLY.setOnClickListener(this);
        upmysqlLYT.setOnClickListener(this);



        //*****測試按鈕
        output = (TextView) findViewById(R.id.lblOutput);//範例測試
        XXXX=(TextView)findViewById(R.id.XXXX);
        testupdatebtn=(Button)findViewById(R.id.testupdatebtn);
        testdelbtn=(Button)findViewById(R.id.testdelbtn);
        testdelbtn.setOnClickListener(this);
        testupdatebtn.setOnClickListener(this);


    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.start: //開始按鈕
                if(chk_httpstate()==false)//如果沒網路就別動作
                {
                    break;
                }
                //秀出通知
                show_Notification();

                t_mode="0";
                startLYT.setVisibility(View.GONE);
                completeLYT.setVisibility(View.VISIBLE);

                startTouch=true;//start 啟動中
                LocationInfoTouch=true;
                //更新位置頻率的條件

                try
                {
                    if(best!=null)  // 取得快取的最後位置,如果有的話
                    {
                        currentLocation=manager.getLastKnownLocation(best);
                        manager.requestLocationUpdates(best,minTime,minDistance,listener);
                        //每秒一寫一次的
                        manager.requestLocationUpdates(best,1000,0,listener1000);
                    }
                    else// 取得快取的最後位置,如果有的話
                    {
                        currentLocation=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDistance,listener);
                        //每秒一寫一次的
                        manager.requestLocationUpdates(best,1000,0,listener1000);
                        // requestLocationUpdates()方法实时更新用户的位置信息,接收四个参数
                        // ①第一个参数是位置提供器的类型
                        // ②第二个参数是监听位置变化间隔的毫秒数
                        // ③第三个参数是监听位置变化间隔的距离，达到设定距离时，触发第四个参数的监听器的onLocationChanged()方法，
                        // 并把新的位置信息作为参数传入
                        // ④第四个参数是LocationListener()监听器接口，所以应该传入的是它的实现类的对象
//               manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10,
//                       listener);
                    }
                }
                catch(SecurityException  e)
                {
                    Log.e("M1907", "GPS權限失敗..." + e.getMessage());
                }
//                    updatePosition();//更新最後位置

                begin = Calendar.getInstance();//按下按鈕時的時間
                //動畫-------------------------------
//                        start.clearAnimation();//先清掉
//                        Animation anim= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.time_stopwatch_fade_in);//增加一個動畫
//                        anim.setInterpolator(new BounceInterpolator());
//                        start.setAnimation(anim);//啟動動畫
                //---------------------------------------------




                //設定顯示文字----------------------------

                startTimeLYT.setText(//開始時間 獲得 小時'、分鐘、秒
                        String.format("%02d",begin.get(Calendar.HOUR_OF_DAY))+":"+
                                String.format("%02d",begin.get(Calendar.MINUTE))+":"+
                                String.format("%02d",begin.get(Calendar.SECOND)));
//                        int a=(begin.get(Calendar.HOUR_OF_DAY)-12)*60*60;
//                        int b=begin.get(Calendar.MINUTE)*60;
//                        int c=begin.get(Calendar.SECOND);
//
//                        int x=a+b+c;
//                        int xx=1;
                //歸零-------------------------------------------
                endTimeLYT.setText(getString(R.string.stopwatch_timezero));//結束時間歸零
                timingLYT.setText(getString(R.string.stopwatch_timezero)); //經過時間 歸零
                ridingtimingLYT.setText(getString(R.string.stopwatch_timezero));
                fastestSpeedLYT.setText(getString(R.string.stopwatch_zero_hkm));
                timeSum=0;//碼表歸零
                timeRecordSum=0;//騎乘時間碼表歸零
                totalDistance=0;//距離歸零
                maxSpeed=0.0f;//最快速度
                maxAltitude=0.0;//最大海拔
                minAltitude=9999;//最低海拔
                upAltitude=0;//爬升距離
                downAltitude=0;//下坡距離
                distance=0;



                //---------------------------------------------
                //取得日期
                t_year=""+date.getYear();
                t_date= String.format("%02d", date.getMonth()+1)+"-"+String.format("%02d", date.getDayOfMonth());


                handler.postDelayed(updatedTime01,1000);

                handler.postDelayed(updateB100,0);//定時寫入B100
                B100_mode=false;
                break;
            case R.id.complete:



                //動畫-------------------------------
//                        start.clearAnimation();//先清掉
//                        Animation anim= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.time_stopwatch_fade_in);//增加一個動畫
//                        anim.setInterpolator(new BounceInterpolator());
//                        start.setAnimation(anim);//啟動動畫
                //--------------------------------------------



//                        if(timeRecordSum>=0.000001)
//                        {
//                            handler.removeCallbacks(updatedRecordTime01);
//                        }


                //完成後跳出確定或取消
                completeDialog=new AlertDialog.Builder(Biker_time_Stopwatch.this);
                completeDialog.setTitle(getString(R.string.stopwatch_CompleteDlg_title));

                completeDialog.setMessage(getString(R.string.stopwatch_CompleteDlg_Message));

                completeDialog.setCancelable(false);

                completeDialog.setPositiveButton(getString(R.string.stopwatch_assent),confirmButton);
                completeDialog.setNegativeButton(getString(R.string.stopwatch_cancel),cancelButton);


                completeDialog.show();
//                        startTouch=false;
//                        handler.removeCallbacks(updatedTime01);
                break;
            case R.id.goHome://回首頁按鈕
                intent.setClass(Biker_time_Stopwatch.this, Biker_home.class);
                startActivity(intent);
                break;


            case R.id.goMap://去導航按鈕
                    intent.setClass(Biker_time_Stopwatch.this,Biker_time_map_plan.class);
                    startActivity(intent);
                break;
            case R.id.gosetting://前往手機設定
                // 使用Intent物件啟動設定程式來更改GPS設定
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);

                break;
            case R.id.myname_29:
//                mysql_insert();
                dbHper.RecCount();
//                dbmysql();

//                dbHper.clearRec();
//                dbmysqlB100_id(u_id);


                //複製檔案用
//                try {
//                    fileCopy(this.getFilesDir()+"/0003.gpx",
//                            Environment.getExternalStorageDirectory().toString()+"/Download/0004.gpx");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


                break;
            case R.id.upmysql:
                if(completeLYT_Flag==false)
                {
                    startLYT.callOnClick();
                }
                else
                {
                    completeLYT.callOnClick();
                }


                break;
            case R.id.testupdatebtn:
//                mysql_update();
                sql_value();
                Toast.makeText(getApplicationContext(),"Q",Toast.LENGTH_SHORT).show();
                break;
            case R.id.testdelbtn:
                mysql_del_save_keep();
                Toast.makeText(getApplicationContext(),"Q",Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void show_Notification() {
        //2.设置点击事件每个通知都应该对点按操作做出响应，通常是在应用中打开对应于该通知的 Activity。
        // 为此，您必须指定通过 PendingIntent 对象定义的内容 Intent，并将其传递给 setContentIntent()
        Intent intent = new Intent(getApplicationContext(), Biker_time_Stopwatch.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, notificationManager_id,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT );//FLAG_UPDATE_CURRENT 建立好  就一直用他

        //3.构造Notification对象并显示通知
        String channelId = createNotificationChannel("my_channel_ID", "my_channel_NAME", NotificationManager.IMPORTANCE_HIGH);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_biker))/**设置通知右邊的大图标**/
                .setSmallIcon(R.drawable.home_biker)/**设置通知左边的小图标**/
                .setContentTitle(getString(R.string.stopwatch_Notice_Title))/**设置通知的标题**/
                .setContentText(getString(R.string.stopwatch_Notice_ContextText))/**设置通知的内容**/
                .setStyle(new NotificationCompat.InboxStyle()
//                        .addLine("請別離開紀錄或導航頁面")
                        .addLine(getString(R.string.stopwatch_addLine01)))
                .setContentIntent(pendingIntent)//按下通知後去哪裡
                .setPriority(NotificationCompat.PRIORITY_MAX)/**设置该通知优先级**/
                .setAutoCancel(false)//setAutoCancel会在用户点按通知后自动移除通知，
//                .addAction(android.R.drawable.btn_star, "按钮", pendingIntent)//按鈕按下後要幹馬

                .setOngoing(true);//這樣就不會被滑動而取消掉摟


        //如果不想用户把他清理掉呢？可以调用setOngoing(true)方法，
        // 这样除非你的app死掉或者在代码中取消，否则他都不会消失。

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(notificationManager_id, notification.build());
    }
    //1.创建渠道 这里封装了一个方法，如果api大于android8.0就创建渠道返回渠道id，否则返回空。这里我只传了三个必要的参数，还有其他的设置在通知渠道那一节单独介绍。
    private String createNotificationChannel(String channelID, String channelNAME, int level) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, channelNAME, level);
            notificationManager.createNotificationChannel(channel);
            return channelID;
        } else {
            return null;
        }
    }

    private List<String> permissionsList=new ArrayList<>();
    private static final String[] permissionarray = new String[]
            {
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,

            };

    private void checkRequiredPermission(Biker_time_Stopwatch biker_time_stopwatch)
    {
        for (String permission : permissionarray) {
            if (ContextCompat.checkSelfPermission(biker_time_stopwatch, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size()!=0) {
            ActivityCompat.requestPermissions(biker_time_stopwatch, permissionsList.toArray(new
                    String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        }

    }
    private void u_checkgps() {
        // 取得系統服務的LocationManager物件
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 檢查是否有啟用GPS
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 顯示對話方塊啟用GPS
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.stopwatch_checkgps_title))
                    .setCancelable(false)
                    .setIcon(R.drawable.time_stopwatch_gpsicon)
                    .setMessage(getString(R.string.stopwatch_checkgps_message))
                    .setPositiveButton(getString(R.string.stopwatch_checkgps_enable), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gosettingLYT.callOnClick();
                            // 使用Intent物件啟動設定程式來更改GPS設定
                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton(getString(R.string.stopwatch_checkgps_notenable), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            btnfrmlyOFF();


//                            Toast.makeText(getApplicationContext(),"qq",Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    private void btnfrmlyOFF() {
        btnfrmLy.setVisibility(View.GONE);
        gosettinLy.setVisibility(View.VISIBLE);
    }

    private void btnfrmlyOn() {
        btnfrmLy.setVisibility(View.VISIBLE);
        gosettinLy.setVisibility(View.GONE);
    }


    private void updatePosition() {





        TextView output,list;
        String str="最近個人行蹤的座標清單\n";
        output=(TextView)findViewById(R.id.lblOutput);
        list = (TextView) findViewById(R.id.route_Name);


        if (currentLocation == null) {
            output.setText("取得定位資訊中...");
        }
        else
        {
            //顯示目前經緯度座標資訊
            output.setText(getLocationInfo(currentLocation));
            //顯示個人行蹤的座標清單
            for(int i=0;i<MAX_RECORDS;i++)
            {
                if(Lats[i]!=0.0)
                {
                    str += Lats[i] + "/" + Lngs[i] +"\n";
                }
            }
            list.setText(str);

//
//
//
////            output.setText(Double.toString(currentLocation.getSpeed()));
//           output.setText(getLocationInfo(currentLocation));//自訂義函數
        }


    }
    // 取得定位資訊
    public String getLocationInfo(Location location) {//自定義函數


        isSave=true;
        double lat,lng;
        Long time;


        lat=location.getLatitude();
        lng=location.getLongitude();

//        if(creat_gpx_ok)
//        {
//            //製作gpx-------------------------------------------
//            time=location.getTime();
////        String aaaw=location.getTime()+"";
////        loc = new Location("");
//            loc.setLatitude(lat);
//            loc.setLongitude(lng);
//            loc.setTime(time);
//            allMusicNum.add(0,loc);
//            index_location+=1;
//
//            DateFormat df = null;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
////                df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
//            }
////        String segments ="";
//            String aaaa="";
////        for (int ai=0;ai<allMusicNum.size();ai++) {
//            Location location_arr=allMusicNum.get(0);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                segments += "<trkpt lat=\"" + location_arr.getLatitude() + "\" lon=\"" + location_arr.getLongitude() + "\"><time>" + df.format(new Date(location_arr.getTime())) + "</time></trkpt>\n";
//            }
////        }
//
//            String footer = "</trkseg></trk></gpx>";
//
//            try {
//                FileWriter writer = new FileWriter(file, false);
//                writer.append(header);
//                writer.append(gpx_name);
//                writer.append(segments);
//                writer.append(footer);
//                writer.flush();
//                writer.close();
//
//            } catch (IOException e) {
////            Log.e("generateGfx", "Error Writting Path",e);
//            }
//        }


        //-------------------------------------------
        if(count>=1)//檢查是否要儲存座標
        {
            // 建立目地GPS座標的Location物件
            Location dest=new Location(location);
            //前一個座標的陣列索引
            int preindex=index-1;
            // 檢查前一座標是否是陣列最後一個元素
            if(preindex<0)
                preindex=Lats.length-1;
            // 指定目的Location物件的座標
            dest.setLatitude(Lats[preindex]);
            dest.setLongitude(Lngs[preindex]);
//            Log.d("M1907", count + " index/preIndex: " + index + "/" + preindex);
//            Log.d("M1907", "dlat: " + Lats[preindex]);
//            Log.d("M1907", "dlng: " + Lngs[preindex]);

            //計算與目的座標的距離
            distance=location.distanceTo(dest);
//            Toast.makeText(this, "距離: " + distance + "公尺",
//                    Toast.LENGTH_SHORT).show();
//            Log.d("M1907", "distance: " + distance);


            //總距離除於秒數
            DecimalFormat avgSpeedFormat=new DecimalFormat("0.0");
            if(Double.isNaN((totalDistance/timeRecordSum)*3600/1000))
            {
                avgSpeedLYT.setText("0.0 h/km");

            }
            else
            {
                avgSpeedLYT.setText(avgSpeedFormat.format((totalDistance/timeRecordSum)*3600/1000)+" h/km");
            }




            // 檢查距離是否小於20公尺, 小於不用存
            if (distance < 5.0)
            {

                isSave = false;
                if(LocationInfoTouch==false)
                {
                    handler.removeCallbacks(updatedRecordTime01);

                }
                LocationInfoTouch=true;








                //浮出自動暫停中
                prompt_pauseLYT.setVisibility(View.VISIBLE);
                prompt_pauseLYT.setSelected(true);
                prompt_pauseLYT.setText(getString(R.string.stopwatch_Auto_pause));

            }
            if(LocationInfoTouch==true &&distance > 5.0)
            {

                //消失自動暫停中
                prompt_pauseLYT.setVisibility(View.INVISIBLE);
                prompt_pauseLYT.setSelected(false);
                prompt_pauseLYT.setText("");

//                startRecordTime();
                handler.postDelayed(updatedRecordTime01,1000);
                LocationInfoTouch=false;
            }


        }


        if (isSave) // 記錄座標
        {
            Lats[index] = lat;
            Lngs[index] = lng;
            count++;
            if (count >= MAX_RECORDS) count = MAX_RECORDS;
            index++; // 陣列索引加一
            // 如果索引大於最大索引, 重設為0
            if (index >= MAX_RECORDS) index = 0;


            totalDistance+=distance;//紀錄距離


            distanceSumLYT.setText(oneDPFormat.format(totalDistance/1000)+" km");//公尺換算成公里




        }
        //取得最快速度
        if(maxSpeed<currentLocation.getSpeed())
        {
            maxSpeed=currentLocation.getSpeed();
            DecimalFormat speedFormat=new DecimalFormat("0.0");
            fastestSpeedLYT.setText(speedFormat.format(maxSpeed*3600/1000)+" h/km");

        }

        //取得最高海拔
        if(maxAltitude<location.getAltitude())
        {

            maxAltitude=location.getAltitude();
        }
        //取得最低海拔
        else if (minAltitude>location.getAltitude())
        {
            minAltitude=location.getAltitude();
        }











        StringBuffer str=new StringBuffer();
//        str.append("定位提供者(Provider): "+location.getProvider());
//        str.append("\n緯度(Latitude): " + Double.toString(lat));
//        str.append("\n經度(Longitude): " + Double.toString(lng));
        str.append("\n高度(Altitude): "+Double.toString(location.getAltitude()));//取得高度 米為單位 一米= 一公尺= 100公分
//        str.append("\n速度(speed)"+Double.toString(location.getSpeed()));//取得速度      米/秒為單位
        str.append("\nMax海拔"+Double.toString(maxAltitude));//最高海拔
        str.append("\nMin海拔"+Double.toString(minAltitude));//最低海拔




        altitudeUpLYT.setText(oneDPFormat.format(maxAltitude)+" m");
        if(minAltitude==9999)
        {
            altitudeDownLYT.setText(getString(R.string.stopwatch_zerometers));
        }
        else
        {
            altitudeDownLYT.setText(oneDPFormat.format(minAltitude)+" m");
        }



        altitudeArray[0]=altitudeArray[1];
        altitudeArray[1]=location.getAltitude();
        if(altitudeArray[1]>altitudeArray[0])
        {

            upAltitude+=distance;
        }
        else
        {
            downAltitude+=distance;
        }

        uphillSumLYT.setText(oneDPFormat.format(upAltitude/1000)+" km");
        downhillSumLYT.setText(oneDPFormat.format(downAltitude/1000)+" km");

        return str.toString();
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 已經取得權限
//                Toast.makeText(this, "取得權限取得GPS資訊",
//                        Toast.LENGTH_SHORT).show();
            } else {
//                intent.setClass(Biker_time_Stopwatch.this,Biker_home.class);
//                startActivity(intent);
//                this.finish();

            }
        }

    }

    // 建立定位服務的傾聽者物件
    private LocationListener listener =new LocationListener() {//給 onResume那邊方法做監聽
        @Override
        public void onLocationChanged(Location location) {
            currentLocation=location;//當前位置

            if(startTouch==true)
            {
                updatePosition();//更新位置
            }else
            {

            }

            XXXX.setText(Double.toString(distance));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override//打開gps時
        public void onProviderEnabled(String provider) {
            btnfrmlyOn();
            startTouch=true;//start 啟動中
//            Toast.makeText(getApplicationContext(),"打開gps時" , Toast.LENGTH_LONG).show();
        }

        @Override//關閉gps時
        public void onProviderDisabled(String provider) {
            btnfrmlyOFF();
            if(timeRecordSum>=0.000001)
            {
                handler.removeCallbacks(updatedRecordTime01);
            }
            startTouch=false;//start關閉

            if(startTouch)
            {
                handler.removeCallbacks(updatedRecordTime01);
            }
//            Toast.makeText(getApplicationContext(),"關閉gps時" , Toast.LENGTH_LONG).show();
        }
    };

    private LocationListener listener1000=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            gpx_location=location;//當前位置
//            String aa=currentLocation.getLatitude()+"";
            String bb=gpx_location.getLatitude()+"";
            String cdsd="";


            double lat,lng,alt;
            Long time;

            DecimalFormat poundsFormat = new DecimalFormat("0.000000");
            lat=Double.parseDouble(poundsFormat.format(location.getLatitude()));
            lng=Double.parseDouble(poundsFormat.format(location.getLongitude()));
            alt = Double.parseDouble(poundsFormat.format(location.getAltitude()));//海拔
            if(distance > 5.0)
            {
                if(creat_gpx_ok)
                {
                    //製作gpx-------------------------------------------
                    time=location.getTime();
//        String aaaw=location.getTime()+"";
//        loc = new Location("");
                    loc.setLatitude(lat);
                    loc.setLongitude(lng);
                    loc.setAltitude(alt);
                    loc.setTime(time);
                    allMusicNum.add(0,loc);
                    index_location+=1;

                    DateFormat df = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//                df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                    }
                    Location location_arr=allMusicNum.get(0);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        segments += "<trkpt lat=\"" + String.format("%.6f", location_arr.getLatitude())+ "\" lon=\"" + String.format("%.6f", location_arr.getLongitude()) + "\">"+"<ele>"+String.format("%.1f", location_arr.getAltitude())+"</ele>"+"<time>" + df.format(new Date(location_arr.getTime())) + "</time></trkpt>\n";
                    }

                    String footer = "</trkseg></trk></gpx>";

                    try {
                        FileWriter writer = new FileWriter(file, false);
                        writer.append(header);
                        writer.append(gpx_name);
                        writer.append(segments);
                        writer.append(footer);
                        writer.flush();
                        writer.close();

                    } catch (IOException e) {
//            Log.e("generateGfx", "Error Writting Path",e);
                    }
                }

            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
//
//    // 啟動Google地圖
//    public void button_Click(View view) {
//        // 取得經緯度座標
//        float latitude = (float) currentLocation.getLatitude();
//        float longitude = (float) currentLocation.getLongitude();
//        // 建立URI字串
//        String uri = String.format("geo:%f,%f?z=18", latitude, longitude);
//        // 建立Intent物件
//        Intent geoMap = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//        startActivity(geoMap);  // 啟動活動
//    }


    //Dialog確認按鈕
    private DialogInterface.OnClickListener confirmButton=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            completeLYT_Flag=true;
            if(chk_httpstate()==false)
            {

                startLYT.setVisibility(View.VISIBLE);
                completeLYT.setVisibility(View.GONE);

                if(timeRecordSum>=0.000001)
                {
                    handler.removeCallbacks(updatedRecordTime01);
                }
                startTouch=false;//start關閉

                if(startTouch)
                {
                    handler.removeCallbacks(updatedRecordTime01);
                }
                handler.removeCallbacks(updatedTime01);
                return;
            }


            startLYT.setVisibility(View.VISIBLE);
            completeLYT.setVisibility(View.GONE);

            if(timeRecordSum>=0.000001)
            {
                handler.removeCallbacks(updatedRecordTime01);
            }
            startTouch=false;//start關閉

            if(startTouch)
            {
                handler.removeCallbacks(updatedRecordTime01);
            }
            handler.removeCallbacks(updatedTime01);

            begin = Calendar.getInstance();//按下結束時的時間
            //--------------------------
            endTimeLYT.setText(//設定結束時間文字
                    String.format("%02d",begin.get(Calendar.HOUR_OF_DAY))+":"+
                            String.format("%02d",begin.get(Calendar.MINUTE))+":"+
                            String.format("%02d",begin.get(Calendar.SECOND)));

            //消失自動暫停中
            prompt_pauseLYT.setVisibility(View.INVISIBLE);
            prompt_pauseLYT.setSelected(false);
            prompt_pauseLYT.setText("");

            DecimalFormat avgSpeedFormat=new DecimalFormat("0.0");

            double a=(totalDistance/timeRecordSum)*3600/1000;
            if(timeRecordSum<=5)
            {
                avgSpeedLYT.setText(getString(R.string.stopwatch_zero_hkm));
            }
            else
            {
                avgSpeedLYT.setText(avgSpeedFormat.format((totalDistance/timeRecordSum)*3600/1000)+" h/km");
            }

            handler.removeCallbacks(updateB100);

            t_mode="1";//確認完畢便回1
            sql_value();
            B100_mode=false;//最後更新玩 變回FALSE
            dbmysql();
            notificationManager.cancel(notificationManager_id);

            intent.setClass(Biker_time_Stopwatch.this,Biker_profile.class);
            startActivity(intent);
            finish();



        }
    };

    //加入sql裡面的值 碼表上的值
    private void sql_value() {
        t_timing=timeSum;//紀錄時間
        t_ridingtiming=timeRecordSum;//騎乘時間

        t_distance=Float.parseFloat(oneDPFormat.format(totalDistance/1000));//距離
        t_uphillSum=Float.parseFloat(oneDPFormat.format(upAltitude/1000));//爬升距離
        t_downhillSum=Float.parseFloat(oneDPFormat.format(downAltitude/1000));//下坡距離
        t_altitudeUp=Float.parseFloat(oneDPFormat.format(maxAltitude));//下坡距離
        t_altitudeDown=Float.parseFloat(oneDPFormat.format(minAltitude));//下坡距離
        if(minAltitude==9999)
        {
            t_altitudeDown=Float.parseFloat(oneDPFormat.format(0.0));//下坡距離

        }

        try
        {
            if(Double.isNaN((totalDistance/timeRecordSum)*3600/1000))
            {
                t_avgSpeed=0.0f;
            }
            else
            {
                t_avgSpeed=Float.parseFloat(oneDPFormat.format((float)((totalDistance/timeRecordSum)*3600/1000)));//平均速度
            }
        }
        catch (Exception a)
        {

        }


        t_fastestSpeed=Float.parseFloat(oneDPFormat.format(maxSpeed*3600/1000));//下坡距離
        t_startTime=startTimeLYT.getText().toString();//開始時間
//        t_endTime=t_endTime_methon();//結束時間



        if(B100_mode==false)
        {
            //B100流水號ID

            mysql_insert(u_id,t_timing,t_ridingtiming,t_distance,t_uphillSum,t_downhillSum,t_altitudeUp,
                    t_altitudeDown,t_avgSpeed,t_fastestSpeed,t_startTime,t_endTime,t_year,t_date,t_mode);
            B100_mode=true;
            B100_id =dbmysqlB100_id(u_id);

        }
        else
        {
            mysql_update(B100_id,u_id,t_timing,t_ridingtiming,t_distance,t_uphillSum,t_downhillSum,t_altitudeUp,
                    t_altitudeDown,t_avgSpeed,t_fastestSpeed,t_startTime,t_endTime,t_year,t_date,t_mode);
        }



    }


    private Runnable updateB100=new Runnable() {
        @Override
        public void run() {
            t_endTime_method();
            sql_value();
            handler.postDelayed(this,  30000); // 真正延
        }
    };
    private void t_endTime_method()
    {
        begin = Calendar.getInstance();
        t_endTime=String.format("%02d",begin.get(Calendar.HOUR_OF_DAY))+":"+
                String.format("%02d",begin.get(Calendar.MINUTE))+":"+
                String.format("%02d",begin.get(Calendar.SECOND));
    }

    private boolean chk_httpstate()
    {
        sqlctl = "SELECT * FROM B100";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        String result = DBConnector29S.executeQuery(nameValuePairs);
        ////-------------------------------
        //存取類別成員 DBConnector01.httpstate 判定是否回應 200(連線要求成功)
        if (DBConnector29S.httpstate == 200)
        {
//            startActivity(intent);
//            dbmysql();
//            this.finish();
            btnfrmLy.setVisibility(View.VISIBLE);
            btnupmysqlLY.setVisibility(View.GONE);
            return true;
        }
        else
        {

            int checkcode = DBConnector29S.httpstate / 100;
            int a=0;
            if(checkcode==2)
            {
                return true;
            }
            switch (checkcode) {
                case 1:
                    ser_msg = "資訊回應(code:" + DBConnector29S.httpstate + ") ";
                    show_dialog();

                    break;
                case 2:
                    ser_msg = "已經完成由伺服器會入資料(code:" + DBConnector29S.httpstate + ") ";
//                    intent.setClass(Biker_time_Stopwatch.this,Biker_profile.class);
//                    startActivity(intent);
//                    dbmysql();
//                    this.finish();

                    break;
                case 3:
                    ser_msg = "伺服器重定向訊息，請稍後在試(code:" + DBConnector29S.httpstate + ") ";
                    show_dialog();

                    break;
                case 4:
                    ser_msg = "用戶端錯誤回應，請稍後在試(code:" + DBConnector29S.httpstate + ") ";
                    show_dialog();

                    break;
                case 5:
                    ser_msg = "伺服器error responses，請稍後在試(code:" + DBConnector29S.httpstate + ") ";
                    show_dialog();

                    break;
            }
//                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();

        }

        if (DBConnector29S.httpstate == 0) {
            ser_msg = "網路不穩定請稍後在試(code:" + DBConnector29S.httpstate + ") ";
            show_dialog();
            return false;
        }

        return false;
        //-------------------------------------------------------------------
    }
    // 讀取MySQL 資料
    private void dbmysql() {
        sqlctl = "SELECT * FROM B100 ORDER BY id ASC";
//        sqlctl = "SELECT * FROM A100 WHERE A101='100504042909700846019'";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            String result = DBConnector29S.executeQuery(nameValuePairs);
            //===========================================
//            chk_httpstate();//檢查連接狀態
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
                int rowsAffected = dbHper.clearRec();                 // 匯入前,刪除所有SQLite資料
//--------------------------------------------------------
                // 處理JASON 傳回來的每筆資料
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    ContentValues newRow = new ContentValues();

                    // ---(2) 使用固定已知欄位---------------------------
                    newRow.put("id",jsonData.getString("id").toString());
                    newRow.put("B101",jsonData.getString("B101").toString());
                    newRow.put("B102",jsonData.getString("B102").toString());
                    newRow.put("B103",jsonData.getString("B103").toString());
                    newRow.put("B104",jsonData.getString("B104").toString());
                    newRow.put("B105",jsonData.getString("B105").toString());
                    newRow.put("B106",jsonData.getString("B106").toString());
                    newRow.put("B107",jsonData.getString("B107").toString());
                    newRow.put("B108",jsonData.getString("B108").toString());
                    newRow.put("B109",jsonData.getString("B109").toString());
                    newRow.put("B110",jsonData.getString("B110").toString());
                    newRow.put("B111",jsonData.getString("B111").toString());
                    newRow.put("B112",jsonData.getString("B112").toString());
                    newRow.put("B113",jsonData.getString("B113").toString());
                    newRow.put("B114",jsonData.getString("B114").toString());
                    newRow.put("B115",jsonData.getString("B115").toString());
                    newRow.put("B116",jsonData.getString("B116").toString());
                    newRow.put("B117",jsonData.getString("B117").toString());
                    int aa=0;
                    // -------------------加入SQLite---------------------------------------
                    long rowID = dbHper.insertRec_m(newRow);
//                        Toast.makeText(getApplicationContext(), "共匯入 " + Integer.toString(jsonArray.length()) + " 筆資料", Toast.LENGTH_SHORT).show();

                }
                // ---------------------------
            } else {
//                    Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
//            recSet = dbHper.getRecSet();  //重新載入SQLite
//            u_setspinner();
            // --------------------------------------------------------
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

    }
    // 讀取MySQL B100_id 資料
    private String dbmysqlB100_id(String u_id) {
        sqlctl = "SELECT * FROM B100 WHERE B101='"+u_id+"' AND B115='0' ";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            String result = DBConnector29S.executeQuery(nameValuePairs);

            /**************************************************************************
             * SQL 結果有多筆資料時使用JSONArray
             * 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            JSONArray jsonArray = new JSONArray(result);

            // -------------------------------------------------------
            if (jsonArray.length() > 0)
            {
                String aaa=jsonArray.getString(0);
                JSONObject jsondata =new JSONObject(aaa);
                seleteB100_id=jsondata.getString("id");;
                return jsondata.getString("id");

            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
            // --------------------------------------------------------
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return "0";
    }
    //讀取B100 更新 上次完成的mode
    private void dbmysqlB100last() {

        //-------------------取SQLite資料
        mList =new ArrayList<Map<String,Object>>();
        Map<String ,Object>item =new HashMap<>();
        recSetB100=dbHper.getRecSetB100_Last();//0112多了這行  選擇所有資料
        String[] fld = recSetB100.get(0).split("#");
        String aa=fld[0];
        String bb=fld[1];
        mysql_update(fld[0],fld[1],Integer.parseInt(fld[2]),Integer.parseInt(fld[3]),
                Float.parseFloat(fld[4]),Float.parseFloat(fld[5]),Float.parseFloat(fld[6]),
                Float.parseFloat(fld[7]),Float.parseFloat(fld[8]),
                Float.parseFloat(fld[9]),Float.parseFloat(fld[10])
                ,fld[11],fld[12],fld[13],fld[14],"1");




        dbmysql();
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }

    private void show_dialog()
    {
        AlertDig = new Dialog(Biker_time_Stopwatch.this);

        AlertDig.setCancelable(false);//不能按其他地方
        AlertDig.setContentView(R.layout.alert_dialog);//選擇layout

        alertBtnOK = (Button)AlertDig.findViewById(R.id.train_btnOK);
        alertBtnCancel = (Button)AlertDig.findViewById(R.id.train_btnCancel);
        Dig_tarin_waring = (TextView) AlertDig.findViewById(R.id.tarin_waring);
        Dig_train_title=(TextView)AlertDig.findViewById(R.id.train_title);
        Dig_train_title.setText("請稍後再試");
        Dig_train_title.setTextSize(16);
        Dig_tarin_waring.setText(ser_msg);
        Dig_tarin_waring.setTextSize(12);
        alertBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnfrmLy.setVisibility(View.GONE);
                btnupmysqlLY.setVisibility(View.VISIBLE);
                AlertDig.cancel();
            }
        });
        alertBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnfrmLy.setVisibility(View.GONE);
                btnupmysqlLY.setVisibility(View.VISIBLE);
                AlertDig.cancel();
            }
        });
        AlertDig.show();
    }



    private void mysql_insert(String g_id,int t_timing,int t_ridingtiming,float t_distance,float t_uphillSum,
                              float t_downhillSum,float t_altitudeUp,
                              float t_altitudeDown,float t_avgSpeed,
                              float t_fastestSpeed,String t_startTime,String t_endTime,
                              String t_year,String t_date,String t_mode) {
        ArrayList<String> nameValuePairs =new ArrayList<>();
        nameValuePairs.add(g_id);
        nameValuePairs.add(t_timing+"");
        nameValuePairs.add(t_ridingtiming+"");
        nameValuePairs.add(t_distance+"");
        nameValuePairs.add(t_uphillSum+"");
        nameValuePairs.add(t_downhillSum+"");
        nameValuePairs.add(t_altitudeUp+"");
        nameValuePairs.add(t_altitudeDown+"");
        nameValuePairs.add(t_avgSpeed+"");
        nameValuePairs.add(t_fastestSpeed+"");
        nameValuePairs.add(t_startTime);
        nameValuePairs.add(t_endTime);
        nameValuePairs.add(t_year);
        nameValuePairs.add(t_date);
        nameValuePairs.add(t_mode);
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }


        //----------------------------------------------- 真正執行mySQL 是這行
        String result = DBConnector29S.executeInsert(nameValuePairs);
        //-----------------------------------------------

//        this.finish();
    }
    //更新mysqli
    private void mysql_update(String B100_id,String g_id,int t_timing,int t_ridingtiming,float t_distance,float t_uphillSum,
                              float t_downhillSum,float t_altitudeUp,
                              float t_altitudeDown,float t_avgSpeed,
                              float t_fastestSpeed,String t_startTime,String t_endTime,
                              String t_year,String t_date,String t_mode) {
        //
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(B100_id);//id
        nameValuePairs.add(g_id);
        nameValuePairs.add(t_timing+"");
        nameValuePairs.add(t_ridingtiming+"");
        nameValuePairs.add(t_distance+"");
        nameValuePairs.add(t_uphillSum+"");
        nameValuePairs.add(t_downhillSum+"");
        nameValuePairs.add(t_altitudeUp+"");
        nameValuePairs.add(t_altitudeDown+"");
        nameValuePairs.add(t_avgSpeed+"");
        nameValuePairs.add(t_fastestSpeed+"");
        nameValuePairs.add(t_startTime);
        nameValuePairs.add(t_endTime);
        nameValuePairs.add(t_year);
        nameValuePairs.add(t_date);
        nameValuePairs.add(t_mode);
        try {
            Thread.sleep(500); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = DBConnector29S.executeUpdate(nameValuePairs);
//-----------------------------------------------
    }

    //刪除mysqli資料
    private void mysql_del_save_keep() {
        b100_id_mode = "";
        b100_id_mode=dbmysqlB100_id(u_id);
        //如果有狀態是0 得先刪除 怕會出錯
        if(b100_id_mode.equals("0"))
        {
            return;
        }
        else
        {
            //完成後跳出確定或取消
            completeDialog=new AlertDialog.Builder(Biker_time_Stopwatch.this);
            completeDialog.setTitle("上筆記錄未完成");

            completeDialog.setMessage("要儲存還是放棄上筆紀錄?");

            completeDialog.setCancelable(false);

            completeDialog.setNegativeButton("捨棄", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ArrayList<String> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(seleteB100_id);//id
                    try {
                        Thread.sleep(100); //  延遲Thread 睡眠0.5秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//-----------------------------------------------
                    String result = DBConnector29S.executeDelet(nameValuePairs);
//-----------------------------------------------
                }
            });
            completeDialog.setPositiveButton("儲存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbmysqlB100last();
//                    mysql_update(b100_id_mode);
                }
            });
//            completeDialog.setNeutralButton("繼續上次", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });


            completeDialog.show();

        }

    }

    //Dialog取消按鈕
    private DialogInterface.OnClickListener cancelButton=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {




        }
    };
    private void save_gpx_id(String id) //儲存gpx流水號避免蓋掉檔案
    {
        //儲存資料   後面開啟儲存的檔案
        SharedPreferences gpx_id =getSharedPreferences("GPX_ID",0);

        gpx_id
                .edit()
                .putString("GPX_ID", id)
                .commit();
//        Toast.makeText(getApplicationContext(),"儲存成功",Toast.LENGTH_SHORT).show();
    }
    private Integer load_gpx_id() //載入gpx_id
    {

        SharedPreferences gpxid =getSharedPreferences("GPX_ID",0);
        return Integer.parseInt(gpxid.getString("GPX_ID",""));
    }
    private void u_load_uid() //載入U_id
    {

        SharedPreferences gameresult =getSharedPreferences("USER_ID",0);
        u_id=gameresult.getString("USER_ID","");
    }
    //紀錄時間
    private Runnable updatedTime01=new Runnable() {
        @Override
        public void run() {
            timeSum++;
            hour=timeSum/60/60;
            minute=timeSum/60%60;
            seconds=timeSum%60;



            timingLYT.setText(
                    String.format("%02d",hour)+":"+
                            String.format("%02d",minute)+":"+
                            String.format("%02d",seconds));//讓TextView元件顯示時間倒數情況


            handler.postDelayed(this,1000);
        }
    };
    //紀錄騎乘時間
    private Runnable updatedRecordTime01=new Runnable() {
        @Override
        public void run() {
            timeRecordSum++;
            hourRecord=timeRecordSum/60/60;
            minuteRecord=timeRecordSum/60%60;
            secondsRecord=timeRecordSum%60;



            ridingtimingLYT.setText(String.format("%02d",hourRecord)+":"+
                    String.format("%02d",minuteRecord)+":"+
                    String.format("%02d",secondsRecord));//讓TextView元件顯示時間倒數情況


            handler.postDelayed(this,1000);

        }
    };
    private void initDB()
    {
        if (dbHper == null)
        {
            dbHper = new FriendDbHelper29S(this, DB_FILE, null, DBversion);
        }



    }

    @Override
    protected void onStart() {
        super.onStart();

        dbmysql();


//        dbmysqlB100last();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        u_checkgps();
        //取得最佳的定位提供者
        Criteria criteria=new Criteria();//精確度
        best=manager.getBestProvider(criteria,true);/// 获得当前的位置提供者
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            btnfrmlyOn();
        }
        if (dbHper == null) {
            dbHper = new FriendDbHelper29S(this, DB_FILE, null, DBversion);
        }
        Log.d(TAG,"onResume");
        btnupmysqlLY.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        try {
//            manager.removeUpdates(listener);
//        } catch (SecurityException e) {
//            Log.e(TAG, "GPS權限失敗..." + e.getMessage());
//        }
        Log.d(TAG,"onPause");
        // 開啟中的話就關掉
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 開啟中的話就關掉
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }
    }

    @Override
    protected void onDestroy() {
//        save_endTime();
        super.onDestroy();
        handler.removeCallbacks(updatedTime01);
        handler.removeCallbacks(updatedRecordTime01);
        handler.removeCallbacks(updateB100);
        //關閉通知
        notificationManager.cancel(notificationManager_id);
        finish();
        Log.d(TAG,"onDestroy");
    }





    @Override
    public void onBackPressed() {
//super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.biker_menu,menu);
        this.menu=menu;
        menu.setGroupVisible(R.id.g01,false);
        menu.setGroupVisible(R.id.g02,false);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)//請自行inten至各個畫面(未整合前先註解起來)　item01~03亮瑜改就好
    {
        switch (item.getItemId()){
            case R.id.action_settings: //原則上不用修改
                intent.setClass(Biker_time_Stopwatch.this, Biker_home.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean fileCopy(String oldFilePath,String newFilePath) throws IOException {
        //如果原檔案不存在
        if(fileExists(oldFilePath) == false){
            return false;
        }
        //獲得原檔案流
        FileInputStream inputStream = new FileInputStream(new File(oldFilePath));
        byte[] data = new byte[8048];
        //輸出流
        try {
            FileOutputStream outputStream =new FileOutputStream(new File(newFilePath));
            //開始處理流
            while (inputStream.read(data) != -1) {
                outputStream.write(data);
            }
            inputStream.close();
            outputStream.close();
        }catch (Exception e)
        {
            String aa=e.getMessage();
            String a="";
        }


        return true;
    }

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
}