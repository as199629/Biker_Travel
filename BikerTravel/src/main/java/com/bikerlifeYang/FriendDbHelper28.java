package com.bikerlifeYang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendDbHelper28 extends SQLiteOpenHelper {

    public String sCreateTableCommand;    // 資料庫名稱
    private static final String DB_FILE = "bikerlife.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
//    public static int VERSION = 1;
    //(講義1405-FriendDbHelper.java)增加table的步驟:1.增加DB_TABLE(1.2.3....)
//2.增加crTBsql(1.2.3...)1.2.3的順序要對應步驟1.
//3.onCreate(SQLiteDatabase db)裡面加上步驟2.增加的crTBsql(1.2.3...)
    // 資料表名稱
    private static final String DB_TABLE = "C100";    // 社團資料庫物件，固定的欄位變數
    private static final String DB_TABLE1 = "B100";    // 紀錄資料庫物件，固定的欄位變數
    private static final String DB_TABLE2 = "C200";    // 社團人員明細資料庫物件，固定的欄位變數
    private static final String DB_TABLE3 = "A100";    // 登入資料庫物件，固定的欄位變數
    private static final String DB_TABLE_G100 = "G100";//揪團資料表名稱

    private static final String crTBsql = "CREATE     TABLE   " + DB_TABLE + "   ( "
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

    private static final String crTBsql1= " CREATE     TABLE   " + DB_TABLE1 + "   ( "
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
            + "B115 INTEGER ,"//備用
            + "B116 INTEGER ,"//備用
            + "B117 INTEGER  );";//備用

    private static final String crTBsql2= " CREATE     TABLE   " + DB_TABLE2 + "   ( "
            + "C201    INTEGER,"//社團UID
            + "C202    TEXT  ,"//會員UID
            + "C203    TEXT  ," //會員狀態
            + "C204    TEXT  ,"//備用
            + "C205    TEXT  ," //備用
            + "PRIMARY KEY ( C201, C202));";//有兩個以上的PRIMARY KEY要這樣寫

    private static final String crTBsql3  = " CREATE     TABLE   " + DB_TABLE3 + "   ( "
            + "id    INTEGER   PRIMARY KEY,"
            + "A101 TEXT NOT NULL,"//g_id
            + "A102 TEXT,"//g_name
            + "A103 TEXT,"//g_email
            + "A104 TEXT,"//g_imgUrl
            + "A105 TEXT);";//g_logining

    private static SQLiteDatabase database;

    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context,int version) {
        if (database == null || !database.isOpen()) {
            database = new FriendDbHelper28(context, DB_FILE, null, version)
                    .getWritableDatabase();
        }
        return database;
    }

    public FriendDbHelper28(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
        super(context, name, null, version);
        sCreateTableCommand = "";
    }

    public int FindRec(String b_sname) {//看社團名稱有沒有人取過了
        database=getReadableDatabase();
        //用AND連接各欄位的查詢，args用，傳進來的引數也要跟著增加
        String sql="SELECT C101 FROM "+DB_TABLE  +" WHERE C101= '"+b_sname+"'";

        Cursor recSet = database.rawQuery(sql,null );//獲得查詢的結果
        if(recSet.getCount()!=0)//有人取過了
        {
            recSet.close();
            database.close();
            return 0;
        }else{
            recSet.close();
            database.close();
            return 1;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(crTBsql);
//        db.execSQL(crTBsql1);
//        db.execSQL(crTBsql2);
//        db.execSQL(crTBsql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP    TABLE    IF     EXISTS    " + DB_TABLE);//有資料表就刪掉
        onCreate(db);//然後去生一個表
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public long insertRec(String b_name, String b_city, String b_area, String b_leader) {//新增社團
        database = getWritableDatabase();
        ContentValues rec = new ContentValues();//陣列，SQLite的一筆資料C100
        rec.put("C101", b_name);
        rec.put("C104", b_city);
        rec.put("C105", b_area);
        rec.put("C106", b_leader);
        long rowID = database.insert(DB_TABLE, null, rec);//這次寫入的是第幾筆
        database.close();
        return rowID;
    }

    public int RecCount() {    //看資料表有幾筆資料
        database = getWritableDatabase();
        String sql = "SELECT   *   FROM    " + DB_TABLE;
        Cursor recSet = database.rawQuery(sql, null);
        recSet.close();
        database.close();
        return recSet.getCount();
    }

    public void deleteSociety(int s_id) {//14-9
        database = getWritableDatabase();
        String whereClause = "id='" + s_id + "'";
        database.delete(DB_TABLE, whereClause, null);
        database.close();
    }

    public void updateSocietyName(int u_id, String u_S_name) {
        database = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("C101", u_S_name);
        String whereClause = "id='" + u_id + "'";
        int rowsAffected = database.update(DB_TABLE, rec, whereClause, null);
        database.close();
    }

    public ArrayList<Map<String, Object>> getAllRecord(String b_id) {//個人歷史紀錄
        database = getReadableDatabase();
        String sql = "SELECT B104,B103,B113,B114,B107,B108,id  FROM  " + DB_TABLE1 +"  WHERE  B101="+b_id;
        ArrayList<Map<String, Object>> mList = new ArrayList<Map<String, Object>>();//全部記錄
        Map<String, Object> item = new HashMap<String, Object>();//一筆紀錄

        Cursor cur_list = database.rawQuery(sql, null);
        //int columnCount=cur_list.getColumnCount();//取得共有幾個欄位

        cur_list.moveToLast();//移到最後一筆，最上面才會是最新的紀錄，一定要加，不然就不是完整列表***moveTo後面有五個位置
        DecimalFormat jaFormat = new DecimalFormat("0.00");//規定格式:小數點後兩位，0改#代表不補零
        //第一個參數對應listview的layout，第二個參數告訴電腦去哪找資料
        int a=cur_list.getInt(6);
        item.put("id",cur_list.getInt(6));//id

        Float distance_f = cur_list.getFloat(0) ;//距離
        String distance = jaFormat.format(distance_f);
        item.put("distance_data", distance);//距離 done

        int time=cur_list.getInt(1);//騎乘時間
        int hours = time/ 60 / 60;// 計算小時
        int minius = (time / 60) % 60;// 計算分鐘
        int seconds = time % 60;// 計算秒數(取餘數)
        String time_data=""+hours+":"+minius+":"+seconds;
        item.put("time_data",time_data);//騎乘時間 done

        item.put("year", cur_list.getString(2));//年
        String date = cur_list.getString(3);//月日
        item.put("date", date);

        Float climb_f = cur_list.getFloat(4) - cur_list.getFloat(5);//爬升高度(最高-最低海拔)
        String climb = jaFormat.format(climb_f);
        item.put("climb_data", climb);//爬升高度 done

        item.put("distance", "距離(km)");
        item.put("time", "時間");
        item.put("climb", "爬升(m)");
        mList.add(item);//將上面找到的資料放進ArrayList
//==================================================================
        while (cur_list.moveToPrevious()) {
            item= new HashMap<String, Object>();//每一筆紀錄都要new一次，沒有new就一直蓋過去
            //第一個參數對應listview的layout，第二個參數告訴電腦去哪找資料
            int aa=cur_list.getInt(6);
            item.put("id",cur_list.getInt(6));//id

            distance_f = cur_list.getFloat(0) ;//距離
            distance = jaFormat.format(distance_f);
            item.put("distance_data", distance);//距離 done

            time=cur_list.getInt(1);//騎乘時間
            hours = time/ 60 / 60;// 計算目前已過小時數
            minius = (time / 60) % 60;// 計算目前已過分鐘數
            seconds = time % 60;// 計算目前已過秒數(取餘數)
            time_data=""+hours+":"+minius+":"+seconds;
            item.put("time_data",time_data);//騎乘時間

            item.put("year", cur_list.getString(2));//年
            date = cur_list.getString(3);//月日
            item.put("date", date);

            climb_f = cur_list.getFloat(4) - cur_list.getFloat(5);//爬升高度(最高-最低海拔)
            climb = jaFormat.format(climb_f);
            item.put("climb_data", climb);//爬升高度 done

            item.put("distance", "距離(km)");
            item.put("time", "時間");
            item.put("climb", "爬升(m)");
            mList.add(item);//將上面找到的資料放進ArrayList
        }
        //        DecimalFormat jaFormat=new DecimalFormat("0.00");//規定格式:小數點後兩位，0改#代表不補零
//        String ja=jaFormat.format(Double.parseDouble(avg))+"km";
        cur_list.close();
        database.close();
        return mList;
    }

    public ArrayList<String> getC200(int b_id) {//社團人員明細--審核中
        database = getReadableDatabase();
        String sql = "SELECT A102 , A104 , C202  FROM  " + DB_TABLE2 +" INNER JOIN  "+DB_TABLE3 + "  ON A100.id=C200.C202  " +
                "   WHERE C201 = '" +b_id+"' AND C203 = '0'";//0=審核中
        ArrayList<String> mList = new ArrayList<>();//全部記錄

        Cursor cur_list = database.rawQuery(sql, null);
//        Cursor cur_list = database.execSQL("a");
        //int columnCount=cur_list.getColumnCount();//取得共有幾個欄位

        cur_list.moveToFirst();//移到第一筆，一定要加，不然就不是完整列表***moveTo後面有五個位置
        mList.add(cur_list.getString(0)+"#"+cur_list.getString(1)+"#"+cur_list.getString(2));//姓名#頭貼#會員UID
        //==================================================================
        while (cur_list.moveToNext()) {
        mList.add(cur_list.getString(0)+"#"+cur_list.getString(1)+"#"+cur_list.getString(2));//姓名#頭貼#會員UID
        }
        cur_list.close();
        database.close();
        return mList;
    }

//    public String getC200_leader(int b_id) {//團長UID
//        database = getReadableDatabase();
//        String sql = "SELECT C202  FROM  " + DB_TABLE2 +"  WHERE C201 = '" +b_id+"' AND C203 = '2'";
//        String mList = "";
//
//        Cursor cur_list = database.rawQuery(sql, null);
//        //int columnCount=cur_list.getColumnCount();//取得共有幾個欄位
//        if(cur_list.getCount()!=0){
//            cur_list.moveToFirst();//移到第一筆，一定要加，不然就不是完整列表***moveTo後面有五個位置
//            mList=cur_list.getString(0);//團長的會員UID
//            //==================================================================
//            cur_list.close();
//            database.close();
//            return mList;
//        } else{
//            return "member";//若使用者不是團長就回傳這個
//        }
//
//
//    }

    public ArrayList<String> getC200_member(int b_id) {//社團人員明細
        database = getReadableDatabase();
        String sql = "SELECT A102 , A104 , C202  FROM  " + DB_TABLE2 +" INNER JOIN  "+DB_TABLE3 + "  ON A100.id=C200.C202  " +
                "   WHERE C201 = '" +b_id+"' AND C203 = '1'";//1=社員
        ArrayList<String> mList = new ArrayList<>();//全部記錄

        Cursor cur_list = database.rawQuery(sql, null);
//        Cursor cur_list = database.execSQL("a");
        //int columnCount=cur_list.getColumnCount();//取得共有幾個欄位

        cur_list.moveToFirst();//移到第一筆，一定要加，不然就不是完整列表***moveTo後面有五個位置
        mList.add(cur_list.getString(0)+"#"+cur_list.getString(1)+"#"+cur_list.getString(2));//姓名#頭貼#會員UID
        //==================================================================
        while (cur_list.moveToNext()) {
        mList.add(cur_list.getString(0)+"#"+cur_list.getString(1)+"#"+cur_list.getString(2));//姓名#頭貼#會員UID
        }
        cur_list.close();
        database.close();
        return mList;
    }

    public String record_detail(String b_id) {//暫存都用b_
        database=getReadableDatabase();
        String sql="SELECT * FROM "+DB_TABLE1 +" WHERE id= '"+b_id+"'";
        Cursor recSet = database.rawQuery(sql, null);
        String recAry = "";
        int columnCount = recSet.getColumnCount();
        recSet.moveToFirst();

        for (int i = 0; i < columnCount; i++)
            recAry += recSet.getString(i) + "#";//所有的欄位加起來(#或其他罕用字)

        recSet.close();
        database.close();
        return recAry;//詳細歷史紀錄
    }

    public String getUserFile(String b_id) {//取得登入者檔案
        database=getReadableDatabase();
        String sql="SELECT  A102 , A104 FROM  "+DB_TABLE3+"  WHERE  id= '"+b_id+"'";
        Cursor recSet = database.rawQuery(sql, null);
        String recAry = "";
        int columnCount = recSet.getColumnCount();
        recSet.moveToFirst();

        for (int i = 0; i < columnCount; i++)
            recAry += recSet.getString(i) + "#";//所有的欄位加起來(#或其他罕用字)

        recSet.close();
        database.close();
        return recAry;//詳細歷史紀錄
    }

    public Long insertRec_login()//測試用
    {
        database = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("A101", "123");
        rec.put("A102", "陳夢晨");
        rec.put("A103", "1@g.c");
        rec.put("A104", "https://scontent.ftpe12-2.fna.fbcdn.net/v/t1.0-0/p640x640/121675691_3388332111262030_7938279514046750963_o.jpg?_nc_cat=107&ccb=2&_nc_sid=e3f864&_nc_ohc=MBEBwsxNWJwAX8p5auO&_nc_ht=scontent.ftpe12-2.fna&tp=6&oh=4a32c2d4b7b8d7e1b869eca45d9bec5f&oe=60272517");
        long rowID = database.insert(DB_TABLE3, null, rec);
        database.close();
        return rowID;
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
    public int clearRec1() {// 刪除紀錄SQLite資料
        //        //-----------
//        Cursor c1=db.execSQL("");
//        Cursor c2=db.rawQuery();
//        Cursor c3=db.insert();
//        Cursor c4=db.update(, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , );
//        Cursor c5=db.delete();
//        //------------------------------------------------
        database = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE1;//紀錄
        Cursor recSet = database.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = database.delete(DB_TABLE1, "1", null); //
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

    public int clearRec2() {// 刪除社團人員明細SQLite資料
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

    public int clearRec_G100() {// 刪除G100的SQLite資料
        //        //-----------
//        Cursor c1=db.execSQL("");
//        Cursor c2=db.rawQuery();
//        Cursor c3=db.insert();
//        Cursor c4=db.update(, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , );
//        Cursor c5=db.delete();
//        //------------------------------------------------
        database = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_G100;//揪團
        Cursor recSet = database.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = database.delete(DB_TABLE_G100, "1", null); //
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

    //    ContentValues values
    public long insertRec_G100(ContentValues rec) {//社團人員明細
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_G100, null, rec);
        db.close();
        return rowID;
    }

    public ArrayList<String> getRecSet() {//將每列資料用#串起來後傳回一個ArrayList
        database = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE;
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

    public ArrayList<String> getRecSet_C200(String b_societyName) {//將每列資料用#串起來後傳回一個ArrayList
        database = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE2  +" WHERE C202 =  '"+b_societyName+"'" ;
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

    public ArrayList<String> society(String b_id) {//撈取使用者有參與的社團名稱
        database = getReadableDatabase();
        ArrayList<String> fldSet = new ArrayList<String>();
        String sql = "SELECT C101 FROM " + DB_TABLE+
                " INNER JOIN `C200` ON `C100`.`id`=`C200`.`C201` WHERE C202 =  '"+b_id+"'   AND  C203 != '0'  ORDER BY C201 DESC";
//        String[] args = {"\"" + b_id+ "\""};
        Cursor recSet = database.rawQuery(sql, null);

        int columnCount = recSet.getColumnCount();

        if (recSet.getCount() != 0) {
            recSet.moveToFirst();
            String b=recSet.getString(0);
            fldSet.add(recSet.getString(0));

            while (recSet.moveToNext()) {
                //for (int i = 0; i < columnCount; i++)
                String a=recSet.getString(0);
                fldSet.add(recSet.getString(0));
            }
        }
        recSet.close();
        database.close();
        return fldSet;
    }

    public String getLeaderName(String b_id) { //取得團長姓名
        String leaderName="";
        database = getReadableDatabase();
        String sql = "SELECT A102 FROM " + DB_TABLE3+"  WHERE id =  '"+b_id+"'";
        Cursor recSet = database.rawQuery(sql, null);

        int columnCount = recSet.getColumnCount();

        if (recSet.getCount() != 0) {
            recSet.moveToFirst();
            leaderName=recSet.getString(0);

//            while (recSet.moveToNext()) {
//                //for (int i = 0; i < columnCount; i++)
//                String a=recSet.getString(0);
//            }
        }
        recSet.close();
        database.close();

        return leaderName;
    }


    public ArrayList<String> competition(int b_id) {//挑出最快慢長短的使用者，不包括審核中
        database=getReadableDatabase();
        ArrayList<String> compare = new ArrayList<>();
//        String sql = "SELECT B101,B104,B109 FROM " + DB_TABLE1+"  WHERE B101 IN(SELECT C202 FROM C200 WHERE C203!=0 AND C201="+b_id+")";
        String sql = "SELECT A102,B101,B104,B109 FROM " + DB_TABLE3+"  INNER JOIN "+DB_TABLE1+"  ON  A100.id=B100.B101 WHERE B101 IN(SELECT C202 FROM C200 WHERE C203!=0 AND C201="+b_id+")";
        Cursor recSet = database.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
            recSet.moveToFirst();
            Double fast_record = recSet.getDouble(3);
            Double slow_record = recSet.getDouble(3);
            Double long_record = recSet.getDouble(2);
            Double short_record = recSet.getDouble(2);
            String fastUser=recSet.getString(0)
                       ,slowUser=recSet.getString(0)
                       ,longUser=recSet.getString(0)
                       ,shortUser=recSet.getString(0);
            while (recSet.moveToNext()) {
                Double k=recSet.getDouble(3);
                if (recSet.getDouble(3) > fast_record) {
                    fastUser = recSet.getString(0);
                    fast_record = recSet.getDouble(3);
                }
                if (recSet.getDouble(3) < slow_record) {
                    slowUser = recSet.getString(0);
                    slow_record = recSet.getDouble(3);
                }
                if (recSet.getDouble(2) > long_record) {
                    longUser = recSet.getString(0);
                    long_record = recSet.getDouble(2);
                }
                if (recSet.getDouble(2) < short_record) {
                    shortUser = recSet.getString(0);
                    short_record = recSet.getDouble(2);
                }
            }
            compare.add(fastUser);
            compare.add(slowUser);
            compare.add(longUser);
            compare.add(shortUser);
            recSet.close();
            database.close();

            return compare;
        }else{
            return null;
        }
    }

    public ArrayList<String> getG100(int b_id) {
        database=getReadableDatabase();
        ArrayList<String> g100_data = new ArrayList<>();
        String sql = "SELECT G102,G103,G104 FROM " + DB_TABLE_G100+"  WHERE  G108="+b_id +"  ORDER BY G103";
        Cursor recSet = database.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
            recSet.moveToFirst();
            String join_name = recSet.getString(0);
            String join_date = recSet.getString(1);
            String join_time = recSet.getString(2);

            g100_data.add(join_name);
            g100_data.add(join_date);
            g100_data.add(join_time);

            recSet.close();
            database.close();
            return g100_data;
        }else{
            return null;
        }
    }
}

