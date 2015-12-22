package com.example.administrator.work7;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import java.text.DecimalFormat;

public class MainActivity extends Activity implements Handler.Callback {
    //显示百度地图的视图控件继承于ViewGroup
    MapView mMapView = null;
    //百度地图控件
    BaiduMap mBaiduMap = null;
    //GPS定位的标注点
    private Marker mMarkerGPS;
    //GSM定位的标注点
    private Marker mMarkerGSM;
    //百度地图的坐标点
    private LatLng position = null;
    //bitmap 信息
    BitmapDescriptor bd = null;
    //基站经度
    private Double gsmLng = 0.0;
    //基站纬度
    private Double gsmLat = 0.0;
    //GPS经度
    private Double gpsLng = 0.0;
    //GPS纬度
    private Double gpsLat = 0.0;
    //handler
    private Handler locationHandler;
    private Bitmap bmp;

    //gps
    public final static int Gps_Location = 1;
    //gsm
    public final static int Gsm_Location = 2;
    //gsm菜单
    private final int MENU_GSM = 1;
    //gps菜单
    private final int MENU_GPS = 2;
    //平均值菜单
    private final int MENU_DIS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        //初始化全局 bitmap 信息，不用时及时 recycle
        bd = BitmapDescriptorFactory.fromResource(R.drawable.a);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //卫星地图
        //    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        position = new LatLng(23.148059, 113.329632);
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(position)
                .zoom(16)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        //为地图上的坐标点创建标注
        MarkerOptions ooGPS = new MarkerOptions().position(position).icon(bd)
                .zIndex(9).draggable(true);
        mMarkerGPS = (Marker) (mBaiduMap.addOverlay(ooGPS));
        MarkerOptions ooGSM = new MarkerOptions().position(position).icon(bd)
                .zIndex(9).draggable(true);
        mMarkerGSM = (Marker) (mBaiduMap.addOverlay(ooGSM));
        locationHandler = new Handler(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy 时执行 mMapView.onDestroy(),实现地图生命周期管理
        mMapView.onDestroy();
        //回收 bitmap 资源
        bd.recycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume() ,实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause(),实现地图生命周期管理
        mMapView.onPause();
    }

    //接收信息处理


    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case Gps_Location:
                //获得GPS定位数据
                gpsLng = msg.getData().getDouble("longitude");
                gpsLat = msg.getData().getDouble("latitude");
                //根据定位信息更新地图位置
                position = new LatLng(gpsLat, gpsLng);
                Log.d("00385", " " + position.latitude + ":" + position.longitude);
                //定义地图状态
                MapStatus mMapStatusGPS = new MapStatus.Builder()
                        .target(position)
                        .zoom(18)
                        .build();
                MapStatusUpdate mMapStatusUpdateGPS = MapStatusUpdateFactory.newMapStatus(mMapStatusGPS);
                //改变地图状态
                mBaiduMap.animateMapStatus(mMapStatusUpdateGPS);
                //显示定位的标注点
                mMarkerGPS = null;
                MarkerOptions ooGPS = new MarkerOptions().position(position).icon(bd)
                        .zIndex(9).draggable(true);
                mMarkerGPS = (Marker) (mBaiduMap.addOverlay(ooGPS));
                Toast.makeText(this, "GPS定位成功！", Toast.LENGTH_SHORT).show();
                break;
            case Gsm_Location:
                //获得基站定位数据
                gsmLng = msg.getData().getDouble("longitude");
                gsmLat = msg.getData().getDouble("latitude");
                //根据定位信息更新地图位置
                position = new LatLng(gsmLat, gsmLng);
                //定义地图状态
                MapStatus mMapStatusGSM = new MapStatus.Builder()
                        .target(position)
                        .zoom(18)
                        .build();
                MapStatusUpdate mMapStatusUpdateGSM = MapStatusUpdateFactory.newMapStatus(mMapStatusGSM);
                //改变地图状态
                mBaiduMap.animateMapStatus(mMapStatusUpdateGSM);
                //显示定位的标注点
                mMarkerGSM = null;
                MarkerOptions ooGSM = new MarkerOptions().position(position).icon(bd)
                        .zIndex(9).draggable(true);
                mMarkerGSM = (Marker) (mBaiduMap.addOverlay(ooGSM));
                Toast.makeText(this, "基站定位成功！", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }

        return false;
    }

    //创建菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_GPS, 0, "GPS定位");
        menu.add(0, MENU_GSM, 0, "基站定位");
        menu.add(0, MENU_DIS, 9, "误差计算");

        return super.onCreateOptionsMenu(menu);

    }

    //菜单事件
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_GPS) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "请开启GPS!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                return false;
            }
            //使用GPS定位
            gpsLocation(locationManager);
            return true;
        } else if (item.getItemId() == MENU_GSM) {
            //使用基站GSM定位
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            gsmLocation(locationManager);

        } else if (item.getItemId() == MENU_DIS) {
            //误差计算
            if (gpsLng != 0.0 && gsmLng != 0.0) {
                double result = getDistance(gpsLat, gpsLng, gsmLat, gsmLng);
                DecimalFormat df = new DecimalFormat(".##");
                Toast.makeText(this, "误差值为：" + df.format(result) + "米", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "请先使用GPS和基站定位后才能计算误差", Toast.LENGTH_SHORT).show();
            }

        } else if (item.getItemId() == 4) {
            position = new LatLng(23.148059, 113.329632);
            //定义地图状态
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(position)
                    .zoom(16)
                    .build();
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //改变地图状态
            mBaiduMap.setMapStatus(mMapStatusUpdate);
        }
        return super.onOptionsItemSelected(item);
    }

    //误差计算
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];

    }

    //使用gsm定位
    private void gsmLocation(LocationManager tm) {

        Location location = tm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = tm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location != null) {
            Bundle bundle = new Bundle();
            bundle.putDouble("longitude", location.getLongitude());
            bundle.putDouble("latitude", location.getLatitude());
            Message gsm_Msg = Message.obtain(locationHandler, Gsm_Location);
            //设置消息体数据
            gsm_Msg.setData(bundle);
            //发送消息
            gsm_Msg.sendToTarget();

        } else {
            Toast.makeText(MainActivity.this, "基站获取失败！" + "请确保AGPS，GPS已被打开", Toast.LENGTH_SHORT).show();
        }
    }

    private void gpsLocation(LocationManager local) {

        local.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, Gps_locationListener);
    }

    //GPS位置变化监听
    private LocationListener Gps_locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //获取位置信息
            Bundle bundle = new Bundle();
            bundle.putDouble("longitude",location.getLongitude());
            bundle.putDouble("latitude",location.getLatitude());

            Message gps_Msg = Message.obtain(locationHandler,Gps_Location);
            //设置消息体数据
            gps_Msg.setData(bundle);
            //发送信息
            gps_Msg.sendToTarget();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    protected boolean isRouteDisplayed(){
        return true;
    }
}
