package com.bikerlifeYang;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DBConnector_reward30
{
    //--------------------------------------------------------
    private static String postUrl;
    private static String myResponse;
    static String result = null;
    private static OkHttpClient client = new OkHttpClient();// 建立 OkHttpClient

   // static String connect_ip = "https://bklifetw.com/T30/webtest/bikerlife/bikerlife/reward_r_api.php";
    //static String connect_ip = " https://bklifetw.com/T30/webtest/bikerlife/bikerlife/reward_achievement_r_api.php";
   static String connect_ip="";


    public static String executeQuery() { //讀取資料用RRRRRRRR
        //        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;  //php的網址
        //--------------
      //  String query_0 = query_string.get(0); //抓array第一個值
        //官方寫法類似stringarray:  =>傳值給php的_post(一個包裹)  設置稍後傳送時所需的資料!
        FormBody body = new FormBody.Builder()
//                .add("selefunc_string","query")
//                .add("query_string", query_0)
                .build();
//--------------
        Request request = new Request.Builder() ////  Request(請求)，設定連線資訊
                .url(postUrl)//請求位置
                .post(body)//請求方法
                .build();
        //response響應 包含響應狀態、可以使用的Body
        try (Response response = client.newCall(request).execute()) {//call=>建立並執行requset的請求
            //把上面build出來的Request帶進來  回傳是null, 就代表timeout或 沒有網路
            return response.body().string();//將請求物件中的字串內容回傳   注:response.body().string()只能取得一次string
            //第二次會得到空字串
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
