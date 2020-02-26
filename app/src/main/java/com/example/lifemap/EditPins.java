package com.example.lifemap;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lifemap.DIY_Kit.ViewAdapterForPins;
import com.example.lifemap.model_view.PinDetail;
import com.example.lifemap.DatabaseExcute;

import java.io.Serializable;
import java.util.ArrayList;
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
    int choosePosition = 0;
    private DatabaseExcute databaseExcute;

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
        String title_search = intent.getStringExtra("title_search");
        String country_search = intent.getStringExtra("country_search");
        String type_search = intent.getStringExtra("type_search");

        queryPinDB(title_search, country_search, type_search);
        ListView listView = (ListView) findViewById(R.id.pin_listView);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adapter = new ViewAdapterForPins(EditPins.this,titleList, dateList, countryList, markerTypeList, markerImageUuidList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onClickListView);
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
            Toast.makeText(EditPins.this,"選擇 "+titleList.get(position), Toast.LENGTH_SHORT).show();
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
            } while (cursor.moveToNext());
        }
        db.close();
    }

    // 返回
    public void goBack(View view) {
        finish();
    }

    // 清除資料
    public void clearPinData() {
        db = openOrCreateDatabase(db_name, 0, null);
        databaseExcute = new DatabaseExcute();
        PinDetail pinDetail = new PinDetail();
        pinDetail.setTitle(titleList.get(choosePosition).toString());
        pinDetail.setDate(dateList.get(choosePosition).toString());
        pinDetail.setCountry(countryList.get(choosePosition).toString());
        pinDetail.setMarkerType(markerTypeList.get(choosePosition).toString());
        databaseExcute.delete(db,tb_name,pinDetail);
        refresh();
    }

    // 刷新頁面
    public void refresh() {
        finish();
        Intent intent = new Intent(EditPins.this, EditSearch.class);
        startActivity(intent);
    }

    // Delete Pin Dialog
    public void deletePinDialog(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(EditPins.this);
        dialog.setTitle("刪除確認");
        dialog.setMessage("是否刪除:" + titleList.get(choosePosition) + "?");
        dialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(EditPins.this, "未做修改",Toast.LENGTH_SHORT).show();
            }

        });
        dialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                clearPinData();
                Toast.makeText(EditPins.this, "刪除:" + titleList.get(choosePosition),Toast.LENGTH_SHORT).show();
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

    // Activity 回傳結果
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(Activity.RESULT_OK == resultCode) {
            if(101 == requestCode) {
                Toast.makeText(this, "編輯成功", Toast.LENGTH_LONG).show();
                refresh();
            }
        } else if (Activity.RESULT_CANCELED == resultCode) {
            if(101 == requestCode) {
                Toast.makeText(this, "沒有編輯", Toast.LENGTH_LONG).show();
            }
        } else {
            if(101 == requestCode) {
                Toast.makeText(this, "編輯失敗", Toast.LENGTH_LONG).show();
            }
        }
    }

}
