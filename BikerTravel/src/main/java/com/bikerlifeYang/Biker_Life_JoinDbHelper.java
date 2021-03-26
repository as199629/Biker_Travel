package com.bikerlifeYang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;

public class Biker_Life_JoinDbHelper extends SQLiteOpenHelper
{
    public String sCreateTableCommand;
    // 資料庫名稱
    private static final String DB_FILE = "bikerlife.db"; //資料庫名稱
    private static final String DB_TABLE_G100 = "G100";//資料表名稱
    private static final String DB_TABLE_G200 = "G200";//資料表名稱
//    private static final String DB_TABLE_G300 = "G300";//資料表名稱
    private static final int VERSION = 1;//資料庫版本
    private static final String joinTBsql = "CREATE     TABLE   " + DB_TABLE_G100 + "   ( "
            + "G101    INTEGER   PRIMARY KEY," + "G102 TEXT NOT NULL," + "G103 TEXT NOT NULL,"
            + "G104 TEXT NOT NULL,"+"G105 TEXT NOT NULL,"+"G106 TEXT NOT NULL,"+"G107 TEXT NOT NULL,"
            +"G108 TEXT NOT NULL ,"+"G109 TEXT NOT NULL ,"+"G110 TEXT NOT NULL ,"+"G111 TEXT NOT NULL ,"+"G112 INTEGER NOT NULL"+");";
    private static final String joinTBsql2 = "CREATE     TABLE   " + DB_TABLE_G200 + "( "
            + "G201    INTEGER   PRIMARY KEY," + "G202 INTEGER NOT NULL," + "G203 INTEGER NOT NULL,"
            + "G204 TEXT NOT NULL,"+"G205 INTEGER NOT NULL,"+"G206 TEXT NOT NULL"+");";
//    private static final String joinTBsql3 = "CREATE     TABLE   " + DB_TABLE_G300 + "   ( "
//            + "G101    INTEGER   PRIMARY KEY," + "G102 TEXT NOT NULL," + "G103 TEXT NOT NULL,"
//            + "G104 TEXT NOT NULL,"+"G105 TEXT NOT NULL,"+"G106 TEXT NOT NULL,"+"G107 TEXT NOT NULL,"
//            +"G108 TEXT NOT NULL ,"+"G109 TEXT NOT NULL ,"+"G110 TEXT NOT NULL ,"+"G111 TEXT NOT NULL ,"+"G112 INTEGER NOT NULL"+");";
//    ID	G101
//    名稱	G102
//    日期	G103
//    時間	G104
//    人數	G105
//    地點	G106
//    描述	G107
//    社團ID G108
//    是否已結束	G109
//測試串接社團用!!－－－－－－－－－－－－－－－－－－－－－－－－
    private static final String DB_TABLE_C100 = "C100";    // 社團資料庫物件，固定的欄位變數
    private static final String DB_TABLE_C200 = "C200";    // 社團人員明細資料庫物件，固定的欄位變數
    private String DB_TABLE_B100="B100";
    private static final String crTBsql = "CREATE     TABLE   " + DB_TABLE_C100 + "   ( "
        + "id    INTEGER   PRIMARY KEY,"
        + "C101 TEXT NOT NULL,"//社團名稱
        + "C102 INTEGER ,"//目前人數
        + "C103 INTEGER,"//人數上限
        + "C104 TEXT,"//所屬縣市
        + "C105 TEXT,"//所屬鄉鎮市區
        + "C106 TEXT,"//團長UID
        + "C107 TEXT,"//建立時間
        + "C108 TEXT,"
        + "C109 TEXT);";
    private static final String crTBsql2= " CREATE     TABLE   " + DB_TABLE_C200 + "   ( "
            + "C201    INTEGER,"//社團UID
            + "C202    TEXT  ,"//會員UID
            + "C203    TEXT  ," //會員狀態
            + "C204    TEXT  ,"
            + "C205    TEXT  ,"
            + "PRIMARY KEY ( C201, C202));";//有兩個以上的PRIMARY KEY要這樣寫
    //－－－－－－－－－－－－－－－－－－－－－
    private static SQLiteDatabase database;


