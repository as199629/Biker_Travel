package com.bikerlifeYang;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import java.lang.reflect.Method;
import java.util.Calendar;
// Toast.makeText(getApplicationContext(),"吐司測試機二號",Toast.LENGTH_LONG).show();
public class Biker_Join_editTeam extends AppCompatActivity
{
    private Button EditCheck;
    private TextView edit_date,edit_time;
    private static final int REQUEST_CODE = 1;
    private EditText edit_title,edit_address,edit_des,edit_people;
    private Menu menu;
    private Intent intent;
    private Dialog edit_Dlg, return_Dlg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_join_edit_team);
        setupViewCompoent();
    }

    private void setupViewCompoent()
    {
        edit_title=(EditText)findViewById(R.id.join_edit_title); //標題
        edit_address=(EditText)findViewById(R.id.join_edit_address);//地點
        edit_des=(EditText)findViewById(R.id.join_edit_des);//備註
        edit_people=(EditText)findViewById(R.id.join_edit_people);//人數
//--------------------------------------
        edit_date=(TextView)findViewById(R.id.join_edit_date);//日期
        //edit_date.setInputType(InputType.TYPE_NULL);//關閉Edit顯示鍵盤
        Calendar calendar = Calendar.getInstance(); //單例模式
        int Nowyear = calendar.get(Calendar.YEAR);
        int Nowmonth = calendar.get(Calendar.MONTH);
        int Nowday = calendar.get(Calendar.DAY_OF_MONTH);
        //---------------------------------------
        edit_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog datePicDlg = new DatePickerDialog(Biker_Join_editTeam.this,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                            {
                                calendar.set(year,month,dayOfMonth);
                                if (calendar.after(Calendar.getInstance())) {//選擇的時間是否為現在時間之後
                                    String dateTime = String.valueOf(year) + "-" + String.format("%02d",month + 1)+ "-" + String.format("%02d",dayOfMonth);//轉換
                                    edit_date.setText(dateTime);
                                }else{
                                   // Toast.makeText(getApplicationContext(),"吐司測試機一號",Toast.LENGTH_LONG).show();
                                    new AlertDialog.Builder(Biker_Join_editTeam.this)
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
        edit_time=(TextView)findViewById(R.id.join_edit_time);//時間
        int Nowhour = calendar.get(Calendar.HOUR_OF_DAY);
        int Nowminute = calendar.get(Calendar.MINUTE);
        edit_time.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TimePickerDialog timePicDlg = new TimePickerDialog(Biker_Join_editTeam.this,
                        new TimePickerDialog.OnTimeSetListener()
                        {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                            {
                                    String dateTime = String.valueOf(hourOfDay) + ":" + String.format("%02d", minute) + "出發";//轉換
                                    edit_time.setText(dateTime);
                            }
                        }, Nowhour, Nowminute, false);
                timePicDlg.setTitle(getString(R.string.join_hint3));
                timePicDlg.setIcon(android.R.drawable.ic_lock_idle_alarm);
                timePicDlg.setCancelable(false);
                timePicDlg.show();
            }
        });





        EditCheck=(Button)findViewById(R.id.join_edit_check);
        EditCheck.setOnClickListener(new View.OnClickListener()//假想用，內容後可刪
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = getIntent();
                  intent.putExtra("title", edit_title.getText().toString());
                  intent.putExtra("people", edit_people.getText().toString());
                  intent.putExtra("date", edit_date.getText().toString());
                  intent.putExtra("time", edit_time.getText().toString());
                  intent.putExtra("address", edit_address.getText().toString());
                  intent.putExtra("des", edit_des.getText().toString());
                setResult(REQUEST_CODE, intent); //REQUEST_CODE 需跟AActivity.class的一樣
                finish();
            }
        });
    }
    ////----------------------------------以下是menu用的方法----------------
    private void Signin_menu(){//登入時顯示的menu
        menu.setGroupVisible(R.id.g01,false); //此頁只能返回
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
              //  Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                return_Dlg = new Dialog(Biker_Join_editTeam.this);
                return_Dlg.setCancelable(false);
                return_Dlg.setContentView(R.layout.biker_alert_dialog);
                TextView re_title=(TextView)return_Dlg.findViewById(R.id.alert_title);
                TextView re_msg=(TextView)return_Dlg.findViewById(R.id.alert_msg);
                Button re_BtnOK = (Button) return_Dlg.findViewById(R.id.alert_btnOK);
                Button re_BtnCancel = (Button) return_Dlg.findViewById(R.id.alert_btnCancel);

                re_title.setText(getString(R.string.warning_title));
                re_msg.setText(getString(R.string.editteam_warning_msg));
                re_BtnOK.setText(getString(R.string.editteam_warning_okbtn));
                re_BtnCancel.setText(getString(R.string.editteteam_warning_cancelbtn));
                re_BtnOK.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                });
                re_BtnCancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        return_Dlg.cancel();
                    }
                });

                return_Dlg.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
