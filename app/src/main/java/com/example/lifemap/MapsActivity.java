package com.example.lifemap;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lifemap.DIY_Kit.BitmapCut;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

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
    private Marker currentMarker, itemMarker;
    // GPS
    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private LocationManager lms;
    double lat, lng;
    DecimalFormat df = new DecimalFormat("##0.0000");
    //
    Bitmap markerIcon;

    // 初始化建立
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        markerIcon = (Bitmap) bundle.getParcelable("bitmap");

        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 建立 Google API 用戶端物件
        configGoogleApiClient();
        // 建立 Location 請求物件
        configLocationRequest();

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

        // 讀取記事儲存的座標
        Intent intent = getIntent();
//        showGPSContacts();
//        lat = intent.getDoubleExtra("lat", 0.0);
//        lng = intent.getDoubleExtra("lng", 0.0);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COARSE_LOCATION);
            return;
        }
        // 取目前位置經緯度資訊
        lms = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lat = location.getLatitude(); // 經度
        lng = location.getLongitude();// 緯度
        // 如果記事已經儲存座標
        if (0.0 != lat && 0.0 != lng) {
            // 建立座標物件
            LatLng itemPlace = new LatLng(lat, lng);
            // 加入地圖標記
            addMarker(itemPlace, "目前位置",
                    "經度:"+ df.format(lat)+" / 緯度:"+df.format(lng));
            // 移動地圖
            moveMap(itemPlace);
        } else {
            // 連線到 Google API 用戶端
            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
            }
        }
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
//                BitmapDescriptorFactory.fromResource(R.id.resultPhotoIv);
        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(place)
                .title(title)
                .snippet(snippet)
                .icon(icon);

        itemMarker = mMap.addMarker(markerOptions);
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
        if(errorCode == connectionResult.SERVICE_MISSING) {
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
        // 設定目前位置標記
        if(null == currentMarker) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        } else {
            currentMarker.setPosition(latLng);
        }
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
        if(!googleApiClient.isConnected() && null != currentMarker) {
            googleApiClient.connect();
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

}
