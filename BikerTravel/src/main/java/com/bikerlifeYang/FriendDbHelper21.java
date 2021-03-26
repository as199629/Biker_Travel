package com.bikerlifeYang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class FriendDbHelper21 extends SQLiteOpenHelper {

    public String sCreateTableCommand;    // 資料庫名稱
    private static final String DB_FILE = "bikerlife.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 2;
    //(講義1405-FriendDbHelper.java)增加table的步驟:1.增加DB_TABLE(1.2.3....)
//2.增加crTBsql(1.2.3...)1.2.3的順序要對應步驟1.
//3.onCreate(SQLiteDatabase db)裡面加上步驟2.增加的crTBsql(1.2.3...)
    // 資料表名稱
    private static final String DB_TABLE = "C100";    // 社團資料庫物件，固定的欄位變數
    private static final String DB_TABLE1 = "B100";    // 紀錄資料庫物件，固定的欄位變數
    private static final String DB_TABLE2 = "C200";    // 社團人員明細資料庫物件，固定的欄位變數
    private static final String DB_TABLE3 = "A100";    // 登入資料庫物件，固定的欄位變數

    private static final String crTBsql = "CREATE     TABLE   " + DB_TABLE + "   ( "
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

    private static final String crTBsql2= " CREATE     TABLE   " + DB_TABLE2 + "   ( "
            + "C201    INTEGER,"//社團UID
            + "C202    TEXT  ,"//會員UID
            + "C203    TEXT  ," //會員狀態
            + "C204    TEXT  ,"
            + "C205    TEXT  ,"
            + "PRIMARY KEY ( C201, C202));";//有兩個以上的PRIMARY KEY要這樣寫
    private static SQLiteDatabase database;


    public FriendDbHelper21(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
        super(context, name, null, version);
        sCreateTableCommand = "";
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(crTBsql);
        db.execSQL(crTBsql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP    TABLE    IF     EXISTS    " + DB_TABLE);//有資料表就刪掉
        onCreate(db);//然後去生一個表
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        super.onDowngrade(db, oldVersion, newVersion);
        db.execSQL("DROP    TABLE    IF     EXISTS    " + DB_TABLE);//有資料表就刪掉
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }


    public int clearRec() {// 刪除社團SQLite資料
        //        //-----------
//        Cursor c1=db.execSQL("");
//        Cursor c2=db.rawQuery();
//        Cursor c3=db.insert();
//        Cursor c4=db.update(, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , );
//        Cursor c5=db.delete();
//        //------------------------------------------------
        database = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE;//社團
        Cursor recSet = database.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = database.delete(DB_TABLE, "1", null); //
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
        //        //-----------
//        Cursor c1=db.execSQL("");
//        Cursor c2=db.rawQuery();
//        Cursor c3=db.insert();
//        Cursor c4=db.update(, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , );
//        Cursor c5=db.delete();
//        //------------------------------------------------
        database = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE2;//社團人員明細
        Cursor recSet = database.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = database.delete(DB_TABLE2, "1", null); //
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
    public long insertRec_m(ContentValues rec) {//社團
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE, null, rec);
        db.close();
        return rowID;
    }


    //    ContentValues values
    public long insertRec_record(ContentValues rec) {//紀錄
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE1, null, rec);
        db.close();
        return rowID;
    }

    //    ContentValues values
    public long insertRec_C200(ContentValues rec) {//社團人員明細
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE2, null, rec);
        db.close();
        return rowID;
    }

    public ArrayList<String> getRecSet(int type,int id) {//將每列資料用#串起來後傳回一個ArrayList
        database = getReadableDatabase();
//        String sql = "SELECT * FROM " + DB_TABLE;
        String sql="";
        if(type==1){ //未加入
            sql = "SELECT * FROM  " + DB_TABLE+" WHERE id NOT IN (SELECT C201 FROM C200 WHERE C202="+id+" )";//暫時寫死2
        }
        else if( type==2) {//已加入(含申請中)
            sql = "SELECT * FROM  " + DB_TABLE + " WHERE id IN (SELECT C201 FROM C200 WHERE C202="+id+" )";//暫時寫死2
        }
        else if( type==3) {//純申請中
            sql = "SELECT * FROM  " + DB_TABLE + " WHERE id IN (SELECT C201 FROM C200 WHERE C202="+id+"  AND C203='0')";//C202暫時寫死2
        }
        Cursor recSet = database.rawQuery(sql, null);
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
        database.close();

        return recAry;
    }
}

