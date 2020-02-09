package com.example.lifemap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;

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
        versionName.setText(R.string.package_version_name + getPackageName());
    }

    // 建立新標記
    public void createNewPinClick(View view) {
        try{
            Intent intentMap = new Intent(MainActivity.this, CreateNewPin.class);
            startActivityForResult(intentMap, 0);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    // Show Map
    public void showAllPins(View view) {
        try{
            Intent intentMap = new Intent(MainActivity.this, ShowMapActivity.class);
            startActivityForResult(intentMap, 101);
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

    // 取得版本號
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
}
