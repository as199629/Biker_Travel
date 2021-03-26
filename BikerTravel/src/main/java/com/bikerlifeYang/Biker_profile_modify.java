package com.bikerlifeYang;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import java.lang.reflect.Method;
import java.util.Calendar;

public class Biker_profile_modify extends AppCompatActivity implements View.OnClickListener {

    private String mode_title;
    private TextView b001, t018;
    private Intent intent01 = new Intent();
    private Intent intent = new Intent();
    private EditText e001, e003;
    private Spinner a04, a05, a06;
    private String u_date = "";
    private Calendar cg;
    private long endTime,birthday;
//    private MediaPlayer mediahaha;
    private String name, birth;
    private int weight=0,city_position=0,area_position=0,gender_position=0;
    private Menu menu;
    private Dialog giveupDlg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_profile_modify);
        setupViewcomponent();
    }

    private void setupViewcomponent() {
        //設定音樂連結
//        mediahaha = MediaPlayer.create(getApplicationContext(), R.raw.haha);

        b001 = (TextView) findViewById(R.id.profile_b001);//確認修改
        e001 = (EditText) findViewById(R.id.profile_e001);//姓名
        e003 = (EditText) findViewById(R.id.profile_e003);//體重
        a04 = (Spinner) findViewById(R.id.profile_a04);//縣市
        a05 = (Spinner) findViewById(R.id.profile_a05);//鄉鎮市區
        a06 = (Spinner) findViewById(R.id.profile_a06);//性別
        t018 = (TextView) findViewById(R.id.profile_t018);//生日選擇日期，超過今天顯示未出生的
        b001.setOnClickListener(this);
        t018.setOnClickListener(this);

        //        取得profile丟來的包裹
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");//初始值為黃漢昇，在Biker_profile有給值
        String birth = bundle.getString("birth");
        int weight = bundle.getInt("weight");
        city_position = bundle.getInt("city");
        area_position = bundle.getInt("area");
        gender_position= bundle.getInt("gender");
        //將包裹內容設置在各欄位
        e001.setText(name);
        t018.setText(birth);
        e003.setText(Integer.toString(weight));

//spinner有兩個方法來設定預設值：.setSelection(position)和.setSelection(position, animate)
        // 設定 spinner item 選項------------
        ArrayAdapter<CharSequence> city = ArrayAdapter
                .createFromResource(this, R.array.profile_a02,
                        R.layout.profile_spinner);//將可選内容與ArrayAdapter連接起來
        city.setDropDownViewResource(R.layout.profile_spinner);//設置下拉列表的風格
        // 準備 Listener a001Lis 需再設定 Listener
        a04.setAdapter(city);
        a04.setOnItemSelectedListener(city_select);
        a04.setSelection(city_position);//設定預設值(get 包裹)

        ArrayAdapter<CharSequence> adapSexList = ArrayAdapter
                .createFromResource(this, R.array.profile_a04,
                        R.layout.profile_spinner);//將可選内容與ArrayAdapter連接起來
        adapSexList.setDropDownViewResource(R.layout.profile_spinner);//設置下拉列表的風格
        // 準備 Listener a001Lis 需再設定 Listener
        a06.setAdapter(adapSexList);
        a06.setOnItemSelectedListener(gender_select);
        a06.setSelection(gender_position);//設定預設值(get 包裹)
    }

    private AdapterView.OnItemSelectedListener gender_select = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            gender_position=position;//紀錄選擇哪個性別
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private AdapterView.OnItemSelectedListener city_select = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            city_position=position;//紀錄選擇哪個縣市

            String country = parent.getSelectedItem().toString();//紀錄選擇哪個縣市
            ArrayAdapter<CharSequence> a_district;

            switch (country) {
                case "台中市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_center, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a05.setAdapter(a_district);
                    break;
                case "台北市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_north, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a05.setAdapter(a_district);
                    break;
                case "台南市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_south, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a05.setAdapter(a_district);
                    break;
                case "台東市":
                    a_district = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.profile_east, R.layout.profile_spinner);
                    a_district.setDropDownViewResource(R.layout.profile_spinner);
                    a05.setAdapter(a_district);
                    break;
            }
            a05.setOnItemSelectedListener(area_select);
            a05.setSelection(area_position);//設定預設值(get 包裹)
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener area_select=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            area_position=position;//紀錄選擇哪個鄉鎮市區
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };//自定義物件要加分號

    @Override
    public void onClick(View v) {
        //抓修改後的姓名
        String name = e001.getText().toString();
        //抓修改後的生日
        String birth=t018.getText().toString();
        //抓修改後的體重
        Integer weight = Integer.parseInt(e003.getText().toString());

        switch (v.getId()) {
            case R.id.profile_b001://確認修改
                //跳去個人檔案，並把姓名，生日，體重、地區、性別丟過去
                Bundle bundle_file = new Bundle();//包裹
                bundle_file.putString("name", name);
                bundle_file.putString("birth", birth);
                bundle_file.putInt("weight", weight);
                bundle_file.putInt("city", city_position);
                bundle_file.putInt("area", area_position);
                bundle_file.putInt("gender", gender_position);
                intent01.putExtras(bundle_file);
                setResult(RESULT_OK, intent01);//回傳值
                this.finish();
                break;
            case R.id.profile_t018://選擇生日
                Calendar now = Calendar.getInstance();//空月曆
                DatePickerDialog datepicDlg = new DatePickerDialog(
                        this,
                        datePicDlgOnDateSelLis,//將選擇的日期顯示在profile_t018
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));

                datepicDlg.setCancelable(false);
                datepicDlg.show();

                break;
        }
    }

    private DatePickerDialog.OnDateSetListener datePicDlgOnDateSelLis =//將選擇的日期顯示在profile_t018
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    u_date = Integer.toString(year) + "/" +
                            Integer.toString(month + 1) + "/" +
                            Integer.toString(dayOfMonth);
                    t018.setText(u_date);//生日

                    //判斷生日在今天以前
                    cg = Calendar.getInstance();//設定日曆新物件
                    cg.set(year, month, dayOfMonth);//將日期及時間設定進去物件
                    endTime = cg.getTimeInMillis();//取得使用者所選的時間(轉成毫秒)
                    birthday = System.currentTimeMillis() - endTime;
                    if (birthday < 1000) {
                        t018.setText(getString(R.string.unborn));
                    }

                }
            };

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
            case R.id.action_settings://確認是否放棄編輯
//                Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                giveupDlg = new Dialog(this);//確認是否放棄編輯
                giveupDlg.setCancelable(false);//一定要給我選，true的話可以按旁邊躲掉

                //R.layout.profile_dialog當作Dialog的畫面
                giveupDlg.setContentView(R.layout.alert_dialog);

                Button btnOK = (Button) giveupDlg.findViewById(R.id.train_btnOK);
                Button btnCancel = (Button) giveupDlg.findViewById(R.id.train_btnCancel);
                TextView giveup=(TextView)giveupDlg.findViewById(R.id.tarin_waring);

                giveup.setText(getString(R.string.giveup));//更改dialog的字
                btnOK.setText(getString(R.string.yes));
                btnCancel.setText(getString(R.string.no));
                giveupDlg.show();

                btnCancel.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     giveupDlg.cancel();//關閉對話盒
                                                 }
                                             });
                        btnOK.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {
                                                         giveupDlg.cancel();
                                                         finish();
                                                     }
                                                 });
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}