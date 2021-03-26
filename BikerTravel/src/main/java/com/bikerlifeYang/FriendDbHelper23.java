package com.bikerlifeYang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class FriendDbHelper23 extends SQLiteOpenHelper {
    public String sCreateTableCommand;
    private static final String DB_FILE = "bikerlife.db";// 資料庫名稱
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public  static int DBversion = 3;
    private static final String DB_TABLE_I100 = "I100" ,DB_TABLE_B100 = "B100"; // 資料表名稱
    private static SQLiteDatabase database;
    // 資料庫物件，固定的欄位變數(名稱)
    private static final String crTBsql_I100 = "CREATE  TABLE   " + DB_TABLE_I100 + "   ( "
            + "id   INTEGER   PRIMARY KEY,"//流水水ID
            + "I101   TEXT,"//U_ID
            + "I102   FLOAT NOT NULL,"//計畫時間
            + "I103   FLOAT NOT NULL,"//預計騎乘里程
            + "I104   FLOAT NOT NULL,"//預計爬升高度
            + "I105   INTEGER NOT NULL,"//預計騎乘時間
            + "I106   TEXT,"//訓練結束日期
            + "I107   TEXT );"; //訓練開始日期

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context){
        if (database == null || !database.isOpen())  {
            database = new FriendDbHelper23(context, DB_FILE, null, DBversion)
                    .getWritableDatabase();
        }
        return database;
    }

    public FriendDbHelper23(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
//        super(context, name, factory, version);
        super(context, DB_FILE, null, version);
        sCreateTableCommand = "";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(crTBsql_I100);//db執行SQL(只有這兩行有用到)
    }

    @Override
    public void onOpen(SQLiteDatabase database) {
        super.onOpen(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS "+DB_TABLE_I100);
        onCreate(database);//再建立一組資料表(database)
    }

    public long insertRec(String user_id, float b_day, float  b_km, float  b_high, int b_totaltime ,
                          String b_endtime  , String b_starttime) {
        database = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("I101", user_id);//(資料表名稱,取得的變數)
        rec.put("I102", b_day);
        rec.put("I103", b_km);
        rec.put("I104", b_high);
        rec.put("I105", b_totaltime);
        rec.put("I106", b_endtime);
        rec.put("I107", b_starttime);
        long rowID = database.insert(DB_TABLE_I100, null, rec);
        database.close();

        return rowID;
     }

    //-----數資料庫有幾行------
    public int RecCount() {
        database = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_I100;
        Cursor recSet = database.rawQuery(sql, null);

        return recSet.getCount();
    }

    //--------------取I100訓練目標資料---------------------------
    public ArrayList<String> FindRec(String U_ID) {     //查找U_ID
        database=getReadableDatabase();
        String sql="SELECT * FROM "+DB_TABLE_I100  +" WHERE I101= '"+U_ID+"'";
        ArrayList<String> recAry = new ArrayList<String>();
        Cursor recSet = database.rawQuery(sql, null);

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
    //----------------------------------------------------

    //===================test取B100需要的資料=======================
    public ArrayList<Float> getRecord_B100(String U_ID,String start_time,String end_time) {//歷史紀錄
        database = getReadableDatabase();

//        String sql = "SELECT B103,B104,B105 FROM B100 WHERE B113||'-'||B114 >="+"'"+start_time+"'";
//        String sql = "SELECT B103,B104,B105 FROM B100 WHERE B113||'-'||B114 <="+"'"+end_time+"'";
        String sql = "SELECT B103,B104,B105 FROM B100 WHERE B101 ="+"'"+U_ID+"'"
                +"AND B113||'-'||B114 >="+"'"+start_time+"'"
                +"AND B113||'-'||B114 <="+"'"+end_time+"'";
//        String sql = "SELECT B103,B104,B105 FROM B100 " +
//                "WHERE B101='" + U_ID + "' AND  B113||'-'||B114 >=  ' " +start_time + " ' AND  B113||'-'||B114 <= ' " + end_time+" ' ";
        ArrayList<Float> mList = new ArrayList<Float>();//全部記錄

        Cursor cur_list = database.rawQuery(sql, null);
        if (cur_list.getCount() == 0) {
            mList=null;
            return mList;
        }else{
            float a = 0.0f; //設定累積里程初始值
            float b = 0.0f;  //設定爬升高度初始值
            float c = 0.0f; //設定騎乘時間初始值
            cur_list.moveToFirst();//移到最後一筆，最上面才會是最新的紀錄，一定要加，不然就不是完整列表***moveTo後面有五個位置
            a = cur_list.getFloat(0);
            b = cur_list.getFloat(1);
            c = cur_list.getFloat(2);

//            int aa=cur_list.getInt(0);
            while (cur_list.moveToNext())
            {
                a += cur_list.getFloat(0);
                b += cur_list.getFloat(1);
                c += cur_list.getFloat(2);
            }



            mList.add(a);//B103累加
            mList.add(b);//B104累加
            mList.add(c);//B105累加

            return mList;
        }
    }
//==================================================================

    //------------------刪除資料表-----------------------------
    public int clearRec() {
        database = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_I100;
        Cursor recSet = database.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = database.delete(DB_TABLE_I100, "1", null); //
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

    //將MYSQL資料匯入SQLITE
    public long insertRec_m(ContentValues rec) {
        database = getWritableDatabase();
        long rowID = database.insert(DB_TABLE_I100, null, rec);
        database.close();
        return rowID;
    }
    // ----------- 結束-----------------

    //------------刪除資料庫---------------
    public void delete() {
        database = getWritableDatabase();
        database.delete(DB_TABLE_I100,null,null);
    }
}