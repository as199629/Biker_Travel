package com.bikerlifeYang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class FriendDbHelper20 extends SQLiteOpenHelper {
    public String sCreateTableCommand;
    String TAG = "tcnr20=>";
    private static final String DB_FILE = "bikerlife.db";// 資料庫名稱
    //    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static int VERSION ;
    ;    // 資料表名稱
    private static final String DB_TABLE = "D100";    // 資料庫物件，固定的欄位變數
    //    //資料庫Table
    private static final String crTBsqlplan =  "CREATE TABLE " + DB_TABLE + " ( "
            + "id INTEGER PRIMARY KEY,"
            + "D101 TEXT,"
            + "D102 date NOT NULL,"
            + "D103 TEXT NOT NULL,"
            + "D104 TEXT NOT NULL);";
    private static SQLiteDatabase database;
    //----------------------------------------------
    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context,int version) {
        if (database == null || !database.isOpen()) {
            database = new FriendDbHelper20(context, DB_FILE, null, version)
                    .getWritableDatabase();
        }
        return database;
    }

    public FriendDbHelper20(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
        super(context, "bikerlife.db", null, version);
        sCreateTableCommand = "";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(crTBsqlplan); //自己要測試時再打開
    }

    @Override
    public void onOpen(SQLiteDatabase database) {
        super.onOpen(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade()");
        database.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(database);
    }


    //新增資料===================
    public long insertRec(String p_name, String p_date, String p_place) {
        database = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("D104", p_name);
        rec.put("D102", p_date);
        rec.put("D103", p_place);
        long rowID = database.insert(DB_TABLE, null, rec);

        database.close();
        return rowID;
    }
    //刪除資料====================
//    public int deleteRec(String tID) {
//        database = getWritableDatabase();
//        String sql = "SELECT * FROM " + DB_TABLE;
//        Cursor recSet = database.rawQuery(sql, null);
////        //-----------
////        Cursor c1=db.execSQL("");
////        Cursor c2=db.rawQuery();
////        Cursor c3=db.insert();
////        Cursor c4=db.update(, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , );
////        Cursor c5=db.delete();
////        //------------------------------------------------
//        if (recSet.getCount() != 0) {
////			String whereClause = "id < 0";
//            String whereClause="plan_name=  '" + tID +  "' ";
//            int rowsAffected = database.delete(DB_TABLE,whereClause,  null); //
//            // From the documentation of SQLiteDatabase delete method:
//            // To remove all rows and get a count pass "1" as the whereClause.
//            database.close();
//            return rowsAffected;
//        } else {
//            database.close();
//            return -1;
//        }
//
//    }
    //資料筆數===================
    public int RecCount() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE;
        Cursor recSet = db.rawQuery(sql, null);
        return recSet.getCount();
    }
    //==編輯資料==================================
    public int updateRec(String tID, String tname, String tgrp, String taddress) {
        database = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE;
        Cursor recSet = database.rawQuery(sql, null);
        if(recSet.getCount()!=0){
            ContentValues rec=new ContentValues();
            rec.put("plan_name",tname);
            rec.put("plan_date",tgrp);
            rec.put("plan_place",taddress);
            String whereClause="id=  '" + tID +  "' ";

            int rowsAffected = database.update(DB_TABLE, rec, whereClause,null);
            database.close();
            return rowsAffected; //回傳資料
        }else {
            database.close();
            return -1;
        }
    }
    //取得SQL資料 確認是否已有資料========================================
    public String FindRec(String U_ID) {
        database = getReadableDatabase();
//        String sql = "SELECT * FROM " + DB_TABLE + " WHERE name LIKE ? ORDER BY id ASC";
//        String[] args = {"%" +U_ID + "%"};
        String sql="SELECT * FROM "+DB_TABLE  +" WHERE D101= '"+U_ID+"'";
//        ArrayList<String> recAry = new ArrayList<String>();
        Cursor recSet = database.rawQuery(sql, null);

        int columnCount = recSet.getColumnCount();
        String fldSet = null;

        if (recSet.getCount() != 0) {
            recSet.moveToFirst();
            fldSet = recSet.getString(0) + "\n";
            while (recSet.moveToNext()) {
                for (int i = 1; i < columnCount; i++)
                    fldSet += recSet.getString(i) + "\n";
            }
        }
        recSet.close();
        database.close();
        return fldSet;
    }

    public ArrayList<String> getRecSet() {
        database = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE;
        Cursor recSet = database.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();

        //----------------------------
        Log.d(TAG, "recSet=" + recSet);
        int columnCount = recSet.getColumnCount();

        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        database.close();

        Log.d(TAG, "recAry=" + recAry);
        return recAry;
    }

    //清除內容
    public int clearRec() {
        database= getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE;
        Cursor recSet = database.rawQuery(sql, null);
//        //-----------
//        Cursor c1=db.execSQL("");
//        Cursor c2=db.rawQuery();
//        Cursor c3=db.insert();
//        Cursor c4=db.update(, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , );
//        Cursor c5=db.delete();
//        //------------------------------------------------
        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = database.delete(DB_TABLE, "1", null); //
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            database.close();
            recSet.close();
            return rowsAffected;
        } else {
            recSet.close();
            database.close();
            return -1;
        }
    }
    //    ContentValues values
    public long insertRec_m(ContentValues rec) {
        database = getWritableDatabase();
        long rowID = database.insert(DB_TABLE, null, rec);
        database.close();
        return rowID;
    }

}
