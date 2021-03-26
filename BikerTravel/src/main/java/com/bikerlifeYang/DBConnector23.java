package com.bikerlifeYang;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DBConnector23 {
    //--------------------------------------------------------
    private static String postUrl;
    static String result = null;
    public static int httpstate = 0;
    private static OkHttpClient client = new OkHttpClient();//client 端
     //-------HOSTING-------
    static String connect_ip = "https://bklifetw.com/T23/train_connect_db_all.php";

    //------------------------查詢------------------------------
    public static String executeQuery(String U_ID) {
        //OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;  //php的網址
        //--------------
        //官方寫法類似stringarray:  =>傳值給php的_post(一個包裹)  設置稍後傳送時所需的資料!
        FormBody body = new FormBody.Builder()
                .add("selefunc_string","query")
                .add("query_string", U_ID)
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
        String query_4 = query_string.get(4);
        String query_5 = query_string.get(5);
        String query_6 = query_string.get(6);

        FormBody body = new FormBody.Builder()
                .add("selefunc_string","insert")
                .add("I101", query_0)//U_ID
                .add("I102", query_1)//計畫時間
                .add("I103", query_2)//預計騎乘里程
                .add("I104", query_3)//預計爬升高度
                .add("I105", query_4)//預計騎乘時間
                .add("I106", query_5)//訓練結束日期
                .add("I107", query_6)//訓練開始日期
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

    //----------------------MYSQL刪除資料-----------------------------
    public static String executeDelet(String U_ID) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------

        FormBody body = new FormBody.Builder()
                .add("selefunc_string","delete")
                .add("id", U_ID)
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
    }
}
