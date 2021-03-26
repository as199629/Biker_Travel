package com.bikerlifeYang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class Biker_team_information extends AppCompatActivity {

    private MenuItem b_itme01, b_itme02, b_itme03, b_itme04, b_itme05;
    String TAG = "mExample";
    RecyclerView mRecyclerView;
    MyListAdapter myListAdapter;
    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

    private Menu menu;
    private String[] strNameArray,strMailArray,strTelArray;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_team_information);
        setupViewComponent();
    }

    private void setupViewComponent() {
        strNameArray=getResources().getStringArray(R.array.name);
        strMailArray=getResources().getStringArray(R.array.mail);
        strTelArray=getResources().getStringArray(R.array.tel);
        for (int i = 0; i < 7; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", strNameArray[i]);
            hashMap.put("mail", strMailArray[i]);
            hashMap.put("tel", strTelArray[i]);
            arrayList.add(hashMap);
        }
        //設置RecycleView
        mRecyclerView = findViewById(R.id.recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        myListAdapter = new MyListAdapter();
        mRecyclerView.setAdapter(myListAdapter);
    }

    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvId, tvSub1, tvSub2;
            private ImageButton telBtn,mailBtn;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvId = itemView.findViewById(R.id.txt_name);
                tvSub1 = itemView.findViewById(R.id.txt_mail);
                tvSub2 = itemView.findViewById(R.id.txt_number);
                telBtn = itemView.findViewById(R.id.tel_button);
                mailBtn = itemView.findViewById(R.id.mail_button);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //:連接剛才寫的layout檔案，return一個View
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.biker_team_information_recycle_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {//在這裡取得元件的控制(每個item內的控制)
            holder.tvId.setText(arrayList.get(position).get("name"));
            holder.tvSub1.setText(arrayList.get(position).get("mail"));
            holder.tvSub2.setText(arrayList.get(position).get("tel"));
            holder.telBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  Toast.makeText(getApplicationContext(),"ya",Toast.LENGTH_SHORT).show();
                    String telNumber="tel:"+arrayList.get(position).get("tel");
                        Uri uri = Uri.parse(telNumber);
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                        startActivity(intent);
                }
            });
            holder.mailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mailNumber="mailto:"+arrayList.get(position).get("mail");
                    Uri uri = Uri.parse(mailNumber);
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    startActivity(intent);


                }
            });
        }

        @Override
        public int getItemCount() {//取得顯示數量，return一個int，通常都會return陣列長度(arrayList.size)
            return arrayList.size();
        }
    }










////----------------------------------以下是menu用的方法----------------
    private void Signin_menu(){//登入時顯示的menu
        menu.setGroupVisible(R.id.g01,false);//聯絡者資訊只能返回
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
                Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
