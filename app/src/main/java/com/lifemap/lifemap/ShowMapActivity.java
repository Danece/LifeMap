package com.example.lifemap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShowMapActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    int[] country_chk_id = {
                    R.id.taiwan_cb, R.id.japan_cb, R.id.south_korea_cb, R.id.united_kingdom_cb, R.id.usa_cb, R.id.thailand_cb,
                    R.id.china_cb, R.id.france_cb, R.id.canada_cb, R.id.vietnam_cb, R.id.greece_cb, R.id.egypt_cb,
                    R.id.russia_cb, R.id.germany_cb, R.id.italy_cb, R.id.new_zealand_cb, R.id.hong_kong_cb, R.id.australia_cb};
    int[] markerType_chk_id = {R.id.attraction_cb, R.id.food_cb};
    boolean company_flag = false;
    boolean markerType_flag = false;
    DatabaseExcute databaseExcute;
    SQLiteDatabase db;  // 資料庫物件
    static final String db_name = "pinDB";      // 資料庫名稱
    static final String tb_name = "pinDetail";  // 資料表名稱
    private ProgressBar mLoadingBar;

    // 鎖住操作返回動作
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.ECLAIR) {
                event.startTracking();
            } else {
                onBackPressed(); // 是其他按鍵則再Call Back方法
            }
        }
        return false;
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showmap_seletcion);
        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Loading close
        mLoadingBar = (ProgressBar) this.findViewById(R.id.loadingProcessBar_showMap);
        mLoadingBar.setVisibility(View.GONE);

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

        TextView toolbarText = (TextView) findViewById(R.id.toolbarText_showMap);
        AssetManager mgr=getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/jf-open.ttf");//根據路徑得到Typeface
        toolbarText.setTypeface(tf);//設定字型

        // 取消 ActionBar
        getSupportActionBar().hide();
        // 取消狀態欄
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

        // Loading open
        mLoadingBar.setVisibility(View.VISIBLE);

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
            String result = getApplicationContext().getResources().getString(R.string.selection_can_not_null);
            Toast toast = Toast.makeText(ShowMapActivity.this, result, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            List titleList = new ArrayList();
            List dateList = new ArrayList();
            List markerImageUuidList = new ArrayList();
            List longitudeList = new ArrayList();
            List latitudeList = new ArrayList();

            databaseExcute = new DatabaseExcute();
            db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
            databaseExcute.checkAndCreateDB(db, tb_name);
            Cursor cursor = databaseExcute.queryForShowMap(db, "pinDetail", countrys, markerTypes);
            if (cursor.moveToFirst()) {
                do {
                    titleList.add(cursor.getString(0));
                    dateList.add(cursor.getString(1));
                    markerImageUuidList.add(cursor.getString(5));
                    longitudeList.add(cursor.getString(6));
                    latitudeList.add(cursor.getString(7));
                } while (cursor.moveToNext());
            }

            if (0 < titleList.size()) {
                Intent intentMap = new Intent(ShowMapActivity.this, MapsActivity.class);
                intentMap.putExtra("entrance", "showPins");
                intentMap.putExtra("title", (Serializable) titleList);
                intentMap.putExtra("date", (Serializable) dateList);
                intentMap.putExtra("markerImageUuid", (Serializable) markerImageUuidList);
                intentMap.putExtra("longitude", (Serializable) longitudeList);
                intentMap.putExtra("latitude", (Serializable) latitudeList);
                startActivityForResult(intentMap, 101);
            } else {
                String result = getApplicationContext().getResources().getString(R.string.search_null);
                Toast toast = Toast.makeText(ShowMapActivity.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                // Loading close
                mLoadingBar.setVisibility(View.GONE);
            }
        }
    }

    // 返回
    public void goBackByShowMap(View view) {
        finish();
    }

    // 接Activity 回傳結果
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(Activity.RESULT_OK == resultCode) {
            if(101 == requestCode) {
                // Loading close
                mLoadingBar.setVisibility(View.GONE);
            }
        }
    }
}
