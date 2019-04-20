package com.example.lifemap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.lifemap.DIY_Kit.PickerView;
import com.example.lifemap.model_view.Country;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    // 按鈕觸發事件
    public void createNewPinClick(View view) {
        try{
            Intent intentMap = new Intent(MainActivity.this, CreateNewPin.class);
            startActivityForResult(intentMap, 0);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }
}
