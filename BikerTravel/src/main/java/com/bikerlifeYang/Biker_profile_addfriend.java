package com.bikerlifeYang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Biker_profile_addfriend extends AppCompatActivity {

    private Intent intent01 = new Intent();
    private String mode_title;
    private ArrayList<Map<String, Object>> mList;
    private String[] listarr;
    private ListView lv;
    private Menu menu;
    RecyclerView mRecyclerView;//for recyclerview
    MyListAdapter myListAdapter;//for recyclerview
    ArrayList<HashMap<String,String>> arrayList = new ArrayList<>();//for recyclerview
    private HashMap<String, String> hashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_profile_addfriend);
        setupViewcomponent();
    }

    private void setupViewcomponent() {
        //---設定class標題
//        Intent intent01 = this.getIntent();
//        mode_title=intent01.getStringExtra("subname");
//        this.setTitle(this.getResources().getIdentifier(mode_title,"string",getPackageName()));
//        this.setTitle(getString(R.string.profile_t016));
        //---

        //for recyclerview
        mRecyclerView = findViewById(R.id.addfriend_re);//相對應的Layout的recycleviewID
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        myListAdapter = new MyListAdapter();
        mRecyclerView.setAdapter(myListAdapter);
        //for recyclerview

        listarr = getResources().getStringArray(R.array.profile_friend);//取得新車友陣列內容
        //固定的+號圖案
        int picPlus = getResources().getIdentifier("ic_add_profile", "drawable", getPackageName());
        //--------------------------------------------巨集
        //for recyclerview
        try {//包在try裡面，for迴圈抓不到值就會break
            for (int i = 49; i < 600; i++) {
//                Map<String,Object> item=new HashMap<String, Object>();

 // %02d執行十進制整數轉換d，格式化補零，寬度為2。 因此，一個int參數，它的值是7，將被格式化為"07"作為一個String
                // 取得陣列profile_friend並丟進textview
                String microNO = String.format("%03d", i);

//                取得圖片
                int picId = getResources().getIdentifier("img" + microNO, "drawable", getPackageName());

                hashMap = new HashMap<>();
                //第一個參數對應layout，第二個參數告訴電腦去哪找資料
                hashMap.put("friendPic",Integer.toString(picId));//頭像
                hashMap.put("friendName",listarr[i-49]);//姓名
                hashMap.put("friendSymbol",Integer.toString(picPlus));//+

                arrayList.add(hashMap);

//                //第一個參數對應listview的layout，第二個參數告訴電腦去哪找資料
//                item.put("friendPic",picId);//頭像，
//                item.put("friendName",listarr[i-49]);//姓名
//                item.put("friendSymbol",picPlus);//+
//                mList.add(item);//將上面找到的資料放進ArrayList
//
//                //-----------------------------------------------------------------------------------
//                SimpleAdapter adapter=new SimpleAdapter(
//                        this,
//                        mList,
//                        R.layout.profile_member_lv,
//                        new String[]{"friendPic","friendName","friendSymbol"},
//                        new int[]{R.id.friendPic, R.id.friendName, R.id.friendSymbol});
//                       lv.setAdapter(adapter);//將adapter撈到的資料放進listview
//                       lv.setOnItemClickListener(addfriend);//選擇該好友後就提出好友申請
//                //-----------------------------------------------------------------------------------------


//                if (picId == 0) {
//                    break;//撈不到值(=傳回0)就跳出迴圈
//                }
            }
        } catch (Exception e) {
            return;
        }
    }
//    private AdapterView.OnItemClickListener addfriend=new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(getApplicationContext(),getString(R.string.addFriend)+listarr[position],Toast.LENGTH_LONG).show();
//
////            view.setVisibility(View.GONE);
//
//        }
//    };

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
//                Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------副程式for recyclerview----------------------------------------------------------------------------------
    private class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{

        class ViewHolder extends RecyclerView.ViewHolder{
            private TextView tvfriendName;
            private ImageView tvfriendPic,tvfriendSymbol;
//            private View mView;

            public ViewHolder(@NonNull View itemView) {//設置recyclerview的內容
                super(itemView);
                tvfriendPic = itemView.findViewById(R.id.friendPic);
                tvfriendName = itemView.findViewById(R.id.friendName);
                tvfriendSymbol = itemView.findViewById(R.id.friendSymbol);
//                mView  = itemView;
            }
        }

        @NonNull
        @Override//選擇用哪個layout放recyclerview
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_member_lv,parent,false);//選擇要顯示的layout格式
            return new ViewHolder(view);
        }

        @Override//生成recyclerview列表
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvfriendPic.setImageResource(Integer.parseInt(arrayList.get(position).get("friendPic")));
            holder.tvfriendName.setText(arrayList.get(position).get("friendName"));
            holder.tvfriendSymbol.setImageResource(Integer.parseInt(arrayList.get(position).get("friendSymbol")));

            holder.tvfriendSymbol.setOnClickListener((v)->{    //按+送出邀請，且改成打勾圖示
                holder.tvfriendSymbol.setImageResource(R.drawable.ic_done_profile);
                Toast.makeText(getApplicationContext(),getString(R.string.addFriend)+listarr[position],Toast.LENGTH_SHORT).show();
            });
            //對話盒--------------------------------------------------------------------------------
            holder.tvfriendName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),arrayList.get(position).get("friendName"),Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public int getItemCount() {
            return arrayList.size();//決定顯示數量，這裡的寫法是顯示全部
        }
    }
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------
    }