    public Biker_Life_JoinDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);//此處變數目前沒有使用
        sCreateTableCommand = "";
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
//    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
//    public static SQLiteDatabase getDatabase(Context context){
//        if (database == null || !database.isOpen())  {
//            database = new Biker_Life_JoinDbHelper(context, DB_FILE, null, VERSION)
//                    .getWritableDatabase();
//        }
//        return database;
//    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(joinTBsql);//建立一資料表
        db.execSQL(joinTBsql2);//建立一資料表
//        db.execSQL(joinTBsql3);//建立一資料表
        db.execSQL(crTBsql);
        db.execSQL(crTBsql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }


    public int RecG200_Count(String joinID) { //顯示G200 指定揪團的人數
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_G200+" WHERE G203='"+joinID+"'" ;
        Cursor recSet = db.rawQuery(sql, null);
        int count=recSet.getCount();
        recSet.close();
        db.close();
        return count;
    }
    public boolean adding(String joinID, String userid ){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_G200+" WHERE G203='"+joinID+"' AND G202='"+userid+"'" ;
        Cursor recSet = db.rawQuery(sql, null);
     //
        if(recSet.getCount()==0){
            recSet.close();
            db.close();
            return false;
        }
        else{
            recSet.close();
            db.close();
            return true;
        }

    }
