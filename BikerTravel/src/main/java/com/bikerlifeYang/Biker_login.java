package com.bikerlifeYang;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Biker_login extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;

    //SQLite
    private static final String DB_FILE = "bikerlife.db";
    private static final String DB_TABLE = "login";

    private int DBversion;//版本號

    private TextView logo02LYT;
    private TextView logo01LYT;
    private Intent intent=new Intent();

    private Menu menu;
    private int loginmode=0;
    private GoogleSignInClient mGoogleSignInClient;
    private CircleImgView img;
    private Uri User_IMAGE;
    private String TAG="tcnr29";
    private TextView show_msgLYT;
    private TextView googlelogoutLYT;
    private SignInButton googleloginLYT;
    public FriendDbHelper29G dbHper;
    private Button btnAdd;
    private String t_name="";
    private String t_id="";
    private String t_imgUrl="";
    private String t_email="";
    private Uri noiconimg;
    private Long rowID;
    private Handler handler=new Handler();
    private Bundle bundle=new Bundle();
    private Button return_btn;
    private MenuItem action;
    private String sqlctl;
    private String ser_msg;
    private int servermsgcolor;
    private TextView textView5;
    private Button TESTUpdatabtn;
    private Button TESTdelbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_login);
        setUpViewComponent();
        DBversion = Integer.parseInt(getString(R.string.SQLite_version));
        initDB();

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




    private void setUpViewComponent() {

        //15-9 google登入
        validateServerClientID();
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleloginLYT=findViewById(R.id.googlelogin);
        googleloginLYT.setSize(SignInButton.SIZE_STANDARD);
        googleloginLYT.setColorScheme(SignInButton.COLOR_LIGHT);

        logo01LYT=(TextView)findViewById(R.id.logo01);
        logo02LYT=(TextView)findViewById(R.id.logo02);

        return_btn=(Button)findViewById(R.id.return_btn);
        show_msgLYT = findViewById(R.id.show_msg);
        googlelogoutLYT=(TextView)findViewById(R.id.googlelogout);
        googleloginLYT=(SignInButton)findViewById(R.id.googlelogin);




        findViewById(R.id.disconnect_button).setOnClickListener(this);
        googlelogoutLYT.setOnClickListener(this);
        googleloginLYT.setOnClickListener(this);


        //testSQLite
        btnAdd=(Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        return_btn.setOnClickListener(this);
        //開機動畫
//        logo.setAnimation(AnimationUtils.loadAnimation(this,R.anim.move_and_scale));

        logo01LYT.setAnimation(null);
        logo01LYT.clearAnimation();
        logo02LYT.setAnimation(null);
        logo02LYT.clearAnimation();
//        img.setAnimation(null);
//        img.clearAnimation();



        logo01LYT.setAnimation(AnimationUtils.loadAnimation(this, R.anim.login_move_left_to_right01));
        logo02LYT.setAnimation(AnimationUtils.loadAnimation(this, R.anim.login_move_left_to_right02));



        ////測試用按鈕以後要刪除測試用按鈕以後要刪除測試用按鈕以後要刪除測試用按鈕以後要刪除測試用按鈕以後要刪除
        textView5=(TextView)findViewById(R.id.textView5);
        textView5.setOnClickListener(this);
        TESTUpdatabtn=(Button)findViewById(R.id.TESTUpdatabtn);
        TESTdelbtn=(Button)findViewById(R.id.TESTdelbtn);
        TESTdelbtn.setOnClickListener(this);
        TESTUpdatabtn.setOnClickListener(this);
    }

    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in google_sign.xml, must end with " + suffix;

            Log.w(TAG, message);
//            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.googlelogin:
//                intent.setClass(Biker_login.this,Biker_home.class);
//                startActivity(intent);
//                this.finish();

                signIn();

                break;
            case R.id.googlelogout:

//                Toast.makeText(getApplicationContext(),"登出",Toast.LENGTH_SHORT).show();
                signOut();

                break;
            case R.id.disconnect_button://中斷
//                revokeAccess();
                break;
            case R.id.btnAdd://test SQLite 增加
                dbHper.RecCount();
                break;
            case R.id.return_btn://回傳值測試

//                String a = dbHper.get_gid(t_id);
//                save_uid(a);
//                intent.setClass(Biker_login.this,Biker_home.class);
//                startActivity(intent);

                finish();

                break;
            case R.id.textView5:
                //匯入SQLITE
//                dbmysql();


//                dbmysql_id_0_1(t_id);//檢查帳號登入ID過
//                String aqwe="105992021210532359470";
//                String bbbb=mysql_selete_id(aqwe);
//                int www=0;
                dbmysql();
                dbHper.RecCount();
                break;
            case R.id.TESTUpdatabtn:
//                mysql_update();//執行更新
                DBConnector29G.Update_A106("71","稱號用");
//                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            case R.id.TESTdelbtn:
                mysql_del();
                break;
        }

    }



