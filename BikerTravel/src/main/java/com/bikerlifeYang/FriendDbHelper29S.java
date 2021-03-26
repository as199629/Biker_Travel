package com.bikerlifeYang;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class FriendDbHelper29S extends SQLiteOpenHelper {
    public String sCreateTableCommand;    // 資料庫名稱
    private static final String DB_FILE = "bikerlife.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;    // 資料表名稱
    private static final String DB_TABLE_STOPWATH = " B100 ";    // 資料庫物件，固定的欄位變數
    private static final String DB_TABLE_STOPWATHB200 = " B200 ";
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
            + "B117 TEXT ,"//預留欄位
            + "B118 TEXT  );";//預留欄位


    private static SQLiteDatabase database;

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context){
        if (database == null || !database.isOpen())  {
            database = new FriendDbHelper29S(context, DB_FILE, null, VERSION)
                    .getWritableDatabase();
        }
        return database;
    }

    public FriendDbHelper29S(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
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
//        db.execSQL(crTBsql_stopwatch);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_STOPWATH);
        onCreate(db);
    }

    public Long insertRec(String g_id,int t_timing,int t_ridingtiming,float t_distance,float t_uphillSum,
                          float t_downhillSum,float t_altitudeUp,
                          float t_altitudeDown,float t_avgSpeed,
                          float t_fastestSpeed,String t_startTime,String t_endTime,
                          String t_year,String t_date)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("B201", g_id);
        rec.put("B202", t_timing);
        rec.put("B203", t_ridingtiming);
        rec.put("B204", t_distance);
        rec.put("B205", t_uphillSum);
        rec.put("B206", t_downhillSum);
        rec.put("B207", t_altitudeUp);
        rec.put("B208", t_altitudeDown);
        rec.put("B209", t_avgSpeed);
        rec.put("B210", t_fastestSpeed);
        rec.put("B211", t_startTime);
        rec.put("B212", t_endTime);
        rec.put("B213", t_year);
        rec.put("B214", t_date);
        rec.put("B215", "");
        rec.put("B216", "");
        rec.put("B217", "");


        long rowID = db.insert(DB_TABLE_STOPWATHB200, null, rec);
        db.close();
        return rowID;
    }

    public int RecCount() {
        //你選擇的tabel的資料 有幾筆
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_STOPWATH;
        Cursor recSet = db.rawQuery(sql, null);
        return recSet.getCount();
    }


    public int clearRec() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_STOPWATH;
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
            int rowsAffected = db.delete(DB_TABLE_STOPWATH, "1", null); //
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
    public long insertRec_m(ContentValues rec) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_STOPWATH, null, rec);
        db.close();
        return rowID;
    }



    //傳回B200最後一筆
    public ArrayList<String> getRecSetB100_Last() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_STOPWATH;
        Cursor recSet = db.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();

        //----------------------------
//        Log.d(TAG, "recSet=" + recSet);
        int columnCount = recSet.getColumnCount();
        recSet.moveToLast();

            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);

        //------------------------
        recSet.close();
        db.close();

//        Log.d(TAG, "recAry=" + recAry);
        return recAry;
    }

//    public String query_g_id()//查詢g_id
//    {
//        SQLiteDatabase db = getWritableDatabase();
////        String sql=" SELECT g_id FROM "+DB_TABLE +" WHERE g_id= "+ g_id;
//        String sql=" SELECT g_id FROM login WHERE g_id="+"'"+g_id+"'";
//
//        Cursor recSet = db.rawQuery(sql, null);
//
//        if (recSet.getCount()==0)//如果沒重複
//        {
//            return false;
//        }
//        else//如果有重複
//        {
//            return true;
//        }
//
//    }

}
