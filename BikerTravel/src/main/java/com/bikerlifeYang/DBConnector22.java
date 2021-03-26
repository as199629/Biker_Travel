package com.bikerlifeYang;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DBConnector22 {
    public static int httpstate = 0;
    //--------------------------------------------------------
    private static String postUrl;
    private static String myResponse;
    static String result = null;
    private static OkHttpClient client = new OkHttpClient();// 建立 OkHttpClient
    //---------------------------------------------------------
//    第四組網址:
//    static String connect_ip = "https://bklifetw.com/android_mysql_connect/android_connect_db_all.php";
//    static String connect_ip = "https://caddish-seaman.000webhostapp.com/android_mysql_connect/dbtools_bikerlife_map.php";
    static String connect_ip = " https://bklifetw.com/T22/android_mysql_connect/dbtools_bikerlife_map.php";
    
    public static String executeQuery(ArrayList<String> query_string) {
        //        OkHttpClient client = new OkHttpClient();
        postUrl = connect_ip;  //php的網址
        //--------------
        String query_0 = query_string.get(0); //抓array第一個值
        //官方寫法類似stringarray:  =>傳值給php的_post(一個包裹)  設置稍後傳送時所需的資料!
        FormBody body = new FormBody.Builder()
                .add("selefunc_string", "query")
                .add("query_string", query_0)
                .build();
//--------------
        Request request = new Request.Builder() ////  Request(請求)，設定連線資訊
                .url(postUrl)//請求位置
                .post(body)//請求方法
                .build();

        //-----------------1420新增(伺服器狀態)
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        //------

        //response響應 包含響應狀態、可以使用的Body
        try (Response response = client.newCall(request).execute()) {//call=>建立並執行requset的請求
            //把上面build出來的Request帶進來  回傳是null, 就代表timeout或 沒有網路
            //-----------------1420新增(伺服器狀態)
            httpstate = response.code();
            //------
            return response.body().string();//將請求物件中的字串內容回傳   注:response.body().string()只能取得一次string
            //第二次會得到空字串
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String executeInsert(ArrayList<String> query_string) { //新增資料至mysql
//        OkHttpClient client = new OkHttpClient();
        postUrl = connect_ip;
        //--------------
        String query_0 = query_string.get(0); //姓名 UserID
        String query_1 = query_string.get(1);//群組 lat
        String query_2 = query_string.get(2);//地址 lon lng

        FormBody body = new FormBody.Builder()//body=>指php代入的參數
                .add("selefunc_string", "insert")
                .add("E201", query_0)
                .add("E202", query_1)
                .add("E203", query_2)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        //-----------------1420新增(伺服器狀態)
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        //------
        try (Response response = client.newCall(request).execute()) {//執行https://000.......php命令
            //-----------------1420新增(伺服器狀態)
            httpstate = response.code();
            //------
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    public static String executeInsert_history(ArrayList<String> query_string) { //新增資料(搜尋歷史紀錄)至mysql
//        OkHttpClient client = new OkHttpClient();
        postUrl = connect_ip;
        //--------------
        String query_0 = query_string.get(0); //姓名 UserID
        String query_1 = query_string.get(1); //出發點
        String query_2 = query_string.get(2); //目的地
        String query_3 = query_string.get(3); //出發點經度
        String query_4 = query_string.get(4); //出發點緯度
        String query_5 = query_string.get(5); //終點經度
        String query_6 = query_string.get(6); //終點緯度

        FormBody body = new FormBody.Builder()//body=>指php代入的參數
                .add("selefunc_string", "insert_history")
                .add("E101", query_0) //UserID
                .add("E102", query_1) //出發點
                .add("E103", query_2) //目的地
                .add("E104", query_3) //出發點經度
                .add("E105", query_4) //出發點緯度
                .add("E106", query_5) //終點經度
                .add("E107", query_6) //終點緯度
                .build();

//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        //-----------------1420新增(伺服器狀態)
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        //------
        try (Response response = client.newCall(request).execute()) {//執行https://000.......php命令
            //-----------------1420新增(伺服器狀態)
            httpstate = response.code();
            //------
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String executeUpdate(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl = connect_ip;
        //--------------
        String query_0 = query_string.get(0); //ID
        String query_1 = query_string.get(1);//姓名 UserID
        String query_2 = query_string.get(2);//群組 lat
        String query_3 = query_string.get(3);//地址 lng

        FormBody body = new FormBody.Builder()//body=>指php代入的參數
                .add("selefunc_string", "update")
                .add("id", query_0)
                .add("E201", query_1)
                .add("E202", query_2)
                .add("E203", query_3)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        //-----------------1420新增(伺服器狀態)
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        //------
        try (Response response = client.newCall(request).execute()) {//執行https://000.......php命令
            //-----------------1420新增(伺服器狀態)
            httpstate = response.code();
            //------
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String executeDelet(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl = connect_ip;
        //--------------
        String query_0 = query_string.get(0);

        FormBody body = new FormBody.Builder()
                .add("selefunc_string", "delete")
                .add("id", query_0)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        //-----------------1420新增(伺服器狀態)
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        //------
        try (Response response = client.newCall(request).execute()) {
            //-----------------1420新增(伺服器狀態)
            httpstate = response.code();
            //------
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //20210214adds
    public static String executeQueryFriendLocation(ArrayList<String> query_string) {
        //        OkHttpClient client = new OkHttpClient();
        postUrl = connect_ip;  //php的網址
        //--------------
        String query_0 = query_string.get(0); //抓array第一個值
        //官方寫法類似stringarray:  =>傳值給php的_post(一個包裹)  設置稍後傳送時所需的資料!
        FormBody body = new FormBody.Builder()
                .add("selefunc_string", "queryFriendLocation")
                .add("query_string", query_0)
                .build();
//--------------
        Request request = new Request.Builder() ////  Request(請求)，設定連線資訊
                .url(postUrl)//請求位置
                .post(body)//請求方法
                .build();

        //-----------------1420新增(伺服器狀態)
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        //------

        //response響應 包含響應狀態、可以使用的Body
        try (Response response = client.newCall(request).execute()) {//call=>建立並執行requset的請求
            //把上面build出來的Request帶進來  回傳是null, 就代表timeout或 沒有網路
            //-----------------1420新增(伺服器狀態)
            httpstate = response.code();
            //------
            return response.body().string();//將請求物件中的字串內容回傳   注:response.body().string()只能取得一次string
            //第二次會得到空字串
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    //20210214addo
}
