package com.bikerlifeYang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/*** Created by vpoint88 on 2017/7/6.*/
////----------------------------------------------------------
//建構式參數說明：
//context
//可以操作資料庫的內容本文，一般可直接傳入Activity物件。
//name
//要操作資料庫名稱，如果資料庫不存在，會自動被建立出來並呼叫onCreate()方法。
//factory
//用來做深入查詢用，入門時用不到。
//version
//版本號碼。
////-----------------------
public class FriendDbHelper22m extends SQLiteOpenHelper {
    String TAG = "tcnr22=>";
    public String sCreateTableCommand;    // 資料庫名稱
    private static final String DB_FILE = "bikerlife.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 2;    // 資料表名稱
    private static final String DB_TABLE = "E200";    // 資料庫物件，固定的欄位變數
    private static final String crTBsql = "CREATE TABLE " + DB_TABLE + " ( "
            + "id INTEGER PRIMARY KEY," + "E201 TEXT," + "E202 TEXT,"
            + "E203 TEXT);";
    private static SQLiteDatabase database;

    //----------------------------------------------
    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new FriendDbHelper22m(context, DB_FILE, null, VERSION)
                    .getWritableDatabase();
        }
        return database;
    }

    public FriendDbHelper22m(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
        super(context, "bikerlife.db", null, VERSION);
        sCreateTableCommand = "";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(crTBsql);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade()");

        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

        onCreate(db);
    }

    public int RecCount() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE;
        Cursor recSet = db.rawQuery(sql, null);
        return recSet.getCount();
    }

    public long insertRec(String b_E201, String b_E202, String b_E203) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("E201", b_E201);
        rec.put("E202", b_E202);
        rec.put("E203", b_E203);
        long rowID = db.insert(DB_TABLE, null, rec);
        db.close();
        return rowID;
    }




    public String FindRec(String tE201) {
        SQLiteDatabase db = getReadableDatabase();
        String fldSet = "";
        String sql = "SELECT * FROM " + DB_TABLE +
                " WHERE E201 LIKE ? ORDER BY id ASC";
        String[] args = {"%" + tE201 + "%"};

        Cursor recSet = db.rawQuery(sql, args);

        int columnCount = recSet.getColumnCount();
        if (recSet.getCount() != 0) {
//            recSet.moveToFirst();
//            fldSet = recSet.getString(0) + "\n";
            while (recSet.moveToNext()) {
                for (int i = 0; i < columnCount; i++)
                    fldSet += recSet.getString(i) + "\n";
            }
        }
        recSet.close();
        db.close();
        return fldSet;
    }

    public ArrayList<String> getRecSet() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE;
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


    public void createTB() {
        // 批次新增
        int maxrec = 20;
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < maxrec; i++) {
            ContentValues newRow = new ContentValues();
            newRow.put("E201", "路人" + u_chinayear(i));
            newRow.put("E202", "第" + u_chinano((int) (Math.random() * 4 + 1)) + "組");
            newRow.put("E203", "台中市西區工業一路" + (100 + i) + "號");
            db.insert(DB_TABLE, null, newRow);
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
        String sql = "SELECT * FROM " + DB_TABLE;
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
            int rowsAffected = db.delete(DB_TABLE, "1", null); //
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

    public int updateRec(String b_id, String b_E201, String b_E202, String b_E203) { //暫存的變數會以b開頭
        SQLiteDatabase db=getWritableDatabase();
        String sql="SELECT * FROM "+ DB_TABLE;
        Cursor recSet=db.rawQuery(sql,null);
        if(recSet.getCount()!=0){
            ContentValues rec=new ContentValues();
            rec.put("E201",b_E201);
            rec.put("E202",b_E202);
            rec.put("E203",b_E203);
            String whereClause="id ='"+b_id+"'";
            int rowsAffected =db.update(DB_TABLE,rec,whereClause,null);
            db.close();
            recSet.close();
            return rowsAffected;
        }
        else{
            db.close();
            recSet.close();
            return -1;
        }
    }

    public int deleteRec(String b_id) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            String whereClause="id ='"+b_id+"'";
            int rowsAffected = db.delete(DB_TABLE, whereClause, null); //
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
        long rowID = db.insert(DB_TABLE, null, rec);
        db.close();
        return rowID;
    }

    public ArrayList<String> getRecSet_query(String tE201, String tE202, String tE203) { //萬用格式查詢
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + DB_TABLE +
                " WHERE E201 LIKE ? AND E202 LIKE ? AND E203 LIKE ? ORDER BY id ASC";
        String[] args = new String[]{"%" + tE201.toString() + "%",
                "%" + tE202.toString() + "%",
                "%" + tE203.toString() + "%"};

        Cursor recSet = db.rawQuery(sql, args);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
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
        return recAry;
    }
    // ----------- 結束-----------------
}