//    public long InsertNewTeam(String Title, String Date, String Time,String poeple,String addr,String Des,String clubid,String mainuserid ) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues rec = new ContentValues();
//        rec.put("G102", Title);
//        rec.put("G103", Date);
//        rec.put("G104", Time);
//        rec.put("G105", poeple);
//        rec.put("G106", addr);
//        rec.put("G107", Des);
//        rec.put("G108", clubid);
//        rec.put("G109", mainuserid);
//        rec.put("G110", "0");
//        rec.put("G111", "0");
//        rec.put("G112",  0);
//        long rowID = db.insert(DB_TABLE_G100, null, rec); //新增失敗會回傳-1
//        db.close();
//        return rowID;
//    }
    //http://hk.uwenku.com/question/p-veditmjt-hu.html
    //顯示目前的資料-------------------------------
    public String[][] FindNowJoin(String ClubID){
        SQLiteDatabase db = getReadableDatabase();
//        String sql = "SELECT * FROM " + DB_TABLE_G100  +" WHERE G108='"+ClubID+"'";
//        String sql = "SELECT * FROM " + DB_TABLE_G100  +" WHERE G108='"+ClubID+"'"+" ORDER BY G103 ASC";

        String sql = "SELECT * FROM " + DB_TABLE_G100  +" WHERE G108='"+ClubID+"'"+
                " ORDER BY CASE WHEN G110='0' THEN G103 END ASC, CASE WHEN G110='1' THEN G103 END DESC";
        Cursor recSet = db.rawQuery(sql,null);
        int columnCount = recSet.getColumnCount();//getColumnCount 資料表欄位數  getCount資料表列數
        String fldSet[][]=new String[recSet.getCount()][columnCount];
        if (recSet.getCount()!=0){
            int conut=0;
            while(recSet.moveToNext()){

                for(int i=0; i<columnCount; i++)
                {
                    fldSet[conut][i]="";
                    fldSet[conut][i] += recSet.getString(i) ;
                }

                conut++;
            }
        }
        recSet.close();
        db.close();
        return fldSet;
    }
    public ArrayList<String> FindNow_Specify_Join(String JoinID){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_G100  +" WHERE G101='"+JoinID+"'";
        Cursor recSet = db.rawQuery(sql,null);
        int columnCount = recSet.getColumnCount();//getColumnCount 資料表欄位數  getCount資料表列數
        ArrayList<String> fldSet=new ArrayList<String>();

        if (recSet.getCount()!=0){

            while(recSet.moveToNext()){

                for(int i=0; i<columnCount; i++)
                {
                    fldSet.add(recSet.getString(i));
                }
            }
        }
        recSet.close();
        db.close();
        return fldSet;
    }
    public void is_end(){//活動是否已經結束???
        SQLiteDatabase db=getReadableDatabase();
        String[] datearray;
        String sql="SELECT * FROM "+ DB_TABLE_G100 +" WHERE G110='0'";
        Cursor recSet =db.rawQuery(sql,null);
        if(recSet.getCount()>0){
            while (recSet.moveToNext()){
                //判定時間
                Calendar calendar=Calendar.getInstance();
                datearray =recSet.getString(2).split("-");//切割單列資料的字串(年月日)
                calendar.set(Integer.valueOf(datearray[0]),Integer.valueOf(datearray[1])-1,Integer.valueOf(datearray[2])+1);//設定cal的時間
                if(!calendar.after(Calendar.getInstance())){//該列資料的出發日期是否是之後的日期，不是的話就是活動結束了 //.after會不包含今天( 所以需+1)=>雖然月尾或年尾會超出日期格式但不影響(12/31變成12/32)
                    DBConnector.executeUpdate_IsEnd_G100(recSet.getString(0));//更新至Mysql
                    //為了少讀一次mysql 直接改sqlite的資料
                    String sql_update="UPDATE "+DB_TABLE_G100+" SET G110='1' WHERE G101="+recSet.getString(0);//變更該列資料G110狀態
                    SQLiteDatabase db2=getWritableDatabase();
                    db2.execSQL(sql_update);
                    db2.close();
                }
            }
        }

        recSet.close();
        db.close();


    }
    //刪除指定ID的欄位
    public void DelectTeam(String id){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM " + DB_TABLE_G100 +" WHERE G101='"+id+"'";
        db.execSQL(sql);
        db.close();
    }

    //更新
    public void updateTeam(String id,String Title, String Date, String Time,String poeple,String addr,String Des) {
        SQLiteDatabase db = getWritableDatabase();

        String sql = "UPDATE " + DB_TABLE_G100 +
                " SET G102='"+Title+"' , G103='"+Date+"' , G104='"+Time+"' , G105='"+poeple+"' , G106='"+addr+"' , G107='"+Des+
                "' WHERE G101='"+id+"'";

        db.execSQL(sql);
        db.close();

    }
    public String FinduserName(String id){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT A102  FROM " + "A100 "+" WHERE id="+ id ;
        Cursor recSetName = db.rawQuery(sql,null);
        if(recSetName.getCount()!=0){
            recSetName.moveToFirst();
            String name=recSetName.getString(0);
            return name;
        }

        return "不明";
    }

    public int clearRec_G100() {//清除Sqlite所有資料(特定資料表)
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_G100;
        Cursor recSet = db.rawQuery(sql, null);

//        //------------------------------------------------
        if (recSet.getCount() != 0) {
            int rowsAffected = db.delete(DB_TABLE_G100, "1", null); //
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
    public int clearRec_G200() {//清除Sqlite所有資料(特定資料表)
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_G200;
        Cursor recSet = db.rawQuery(sql, null);

//        //------------------------------------------------
        if (recSet.getCount() != 0) {
            int rowsAffected = db.delete(DB_TABLE_G200, "1", null); //
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
//    public int clearRec_G300() {//清除Sqlite所有資料(特定資料表)
//        SQLiteDatabase db = getWritableDatabase();
//        String sql = "SELECT * FROM " + DB_TABLE_G300;
//        Cursor recSet = db.rawQuery(sql, null);
//
////        //------------------------------------------------
//        if (recSet.getCount() != 0) {
//            int rowsAffected = db.delete(DB_TABLE_G300, "1", null); //
//            // From the documentation of SQLiteDatabase delete method:
//            // To remove all rows and get a count pass "1" as the whereClause.
//            recSet.close();
//            db.close();
//            return rowsAffected;
//        } else {
//            recSet.close();
//            db.close();
//            return -1;
//        }
//    }
    public long insertRec_G100(ContentValues rec)
    {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_G100, null, rec);
        db.close();
        return rowID;
    }
    public long insertRec_G200(ContentValues rec)
    {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_G200, null, rec);
        db.close();
        return rowID;
    }
//    public long insertRec_G300(ContentValues rec)
//    {
//        SQLiteDatabase db = getWritableDatabase();
//        long rowID = db.insert(DB_TABLE_G300, null, rec);
//        db.close();
//        return rowID;
//    }





    //***********Club測試****************
    public int clearRec_C100() {// 刪除社團SQLite資料

//        //------------------------------------------------
        database = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_C100;//社團
        Cursor recSet = database.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = database.delete(DB_TABLE_C100, "1", null); //
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
    public int clearRec_C200() {// 刪除社團人員明細SQLite資料
        database = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_C200;//社團人員明細
        Cursor recSet = database.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = database.delete(DB_TABLE_C200, "1", null); //
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
    public long insertRec_B100(ContentValues rec) {//紀錄
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_B100, null, rec);
        db.close();
        return rowID;
    }
    //    ContentValues values
    public long insertRec_C100(ContentValues rec) {//社團
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_C100, null, rec);
        db.close();
        return rowID;
    }




    //    ContentValues values
    public long insertRec_C200(ContentValues rec) {//社團人員明細
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_C200, null, rec);
        db.close();
        return rowID;
    }

    public ArrayList<String> getRecSet_C100() {//將每列資料用#串起來後傳回一個ArrayList
        database = getReadableDatabase();
//        String sql = "SELECT * FROM " + DB_TABLE;
        String sql="";
            sql = "SELECT * FROM  " + DB_TABLE_C100 ;//因為mysql已經篩選過才加入sqlite 所以直接抓sqlite中全部社團

        Cursor recSet = database.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();

        //----------------------------
        int columnCount = recSet.getColumnCount();
        if (recSet.getCount()!=0)
        {
            while (recSet.moveToNext())
            {
                String fldSet = "";
                for (int i = 0; i < columnCount; i++)
                    fldSet += recSet.getString(i) + "#";
                recAry.add(fldSet);
            }
        }
        //------------------------
        recSet.close();
        database.close();

        return recAry;
    }
    public String getRecSet_C106(String joinid) {//團長ID//將每列資料用#串起來後傳回一個ArrayList
        database = getReadableDatabase();
        String sql="";
        sql = "SELECT C106 FROM  " + DB_TABLE_C100 +" WHERE id="+joinid;//因為mysql已經篩選過才加入sqlite 所以直接抓sqlite中全部社團

        Cursor recSet = database.rawQuery(sql, null);
        String fldSet = "";

        //----------------------------
        int columnCount = recSet.getColumnCount();
        if (recSet.getCount()!=0)
        {
            while (recSet.moveToNext())
            {

                for (int i = 0; i < columnCount; i++)
                    fldSet += recSet.getString(i) ;
            }
        }
        //------------------------
        recSet.close();
        database.close();

        return fldSet;
    }
    public String getRecSet_B104_total(String userid){
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

        return mileage_total+"";//成就進度
    }
    //***********抓取G200成員資料
    public ArrayList<String> getRecSet_G202(String joinid) {//將每列資料用#串起來後傳回一個ArrayList
        database = getReadableDatabase();
        String sql="";
        sql = "SELECT G202 FROM  " + DB_TABLE_G200+" WHERE G203='"+joinid+"'" ;

        Cursor recSet = database.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();

        //----------------------------
        int columnCount = recSet.getColumnCount();
        if (recSet.getCount()!=0)
        {
            while (recSet.moveToNext())
            {
                String fldSet = "";
                for (int i = 0; i < columnCount; i++)
                    fldSet += recSet.getString(i) + "#";
                recAry.add(fldSet);
            }
        }
        //------------------------
        recSet.close();
        database.close();

        return recAry;
    }

    //******************************
    public ArrayList<String> getRecSet_A100(int u_id) {//將每列資料用#串起來後傳回一個ArrayList
        database = getReadableDatabase();
//        String sql = "SELECT * FROM " + DB_TABLE;
        String sql="";
        sql = "SELECT * FROM  A100 WHERE id="+u_id ;

        Cursor recSet = database.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();

        //----------------------------
        int columnCount = recSet.getColumnCount();
        if (recSet.getCount()!=0)
        {
            while (recSet.moveToNext())
            {
                String fldSet = "";
                for (int i = 0; i < columnCount; i++)
                    fldSet += recSet.getString(i) + "#";
                recAry.add(fldSet);
            }
        }
        //------------------------
        recSet.close();
        database.close();

        return recAry;
    }

}
