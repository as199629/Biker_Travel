package com.bikerlifeYang;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DBConnector
{
    //--------------------------------------------------------
    private static String postUrl;
    private static String myResponse;
    static String result = null;
    private static OkHttpClient client = new OkHttpClient();// 建立 OkHttpClient
    public static int httpstate=0;
    static String connect_ip = "https://bklifetw.com/T30/webtest/bikerlife/bikerlife/join_api_all.php";


    public static String executeQuery(ArrayList<String> query_string) {
        //        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;  //php的網址
        //--------------
        String query_0 = query_string.get(0); //抓array第一個值
        //官方寫法類似stringarray:  =>傳值給php的_post(一個包裹)  設置稍後傳送時所需的資料!
        FormBody body = new FormBody.Builder()
                .add("selefunc_string","query_join")
                .add("query_string", query_0)
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
            httpstate=response.code();//httpcode
            //把上面build出來的Request帶進來  回傳是null, 就代表timeout或 沒有網路
            return response.body().string();//將請求物件中的字串內容回傳   注:response.body().string()只能取得一次string
            //第二次會得到空字串
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    //新增揪團G100
    public static String executeInsert_G100(ArrayList<String> data) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------

        FormBody body = new FormBody.Builder()//body指PHP問號後面帶入的參數
                .add("selefunc_string","insert_G100")
                .add("G102", data.get(0))//名稱
                .add("G103", data.get(1)) //日期
                .add("G104", data.get(2))//時間
                .add("G105", data.get(3))//人數
                .add("G106", data.get(4)) //地點
                .add("G107", data.get(5))//描述
                .add("G108", data.get(6))//社團ID
                .add("G109", data.get(7)) //主辦人ID
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
    //加入揪團清單G200
    public static String executeInsert_G200(ArrayList<String> data) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------

        FormBody body = new FormBody.Builder()//body指PHP問號後面帶入的參數
                .add("selefunc_string","insert_G200")
                .add("G202", data.get(0))//USERID
                .add("G203", data.get(1)) //揪團ID
                .add("G204", data.get(2))//是否為主辦人
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
    //更新揪團資訊
    public static String executeUpdate_G100(ArrayList<String> data) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------

        FormBody body = new FormBody.Builder()//body指PHP問號後面帶入的參數
                .add("selefunc_string","update_G100")
                .add("G101", data.get(0))//名稱
                .add("G102", data.get(1))//名稱
                .add("G103", data.get(2)) //日期
                .add("G104", data.get(3))//時間
                .add("G105", data.get(4))//人數
                .add("G106", data.get(5)) //地點
                .add("G107", data.get(6))//描述
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
    //自動更新是否已經結束揪團
    public static String executeUpdate_IsEnd_G100(String joinid) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------

        FormBody body = new FormBody.Builder()//body指PHP問號後面帶入的參數
                .add("selefunc_string","update_IsEnd_G100")
                .add("G101", joinid)//名稱
                .add("G110", "1")//描述
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
    //---刪除申請加入資料(G200)--------------------------------------------------------------
    public static String executeDeletJoinTeam(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);//G200的ID
        String query_1 = query_string.get(1);//G200的ID
        FormBody body = new FormBody.Builder()
                .add("selefunc_string","delete_G200")
                .add("G202", query_0)//UID
                .add("G203", query_1)//JID
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
    //---刪除揪團資料(G100)--------------------------------------------------------------
    public static String executeDeletJoin(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);//G100的ID
        FormBody body = new FormBody.Builder()
                .add("selefunc_string","delete_G100")
                .add("G101", query_0)//UID
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
