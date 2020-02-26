package com.example.lifemap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShowMapActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    SQLiteDatabase db;
    int[] country_chk_id = {R.id.taiwan_cb, R.id.japan_cb, R.id.south_korea_cb, R.id.united_kingdom_cb, R.id.usa_cb,
                    R.id.china_cb, R.id.france_cb, R.id.canada_cb, R.id.vietnam_cb};
    int[] markerType_chk_id = {R.id.attraction_cb, R.id.food_cb};
    boolean company_flag = false;
    boolean markerType_flag = false;
    DatabaseExcute databaseExcute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showmap_seletcion);
        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        CheckBox country_chk = (CheckBox) findViewById(R.id.country_all_cb);
        country_chk.setOnCheckedChangeListener(this);
        for(int id:country_chk_id) {
            CheckBox chk = (CheckBox) findViewById(id);
            chk.setOnCheckedChangeListener(this);
        }
        CheckBox marker_type_chk = (CheckBox) findViewById(R.id.marker_type_all_cb);
        marker_type_chk.setOnCheckedChangeListener(this);
        for(int id:markerType_chk_id) {
            CheckBox chk = (CheckBox) findViewById(id);
            chk.setOnCheckedChangeListener(this);
        }
    }

    // CheckBox 改變
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.country_all_cb) {
            if(isChecked) {
                company_flag = false;
                for (int id : country_chk_id) {
                    CheckBox chk = (CheckBox) findViewById(id);
                    chk.setChecked(true);
                }

            } else {
                if (!company_flag) {
                    for (int id : country_chk_id) {
                        CheckBox chk = (CheckBox) findViewById(id);
                        chk.setChecked(false);
                    }
                }
            }
        }
        if(buttonView.getId() == R.id.marker_type_all_cb) {
            if(isChecked) {
                markerType_flag = false;
                for(int id:markerType_chk_id) {
                    CheckBox chk = (CheckBox) findViewById(id);
                    chk.setChecked(true);
                }
            } else {
                if(!markerType_flag) {
                    for (int id : markerType_chk_id) {
                        CheckBox chk = (CheckBox) findViewById(id);
                        chk.setChecked(false);
                    }
                }
            }
        }
        if(buttonView.getId() != R.id.country_all_cb) {
            for (int id : country_chk_id) {
                if (buttonView.getId() == id) {
                    if (!isChecked) {
                        company_flag = true;
                        CheckBox chk = (CheckBox) findViewById(R.id.country_all_cb);
                        chk.setChecked(false);
                    }
                }
            }
        }
        for(int id:markerType_chk_id) {
            if(buttonView.getId() == id) {
                if(!isChecked) {
                    markerType_flag = true;
                    CheckBox chk = (CheckBox) findViewById(R.id.marker_type_all_cb);
                    chk.setChecked(false);
                }
            }
        }
    }

    // Show Map
    public void showMap(View view) {

        String countrys=null;
        String markerTypes=null;

        for(int id:markerType_chk_id) {
            CheckBox chk = (CheckBox) findViewById(id);
            if(chk.isChecked()) {
                if(null != markerTypes) {
                    markerTypes = markerTypes + ",";
                    markerTypes = markerTypes + "'"+ chk.getText().toString() + "'";
                } else {
                    markerTypes = "'"+ chk.getText().toString() + "'";
                }
            }
        }

        for(int id:country_chk_id) {
            CheckBox chk = (CheckBox) findViewById(id);
            if(chk.isChecked()) {
                if(null != countrys) {
                    countrys = countrys + ",";
                    countrys = countrys + "'"+ chk.getText().toString() + "'";
                } else {
                    countrys = "'"+ chk.getText().toString() + "'";
                }
            }
        }

        if(null == countrys || null == markerTypes) {
            Toast.makeText(ShowMapActivity.this, "選項不可為空",Toast.LENGTH_SHORT).show();
        } else {
            List titleList = new ArrayList();
            List dateList = new ArrayList();
            List markerImageUuidList = new ArrayList();
            List longitudeList = new ArrayList();
            List latitudeList = new ArrayList();

            db = openOrCreateDatabase("pinDB", Context.MODE_PRIVATE, null);
            databaseExcute = new DatabaseExcute();
            Cursor cursor = databaseExcute.queryForShowMap(db, "pinDetail",countrys, markerTypes);
            if(cursor.moveToFirst()) {
                do {
                    titleList.add(cursor.getString(0));
                    dateList.add(cursor.getString(1));
                    markerImageUuidList.add(cursor.getString(5));
                    longitudeList.add(cursor.getString(6));
                    latitudeList.add(cursor.getString(7));
                } while (cursor.moveToNext());
            }

            Intent intentMap = new Intent(ShowMapActivity.this, MapsActivity.class);
            intentMap.putExtra("entrance", "showPins");
            intentMap.putExtra("title", (Serializable) titleList);
            intentMap.putExtra("date", (Serializable) dateList);
            intentMap.putExtra("markerImageUuid", (Serializable) markerImageUuidList);
            intentMap.putExtra("longitude", (Serializable) longitudeList);
            intentMap.putExtra("latitude", (Serializable) latitudeList);
            startActivityForResult(intentMap, 101);
        }
    }

    // 返回
    public void goBackByShowMap(View view) {
        finish();
    }
}
