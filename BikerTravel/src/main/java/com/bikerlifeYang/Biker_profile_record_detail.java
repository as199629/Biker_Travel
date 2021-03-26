package com.bikerlifeYang;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Biker_profile_record_detail extends AppCompatActivity {

    private Menu menu;
    private TextView timing_r,ridingtiming_r,distanceSum_r,uphillSum_r,downhillSum_r,altitudeUp_r,
            altitudeDown_r,avgSpeed_r,fastestSpeed_r,startTime_r,endTime_r,del_record;
    private FriendDbHelper28 dbHper;
    private static final String DB_FILE = "bikerlife.db";
    private int DBversion = 1;
    private String r_id,m_id;
    private Dialog del_record_Dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_profile_record_detail);
        initDB();//有用到DB一定要加***放setupViewcomponent前面
        setupViewcomponent();
    }

    private void setupViewcomponent() {
        timing_r=(TextView)findViewById(R.id.timing_r);//紀錄時間
        ridingtiming_r=(TextView)findViewById(R.id.ridingtiming_r); //騎乘時間
        distanceSum_r=(TextView)findViewById(R.id.distanceSum_r);//距離
        uphillSum_r=(TextView)findViewById(R.id.uphillSum_r);//爬升距離
        downhillSum_r=(TextView)findViewById(R.id.downhillSum_r);//下坡距離
        altitudeUp_r=(TextView)findViewById(R.id.altitudeUp_r);//最高海拔
        altitudeDown_r=(TextView)findViewById(R.id.altitudeDown_r);//最低海拔
        avgSpeed_r=(TextView)findViewById(R.id.avgSpeed_r);//平均速度
        fastestSpeed_r=(TextView)findViewById(R.id.fastestSpeed_r);//最快速度
        startTime_r=(TextView)findViewById(R.id.startTime_r);//開始時間
        endTime_r=(TextView)findViewById(R.id.endTime_r);//結束時間
        del_record=(TextView)findViewById(R.id.del_record);//刪除紀錄


        Intent intent = this.getIntent();//取得傳遞過來的record_id
        r_id = intent.getStringExtra("record_id");
        m_id = intent.getStringExtra("member_uid");
        if(m_id!=null){//判斷是使用者自己的紀錄還是其他社員的紀錄，是其他社員刪除要隱藏
                del_record.setVisibility(View.INVISIBLE);
        }
        detail(r_id);
    }

    private void detail(String b_id) {//暫存都用b_
        String record_data = dbHper.record_detail(b_id);//取得此筆的所有資料

        String[] fld = record_data.split("#");//一條字串用#分割(M1405browse)
        int r_time=Integer.parseInt(fld[2]);//紀錄時間
        int hours = r_time/ 60 / 60;// 計算小時
        int minius = (r_time / 60) % 60;// 計算分鐘
        int seconds = r_time % 60;// 計算秒數(取餘數)
        String s_r_time=""+hours+":"+minius+":"+seconds;
        timing_r.setText(s_r_time);//紀錄時間 done

        r_time=Integer.parseInt(fld[3]);//騎乘時間
        hours = r_time/ 60 / 60;// 計算小時
        minius = (r_time / 60) % 60;// 計算分鐘
        seconds = r_time % 60;// 計算秒數(取餘數)
        s_r_time=""+hours+":"+minius+":"+seconds;
        ridingtiming_r.setText(s_r_time);//騎乘時間 done

        distanceSum_r.setText(fld[4]+"km");//距離
        uphillSum_r.setText(fld[5]+"km");//爬升距離
        downhillSum_r.setText(fld[6]+"km");//下坡距離
        altitudeUp_r.setText(fld[7]+"m");//最高海拔
        altitudeDown_r.setText(fld[8]+"m");//最低海拔
        avgSpeed_r.setText(fld[9]+"km/h");//平均速度
        fastestSpeed_r.setText(fld[10]+"km/h");//最快速度
        startTime_r.setText(fld[11]);//開始時間
        endTime_r.setText(fld[12]);//結束時間
    }

    public void del_record(View view) {
            del_record_Dlg=new Dialog(this);
            del_record_Dlg.setCancelable(false);//一定要給我選，true的話可以按旁邊躲掉

            //R.layout.alert_dialog當作Dialog的畫面
            del_record_Dlg.setContentView(R.layout.alert_dialog);

            Button btnOK = (Button) del_record_Dlg.findViewById(R.id.train_btnOK);
            Button btnCancel = (Button) del_record_Dlg.findViewById(R.id.train_btnCancel);

            TextView warning=(TextView)del_record_Dlg.findViewById(R.id.tarin_waring);
            warning.setText(getString(R.string.del_record)+"?");//更改dialog的字

            btnCancel.setOnClickListener(new View.OnClickListener() {//取消刪除紀錄
                @Override
                public void onClick(View v) {
                    del_record_Dlg.cancel();//關閉對話盒
                }
            });
            btnOK.setOnClickListener(new View.OnClickListener() {//確認刪除紀錄
                @Override
                public void onClick(View v) {
                    ArrayList<String> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(r_id);
                    DBConnector29S.executeDelet(nameValuePairs);//從MySQL刪除紀錄
                    del_record_Dlg.cancel();//關閉對話盒
                    finish();
                }
            });
            del_record_Dlg.show();
    }

    //有用到DB一定要加***
    private void initDB () {
        DBversion=Integer.parseInt(getString(R.string.SQLite_version));
        if (dbHper == null) {      //dbHper=SQLite
            dbHper = new FriendDbHelper28(this, DB_FILE, null, DBversion);
        }
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
        if (dbHper == null) {      //dbHper=SQLite
            dbHper = new FriendDbHelper28(this, DB_FILE, null, DBversion);
        }
//        a01.setAdapter(null);//清空spinner
    }

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
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
