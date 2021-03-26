package com.bikerlifeYang;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FriendDbHelper29G extends SQLiteOpenHelper {
    public String sCreateTableCommand;    // 資料庫名稱
    private static final String DB_FILE = "bikerlife.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;    // 資料表名稱
    private static final String DB_TABLE_LOGIN = "A100";    // 資料庫物件，固定的欄位變數


    //登入畫面
    private static final String crTBsql_login = " CREATE     TABLE   " + DB_TABLE_LOGIN + "   ( "
            + "id    INTEGER   PRIMARY KEY,"
            + "A101 TEXT NOT NULL,"//g_id
            + "A102 TEXT,"//g_name
            + "A103 TEXT,"//g_email
            + "A104 TEXT,"//g_imgUrl
            + "A105 TEXT);";//g_logining
    private static SQLiteDatabase database;

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context){
        if (database == null || !database.isOpen())  {
            database = new FriendDbHelper29G(context, DB_FILE, null, VERSION)
                    .getWritableDatabase();
        }
        return database;
    }

    public FriendDbHelper29G(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
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
//        db.execSQL(crTBsql_login);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(" DROP      TABLE     IF     EXISTS    " +DB_TABLE_LOGIN);
        onCreate(db);
    }

    public Long insertRec(String b_id,String b_name, String b_email, String b_imgUrl)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("A101", b_id);
        rec.put("A102", b_name);
        rec.put("A103", b_email);
        rec.put("A104", b_imgUrl);
        long rowID = db.insert(DB_TABLE_LOGIN, null, rec);
        db.close();
        return rowID;
    }

    public int RecCount() {
        //你選擇的tabel的資料 有幾筆
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_LOGIN;
        Cursor recSet = db.rawQuery(sql, null);
        return recSet.getCount();
    }
    public boolean SELECTg_id(String g_id)
    {
        SQLiteDatabase db = getWritableDatabase();
//        String sql=" SELECT A101 FROM A100 WHERE g_id="+"'"+g_id+"'";
        String sql=" SELECT A101 FROM "+DB_TABLE_LOGIN +" WHERE A101="+"'"+g_id+"'";


        Cursor recSet = db.rawQuery(sql, null);

        if (recSet.getCount()==0)//如果沒重複
        {
            return false;
        }
        else//如果有重複
        {
            return true;
        }

    }

//    //備用 陣列
//    public String[] get_gid(String b_id)
//    {
//        SQLiteDatabase db = getWritableDatabase();
//        String sql=" SELECT id,A101 ,A104 FROM A100 WHERE A101="+"'"+b_id+"'";
//        Cursor recSet = db.rawQuery(sql, null);
//
//        recSet.moveToFirst();
//
//        String a[]=new String[3];
//        if(recSet.getCount()==1)//如果有重複
//        {
//            a[0]=recSet.getString(0);//抓ID
//            a[1]=recSet.getString(1);//抓googleid
//            a[2]=recSet.getString(2);//抓圖片
//
//        }
//        return a;
//
//    }
    public String get_gid(String b_id)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql=" SELECT id,A101 ,A104 FROM A100 WHERE A101="+"'"+b_id+"'";
        Cursor recSet = db.rawQuery(sql, null);

        recSet.moveToFirst();

        String id="";
        if(recSet.getCount()==1)//如果有重複
        {
            id=recSet.getString(0);//抓ID
//            a[1]=recSet.getString(1);//抓googleid
//            a[2]=recSet.getString(2);//抓圖片

        }
        return id;

    }
    public int clearRec() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_LOGIN;
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
            int rowsAffected = db.delete(DB_TABLE_LOGIN, "1", null); //
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
        long rowID = db.insert(DB_TABLE_LOGIN, null, rec);
        db.close();
        return rowID;
    }

}