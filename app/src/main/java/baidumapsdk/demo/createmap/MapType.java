package baidumapsdk.demo.createmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import baidumapsdk.demo.R;

/**
 * 基础地图类型
 */
public class MapType extends AppCompatActivity {

    // MapView 是地图主控件
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private boolean isFirstLocate=true;
    private LocationClient locationClient=null;
    private MyLocationListener myListener=new MyLocationListener();
    private  CheckBox heat_map;
    private CheckBox road_map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map_type);
        locationClient=new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());

        mMapView = findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setMyLocationEnabled(true);
        requestLocation();
        //若无法获得位置，默认天安门为初始位置
        double latitude=39.915071,longitude=116.403907;
        if(mBaiduMap.getLocationData()!=null){
            latitude=mBaiduMap.getLocationData().latitude;
            longitude=mBaiduMap.getLocationData().longitude;
        }

        // 构建地图状态
        MapStatus.Builder builder = new MapStatus.Builder();
        LatLng center = new LatLng(latitude, longitude);
        // 默认 11级
        float zoom = 11.0f;

        // 该Intent是OfflineDemo中查看离线地图调起的
        Intent intent = getIntent();
        if (null != intent) {
            center = new LatLng(intent.getDoubleExtra("y", latitude),
                    intent.getDoubleExtra("x", longitude));
            zoom = intent.getFloatExtra("level", 11.0f);
        }

        builder.target(center).zoom(zoom);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(builder.build());

        // 设置地图状态
        mBaiduMap.setMapStatus(mapStatusUpdate);
        heat_map=this.findViewById(R.id.heat_map);
        road_map=this.findViewById(R.id.road_map);
        heat_map.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mBaiduMap.setBaiduHeatMapEnabled(b);
            }
        });
        road_map.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mBaiduMap.setTrafficEnabled(b);
            }
        });
    }

    /**
     * 设置底图显示模式
     */
    public void setMapMode(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            // 普通图
            case R.id.normal:
                if (checked) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                }
                break;
            // 卫星图
            case R.id.statellite:
                if (checked) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                }
                break;
            // 空白地图
            case R.id.none:
                if (checked) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 开启室内图
     */
    public void openIndoorView(View view){
        Intent intent=new Intent(MapType.this, IndoorMap.class);
        intent.putExtra("latitude",mBaiduMap.getLocationData().latitude);
        intent.putExtra("longitude",mBaiduMap.getLocationData().longitude);
        this.startActivity(intent);
    }
    /**
     * 清除地图缓存数据，支持清除普通地图和卫星图缓存，再次进入地图页面生效。
     */
    public void cleanMapCache(View view) {
        if (mBaiduMap == null){
            return;
        }
        int mapType = mBaiduMap.getMapType();
        if (mapType == BaiduMap.MAP_TYPE_NORMAL) {
            // // 清除地图缓存数据
            mBaiduMap.cleanCache(BaiduMap.MAP_TYPE_NORMAL);
        } else if (mapType == BaiduMap.MAP_TYPE_SATELLITE) {
            // 清除地图缓存数据
            mBaiduMap.cleanCache(BaiduMap.MAP_TYPE_SATELLITE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时必须调用mMapView. onResume ()
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时必须调用mMapView. onPause ()
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时必须调用mMapView.onDestroy()
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        locationClient.stop();
    }

    private void requestLocation(){
        initLocationOption();
        locationClient.start();
    }
    /**
     * 初始化定位参数配置
     */
    private void initLocationOption() {
        LocationClientOption locationClientOption=new LocationClientOption();
        MyLocationListener myLocationListener=new MyLocationListener();
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener);
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationClientOption.setCoorType("bd09ll");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationClientOption.setScanSpan(1000);
//可选，设置是否需要地址信息，默认不需要
        locationClientOption.setIsNeedAddress(true);
//可选，设置是否需要地址描述
        locationClientOption.setIsNeedLocationDescribe(true);
//可选，设置是否需要设备方向结果
        locationClientOption.setNeedDeviceDirect(false);
//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationClientOption.setLocationNotify(true);
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationClientOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationClientOption.setIsNeedLocationDescribe(true);
//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationClientOption.setIsNeedLocationPoiList(true);
//可选，默认false，设置是否收集CRASH信息，默认收集
        locationClientOption.SetIgnoreCacheException(false);
//可选，默认false，设置是否开启Gps定位
        locationClientOption.setOpenGps(true);
//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationClientOption.setIsNeedAltitude(false);
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationClientOption.setOpenAutoNotifyMode();
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationClientOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationClientOption);
        locationClientOption.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
//开始定位
    }
    public class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location) {
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            float radius=location.getRadius();
            String coorType=location.getCoorType();
            int errorCode = location.getLocType();
            navigateTo(location);
        }
    }
    private void navigateTo(BDLocation location){
        if(isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            mBaiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }

        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.longitude(location.getLongitude());
        locationBuilder.latitude(location.getLatitude());
        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);
    }
}
