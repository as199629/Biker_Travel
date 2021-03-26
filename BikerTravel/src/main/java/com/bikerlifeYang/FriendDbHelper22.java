package com.bikerlifeYang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class FriendDbHelper22 extends SQLiteOpenHelper {
    String TAG = "tcnr22=>";
    public String sCreateTableCommand;    // 資料庫名稱
    private static final String DB_FILE = "bikerlife.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 2;    // 資料表名稱
    private static final String DB_TABLE22 = "E100"; // 資料庫物件，固定的欄位變數
    private static final String crTBsqlMap22 = "CREATE TABLE " + DB_TABLE22 + "  (id INTEGER PRIMARY KEY, E101 TEXT ,E102 TEXT,E103 TEXT,E104 TEXT,E105 TEXT,E106 TEXT,E107 TEXT);";

    private static SQLiteDatabase database;

    //----------------------------------------------
    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new FriendDbHelper22(context, DB_FILE, null, VERSION)
                    .getWritableDatabase();
        }
        return database;
    }

    public FriendDbHelper22(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
        super(context, "bikerlife.db", null, version);
        sCreateTableCommand = "";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(crTBsqlMap22);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade()");

        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE22);

        onCreate(db);
    }


    public int RecCount() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE22;
        Cursor recSet = db.rawQuery(sql, null);
        return recSet.getCount();
    }

    public String FindRec(String tname) {
        SQLiteDatabase db = getReadableDatabase();
        String fldSet = null;
        String sql = "SELECT * FROM " + DB_TABLE22 +
                " WHERE name LIKE ? ORDER BY id ASC";
        String[] args = {"%" + tname + "%"};

        Cursor recSet = db.rawQuery(sql, args);

        int columnCount = recSet.getColumnCount();

        if (recSet.getCount() != 0) {
            recSet.moveToFirst();
            fldSet = recSet.getString(0) + "\n";
            while (recSet.moveToNext()) {
                for (int i = 1; i < columnCount; i++)
                    fldSet += recSet.getString(i) + "\n";
            }
        }
        recSet.close();
        db.close();
        return fldSet;
    }

    //---------------------------------------------------------------------------------------20210115
    public ArrayList<String> getRecSet() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE22;
        Cursor recSet = db.rawQuery(sql, null);
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
        db.close();

        Log.d(TAG, "recAry=" + recAry);
        return recAry;
    }

    //輸入資料庫
    public void insertRec(String addressName, String addressName_goal, double latitude, double longitude, double latitude_goal, double longitude_goal) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues newRow = new ContentValues(); //SQLite 一筆資料
        //newRow.put("E101", userID);  //UserID
        //綠字為欄位名稱 前key 後value
        newRow.put("E102", addressName);  //出發點
        newRow.put("E103", addressName_goal);  //目的地
        newRow.put("E104", longitude);  //出發點經度
        newRow.put("E105", latitude);  //出發點緯度
        newRow.put("E106", longitude_goal);  //終點經度
        newRow.put("E107", latitude_goal);  //終點緯度
        db.insert(DB_TABLE22, null, newRow);  //呼叫SQLite
        db.close();
    }

    //刪除/清空資料表資料
    //public int deleteRec(String b_id) {
    public int deleteRec() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE22;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
            //String whereClause="id='" + b_id + "'";
            //int rowsAffected=db.delete(DB_TABLE22,whereClause,null);  // rowsAffected更新幾筆 whereClause第幾筆
            int rowsAffected = db.delete(DB_TABLE22, null, null);  // rowsAffected更新幾筆 whereClause第幾筆
            db.close();
            recSet.close();
            return rowsAffected;
        } else {
            db.close();
            recSet.close();
            return -1;  //代表沒有資料
        }
    }
    public void createTB() {
        // 批次新增
        int maxrec = 20;
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < maxrec; i++) {
            ContentValues newRow = new ContentValues();
            newRow.put("name", "路人" + u_chinayear(i));
            //newRow.put("grp", "第" + u_chinano((int) (Math.random() * 4 + 1)) + "組");
            newRow.put("grp", "第" + u_chinano(i) + "組");
            newRow.put("address", "台中市西區工業一路" + (100 + i) + "號");
            db.insert(DB_TABLE22, null, newRow);
        }
        db.close();
    }

    private String u_chinano(int input_i) {
        String c_number = "";
        String china_no[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        c_number = china_no[input_i % 10];

        return c_number;
    }

    private String u_chinayear(int input_i) {
        String c_number = "";
        String china_no[] = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
        c_number = china_no[input_i % 10];

        return c_number;
    }

    public int clearRec() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE22;
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
            int rowsAffected = db.delete(DB_TABLE22, "1", null); //
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            db.close();
            return rowsAffected;
        } else {
            db.close();
            return -1;
        }
    }

    public int updateRec(String b_id, String b_name, String b_grp, String b_address) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE22;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
            ContentValues rec = new ContentValues();
            rec.put("name", b_name);
            rec.put("grp", b_grp);
            rec.put("address", b_address);
            String whereClause = "id='" + b_id + "'";

            int rowsAffected = db.update(DB_TABLE22, rec, whereClause, null);  // rowsAffected更新幾筆 whereClause第幾筆

            db.close();
            recSet.close();
            return rowsAffected;
        } else {
            db.close();
            recSet.close();
            return -1;  //代表沒有資料
        }
    }

    //0129新增S----------------------
    //    ContentValues values
    public long insertRec_m(ContentValues rec) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE22, null, rec);
        db.close();
        return rowID;
    }
    //0129新增O----------------------
    //---------------------------------------------------------------------------------------20210115
//    public long insertRec(String b_name, String b_grp, String b_address) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues rec = new ContentValues();
//        rec.put("name", b_name);
//        rec.put("grp", b_grp);
//        rec.put("address", b_address);
//        long rowID = db.insert(DB_TABLE22, null, rec);
//        db.close();
//        return rowID;
//    }

//    //輸入資料庫
//    private void u_dbadd() {
//
//        ContentValues newRow = new ContentValues(); //SQLite 一筆資料
//        //newRow.put("E101", userID);  //UserID
//        //綠字為欄位名稱 前key 後value
//        newRow.put("E102", location_start.getText().toString());  //出發點
//        newRow.put("E103", location_goal.getText().toString());  //目的地
//        newRow.put("E104", longitude);  //出發點經度
//        newRow.put("E105", latitude);  //出發點緯度
//        newRow.put("E106", longitude_goal);  //終點經度
//        newRow.put("E107", latitude_goal);  //終點緯度
//
//        mBikermapDb.insert(DB_TABLE22, null, newRow);  //呼叫SQLite
//    }
    //---------------------------------------------------------------------------------------20210115
    // ----------- 結束-----------------
}


//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//public class BikerMapDbHelper_22 extends SQLiteOpenHelper {
//    public String sCreateTableCommand;
//    String TAG = "tcnr22=>";
//
//    public BikerMapDbHelper_22(Context context,
//                               String name,
//                               SQLiteDatabase.CursorFactory factory,
//                               int version)
//    {
//        super(context, name, factory, version);
//        Log.d(TAG, "BikerMapDbHelper()");
//        sCreateTableCommand="";
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        if (sCreateTableCommand.isEmpty()){
//            return;
//        }
//
//        db.execSQL(sCreateTableCommand);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.d(TAG, "onUpgrade()");
//    }
//}