//    private void sql_add()
//    {
//        if(t_name.equals("")|| t_id.equals("")|| t_imgUrl.equals("")||t_email.equals(""))
//        {
////            Toast.makeText(getApplicationContext(),"空白無法新增",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if(dbHper.SELECTg_id(t_id.trim())==false)
//        {
////            " SELECT g_id FROM "+DB_TABLE +" WHERE g_id= "+ t_id;
//            dbHper.insertRec(t_id,t_name,t_email,t_imgUrl);
//            dbHper.RecCount();
//        }
//        //-----直接增加到Mysql------------------------------
//        mysql_insert(t_id,t_name,t_email,t_imgUrl);
////        dbmysql();
//
//
//    }


    private void mysql_insert(String t_id,String t_name,String t_email,String t_imgUrl) {
        ArrayList<String> nameValuePairs =new ArrayList<>();
//        nameValuePairs.add(tname);
//        nameValuePairs.add(tgrp);
//        nameValuePairs.add(taddr);
//        t_id="10";
//        t_name="20";
//        t_email="30";
//        t_imgUrl="40";
        nameValuePairs.add(t_id);
        nameValuePairs.add(t_name);
        nameValuePairs.add(t_email);
        nameValuePairs.add(t_imgUrl);
        nameValuePairs.add("0");

        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }


        //----------------------------------------------- 真正執行mySQL 是這行
        String result = DBConnector29G.executeInsert(nameValuePairs);
        //-----------------------------------------------

    }
    // 讀取MySQL 資料
    private boolean dbmysql_id_0_1(String t_id) {
//        sqlctl = "SELECT * FROM A100 ORDER BY id ASC";
//        sqlctl = "SELECT * FROM A100 WHERE A101='105992021210532359470'";
//        dbmysql();
        String aa=t_id;
        String sql_id_0_1 = "SELECT A101 FROM A100 WHERE A101=" + "'" + t_id + "'";

        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sql_id_0_1);
        try {
            String result = DBConnector29G.executeQuery(nameValuePairs);
            //===========================================
            chk_httpstate();//檢查連接狀態
            String bb=result.trim();
            int a=0;
            if(result.trim().equals("0"))
            {
//                textView5.setText("0");
                return false;
            }
            else
            {
//                textView5.setText("1");
                return true;
            }

            //===============================
        }catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        return true;
    }
    // 讀取MySQL 資料
    private void dbmysql() {
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
                int rowsAffected = dbHper.clearRec();                 // 匯入前,刪除所有SQLite資料
//--------------------------------------------------------
                // 處理JASON 傳回來的每筆資料
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    ContentValues newRow = new ContentValues();
//                    // --(1) 自動取的欄位 --取出 jsonObject 每個欄位("key","value")-----------------------
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
                    // ---(2) 使用固定已知欄位---------------------------
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

//        sqlctl = "SELECT * FROM member ORDER BY id ASC";
//        ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
//        try {
//            String result = DBConnector.executeQuery(nameValuePairs);
//
//            /**************************************************************************
//             * SQL 結果有多筆資料時使用JSONArray
//             * 只有一筆資料時直接建立JSONObject物件 JSONObject
//             * jsonData = new JSONObject(result);
//             **************************************************************************/
//            JSONArray jsonArray = new JSONArray(result);
//            // -------------------------------------------------------
//            if (jsonArray.length() > 0) { // MySQL 連結成功有資料
//
//                int rowsAffected = dbHper.clearRec();                 // 匯入前,刪除所有SQLite資料
//
//                // 處理JASON 傳回來的每筆資料
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonData = jsonArray.getJSONObject(i);
//                    ContentValues newRow = new ContentValues();
//                    // --(1) 自動取的欄位 --取出 jsonObject 每個欄位("key","value")-----------------------
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
//                    // newRow.put("id", jsonData.getString("id").toString());
//                    // newRow.put("name",
//                    // jsonData.getString("name").toString());
//                    // newRow.put("grp", jsonData.getString("grp").toString());
//                    // newRow.put("address", jsonData.getString("address")
//                    // -------------------加入SQLite---------------------------------------
//                    long rowID = dbHper.insertRec_m(newRow);
//                    Toast.makeText(getApplicationContext(), "共匯入 " + Integer.toString(jsonArray.length() ) + " 筆資料", Toast.LENGTH_SHORT).show();
//                }
//                // ---------------------------
//            } else {
//                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
//            }
//            recSet = dbHper.getRecSet();  //重新載入SQLite
//            u_setspinner();
//            // --------------------------------------------------------
//        } catch (Exception e) {
//            Log.d(TAG, e.toString());
//        }
    }

    private String mysql_selete_id(String t_id)
    {
        String sql_id_0_1 = "SELECT id FROM A100 WHERE A101=" + "'" + t_id + "'";

        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sql_id_0_1);
        try {
            String result = DBConnector29G.executeQuery(nameValuePairs);
            //===========================================
            chk_httpstate();//檢查連接狀態
            String bb=result.trim();
            int a=0;
            JSONArray jsonArray = new JSONArray(result);

                // 處理JASON 傳回來的每筆資料

            JSONObject jsonData = jsonArray.getJSONObject(0);
            ContentValues newRow = new ContentValues();
            // ---(2) 使用固定已知欄位---------------------------

            String iddd=jsonData.getString("id").toString();
            int aaaa=0;
            return iddd.trim();

                // ---------------------------

        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
        }
        return "0";

    }

    //更新mysqli
    private void mysql_update() {

        //
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add("70");
        nameValuePairs.add("1234567890");//gid
        nameValuePairs.add("冠廷");//name
        nameValuePairs.add("XXXX");//email
        nameValuePairs.add("XXXXX");//urlimg
        try {
            Thread.sleep(100); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = DBConnector29G.executeUpdate(nameValuePairs);
//-----------------------------------------------
    }
    //刪除mysqli資料
    private void mysql_del() {


        ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
        nameValuePairs.add("69");
        try {
            Thread.sleep(100); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = DBConnector29G.executeDelet(nameValuePairs);
//-----------------------------------------------
    }


    private void chk_httpstate()
    {
        ////-------------------------------
        //存取類別成員 DBConnector01.httpstate 判定是否回應 200(連線要求成功)
        if (DBConnector29G.httpstate == 200) {
            ser_msg = "伺服器匯入資料(code:" + DBConnector29G.httpstate + ") ";
            servermsgcolor = ContextCompat.getColor(this, R.color.Navy);
//                Toast.makeText(getBaseContext(), "由伺服器匯入資料 ",
//                        Toast.LENGTH_SHORT).show();


        } else {
            int checkcode = DBConnector29G.httpstate / 100;
            switch (checkcode) {
                case 1:
                    ser_msg = "資訊回應(code:" + DBConnector29G.httpstate + ") ";
                    break;
                case 2:
                    ser_msg = "已經完成由伺服器會入資料(code:" + DBConnector29G.httpstate + ") ";
                    break;
                case 3:
                    ser_msg = "伺服器重定向訊息，請稍後在試(code:" + DBConnector29G.httpstate + ") ";
                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
                case 4:
                    ser_msg = "用戶端錯誤回應，請稍後在試(code:" + DBConnector29G.httpstate + ") ";
                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
                case 5:
                    ser_msg = "伺服器error responses，請稍後在試(code:" + DBConnector29G.httpstate + ") ";
                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
            }
//                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        }
        if (DBConnector29G.httpstate == 0) {
            ser_msg = "遠端資料庫異常(code:" + DBConnector29G.httpstate + ") ";
        }
//        b_servermsg.setText(ser_msg);
//        b_servermsg.setTextColor(servermsgcolor);

        //-------------------------------------------------------------------
    }


    private void signIn()
    {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // --TART handleSignInResult--
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account);


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void signOut()
    {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //--START_EXCLUDE--
                        updateUI(null);
                        // [END_EXCLUDE]
                        img.setImageResource(R.drawable.googleg_color); //還原圖示
                    }
                });
    }

    private void save_uid(String a) //儲存ID
    {
        //儲存資料   後面開啟儲存的檔案
        SharedPreferences gameresult =getSharedPreferences("USER_ID",0);

        gameresult
                .edit()
                .putString("USER_ID", a)
                .commit();
//        Toast.makeText(getApplicationContext(),"儲存成功",Toast.LENGTH_SHORT).show();
    }
    private void initDB()//初始化資料庫 這邊就開始創資料庫但沒資料
    {
        if (dbHper == null)
        {
            dbHper = new FriendDbHelper29G(this, DB_FILE, null, DBversion);
        }
    }



    //↓↓↓↓↓除了亮瑜使用此方法外，其他人原則上都使用Signin_menu↑↑的方法顯示menu item（167行已經寫好(亮瑜的部分改167行)）
    private void Signout_menu(){//登出時顯示的menu
        menu.setGroupVisible(R.id.g01,false);
        menu.setGroupVisible(R.id.g02,false);
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }






    //---------------------------------
    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {

//-------改變圖像--------------
            User_IMAGE = account.getPhotoUrl();
            //沒有照片的話
            if(User_IMAGE==null){
//                noiconimg=Uri.parse("https://lh3.googleusercontent.com/pw/ACtC-3f7ifqOfGrkeKoxWel1YUubvk1UzdlwSpsIY_Wfxa3jCYE75R1aYZlFtZd-jvFPzp5aUNfJksNAtXYj0OhzV-brFWU7E81L8H5td0SZTDgeWDp7PdVcBwKYxChccjyhUsTjVb2L8Zrqh7xJEGBIuhyK=w200-h192-no?authuser=0");
//                User_IMAGE=noiconimg;
                return;
            }
            img = (CircleImgView) findViewById(R.id.google_icon);
            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    String url = params[0];
                    return getBitmapFromURL(url);
                }

                @Override
                protected void onPostExecute(Bitmap result) {
                    img.setImageBitmap(result);
                    super.onPostExecute(result);
                }
            }.execute(User_IMAGE.toString().trim());


            t_id=account.getId();
            t_name=account.getDisplayName();
            t_email=account.getEmail();
            t_imgUrl= User_IMAGE+"";


            show_msgLYT.setText("Email:"+account.getEmail()+
                    "\n 全名:"+account.getDisplayName()+
                    "\n Firstname:"+account.getGivenName()+
                    "\n Last name:"+account.getFamilyName()+
                    "\n Id:"+account.getId()+
                    "\n 頭貼網址:"+User_IMAGE

            );

            //-------------------------
