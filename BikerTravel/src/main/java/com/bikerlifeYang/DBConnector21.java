package com.bikerlifeYang;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DBConnector21
{
    //--------------------------------------------------------
    private static String postUrl;
    private static String myResponse;
    static String result = null;
    private static OkHttpClient client = new OkHttpClient();
    //---------------------------------------------------------
// -------HOSTING-------
    static String connect_ip = "https://bklifetw.com/T28/profile_connect_db_all.php";

    public static int httpstate=0;

    //查詢--做社團spinner
    public static String executeQuery(ArrayList<String> query_string) {
        //        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);
        FormBody body = new FormBody.Builder()//body指PHP問號後面帶入的參數(會丟到PHP問號後面)
                .add("selefunc_string","query")
                .add("query_string", query_0)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        try (Response response = client.newCall(request).execute()) {//真正執行MySQL，執行HTTP命令
            httpstate=response.code();//httpcode
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    //新增社團C200  (加入社團)
    public static String executeInsert_C200(ArrayList<String> query_string,String CID,String UID) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------

        FormBody body = new FormBody.Builder()//body指PHP問號後面帶入的參數
                .add("selefunc_string","insert_C200")//要跟$_REQUEST['selefunc_string'];字完全一樣(PHP)
                .add("C201", CID)//假設社團ID:6
                .add("C202", UID) //社員ID
                .add("C203", "0")//權限
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)//post(要回值--PHP)
                .build();
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        try (Response response = client.newCall(request).execute()) {//真正執行MySQL，執行HTTP命令
            httpstate=response.code();//httpcode
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }


    //---刪除申請加入資料--------------------------------------------------------------
    public static String executeDeletClubApplying(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);//社團id
        String query_1 = query_string.get(1);//會員id
        FormBody body = new FormBody.Builder()
                .add("selefunc_string","delete_C200")
                .add("C201", query_0)
                .add("C202", query_1)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        try (Response response = client.newCall(request).execute()) {//真正執行MySQL，執行HTTP命令
            httpstate=response.code();//httpcode
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
