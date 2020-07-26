package com.lifeMap.lifemap;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lifeMap.lifemap.R;
import com.lifeMap.lifemap.DIY_Kit.ViewAdapterForPins;
import com.lifeMap.lifemap.model_view.PinDetail;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditPins extends AppCompatActivity {
    ViewAdapterForPins adapter;
    static final String db_name = "pinDB";      // 資料庫名稱
    static final String tb_name = "pinDetail";  // 資料表名稱
    SQLiteDatabase db;  // 資料庫物件
    List titleList = new ArrayList();
    List dateList = new ArrayList();
    List countryList = new ArrayList();
    List markerTypeList = new ArrayList();
    List markerImageUuidList = new ArrayList();
    List longitudeList = new ArrayList();
    List latitudeList = new ArrayList();
    int choosePosition = 0;
    private DatabaseExcute databaseExcute;
    String title_search = "";
    String country_search = "";
    String type_search = "";

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
        setContentView(R.layout.activity_edit_pins);
        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 讀取傳入參數
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        title_search = intent.getStringExtra("title_search");
        country_search = intent.getStringExtra("country_search");
        type_search = intent.getStringExtra("type_search");

        databaseExcute = new DatabaseExcute();
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        databaseExcute.checkAndCreateDB(db, tb_name);

        queryPinDB(title_search, country_search, type_search);
        ListView listView = (ListView) findViewById(R.id.pin_listView);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adapter = new ViewAdapterForPins(EditPins.this,titleList, dateList, countryList, markerTypeList, markerImageUuidList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onClickListView);

        // 查無資料則鎖住編輯與刪除按鈕
        if (0 == listView.getAdapter().getCount()) {
            Button editBtn = (Button) findViewById(R.id.editBtn);
            editBtn.setEnabled(false);
            Button deleteBtn = (Button) findViewById(R.id.deleteBtn);
            deleteBtn.setEnabled(false);
        }

        TextView itemCount = (TextView) findViewById(R.id.tvItemsCount);
        itemCount.setText(getApplicationContext().getResources().getString(R.string.item_count) + " : " + listView.getAdapter().getCount());

        TextView toolbarText = (TextView) findViewById(R.id.toolbarText_showPin);
        AssetManager mgr=getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/jf-open.ttf");//根據路徑得到Typeface
        toolbarText.setTypeface(tf);//設定字型

        // 取消 ActionBar
        getSupportActionBar().hide();
        // 取消狀態欄
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /***
     * 點擊ListView事件Method
     */
    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            choosePosition = position;
            adapter.setCurrentItem(position);
            adapter.setClick(true);
            adapter.notifyDataSetChanged();
            String result = getApplicationContext().getResources().getString(R.string.choose) +
                    titleList.get(position);
            Toast toast = Toast.makeText(EditPins.this, result, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    };

    // 查DB資料
    public void queryPinDB(String title, String country, String type) {
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);

        String sql = "SELECT * FROM pinDetail WHERE 1=1 ";
        if (!"".equals(title)) {
            sql = sql + "AND title LIKE '%" + title + "%'";
        }
        if (!"".equals(country)) {
            sql = sql + "AND country = '" + country + "' ";
        }
        if (!"全部".equals(type)) {
            sql = sql + "AND markerType = '" + type + "' ";
        }

        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()) {
            do {
                titleList.add(cursor.getString(0));
                dateList.add(cursor.getString(1));
                countryList.add(cursor.getString(2));
                markerTypeList.add(cursor.getString(4));
                markerImageUuidList.add(cursor.getString(5));
                longitudeList.add(cursor.getString(6));
                latitudeList.add(cursor.getString(7));
            } while (cursor.moveToNext());
        }
        db.close();
    }

    // 返回
    public void goBack(View view) {
        finish();Intent intent = new Intent(EditPins.this, EditSearch.class);
        startActivity(intent);
    }

    // 清除資料
    public void clearPinData() {
        // Delete DB Data Info
        db = openOrCreateDatabase(db_name, 0, null);
        databaseExcute = new DatabaseExcute();
        PinDetail pinDetail = new PinDetail();
        pinDetail.setTitle(titleList.get(choosePosition).toString());
        pinDetail.setDate(dateList.get(choosePosition).toString());
        pinDetail.setCountry(countryList.get(choosePosition).toString());
        pinDetail.setMarkerType(markerTypeList.get(choosePosition).toString());
        databaseExcute.delete(db, tb_name, pinDetail);

        // Delete Image Files
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/markerImage/";
        File file = new File(dir + markerImageUuidList.get(choosePosition).toString() + ".png");
        file.delete();
        File file2 = new File(dir + markerImageUuidList.get(choosePosition).toString() + "_edit.png");
        file2.delete();

        refresh();
    }

    // 刷新頁面
    public void refresh() {
        finish();
        Intent intent = new Intent(EditPins.this, EditPins.class);
        intent.putExtra("title_search", title_search);
        intent.putExtra("country_search", country_search);
        intent.putExtra("type_search", type_search);
        startActivityForResult(intent, 101);
        startActivity(intent);
    }

    // Delete Pin Dialog
    public void deletePinDialog(View view) {
        AssetManager mgr=getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/jf-open.ttf");//根據路徑得到Typeface

        AlertDialog.Builder dialog = new AlertDialog.Builder(EditPins.this);
        View customDialog = getLayoutInflater().inflate(R.layout.dialog_delete_confirm, null);
        dialog.setView(customDialog);
        TextView dialogTitle = (TextView) customDialog.findViewById(R.id.tVdeleteTitle);
        dialogTitle.setTypeface(tf);//設定字型
        dialogTitle.setText(getApplicationContext().getResources().getString(R.string.dialog_delete_title));
        TextView dialogInfo = (TextView) customDialog.findViewById(R.id.tVdeleteInfo);
        dialogInfo.setTypeface(tf);//設定字型
        dialogInfo.setText(getApplicationContext().getResources().getString(R.string.dialog_delete_info) + ":" + titleList.get(choosePosition) + " ?");

        dialog.setNegativeButton(getApplicationContext().getResources().getString(R.string.no),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                String result = getApplicationContext().getResources().getString(R.string.edit_none);
                Toast toast = Toast.makeText(EditPins.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                refresh();
            }

        });
        dialog.setPositiveButton(getApplicationContext().getResources().getString(R.string.yes),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                String result = getApplicationContext().getResources().getString(R.string.delete_pin_success) + ":" + titleList.get(choosePosition);
                Toast toast = Toast.makeText(EditPins.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                refresh();
                clearPinData();
            }

        });
        dialog.show();
    }

    // 修改標記資訊
    public void editPinInfo(View view) {
        Intent intentMap = new Intent(EditPins.this, EditActivity.class);
        intentMap.putExtra("title", titleList.get(choosePosition).toString());
        intentMap.putExtra("country", countryList.get(choosePosition).toString());
        intentMap.putExtra("markerType", markerTypeList.get(choosePosition).toString());
        startActivityForResult(intentMap, 101);
    }

    // 接Activity 回傳結果
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(Activity.RESULT_OK == resultCode) {
            if(101 == requestCode) {
                String result = getApplicationContext().getResources().getString(R.string.edit_success);
                Toast toast = Toast.makeText(EditPins.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                refresh();
            }
        } else if (Activity.RESULT_CANCELED == resultCode) {
            if(101 == requestCode) {
                String result = getApplicationContext().getResources().getString(R.string.edit_none);
                Toast toast = Toast.makeText(EditPins.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } else {
            if(101 == requestCode) {
                String result = getApplicationContext().getResources().getString(R.string.edit_fail);
                Toast toast = Toast.makeText(EditPins.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    // 排序反轉
    public void sortReverse (View view) {
        ListView listView = (ListView) findViewById(R.id.pin_listView);
        Collections.reverse(titleList);
        Collections.reverse(dateList);
        Collections.reverse(countryList);
        Collections.reverse(markerTypeList);
        Collections.reverse(markerImageUuidList);
        adapter = new ViewAdapterForPins(EditPins.this,titleList, dateList, countryList, markerTypeList, markerImageUuidList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onClickListView);
    }

}
