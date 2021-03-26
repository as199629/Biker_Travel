package com.bikerlifeYang;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DBConnector28 {
    //--------------------------------------------------------
    private static String postUrl;
    private static String myResponse;
    static String result = null;
    private static OkHttpClient client = new OkHttpClient();
    //---------------------------------------------------------
// -------HOSTING-------
//    static String connect_ip = "https://oldpa88.com/android_mysql_connect/android_connect_db_all.php";
    // -------000webhost(自己的)-------
    static String connect_ip = "https://bklifetw.com/T28/profile_connect_db_all.php";
    // -------班長-------
//    static String connect_ip = "https://mad-muscle.000webhostapp.com/android_mysql_connect/android_connect_db_all.php";
    // -------副班長-------
//    static String connect_ip = "https://monachal-bandage.000webhostapp.com/android_mysql_connect/android_connect_db_all.php";
    // -------第一組松逸-------
//    static String connect_ip = "https://iron61700.000webhostapp.com/android_mysql_connect/android_connect_db_all.php";
    // -------第二組其軒-------
//    static String connect_ip= "https://kartg0203.000webhostapp.com/android_mysql_connect/android_connect_db_all.php";
    // -------第三組小石-------
//    static String connect_ip = "https://volitionary-blocks.000webhostapp.com/android_mysql_connect/android_connect_db_all.php";
    // -------第四組皓文-------
//    static String connect_ip = "https://oldba29.000webhostapp.com/android_mysql_connect/android_connect_db_all.php";
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

    //新增社團
    public static String executeInsert(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);//C101
        String query_1 = query_string.get(1);//C104
        String query_2 = query_string.get(2);//C105
        String query_3 = query_string.get(3);//C106
        String query_4 = query_string.get(4);//C203

        FormBody body = new FormBody.Builder()//body指PHP問號後面帶入的參數
                .add("selefunc_string","insert")//要跟$_REQUEST['selefunc_string'];字完全一樣(PHP)
                .add("C101", query_0)//對應PHP的$_REQUEST['C101'];
                .add("C104", query_1)
                .add("C105", query_2)
                .add("C106", query_3)
                .add("C203", query_4)
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
    //新增社團C200
    public static String executeInsert_C200(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_3 = query_string.get(3);//C202

        FormBody body = new FormBody.Builder()//body指PHP問號後面帶入的參數
                .add("selefunc_string","insert_C200")//要跟$_REQUEST['selefunc_string'];字完全一樣(PHP)
                .add("C201", "-1")//對應PHP的$_REQUEST['C201'];為了符合PHP的判斷式
                .add("C202", query_3)//對應PHP的$_REQUEST['C202'];
                .add("C203", "2")//2為團長
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
//更新社團名稱或公告
    public static String executeUpdate(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);//id
        String query_1 = query_string.get(1);//C101或C108
        String query_2 = query_string.get(2);//判斷是要更新1.名稱還是2.公告

        FormBody body = new FormBody.Builder()//body指PHP問號後面帶入的參數
                .add("selefunc_string","update")//要跟$_REQUEST['selefunc_string'];字完全一樣(PHP)
                .add("id", query_0)//要跟$_REQUEST['這裡的'];字完全一樣(PHP)
                .add("C101", query_1)//C101或C108
                .add("name_or_post", query_2)
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

    //更新(審核中->社員)
    public static String executeUpdate_C200(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);//C201
        String query_1 = query_string.get(1);//C202

        FormBody body = new FormBody.Builder()//body指PHP問號後面帶入的參數
                .add("selefunc_string","update_C200")//要跟$_REQUEST['selefunc_string'];字完全一樣(PHP)
                .add("C201", query_0)//要跟$_REQUEST['這裡的'];字完全一樣(PHP)
                .add("C202", query_1)
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

    //刪除會員
    public static String executeDelete_member(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);//C201
        String query_1 = query_string.get(1);//C202

        FormBody body = new FormBody.Builder()//body指PHP問號後面帶入的參數
                .add("selefunc_string","delete_member")//要跟$_REQUEST['selefunc_string'];字完全一樣(PHP)
                .add("C201", query_0)//要跟$_REQUEST['這裡的'];字完全一樣(PHP)
                .add("C202", query_1)
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

    //不給過審核
    public static String executeDelete_C200(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);//C201
        String query_1 = query_string.get(1);//C202

        FormBody body = new FormBody.Builder()//body指PHP問號後面帶入的參數
                .add("selefunc_string","delete_C200")//要跟$_REQUEST['selefunc_string'];字完全一樣(PHP)
                .add("C201", query_0)//要跟$_REQUEST['這裡的'];字完全一樣(PHP)
                .add("C202", query_1)
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

    //---刪除社團--------------------------------------------------------------
    public static String executeDelet(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);//id

        FormBody body = new FormBody.Builder()
                .add("selefunc_string","delete")
                .add("id", query_0)
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
