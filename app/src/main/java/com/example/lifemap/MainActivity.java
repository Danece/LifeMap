package com.example.lifemap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 顯示版本號碼
        TextView versionName = (TextView) findViewById(R.id.versionTv);
        versionName.setText("Version Code : "+getPackageName(getApplicationContext()));

        // 標題欄設定
        TextView toolbarText = (TextView) findViewById(R.id.toolbarText);
        AssetManager mgr=getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/jf-open.ttf");//根據路徑得到Typeface
        toolbarText.setTypeface(tf);//設定字型

        // 取消 ActionBar
        getSupportActionBar().hide();
        // 取消狀態欄
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (!checkLocationPermission()) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        createLifeMapFolder();
        createBackupFolder();

    }

    // 建立新標記
    public void createNewPinClick(View view) {
        try{
            if (!checkLocationPermission()) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                Intent intentMap = new Intent(MainActivity.this, CreateNewPin.class);
                startActivityForResult(intentMap, 0);
            }
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    // Show Map
    public void showAllPins(View view) {
        try{
            if (!checkLocationPermission()) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                Intent intentMap = new Intent(MainActivity.this, ShowMapActivity.class);
                startActivityForResult(intentMap, 101);
            }
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    // 編輯標記
    public void editPins(View view) {
        try{
            Intent intentMap = new Intent(MainActivity.this, EditSearch.class);
            startActivityForResult(intentMap, 102);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    // 備分&還原
    public void backupAndRestore(View view) {
        try{
            Intent intentMap = new Intent(MainActivity.this, BackupAndRestoreActivity.class);
            startActivityForResult(intentMap, 103);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    // 取得版本資訊
    public static String getPackageName (Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;

        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    // 取得版本號
    public static int getPackageCode (Context context) {
        PackageManager manager = context.getPackageManager() ;
        int code = 0 ;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName() , 0 ) ;
            code = info. versionCode ;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace() ;
        }
        return code ;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void createLifeMapFolder () {
        // Check LifeMap Folder Exist
        String dirMain = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/";
        File mainFile = new File(dirMain);

        // 資料夾是否存在，不存在則建立資料夾
        if(!mainFile.exists()) {
            mainFile.mkdir();
        }
    }

    public void createBackupFolder () {
        // Check LifeMap Folder Exist
        String dirMain = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/backup/";
        File mainFile = new File(dirMain);

        // 資料夾是否存在，不存在則建立資料夾
        if(!mainFile.exists()) {
            mainFile.mkdir();
        }
    }
}
