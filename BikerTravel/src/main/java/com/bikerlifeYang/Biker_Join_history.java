package com.bikerlifeYang;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// Toast.makeText(getApplicationContext(),"吐司測試機三號",Toast.LENGTH_LONG).show();
public class Biker_Join_history extends AppCompatActivity
{

    private static final String DB_FILE = "bikerlife.db"; //資料庫名稱
    private Button Exit;
    private Menu menu;
    private String[] db_array;
    private TextView join_history_title,join_history_organiser,join_history_date,join_history_address,join_history_Des,join_history_people;
    private Biker_Life_JoinDbHelper BdbHper;
    private ArrayList<String> joindata;
    private int people_count;
    private Context mContext=this;
    private ImageView join_history_img;
    private Button join_history_member;
    private Button join_history_cancel_member;
    private RelativeLayout R1,R2;
    private RecyclerView mRecyclerView;
    ArrayList<HashMap<String, String>> RecyclerView_arrayList = new ArrayList<>();
    private ArrayList<Map<String, Object>> mList;
    MyListAdapter myListAdapter;
    private RequestOptions options;
    private String sqlctl="";
    private String ser_msg="";
    private int server_error;
    private ProgressDialog progDlg;
    private Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.biker_join_history);
        initDB();
       SetupViewConpoent();
        show_ProgDlg();//啟動進度條
        handler.postDelayed(readSQL, 1000);
