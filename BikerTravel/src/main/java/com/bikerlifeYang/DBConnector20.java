package com.bikerlifeYang;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DBConnector20 {
    //--------------------------------------------------------
    //--------------------------------------------------------
    private static String postUrl;
    static String result = null;
    public static int httpstate = 0;
    private static OkHttpClient client = new OkHttpClient();//client 端
    //-------HOSTING-------
    static String connect_ip = "https://bklifetw.com/T20/android_mysql_connect/plan_connect_db_all.php";
    //------------------------查詢------------------------------
    public static String executeQuery(String U_ID) {
        //OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;  //php的網址
        //--------------
        //官方寫法類似stringarray:  =>傳值給php的_post(一個包裹)  設置稍後傳送時所需的資料!
        String sql_D100="SELECT * FROM D100 WHERE D101="+U_ID;
        FormBody body = new FormBody.Builder()
                .add("selefunc_string","query")
                .add("query_string", sql_D100)
                .build();
        //--------------
        Request request = new Request.Builder() ////  Request(請求)，設定連線資訊
                .url(postUrl)//請求位置
                .post(body)//請求方法
                .build();
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        //response響應 包含響應狀態、可以使用的Body
        try (Response response = client.newCall(request).execute()) {//call=>建立並執行requset的請求
            //把上面build出來的Request帶進來  回傳是null, 就代表timeout或 沒有網路
            httpstate=response.code();
            return response.body().string();//將請求物件中的字串內容回傳   注:response.body().string()只能取得一次string
            //第二次會得到空字串
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    //----------------------真正寫入mysql---------------------------------------------
    public static String executeInsert(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);
        String query_1 = query_string.get(1);
        String query_2 = query_string.get(2);
        String query_3 = query_string.get(3);

        FormBody body = new FormBody.Builder()
                .add("selefunc_string","insert")
                .add("D101", query_0)//U_ID
                .add("D102", query_1)// D102	日期
                .add("D103", query_2)// D103	地區
                .add("D104", query_3)// D104	計畫名稱
                .build();
        //--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            httpstate=response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    //mysql更新資料===================================
    public static String executeUpdate(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);
        String query_1 = query_string.get(1);
        String query_2 = query_string.get(2);
        String query_3 = query_string.get(3);
        String query_4 = query_string.get(4);

        FormBody body = new FormBody.Builder()
                .add("selefunc_string","update")
                .add("id", query_0)//id
                .add("D101", query_1)//U_ID
                .add("D102", query_2)// D102	日期
                .add("D103", query_4)// D103	地區
                .add("D104", query_3)// D104	計畫名稱
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    //MYSQL刪除資料==================================
    public static String executeDelet(String planIDs) {
        postUrl=connect_ip ;
        String planID = planIDs;
        //--------------
        FormBody body = new FormBody.Builder()
                .add("selefunc_string","delete")
                .add("id", planID)
                .build();
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            httpstate=response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
        //=參考======================================

//        postUrl=connect_ip ;
//        //--------------
//        String query_0 = query_string.get(0);
//
//        FormBody body = new FormBody.Builder()
//                .add("selefunc_string","delete")
//                .add("id", query_0)
//                .build();
////--------------
//        Request request = new Request.Builder()
//                .url(postUrl)
//                .post(body)
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            return response.body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
    }
}
