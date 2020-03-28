package com.example.lifemap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifemap.DIY_Kit.DatePickerFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ExportCsvActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    int[] country_chk_id_csv = {
            R.id.taiwan_cb_csv, R.id.japan_cb_csv, R.id.south_korea_cb_csv, R.id.united_kingdom_cb_csv, R.id.usa_cb_csv, R.id.thailand_cb_csv,
            R.id.china_cb_csv, R.id.france_cb_csv, R.id.canada_cb_csv, R.id.vietnam_cb_csv, R.id.greece_cb_csv, R.id.egypt_cb_csv,
            R.id.russia_cb_csv, R.id.germany_cb_csv, R.id.italy_cb_csv, R.id.new_zealand_cb_csv, R.id.hong_kong_cb_csv, R.id.australia_cb_csv};
    int[] markerType_chk_id_csv = {R.id.attraction_cb_csv, R.id.food_cb_csv};
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportcsv);
        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // For CheckBox
        CheckBox country_chk = (CheckBox) findViewById(R.id.country_all_cb_csv);
        country_chk.setOnCheckedChangeListener(this);
        for(int id:country_chk_id_csv) {
            CheckBox chk = (CheckBox) findViewById(id);
            chk.setOnCheckedChangeListener(this);
        }
        CheckBox marker_type_chk = (CheckBox) findViewById(R.id.marker_type_all_cb_csv);
        marker_type_chk.setOnCheckedChangeListener(this);
        for(int id:markerType_chk_id_csv) {
            CheckBox chk = (CheckBox) findViewById(id);
            chk.setOnCheckedChangeListener(this);
        }

        TextView toolbarText = (TextView) findViewById(R.id.toolbarText_exportCsv);
        AssetManager mgr=getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/jf-open.ttf");//根據路徑得到Typeface
        toolbarText.setTypeface(tf);//設定字型

        // 取消 ActionBar
        getSupportActionBar().hide();
        // 取消狀態欄
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView date = (TextView) findViewById(R.id.tvDate_csv);
        if (date.getText().toString().equals(getApplicationContext().getResources().getString(R.string.select_date_title))) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            date.setText(formatter.format(new Date()));
        }
    }

    // 產生CSV檔案
    public void exportCvs(View view){

        TextView date = (TextView) findViewById(R.id.tvDate_csv);

        String countrys=null;
        String markerTypes=null;

        for(int id:markerType_chk_id_csv) {
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

        for(int id:country_chk_id_csv) {
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

        if(null == countrys || null == markerTypes ) {
            String result = getApplicationContext().getResources().getString(R.string.selection_can_not_null);
            Toast toast = Toast.makeText(ExportCsvActivity.this, result, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            List titleList = new ArrayList();
            List dateList = new ArrayList();
            List longitudeList = new ArrayList();
            List latitudeList = new ArrayList();

            databaseExcute = new DatabaseExcute();
            db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
            databaseExcute.checkAndCreateDB(db, tb_name);
            Cursor cursor = databaseExcute.getDbAllInfo(db, tb_name, countrys, markerTypes, "'"+date.getText().toString()+"'");
            if (cursor.moveToFirst()) {
                do {
                    titleList.add(cursor.getString(0));
                    dateList.add(cursor.getString(1));
                    longitudeList.add(cursor.getString(6));
                    latitudeList.add(cursor.getString(7));
                } while (cursor.moveToNext());
            }
            databaseExcute.finish();

            StringBuilder data = new StringBuilder();
            String title_0 = getApplicationContext().getResources().getString(R.string.cvs_title_name);
            String title_1 = getApplicationContext().getResources().getString(R.string.cvs_position);
            data.append(title_0 + "," + title_1);

            Log.d("XX", title_0 + "," + title_1);

            if (0 < titleList.size()) {
                for (int i = 0; i < titleList.size(); i++) {
                    String cell_0 = titleList.get(i) + "(" + dateList.get(i) + ")";
                    String cell_1 = "\"" + longitudeList.get(i) + "," + latitudeList.get(i) + "\"";
                    data.append("\n" + cell_0 + "," + cell_1);
                }

                try {
                    //saving the file into device
                    FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
                    out.write((data.toString()).getBytes());
                    out.close();

                    //exporting
                    Context context = getApplicationContext();
                    File filelocation = new File(getFilesDir(), "data.csv");
                    Uri path = FileProvider.getUriForFile(context, "com.example.lifemap.fileprovider", filelocation);
                    this.grantUriPermission(getPackageName(), path, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent fileIntent = new Intent(Intent.ACTION_SEND);
                    fileIntent.setType("text/csv");
                    fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
                    fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                    startActivity(fileIntent);
//                    startActivity(Intent.createChooser(fileIntent, "Send mail"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String result = getApplicationContext().getResources().getString(R.string.null_data_info);
                Toast toast = Toast.makeText(ExportCsvActivity.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    // CheckBox 改變
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d("XX", "IN-exportcsv");
        if(buttonView.getId() == R.id.country_all_cb_csv) {
            if(isChecked) {
                company_flag = false;
                for (int id : country_chk_id_csv) {
                    CheckBox chk = (CheckBox) findViewById(id);
                    chk.setChecked(true);
                }

            } else {
                if (!company_flag) {
                    for (int id : country_chk_id_csv) {
                        CheckBox chk = (CheckBox) findViewById(id);
                        chk.setChecked(false);
                    }
                }
            }
        }
        if(buttonView.getId() == R.id.marker_type_all_cb_csv) {
            if(isChecked) {
                markerType_flag = false;
                for(int id:markerType_chk_id_csv) {
                    CheckBox chk = (CheckBox) findViewById(id);
                    chk.setChecked(true);
                }
            } else {
                if(!markerType_flag) {
                    for (int id : markerType_chk_id_csv) {
                        CheckBox chk = (CheckBox) findViewById(id);
                        chk.setChecked(false);
                    }
                }
            }
        }
        if(buttonView.getId() != R.id.country_all_cb_csv) {
            for (int id : country_chk_id_csv) {
                if (buttonView.getId() == id) {
                    if (!isChecked) {
                        company_flag = true;
                        CheckBox chk = (CheckBox) findViewById(R.id.country_all_cb_csv);
                        chk.setChecked(false);
                    }
                }
            }
        }
        for(int id:markerType_chk_id_csv) {
            if(buttonView.getId() == id) {
                if(!isChecked) {
                    markerType_flag = true;
                    CheckBox chk = (CheckBox) findViewById(R.id.marker_type_all_cb_csv);
                    chk.setChecked(false);
                }
            }
        }
    }

    // 選擇時間
    public void selectDate(View view) {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getFragmentManager(), "DatePicker");

    }

    public void goBack(View view) {
        finish();
    }
}
