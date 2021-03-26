package com.bikerlifeYang;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class FriendDbHelper29h extends SQLiteOpenHelper {
    public String sCreateTableCommand;    // 資料庫名稱
    private static final String DB_FILE = "bikerlife.db";
    private static SQLiteDatabase database;
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;    // 資料表名稱

    //table 名稱
    // 資料庫物件，固定的欄位變數
    private static final String DB_TABLE_LOGIN = "A100";
    private static final String DB_TABLE_STOPWATH = " B100 ";
    private static final String DB_TABLE_STOPWATHB200 = " B200 ";
    private static final String DB_TABLE28 = "C100";
    private static final String DB_TABLE28_C200 = "C200";
    private static final String DB_TABLE20 = "D100";
    private static final String DB_TABLE22 = "E100";
    private static final String DB_TABLE22_E200 = "E200";
    private static final String DB_TABLE_F100 = "F100";//資料表名稱
    private static final String DB_TABLE_F200 = "F200";//資料表名稱
    private static final String DB_TABLE_F300 = "F300";//資料表名稱
    private static final String DB_TABLE_F400 = "F400";//資料表名稱
    private static final String DB_TABLE_G100 = "G100";//資料表名稱
    private static final String DB_TABLE_G200 = "G200";//資料表名稱
    private static final String DB_TABLE_HOME = "H100";
    private static final String DB_TABLE_I100 = "I100";


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


    private static final String crTBsql_home = " CREATE     TABLE   " + DB_TABLE_HOME + "   ( "
            + "id    INTEGER   PRIMARY KEY,"
            + "H101 TEXT NOT NULL,"
            + "H102 TEXT,"
            + "H103 TEXT,"
            + "H104 TEXT);";
    //登入畫面
    private static final String crTBsql_login = " CREATE     TABLE   " + DB_TABLE_LOGIN + "   ( "
            + "id    INTEGER   PRIMARY KEY,"
            + "A101 TEXT NOT NULL,"//g_id
            + "A102 TEXT,"//g_name
            + "A103 TEXT,"//g_email
            + "A104 TEXT,"//g_imgUrl
            + "A105 TEXT,"//登入狀態
            + "A106 TEXT,"//
            + "A107 TEXT,"//
            + "A108 TEXT);";//
    //碼表
    private static final String crTBsql_stopwatch = " CREATE     TABLE   " + DB_TABLE_STOPWATH + "   ( "
            + "id    INTEGER   PRIMARY KEY,"
            + "B101 TEXT ,"//google的id g_id
            + "B102 INTEGER ,"//紀錄時間 t_s_timing
            + "B103 INTEGER ,"//騎乘時間 t_s_ridingtiming
            + "B104 FLOAT ,"//距離 t_s_distance
            + "B105 FLOAT ,"//爬升距離 t_s_uphillSum
            + "B106 FLOAT ,"//下坡距離 t_s_downhillSum
            + "B107 FLOAT ,"//最高海拔 t_s_altitudeUp
            + "B108 FLOAT ,"//最低海拔t_s_altitudeDown
            + "B109 FLOAT ,"//平均速度 t_s_avgSpeed
            + "B110 FLOAT ,"//最快速度 t_s_fastestSpeed
            + "B111 TEXT ,"//開始時間 t_s_startTime
            + "B112 TEXT ,"//結束時間 t_s_endTime
            + "B113 TEXT ,"//年 t_s_year
            + "B114 TEXT ,"//月日 t_s_date
            + "B115 TEXT ,"//預留欄位
            + "B116 TEXT ,"//預留欄位
            + "B117 TEXT  );";//預留欄位

    private static final String crTBsql_stopwatchB200 = " CREATE     TABLE   " + DB_TABLE_STOPWATHB200 + "   ( "
            + "id    INTEGER   PRIMARY KEY,"
            + "B201 TEXT ,"//google的id g_id
            + "B202 INTEGER ,"//紀錄時間 t_s_timing
            + "B203 INTEGER ,"//騎乘時間 t_s_ridingtiming
            + "B204 FLOAT ,"//距離 t_s_distance
            + "B205 FLOAT ,"//爬升距離 t_s_uphillSum
            + "B206 FLOAT ,"//下坡距離 t_s_downhillSum
            + "B207 FLOAT ,"//最高海拔 t_s_altitudeUp
            + "B208 FLOAT ,"//最低海拔t_s_altitudeDown
            + "B209 FLOAT ,"//平均速度 t_s_avgSpeed
            + "B210 FLOAT ,"//最快速度 t_s_fastestSpeed
            + "B211 TEXT ,"//開始時間 t_s_startTime
            + "B212 TEXT ,"//結束時間 t_s_endTime
            + "B213 TEXT ,"//年 t_s_year
            + "B214 TEXT ,"//月日 t_s_date
            + "B215 TEXT ,"//預留欄位
            + "B216 TEXT ,"//預留欄位
            + "B217 TEXT  );";//預留欄位

    //--------------------Train------------------------------

    private static final String crTBsql_I100 = "CREATE  TABLE   " + DB_TABLE_I100 + "   ( "
            + "id   INTEGER   PRIMARY KEY,"//流水水ID
            + "I101   TEXT,"//U_ID
            + "I102   FLOAT NOT NULL,"//計畫時間
            + "I103   FLOAT NOT NULL,"//預計騎乘里程
            + "I104   FLOAT NOT NULL,"//預計爬升高度
            + "I105   INTEGER NOT NULL,"//預計騎乘時間
            + "I106   TEXT,"//訓練結束日期
            + "I107   TEXT );"; //訓練開始日期
    //----------------------map

    private static final String crTBsqlMap22 ="CREATE TABLE " + DB_TABLE22 + "  (id INTEGER PRIMARY KEY, E101 TEXT ,E102 TEXT,E103 TEXT,E104 TEXT,E105 TEXT,E106 TEXT,E107 TEXT);";
    //----------------------society
    //society

    private static final String crTBsql28 = "CREATE     TABLE   " + DB_TABLE28 + "   ( "
            + "id    INTEGER   PRIMARY KEY,"
            + "C101 TEXT NOT NULL,"//社團名稱
            + "C102 INTEGER ,"//目前人數
            + "C103 INTEGER,"//人數上限
            + "C104 TEXT,"//所屬縣市
            + "C105 TEXT,"//所屬鄉鎮市區
            + "C106 TEXT,"//團長UID
            + "C107 TEXT,"//建立時間
            + "C108 TEXT,"//備用
            + "C109 TEXT);";//備用
    //society


    private static final String crTBsql28_1= " CREATE     TABLE   " + DB_TABLE28_C200 + "   ( "
            + "C201    INTEGER,"//社團UID
            + "C202    TEXT  ,"//會員UID
            + "C203    TEXT  ," //會員狀態
            + "C204    TEXT  ,"//備用
            + "C205    TEXT  ," //備用
            + "PRIMARY KEY ( C201, C202));";//有兩個以上的PRIMARY KEY要這樣寫
    //---------------------plan

    private static final String crTBsqlplan =  "CREATE TABLE " + DB_TABLE20 + " ( "
            + "id INTEGER PRIMARY KEY,"
            + "D101 TEXT,"
            + "D102 date NOT NULL,"
            + "D103 TEXT NOT NULL,"
            + "D104 TEXT NOT NULL);";


    //--------------------琣黃成就


    //join
    private static final String joinTBsql = "CREATE     TABLE   " + DB_TABLE_G100 + "   ( "
            + "G101    INTEGER   PRIMARY KEY," + "G102 TEXT NOT NULL," + "G103 TEXT NOT NULL,"
            + "G104 TEXT NOT NULL,"+"G105 TEXT NOT NULL,"+"G106 TEXT NOT NULL,"+"G107 TEXT NOT NULL,"
            +"G108 INTEGER NOT NULL ,"+"G109 INTEGER NOT NULL ,"+"G110 TEXT NOT NULL ,"+"G111 TEXT NOT NULL ,"+"G112 INTEGER NOT NULL"+");";
    private static final String joinTBsql2 = "CREATE     TABLE   " + DB_TABLE_G200 + "( "
            + "G201    INTEGER   PRIMARY KEY," + "G202 INTEGER NOT NULL," + "G203 INTEGER NOT NULL,"
            + "G204 TEXT NOT NULL,"+"G205 INTEGER NOT NULL,"+"G206 TEXT NOT NULL"+");";

    private static final String crTBsqlMap22_E200 ="CREATE TABLE " + DB_TABLE22_E200 + " (id INTEGER PRIMARY KEY, E201 TEXT ,E202 TEXT,E203 TEXT);";
    //society



    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context){
        if (database == null || !database.isOpen())  {
            database = new FriendDbHelper29h(context, DB_FILE, null, VERSION)
                    .getWritableDatabase();
        }
        return database;
    }

    public FriendDbHelper29h(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
//        super(context, name, factory, version);
        super(context, name, null, version);
        sCreateTableCommand = "";
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(rwTBsql);//建立一資料表
        db.execSQL(rwTBsql2);//建立一資料表
        db.execSQL(rwTBsql3);//建立一資料表
        db.execSQL(rwTBsql4);//建立一資料表
        db.execSQL(crTBsql_stopwatchB200);
        db.execSQL(crTBsql_home);
        db.execSQL(crTBsql_login);
        db.execSQL(crTBsql_stopwatch);
        db.execSQL(crTBsql_I100);
        db.execSQL(crTBsqlMap22);
        db.execSQL(crTBsql28);
        db.execSQL(crTBsqlplan);
        db.execSQL(joinTBsql);
        db.execSQL(crTBsqlMap22_E200);
        db.execSQL(crTBsql28_1);
        db.execSQL(joinTBsql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_LOGIN);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_STOPWATH);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_STOPWATHB200);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE28);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE28_C200);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE20);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE22);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE22_E200);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_F100);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_F200);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_F300);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_F400);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_G100);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_HOME);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_I100);
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_G200);

        onCreate(db);
    }

    public Long insertRec(String b_name,String b_ctiy, String b_length, String b_describe)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("H101", b_name);
        rec.put("H102", b_ctiy);
        rec.put("H103", b_length);
        rec.put("H104", b_describe);
        long rowID = db.insert(DB_TABLE_HOME, null, rec);
        db.close();
        return rowID;


    }

    public int RecCount() {
        //你選擇的tabel的資料 有幾筆
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_HOME;
        Cursor recSet = db.rawQuery(sql, null);

        int a=recSet.getCount();
        recSet.close();
        db.close();
        return a;
    }
//查詢專用不關閉資料庫
    public int RecCountTEST() {
        //你選擇的tabel的資料 有幾筆
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_HOME;
        Cursor recSet = db.rawQuery(sql, null);
        int a=recSet.getCount();

        return recSet.getCount();
    }
    public boolean selete_login_mode()
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql=" SELECT * FROM "+DB_TABLE_LOGIN;
        boolean fldSet = false;
        Cursor recSet = db.rawQuery(sql, null);
        int columnCount = recSet.getColumnCount();
        if (recSet.getCount() != 0) {
            fldSet=true;
        }
        recSet.close();
        db.close();
        return fldSet;

    }
    public ArrayList<String> getRecSet_list() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_HOME;
        Cursor recSet = db.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();

        //----------------------------
//        Log.d(TAG, "recSet=" + recSet);
        int columnCount = recSet.getColumnCount();

        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();

//        Log.d(TAG, "recAry=" + recAry);
        return recAry;
    }

    public int clearRec() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_HOME;
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
            int rowsAffected = db.delete(DB_TABLE_HOME, "1", null); //
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

    //    ContentValues values
    public long insertRec_A100(ContentValues rec) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_LOGIN, null, rec);
        db.close();
        return rowID;
    }
}
