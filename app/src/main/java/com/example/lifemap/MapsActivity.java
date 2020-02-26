package com.example.lifemap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lifemap.cluster.CustomClusterRenderer;
import com.example.lifemap.cluster.MyClusterItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks {
    private GoogleMap mMap;
    // Google API用戶端物件
    private GoogleApiClient googleApiClient;
    // Location 請求物件
    private LocationRequest locationRequest;
    // 記錄目前最新位置
    private Location currentLocation;
    // 顯示目前與儲存位置的標記物件
    public Marker currentMarker, itemMarker;
    List<Marker> markerList = new ArrayList<Marker>();
    // GPS
    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private LocationManager lms;
    double lat, lng;
    String entrance;
    DecimalFormat df = new DecimalFormat("##0.0000");
    // 標記圖
    Bitmap markerIcon;

    // 傳入參數For ShowMap
    List titleList = new ArrayList();
    List contentList = new ArrayList();
    List dateList = new ArrayList();
    List markerImageUuidList = new ArrayList();
    List longitudeList = new ArrayList();
    List latitudeList = new ArrayList();

    //
    private ClusterManager<MyClusterItem> mClusterManager;
    private int runCount=0;
    // 初始化建立
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        lms = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 讀取傳入參數
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        markerIcon = (Bitmap) bundle.getParcelable("bitmap");
        titleList = bundle.getParcelableArrayList("title");
        contentList = bundle.getParcelableArrayList("content");
        dateList = bundle.getParcelableArrayList("date");
        markerImageUuidList = bundle.getParcelableArrayList("markerImageUuid");
        longitudeList = bundle.getParcelableArrayList("longitude");
        latitudeList = bundle.getParcelableArrayList("latitude");
        entrance = intent.getStringExtra("entrance");

        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 建立 Google API 用戶端物件
        configGoogleApiClient();
        // 建立 Location 請求物件
        configLocationRequest();

        //
        Button refreshBtn = (Button) findViewById(R.id.refreshBtn);
        if (entrance.equals("showPins")) {
            refreshBtn.setVisibility(View.INVISIBLE );
        }
    }

    // 建立 Google API 用戶端物件
    private synchronized void configGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // 建立 Location 請求物件
    @SuppressLint("RestrictedApi")
    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        // 設定讀取位置的間隔時間為一秒(1000ms)
        locationRequest.setInterval(1000);
        // 設定讀取位置最快間隔時間為一秒
        locationRequest.setFastestInterval(1000);
        // 設定優先讀取高精確度的位置資訊(GPS)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initClusterManager();
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("TAG", "Can't find style. Error: ", e);
        }

        if (entrance.equals("createPin")) {
            createNewPinPositioning();
        } else if (entrance.equals("showPins")) {
            showAllPins();
            runCount++;
            if (runCount==2) mClusterManager.cluster();
        }
        processController();
    }

    // 建立標記定位
    private void createNewPinPositioning() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COARSE_LOCATION);
            return;
        }
        Location location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lat = location.getLatitude(); // 經度
        lng = location.getLongitude();// 緯度
        // 如果記事已經儲存座標
        if (0.0 != lat && 0.0 != lng) {
            // 建立座標物件
            LatLng itemPlace = new LatLng(lat, lng);
            // 加入地圖標記
            addMarker(itemPlace, "目前位置",
                    "經度:" + df.format(lat) + " / 緯度:" + df.format(lng));
            // 移動地圖
            moveMap(itemPlace);
        } else {
            // 連線到 Google API 用戶端
            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
            }
        }
    }

    // 展現所標標記地圖
    private void showAllPins() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COARSE_LOCATION);
            return;
        }
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/markerImage/";
        if (titleList.size() > 0 && null != titleList) {
            for (int i = 0; i < titleList.size(); i++) {
                // 建立座標物件
                Double latitude = Double.parseDouble(latitudeList.get(i).toString());
                Double longitude = Double.parseDouble(longitudeList.get(i).toString());
                LatLng itemPlace = new LatLng(latitude, longitude);
                markerIcon = BitmapFactory.decodeFile(dir + markerImageUuidList.get(i).toString() + ".png");
                mClusterManager.addItem(new MyClusterItem(titleList.get(i).toString(), dateList.get(i).toString(),itemPlace,markerIcon));
            }
        }
        // 取目前位置經緯度資訊
        lms = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lat = location.getLatitude(); // 經度
        lng = location.getLongitude();// 緯度
        LatLng itemPlace = new LatLng(lat, lng);
        moveMap(itemPlace);
        // 關閉更新Manager
        lms.removeUpdates(this);
    }

    // 移動地圖到指定位置
    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(17)
                        .build();
        // 使用動畫效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    // 在地圖加入指定位置與標題的標記
    private void addMarker(LatLng place, String title, String snippet) {

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(markerIcon);
        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(place)
                .title(title)
                .snippet(snippet)
                .icon(icon);

        itemMarker = mMap.addMarker(markerOptions);
        markerList.add(itemMarker);
    }

    // 連線 Google Services
    public void onConnected(Bundle bundle) {
        // 啟動位置更新服務
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) MapsActivity.this
        );
    }

    // Google Services 連線中斷
    public void onConnectionSuspended(int i) {
        // i -> 連線中斷代號

    }

    // Google Services 連線失敗
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // connectionResult -> 連線失敗的資訊
        int errorCode = connectionResult.getErrorCode();

        // 裝置無安裝 Google play 服務
        if (errorCode == connectionResult.SERVICE_MISSING) {
            Toast.makeText(this, R.string.common_google_play_services_unsupported_text,
                    Toast.LENGTH_LONG).show();
        }
    }

    // 位置改變
    @Override
    public void onLocationChanged(Location location) {
        // location -> 目前位置
        currentLocation = location;
        LatLng latLng = new LatLng(
                location.getLatitude(), location.getLongitude()
        );
        // 移動地圖到目前位置
        moveMap(latLng);
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

    // Google API 生命週期控制-Resume
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        // 連線到 Google API 用戶端
        if (!googleApiClient.isConnected() && null != currentMarker) {
            googleApiClient.connect();
        }
        String best = lms.getBestProvider(new Criteria(), true);
        if (null != best) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lms.requestLocationUpdates(best, 5000, 5, this);
        }
    }

    // Google API 生命週期控制-Pause
    @Override
    protected void onPause() {
        super.onPause();
        // 移除位置請求
        if(googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient,
                    (com.google.android.gms.location.LocationListener) this
            );
        }
    }

    // Google API 生命週期控制-Stop
    @Override
    protected void onStop() {
        super.onStop();
        // 移除 Google API 用戶端連線
        if(googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    // 提供使用者選擇標記與訊息框的操作功能
    private void processController() {
        // 對話框按鈕事件
        final DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            // 更新位置資訊
                            case DialogInterface.BUTTON_POSITIVE:
                                // 連線到 Google API 用戶端
                                if(!googleApiClient.isConnected()) {
                                    googleApiClient.connect();
                                }
                                break;
                            // 清除位置資訊
                            case DialogInterface.BUTTON_NEUTRAL:
                                Intent result = new Intent();
                                result.putExtra("lat", 0);
                                result.putExtra("lng", 0);
                                setResult(Activity.RESULT_OK, result);
                                finish();
                                break;
                            // 取消
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
        // 標記訊息框點擊事件
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // 如果是記事儲存的標記
                if(marker.equals(itemMarker)) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(MapsActivity.this);
                    ab.setTitle(R.string.title_update_location)
                            .setMessage(R.string.message_update_location)
                            .setCancelable(true);
                    ab.setPositiveButton(R.string.update, listener);
                    ab.setNeutralButton(R.string.clear, listener);
                    ab.setNegativeButton(R.string.cancel, listener);

                    ab.show();
                }
            }
        });

        // 標記點擊事件
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 如果是目前位置標記
                if(marker.equals(currentMarker)) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(MapsActivity.this);

                    ab.setTitle(R.string.title_current_location)
                            .setMessage(R.string.message_current_location)
                            .setCancelable(true);
                    ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent result = new Intent();
                            result.putExtra("lat", currentLocation.getLatitude());
                            result.putExtra("lng", currentLocation.getLongitude());
                            setResult(Activity.RESULT_OK, result);
                            finish();
                        }
                    });
                    ab.setNegativeButton(android.R.string.cancel, null);
                    ab.show();
                    return true;
                }
                return false;
            }
        });
    }

    // 判斷是否重設地圖
    private void setUpMapIfNeeded() {
        if(null == mMap) {
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

            if(null != mMap) {
                // 移除地圖設定
                processController();
            }
        }
    }

    // 移除地圖設定方法
    private void setUpMap() {
        // 建立位置的座標物件
        LatLng place = new LatLng(24.736128, 120.889648);
        // 移動地圖
        moveMap(place);
        // 加入地圖標記
        addMarker(place, "Hello",   "Google Map");
    }

    // Confirm
    public void doConfirm(View view) {
        Intent intent = new Intent();
        intent.putExtra("longitude", lng);
        intent.putExtra("latitude", lat);
        setResult(RESULT_OK, intent);
        finish();
    }

    // 重新查詢資料
    public void refresh(View view) {
        for(int i=0; i<markerList.size(); i++) {
            markerList.get(i).remove();
        }
        this.createNewPinPositioning();
    }

    // 群集設定
    public void initClusterManager() {
        mClusterManager = new ClusterManager<>(this, mMap);
        // 設定 item 外框
        CustomClusterRenderer renderer = new CustomClusterRenderer(this, mMap, mClusterManager);
        mClusterManager.setRenderer(renderer);

        // 點擊群集
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyClusterItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyClusterItem> cluster) {
                return false;
            }
        });
        // 點擊群集裡面的項目
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyClusterItem>() {
            @Override
            public boolean onClusterItemClick(MyClusterItem myClusterItem) {
                Toast.makeText(MapsActivity.this,"SSSs",Toast.LENGTH_LONG);
                return false;
            }
        });

        // 地圖的事件放入 ClusterManager
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
    }

}
