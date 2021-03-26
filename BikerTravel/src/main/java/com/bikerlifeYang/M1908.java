package com.bikerlifeYang;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class M1908 extends AppCompatActivity implements
        OnMapReadyCallback{

    private GoogleMap map;

    static LatLng VGPS = new LatLng(24.172127, 120.610313);
    float currentZoom = 14;
    double dLat, dLon;
    private BitmapDescriptor image_des;// 圖標顯示
    private int icosel=0; //圖示旗標
    private Marker markerMe;

    private final String TAG = "oldpa=>";
    //-----------------所需要申請的權限數組---------------
    private static final String[] permissionsArray = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION};
    private List<String> permissionsList = new ArrayList<String>();
    //申請權限後的返回碼
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private ArrayList<HashMap<String, Object>> arrayList=new ArrayList<HashMap<String, Object>>();
    private ArrayList<LatLng> mytrace;// 追蹤我的位置
    private Button b001;


    //----------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m1908);
        checkRequiredPermission(this);     //  檢查SDK版本, 確認是否獲得權限.
        //------------設定MapFragment-----------------------------------
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupViewComponent();



    }

    //--------------------
    private void setupViewComponent() {
        b001=(Button)findViewById(R.id.b001);
        b001.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Download");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //如果您希望自己的应用只需阅读/导入，请使用ACTION_GET_CONTENT
                // 数据。通过这种方法，应用程序可以导入数据的副本，例如   图像文件。

                //目前這不能用
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                //如果您希望自己的应用拥有，请使用ACTION_OPEN_DOCUMENT
                // 对文档拥有的文档的长期持久访问   供应商。一个例子是一个允许用户编辑的照片编辑应用程序
                // 存储在文档提供程序中的图像。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
                startActivityForResult(intent,0);

            }
        });
        //清楚軌跡圖
        Button b002 = (Button) findViewById(R.id.mapclear);
        b002.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map != null) map.clear();  //218
                if (arrayList != null) arrayList.clear();
                if (mytrace != null) mytrace.clear();
                if (markerMe != null) markerMe.remove();
            }
        });
    }




    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            String path =getRealPathFromURI_API19(this,uri);
            if (map != null) map.clear();  //218
            if (arrayList != null) arrayList.clear();
            if (mytrace != null) mytrace.clear();
            if (markerMe != null) markerMe.remove();

            if(uri != null){
                try{
                    File gpxFile = new File(path);
                    List<Location> gpxList = decodeGPX(gpxFile);

                    for(int i = 0; i < gpxList.size(); i++){

                        Double lat=((Location)gpxList.get(i)).getLatitude();
                        Double lon=((Location)gpxList.get(i)).getLongitude();
                        trackMe(lat,lon);//軌跡圖
                        VGPS = new LatLng(lat,lon);
                        if(i==0)
                        {
                            image_des = BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_RED); //使用系統水滴
                            map.addMarker(new MarkerOptions().position(VGPS).title("起點").icon(image_des));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS, currentZoom));
                        }
                        else if(i==gpxList.size()-1)
                        {
                            image_des = BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_GREEN); //使用系統水滴
                            map.addMarker(new MarkerOptions().position(VGPS).title("終點").icon(image_des));
                        }

                        //超過10分鐘 怕點太多 就5個點露一個

                        if(gpxList.size()/600>0)
                        {
                            int ez_gpx=gpxList.size()/600;
                            i +=ez_gpx+4;
                            boolean ok_end=false;
                            if(gpxList.size()-i<=0)
                            {
                                map.addMarker(new MarkerOptions().position(VGPS).title("終點").icon(image_des));
                            }


                        }

                    }



                }
                catch (Exception message)
                {
//                    t001.setText(uri.getPath());
                }
            }
            else{
//                t001.setText("無效的檔案路徑!!");
            }
        }
        else {
//            t001.setText("取消選擇檔案!!");
        }

    }
    //解析GPX
    private List<Location> decodeGPX(File file){
        List<Location> list = new ArrayList<Location>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            FileInputStream fileInputStream = new FileInputStream(file);
            Document document = documentBuilder.parse(fileInputStream);
            Element elementRoot = document.getDocumentElement();

            NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");

            for(int i = 0; i < nodelist_trkpt.getLength(); i++){

                Node node = nodelist_trkpt.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String newLatitude = attributes.getNamedItem("lat").getTextContent();
                Double newLatitude_double = Double.parseDouble(newLatitude);

                String newLongitude = attributes.getNamedItem("lon").getTextContent();
                Double newLongitude_double = Double.parseDouble(newLongitude);

                String newLocationName = newLatitude + ":" + newLongitude;
                Location newLocation = new Location(newLocationName);
                newLocation.setLatitude(newLatitude_double);
                newLocation.setLongitude(newLongitude_double);

                list.add(newLocation);

            }

            fileInputStream.close();

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return list;
    }
    //轉換正確路徑 因為有符號
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";

        // ExternalStorageProvider
        String docId = DocumentsContract.getDocumentId(uri);
        String[] split = docId.split(":");
        String type = split[0];

        if ("primary".equalsIgnoreCase(type)) {
            return Environment.getExternalStorageDirectory() + "/" + split[1];
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                //getExternalMediaDirs() added in API 21
                File[] external = context.getExternalMediaDirs();
                if (external.length > 1) {
                    filePath = external[1].getAbsolutePath();
                    filePath = filePath.substring(0, filePath.indexOf("Android")) + split[1];
                }
            } else {
                filePath = "/storage/" + type + "/" + split[1];
            }
            return filePath;
        }
    }
    //追蹤目前我的位置畫軌跡圖
    private void trackMe(double lat, double lng) {
        for (int i = 0; i < arrayList.size(); i++) {
            String vtitle = arrayList.get(i).get("vtitle").toString();
            String timeString = arrayList.get(i).get("timeString").toString();
            dLat = Double.valueOf(arrayList.get(i).get("lat").toString());
            dLon = Double.valueOf(arrayList.get(i).get("lng").toString());

            image_des = BitmapDescriptorFactory.fromResource(R.drawable.biker_login);// 使用照片
            MarkerOptions markerOpt = new MarkerOptions();
            markerOpt.position(new LatLng(dLat, dLon));
            markerOpt.title(vtitle + "-" + timeString);
            markerOpt.snippet("緯度" + dLat +"經度" + dLon);
            markerOpt.infoWindowAnchor(0.5f, 0.9f);
            markerOpt.draggable(true);
            markerOpt.icon(image_des);
            markerMe = map.addMarker(markerOpt);
        }
//------------------------------------------------------------------
        if (mytrace == null) {
            mytrace = new ArrayList<LatLng>();
        }
        mytrace.add(new LatLng(lat, lng));

        //畫線
        PolylineOptions polylineOpt = new PolylineOptions();
        for (LatLng latlng : mytrace) {
            polylineOpt.add(latlng);
        }
        polylineOpt.color(Color.BLUE); // 軌跡顏色
        Polyline line = map.addPolyline(polylineOpt);
        line.setWidth(10); // 線寬軌跡寬度
//---

        line.setPoints(mytrace);//這句話才把線加進去
    }
    //--------------------------------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
//        mUiSettings = map.getUiSettings();//
//        開啟 Google Map 拖曳功能
        map.getUiSettings().setScrollGesturesEnabled(true);
//        右下角的導覽及開啟 Google Map功能
        map.getUiSettings().setMapToolbarEnabled(true);
//        左上角顯示指北針，要兩指旋轉才會出現
        map.getUiSettings().setCompassEnabled(true);
//        右下角顯示縮放按鈕的放大縮小功能
        map.getUiSettings().setZoomControlsEnabled(true);
        // --------------------------------
//        map.addMarker(new MarkerOptions().position(VGPS).title("中區職訓"));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS, currentZoom));
        //----------取得定位許可-----------------------
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //----顯示我的位置ICO-------
            map.setMyLocationEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(), "GPS定位權限未允許", Toast.LENGTH_LONG).show();
        }
        //---------------------------------------------
    }
    private void checkRequiredPermission(Activity activity) {
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size() != 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new
                    String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

}

