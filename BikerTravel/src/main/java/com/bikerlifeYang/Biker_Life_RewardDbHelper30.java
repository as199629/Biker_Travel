package com.bikerlifeYang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Biker_Life_RewardDbHelper30 extends SQLiteOpenHelper
{
    public String sCreateTableCommand;
    // 資料庫名稱
    private static final String DB_FILE = "bikerlife.db"; //資料庫名稱
    private static final String DB_TABLE_F100 = "F100";//資料表名稱
    private static final String DB_TABLE_F200 = "F200";//資料表名稱
    private static final String DB_TABLE_F300 = "F300";//資料表名稱
    private static final String DB_TABLE_F400 = "F400";//資料表名稱
    private static final String DB_TABLE_B100 = "B100";//資料表名稱
//    private String[] reward=new String[]{"新手上路","不需要輔助輪","車行百里","鐵石小腿","胎紋都沒了","衝出大氣層","已經環繞地球一周了"};
//    private String[] reward_condition
//            =new String[]{"1","20","100","200","500","10000","40000"};
    private ArrayList<String> reward_condition;
    private static final int VERSION = 1;//資料庫版本
    private static final String rwTBsql = "CREATE     TABLE   " + DB_TABLE_F100 + "   ( "
            + "F101    INTEGER   PRIMARY KEY," + "F102 INTEGER NOT NULL," + "F103 INTEGER NOT NULL"+");";
    //----------------------------------
    private static final String rwTBsql2 = "CREATE     TABLE   " + DB_TABLE_F200 + "   ( "
            + "F201    INTEGER   PRIMARY KEY," + "F202 TEXT NOT NULL"+");";
    //成就名稱及條件
    private static final String rwTBsql3 = "CREATE     TABLE   " + DB_TABLE_F300+ "   ( "
            + "F301    INTEGER   PRIMARY KEY," + "F302 TEXT NOT NULL," + "F303 INTEGER NOT NULL,"+"F304 FLOAT NOT NULL,"
            +"F305 INTEGER NOT NULL,"+"F306 INTEGER NOT NULL"+");";
    private static final String rwTBsql4 = "CREATE     TABLE   " + DB_TABLE_F400+ "   ( "
            + "F401    INTEGER   PRIMARY KEY," + "F402 TEXT NOT NULL," + "F403 INTEGER NOT NULL,"+"F404 TEXT NOT NULL"
           +");";

    private static final String crTBsql4 = " CREATE     TABLE   " + DB_TABLE_B100 + "   ( "
            + "id    INTEGER   PRIMARY KEY,"
            + "t_s_timing INTEGER ,"//紀錄時間
            + "t_s_ridingtiming INTEGER ,"//騎乘時間
            + "B104 FLOAT ,"//距離
            + "t_s_uphillSum FLOAT ,"//爬升距離
            + "t_s_downhillSum FLOAT ,"//下坡距離
            + "t_s_altitudeUp FLOAT ,"//最高海拔
            + "t_s_altitudeDown FLOAT ,"//最低海拔
            + "t_s_avgSpeed FLOAT ,"//平均速度
            + "t_s_fastestSpeed FLOAT ,"//最快速度
            + "t_s_startTime TEXT ,"//開始時間
            + "t_s_endTime TEXT ,"//結束時間
            + "t_s_year TEXT ,"//年
            +"t_s_date TEXT  );";//月
//    ID	F101
//    User_ID	F102
//    達成成就ID	F103

//    成就種類
//    ID	F201
//    類型	F202
//    成就條件
//    ID	F301
//    成就名稱	F302
//    成就種類	F303
//    成就條件	F304
    private static SQLiteDatabase database;


    public Biker_Life_RewardDbHelper30(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);//此處變數目前沒有使用
        sCreateTableCommand = "";
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
//    public static SQLiteDatabase getDatabase(Context context){
//        if (database == null || !database.isOpen())  {
//            database = new Biker_Life_RewardDbHelper(context, DB_FILE, null, VERSION)
//                    .getWritableDatabase();
//        }
//        return database;
//    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(rwTBsql);//建立一資料表
        db.execSQL(rwTBsql2);//建立一資料表
        db.execSQL(rwTBsql3);//建立一資料表
        db.execSQL(rwTBsql4);//建立一資料表
        db.execSQL(crTBsql4);//建立一資料表
        //insertreward_initialization(db);//存進預設成就名稱
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
//    public void insertreward_initialization(SQLiteDatabase db) {//存進預設成就名稱(之後應該會刪)
//        //SQLiteDatabase db = getWritableDatabase();
//        ContentValues rec = new ContentValues();
////        for(int i=0;i<reward.length;i++){
////            rec.put("F302", reward[i]);
////            rec.put("F303", 1);
////            rec.put("F304", reward_condition[i]);
////            db.insert(DB_TABLE_F300, null, rec); //新增失敗會回傳-1
////            rec.clear();
////        }
//        //測試用
//        rec.put("B104", 500);
//        db.insert(DB_TABLE_B100, null, rec); //新增失敗會回傳-1
//        rec.clear();
//        rec.put("B104", 1000);
//        db.insert(DB_TABLE_B100, null, rec); //新增失敗會回傳-1
//        rec.clear();
//       // db.close();
//    }

    public int RecF300_Count() { //顯示F300資料表目前資料數
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_F300;
        Cursor recSet = db.rawQuery(sql, null);
        int F300_Count=recSet.getCount();
        recSet.close();
        db.close();
        return F300_Count;
    }
    public ArrayList<String> FindF302() { //回傳成就名稱
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT F302 FROM " + DB_TABLE_F300;
        ArrayList<String> strarray=new   ArrayList<String>(); //陣列長度=資料列數
        Cursor recSet = db.rawQuery(sql, null);
        if(recSet.getCount()!=0){
            while (recSet.moveToNext()){
                strarray.add(recSet.getString(0));
            }
            recSet.close();
            db.close();
            return strarray;

        }
        else{
            recSet.close();
            db.close();
            return null;//沒有任何資料的話回傳null
        }
    }
    public ArrayList<String> FindF304() { //回傳成就條件
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT F304 FROM " + DB_TABLE_F300;
        ArrayList<String> strarray=new   ArrayList<String>(); //陣列長度=資料列數
        Cursor recSet = db.rawQuery(sql, null);
        if(recSet.getCount()!=0){
            while (recSet.moveToNext()){
                strarray.add(recSet.getString(0));
            }
            recSet.close();
            db.close();

            return strarray;
        }
        else{
            recSet.close();
            db.close();
            return null;//沒有任何資料的話回傳null
        }
    }
    public ArrayList<String> FindF402() { //回傳稱號名稱
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT F402 FROM " + DB_TABLE_F400;
        ArrayList<String> strarray=new   ArrayList<String>(); //陣列長度=資料列數
        Cursor recSet = db.rawQuery(sql, null);
        if(recSet.getCount()!=0){
            while (recSet.moveToNext()){
                strarray.add(recSet.getString(0));
            }
            recSet.close();
            db.close();

            return strarray;

        }
        else{
            recSet.close();
            db.close();
            return null;//沒有任何資料的話回傳null
        }
    }
    public ArrayList<String> FindF403() { //回傳稱號條件
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT F403 FROM " + DB_TABLE_F400;
        ArrayList<String> strarray=new   ArrayList<String>(); //陣列長度=資料列數
        Cursor recSet = db.rawQuery(sql, null);
        if(recSet.getCount()!=0){
            while (recSet.moveToNext()){
                strarray.add(recSet.getString(0));
            }
            recSet.close();
            db.close();
            return strarray;

        }
        else{
            recSet.close();
            db.close();
            return null;//沒有任何資料的話回傳null
        }
    }
    //http://hk.uwenku.com/question/p-veditmjt-hu.html
    //顯示目前的資料
    public boolean  Findreward(int rewardid,int userid){ //判定是否達成成就
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT F304  FROM " + DB_TABLE_F300+" WHERE F301="+ rewardid ;
//        String sql2 = "SELECT B104  FROM " + DB_TABLE_B100+" WHERE id="+ 1 ;  //id之後須使用引數 現在暫定1 整合時 註解起來
//        String sql2 = "SELECT B104  FROM " + DB_TABLE_B100 ;  //整合時 註解起來
        String sql2 = "SELECT B104  FROM " + DB_TABLE_B100+" WHERE B101="+ userid ;  //整合時 關閉註解


        Cursor recSet = db.rawQuery(sql,null);  //取得指定成就名稱的條件
        Cursor recSet2 = db.rawQuery(sql2,null);//取得特定uid的距離資料;
        float F304_f=0.0f;
        if (recSet.getCount()!=0){
            recSet.moveToFirst();
            F304_f+=recSet.getFloat(0);//成就條件(里程)
        }


        float mileage_total=0;  //里程總數
        if(recSet2.getCount()!=0){
            while (recSet2.moveToNext()){
                mileage_total+=recSet2.getFloat(0);
            }
        }


        if(mileage_total> F304_f){//成就達成
            recSet.close();
            recSet2.close();
            db.close();
            return true;
        }
        else//成就未達成
            recSet.close();
            recSet2.close();
            db.close();
            return false;
    }
    public String  Findreward2(int position,int userid){ //顯示成就數據
        SQLiteDatabase db = getReadableDatabase();
//        String sql = "SELECT B104  FROM " + DB_TABLE_B100+" WHERE id="+ 1 ;  //id之後須使用引數 現在暫定1 整合時 註解起來
//        String sql = "SELECT B104  FROM " + DB_TABLE_B100;  //id之後須使用引數 現在暫定1 整合時 註解起來
        String sql = "SELECT B104  FROM " + DB_TABLE_B100+" WHERE B101="+userid ;  //整合時解開註解
        Cursor recSet2 = db.rawQuery(sql,null);  //取得指定成就名稱的條件
     int a=recSet2.getCount();
        float mileage_total=0;
        if(recSet2.getCount()!=0){
            while (recSet2.moveToNext()){
                mileage_total+=recSet2.getFloat(0);
            }
        }
        recSet2.close();
        db.close();
        reward_condition=FindF304();
//        return "目前進度:"+mileage_total+"公里；達成條件:"+reward_condition.get(position)+"公里";//成就進度
        return "目前進度:"+mileage_total+"KM；達成條件:"+reward_condition.get(position)+"KM";//成就進度

    }
    public String FinduserName(String id){//顯示使用者的名稱
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT A102  FROM " + "A100 "+" WHERE id="+ id ;
        Cursor recSetName = db.rawQuery(sql,null);
        if(recSetName.getCount()!=0){
            recSetName.moveToFirst();
            String name=recSetName.getString(0);
            recSetName.close();
            db.close();
            return name;
        }
        recSetName.close();
        db.close();
        return "不明";
    }
    public String FinduserUrl(String id){//顯示使用者的頭貼
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT A104  FROM " + "A100 "+" WHERE id="+ id ;
        Cursor recSetName = db.rawQuery(sql,null);
        if(recSetName.getCount()!=0){
            recSetName.moveToFirst();
            String url=recSetName.getString(0);
            recSetName.close();
            db.close();
            return url;
        }
        recSetName.close();
        db.close();
        return "https://bklifetw.com/img/nopic1.jpg";
    }
    public String FinduserRewardName(String id){//顯示使用者的成就名稱
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT A106  FROM " + "A100 "+" WHERE id="+ id ;
        Cursor recSetName = db.rawQuery(sql,null);
        if(recSetName.getCount()!=0){
            recSetName.moveToFirst();
            String RewardName=recSetName.getString(0);
            recSetName.close();
            db.close();
            return RewardName;
        }
        recSetName.close();
        db.close();
        return "未選擇";
    }

    public int clearRec_F(String sql_species) {//清除Sqlite所有資料(特定資料表)
        SQLiteDatabase db = getWritableDatabase();
        String sql="";
        if(sql_species.equals("F300"))
            sql = "SELECT * FROM " + DB_TABLE_F300;
        if(sql_species.equals("F400"))
            sql = "SELECT * FROM " + DB_TABLE_F400;
        Cursor recSet = db.rawQuery(sql, null);

//        //------------------------------------------------
        if (recSet.getCount() != 0) {
            int rowsAffected=-1;
            if(sql_species.equals("F300"))
             rowsAffected = db.delete(DB_TABLE_F300, "1", null); //
            if(sql_species.equals("F400"))
                rowsAffected = db.delete(DB_TABLE_F400, "1", null); //
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }
    public int clearRec_A100() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + "A100";
        Cursor recSet = db.rawQuery(sql, null);
//        //-----------
//        Cursor c1=db.execSQL("");
//        Cursor c2=db.rawQuery();
//        Cursor c3=db.insert();
//        Cursor c4=db.update(, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , );
//        Cursor c5=db.delete();
//        //------------------------------------------------
        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = db.delete("A100", "1", null); //
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }
    public int clearRec_B100() {// 刪除紀錄SQLite資料
        //        //-----------
//        Cursor c1=db.execSQL("");
//        Cursor c2=db.rawQuery();
//        Cursor c3=db.insert();
//        Cursor c4=db.update(, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , );
//        Cursor c5=db.delete();
//        //------------------------------------------------
        database = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_B100;//紀錄
        Cursor recSet = database.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = database.delete(DB_TABLE_B100, "1", null); //
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            database.close();
            return rowsAffected;
        } else {
            recSet.close();
            database.close();
            return -1;
        }
    }
    //    ContentValues values
    public long insertRec_B100(ContentValues rec) {//紀錄
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_B100, null, rec);
        db.close();
        return rowID;
    }
    public long insertRec_F300(ContentValues rec)//將mysql中的資料 寫入sqlite
    {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_F300, null, rec);
        db.close();
        return rowID;
    }
    public long insertRec_F400(ContentValues rec)//將mysql中的資料 寫入sqlite
    {
        int i =rec.size();
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_F400, null, rec);
        db.close();
        return rowID;
    }
    //    ContentValues values
    public long insertRec_A100(ContentValues rec) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert("A100", null, rec);
        db.close();
        return rowID;
    }

}