//        showResult();
    }

    private void SetupViewConpoent()
    {
        mRecyclerView = findViewById(R.id.RecyclerView_history_members);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //******成員名單----------------------
        // 動態調整高度 抓取使用裝置尺寸
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newscrollheight = displayMetrics.heightPixels * 50 / 100; // 設定ScrollView使用尺寸的4/5
        mRecyclerView.getLayoutParams().height = newscrollheight;
        mRecyclerView.setLayoutParams(mRecyclerView.getLayoutParams()); // 重定ScrollView大小
        //-----*********----------------------------
        join_history_title=(TextView)findViewById(R.id.join_history_title);
        join_history_organiser=(TextView)findViewById(R.id.join_history_organiser);
        join_history_people=(TextView)findViewById(R.id.join_history_people);
        join_history_date=(TextView)findViewById(R.id.join_history_date);
        join_history_address=(TextView)findViewById(R.id.join_history_address);
        join_history_Des=(TextView)findViewById(R.id.join_history_Des);
        join_history_img=(ImageView)findViewById(R.id.join_history_img);
       Exit=(Button)findViewById(R.id.join_history_Exit);
       Exit.setOnClickListener(new View.OnClickListener()
       {
           @Override
           public void onClick(View v)
           {
               finish();
           }
       });
        R1=(RelativeLayout) findViewById(R.id.His_R1);
        R2=(RelativeLayout) findViewById(R.id.His_R2);
        join_history_member=(Button)findViewById(R.id.join_history_member);
        join_history_member.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("name",  members_data_A100[2]);
                        hashMap.put("reward",  members_data_A100[6]);
                        hashMap.put("distance", member_B100);
                        hashMap.put("img_url",  members_data_A100[4]);
                        RecyclerView_arrayList.add(hashMap);
                    }

                    myListAdapter = new MyListAdapter();
                    mRecyclerView.setAdapter(myListAdapter);
                }

                else
                {
                    mRecyclerView.setAdapter(null);
//                    Toast.makeText(getApplicationContext(),"沒有成員",Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),getString(R.string.join_n_member),Toast.LENGTH_SHORT).show();
                }
            }
        });
        join_history_cancel_member=(Button)findViewById(R.id.join_history_cancel_member);
        join_history_cancel_member.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                R1.setVisibility(View.VISIBLE);
                R2.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void initDB()
    {
        int DBversion = Integer.parseInt(getString(R.string.SQLite_version));
        if (BdbHper == null)
        {  //如果沒有連線資料庫 就開啟
            BdbHper = new Biker_Life_JoinDbHelper(this, DB_FILE, null, DBversion);
            //Toast.makeText(getApplicationContext(),"yaa",Toast.LENGTH_SHORT).show();
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
        Dialog AlertDig = new Dialog(Biker_Join_history.this);

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
    private void chk_httpstate() {
        ////-------------------------------
        //存取類別成員 DBConnector01.httpstate 判定是否回應 200(連線要求成功)
        if (DBConnector28.httpstate == 200) {
//            ser_msg = "伺服器匯入資料(code:" + DBConnector.httpstate + ") ";
            ser_msg = getString(R.string.join_okHttp_error0) + DBConnector28.httpstate + ") ";
        } else {
            int checkcode = DBConnector28.httpstate / 100;
            switch (checkcode) {
                case 1:
//                    ser_msg = "資訊回應(code:" + DBConnector.httpstate + ") ";
                    ser_msg = getString(R.string.join_okHttp_error1) + DBConnector28.httpstate + ") ";
                    break;
                case 2:
//                    ser_msg = "已經完成由伺服器匯入資料(code:" + DBConnector.httpstate + ") ";
                    ser_msg = getString(R.string.join_okHttp_error2) + DBConnector28.httpstate + ") ";
                    break;
                case 3:
//                    ser_msg = "伺服器重定向訊息，請稍後在試(code:" + DBConnector.httpstate + ") ";
                    ser_msg = getString(R.string.join_okHttp_error3) + DBConnector28.httpstate + ") ";
                    break;
                case 4:
//                    ser_msg = "用戶端錯誤回應，請稍後在試(code:" + DBConnector.httpstate + ") ";
                    ser_msg = getString(R.string.join_okHttp_error4) + DBConnector28.httpstate + ") ";
                    break;
                case 5:
//                    ser_msg = "伺服器error responses，請稍後在試(code:" + DBConnector.httpstate + ") ";
                    ser_msg = getString(R.string.join_okHttp_error5) + DBConnector28.httpstate + ") ";
                    break;
            }
//                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        }
        if (DBConnector28.httpstate == 0) {
//            ser_msg = "遠端資料庫異常(code:" + DBConnector.httpstate + ") ";
            ser_msg = getString(R.string.join_okHttp_error6) + DBConnector28.httpstate + ") ";
            server_error++;
        }
//        Toast.makeText(getBaseContext(), ser_msg, Toast.LENGTH_SHORT).show();

        //-------------------------------------------------------------------
    }
    private void showResult()//取得從join頁面中的資料
    {
        Bundle bundle=this.getIntent().getExtras();
        db_array=bundle.getStringArray("now_information");
        //-----------MYSQL資料=>SQLITE
        dbmysql_B100();//抓取記錄的資料
        //----------
        joindata= BdbHper.FindNow_Specify_Join(db_array[0]);
        join_history_title.setText(joindata.get(1)); //標題
        ArrayList<String> member_ar=BdbHper.getRecSet_A100(Integer.parseInt(joindata.get(8)));
        String str_member[]=member_ar.get(0).split("#");
        join_history_organiser.setText(str_member[2]); //主辦人
        people_count=BdbHper.RecG200_Count(db_array[0]);//目前人數
        join_history_people.setText(people_count+""); //人數
        join_history_date.setText(joindata.get(2)); //日期　ＸＸ－ＸＸ－ＸＸ
        join_history_address.setText(joindata.get(5)); //地點
        join_history_Des.setText(joindata.get(6)); //敘述
        Glide.with(mContext)
                .load(str_member[4])
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(100, 75)
//                    .transition(withCrossFade())
                .error(
                        Glide.with(mContext)
                                .load("https://bklifetw.com//img/nopic1.jpg"))
                .into(join_history_img);
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
               // Toast.makeText(getApplicationContext(), getString(R.string.action_settings), Toast.LENGTH_LONG).show();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
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
            //變圓形
            options = new RequestOptions()
                    .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(50)))
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .priority(Priority.NORMAL);

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

//            Picasso.get().load(RecyclerView_arrayList.get(position).get("img_url")).into(holder.circleImg_member);//頭像
            Glide.with(mContext)
                    .load(RecyclerView_arrayList.get(position).get("img_url"))
                    .apply(options)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
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
}