//            String g_id=account.getId();
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            googleloginLYT.setVisibility(View.GONE);
            googlelogoutLYT.setVisibility(View.VISIBLE);

            if(dbmysql_id_0_1(t_id)==false)
            {
                mysql_insert(t_id,t_name,t_email,t_imgUrl);
            }

            save_uid(mysql_selete_id(t_id));

            int aarwerew=0;

            handler.postDelayed(updateTimer, 3000);// 停多久開始做這個動作
        }
        else
        {


            t_id="";
            t_name="";
            t_email="";
            t_imgUrl="";
            show_msgLYT.setText(R.string.login_signout);

            googleloginLYT.setVisibility(View.VISIBLE);
            googlelogoutLYT.setVisibility(View.GONE);
        }


    }
    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            dbmysql();
            return_btn.callOnClick();
        }
    };

    //--------------------------------------------
    private Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // --START on_start_sign_in--
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && GoogleSignIn.hasPermissions(account, new Scope(Scopes.DRIVE_APPFOLDER))) {
            updateUI(account);
        } else {
            updateUI(null);
            //--END on_start_sign_in--
        }
        //--END on_start_sign_in--
        Log.d(TAG,"onStart");
    }
    @Override
    protected void onPause() {
        super.onPause();
        // 開啟中的話就關掉
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }

        Log.d(TAG,"onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        // 開啟中的話就關掉
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }
        Log.d(TAG,"onStop");
        handler.removeCallbacks(updateTimer);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbHper == null) {
            dbHper = new FriendDbHelper29G(this, DB_FILE, null, DBversion);
        }

        Log.d(TAG,"onResume");

//        sql_add();


        //100504042909700846019本


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG,"onDestroy");
        handler.removeCallbacks(updateTimer);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.biker_menu,menu);
        this.menu=menu;
        action = menu.findItem(R.id.action_settings);

        Signout_menu();
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)//請自行inten至各個畫面(未整合前先註解起來)　item01~03亮瑜改就好
    {
        switch (item.getItemId()){

            case R.id.action_settings: //原則上不用修改
                return_btn.callOnClick();
//                intent.setClass(Biker_login.this,Biker_home.class);
//                startActivity(intent);
//                this.finish();
//                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}