package com.lifeMap.lifemap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lifeMap.lifemap.DIY_Kit.PickerView;
import com.lifeMap.lifemap.model_view.PinDetail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    SQLiteDatabase db;
    private DatabaseExcute databaseExcute;
    static final String db_name = "pinDB";      // 資料庫名稱
    static final String tb_name = "pinDetail";  // 資料表名稱
    PickerView country_pv ;
    private PinDetail oldInfo;

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
        setContentView(R.layout.activity_edit_info);
        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();

        // 初始化
        EditText titleEt = (EditText) findViewById(R.id.titleEt);
        titleEt.setText(intent.getStringExtra("title"));
        Button selectCountryBtn = (Button) findViewById(R.id.selectCountryBtn);
        selectCountryBtn.setText(intent.getStringExtra("country"));
        ImageView imageView = (ImageView) findViewById(R.id.countryLogoIv);
        if(getApplicationContext().getResources().getString(R.string.taiwan).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.taiwan_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.japan).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.japan_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.south_korea).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.south_korea_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.united_kingdom).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.united_kingdom_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.united_states_of_america).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.united_states_of_america_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.vietnam).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.vietnam_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.china).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.chin_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.france).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.france_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.canada).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.canada_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.egypt).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.egypt_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.russia).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.russia_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.germany).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.germany_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.hong_kong).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.hong_kong_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.greece).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.greece_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.new_zealand).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.new_zealand_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.australia).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.australia_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.italy).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.italy_flag_icon);

        } else if(getApplicationContext().getResources().getString(R.string.thailand).equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.thailand_flag_icon);

        }

        RadioGroup marker_type_group = (RadioGroup) findViewById(R.id.marker_type_group);
        if("景點".equals(intent.getStringExtra("markerType"))) {
            marker_type_group.check(findViewById(R.id.attractions_radioButton).getId());
        } else {
            marker_type_group.check(findViewById(R.id.food_radioButton).getId());
        }

        oldInfo = new PinDetail();
        oldInfo.setTitle(intent.getStringExtra("title"));
        oldInfo.setDate(intent.getStringExtra("date"));
        oldInfo.setCountry(intent.getStringExtra("country"));
        oldInfo.setMarkerType(intent.getStringExtra("markerType"));
        oldInfo.setMarkerImageUuid(intent.getStringExtra("markerImageUuid"));
        oldInfo.setLongitude(Double.parseDouble(intent.getStringExtra("longitude")));
        oldInfo.setLatitude(Double.parseDouble(intent.getStringExtra("latitude")));

        TextView toolbarText = (TextView) findViewById(R.id.toolbarText_edit);
        AssetManager mgr=getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/jf-open.ttf");//根據路徑得到Typeface
        toolbarText.setTypeface(tf);//設定字型

        // 取消 ActionBar
        getSupportActionBar().hide();
        // 取消狀態欄
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // 國家選單
    public void selectCountry(View view) {
        LayoutInflater inflater = LayoutInflater.from(EditActivity.this);
        View countryPickerView = getLayoutInflater().inflate(R.layout.pickerview_country, null);
        final String[] selected = new String[1];
        country_pv  = (PickerView)  countryPickerView.findViewById(R.id.country_pv);
        List data  = new ArrayList();
        data.add(getApplicationContext().getResources().getString(R.string.japan));
        data.add(getApplicationContext().getResources().getString(R.string.south_korea));
        data.add(getApplicationContext().getResources().getString(R.string.united_kingdom));
        data.add(getApplicationContext().getResources().getString(R.string.united_states_of_america));
        data.add(getApplicationContext().getResources().getString(R.string.vietnam));
        data.add(getApplicationContext().getResources().getString(R.string.china));
        data.add(getApplicationContext().getResources().getString(R.string.france));
        data.add(getApplicationContext().getResources().getString(R.string.canada));
        data.add(getApplicationContext().getResources().getString(R.string.taiwan));
        data.add(getApplicationContext().getResources().getString(R.string.egypt));
        data.add(getApplicationContext().getResources().getString(R.string.australia));
        data.add(getApplicationContext().getResources().getString(R.string.germany));
        data.add(getApplicationContext().getResources().getString(R.string.hong_kong));
        data.add(getApplicationContext().getResources().getString(R.string.russia));
        data.add(getApplicationContext().getResources().getString(R.string.italy));
        data.add(getApplicationContext().getResources().getString(R.string.new_zealand));
        data.add(getApplicationContext().getResources().getString(R.string.greece));
        data.add(getApplicationContext().getResources().getString(R.string.thailand));

        country_pv . setData ( data );
        country_pv . setOnSelectListener ( new  PickerView.onSelectListener()
        {
            @Override
            public void  onSelect ( String  text )
            {
                selected[0] = text;
            }
        });

        AssetManager mgr=getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/jf-open.ttf");//根據路徑得到Typeface

        AlertDialog.Builder dialog = new AlertDialog.Builder(EditActivity.this);
        dialog.setView(countryPickerView);
        TextView dialogTitle = (TextView) countryPickerView.findViewById(R.id.tvTitle_pickerView);
        dialogTitle.setTypeface(tf);//設定字型
        dialogTitle.setText(getApplicationContext().getResources().getString(R.string.pickerView_title_country));
        TextView dialogInfo = (TextView) countryPickerView.findViewById(R.id.tvInfo_pickerView);
        dialogInfo.setTypeface(tf);//設定字型
        dialogInfo.setText(getApplicationContext().getResources().getString(R.string.pickerView_info_country));

        dialog.setNegativeButton(getApplicationContext().getResources().getString(R.string.no),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            String result = getApplicationContext().getResources().getString(R.string.edit_none);
            Toast toast = Toast.makeText(EditActivity.this, result, Toast.LENGTH_SHORT);
            toast.show();
            }

        });
        dialog.setPositiveButton(getApplicationContext().getResources().getString(R.string.yes),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String result =  country_pv.getSelected();
                ImageView imageView = (ImageView) findViewById(R.id.countryLogoIv);
                Button button = (Button) findViewById(R.id.selectCountryBtn);
                if(getApplicationContext().getResources().getString(R.string.taiwan) == result) {
                    imageView.setImageResource(R.mipmap.taiwan_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.taiwan));

                } else if(getApplicationContext().getResources().getString(R.string.japan) == result) {
                    imageView.setImageResource(R.mipmap.japan_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.japan));

                } else if(getApplicationContext().getResources().getString(R.string.south_korea) == result) {
                    imageView.setImageResource(R.mipmap.south_korea_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.south_korea));

                } else if(getApplicationContext().getResources().getString(R.string.united_kingdom) == result) {
                    imageView.setImageResource(R.mipmap.united_kingdom_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.united_kingdom));

                } else if(getApplicationContext().getResources().getString(R.string.united_states_of_america) == result) {
                    imageView.setImageResource(R.mipmap.united_states_of_america_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.united_states_of_america));

                } else if(getApplicationContext().getResources().getString(R.string.vietnam) == result) {
                    imageView.setImageResource(R.mipmap.vietnam_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.vietnam));

                } else if(getApplicationContext().getResources().getString(R.string.china) == result) {
                    imageView.setImageResource(R.mipmap.chin_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.china));

                } else if(getApplicationContext().getResources().getString(R.string.france) == result) {
                    imageView.setImageResource(R.mipmap.france_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.france));

                } else if(getApplicationContext().getResources().getString(R.string.canada) == result) {
                    imageView.setImageResource(R.mipmap.canada_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.canada));

                } else if(getApplicationContext().getResources().getString(R.string.egypt) == result) {
                    imageView.setImageResource(R.mipmap.egypt_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.egypt));

                } else if(getApplicationContext().getResources().getString(R.string.russia) == result) {
                    imageView.setImageResource(R.mipmap.russia_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.russia));

                } else if(getApplicationContext().getResources().getString(R.string.germany) == result) {
                    imageView.setImageResource(R.mipmap.germany_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.germany));

                } else if(getApplicationContext().getResources().getString(R.string.hong_kong) == result) {
                    imageView.setImageResource(R.mipmap.hong_kong_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.hong_kong));

                } else if(getApplicationContext().getResources().getString(R.string.greece) == result) {
                    imageView.setImageResource(R.mipmap.greece_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.greece));

                } else if(getApplicationContext().getResources().getString(R.string.new_zealand) == result) {
                    imageView.setImageResource(R.mipmap.new_zealand_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.new_zealand));

                } else if(getApplicationContext().getResources().getString(R.string.australia) == result) {
                    imageView.setImageResource(R.mipmap.australia_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.australia));

                } else if(getApplicationContext().getResources().getString(R.string.italy) == result) {
                    imageView.setImageResource(R.mipmap.italy_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.italy));

                } else if(getApplicationContext().getResources().getString(R.string.thailand) == result) {
                    imageView.setImageResource(R.mipmap.thailand_flag_icon);
                    button.setText(getApplicationContext().getResources().getString(R.string.thailand));

                }
            }
        });
        dialog.show();
    }

    // 返回
    public void goBack(View view) {
        finish();
    }

    // 更新
    public void doUpdate(View view) {
        EditText title = (EditText) findViewById(R.id.titleEt);
        Button button = (Button) findViewById(R.id.selectCountryBtn);
        String country =  button.getText().toString();
        String markerType = null;
        RadioGroup markerTypeGroup = (RadioGroup) findViewById(R.id.marker_type_group);
        switch (markerTypeGroup.getCheckedRadioButtonId()) {
            case R.id.attractions_radioButton:
                markerType = "景點";
                break;
            case R.id.food_radioButton:
                markerType = "美食";
                break;
        }

        db = openOrCreateDatabase(db_name, 0, null);
        databaseExcute = new DatabaseExcute();
        PinDetail newInfo = new PinDetail();
        newInfo.setTitle(title.getText().toString());
        newInfo.setCountry(country);
        newInfo.setMarkerType(markerType);
        databaseExcute.updateForEditPinInfo(db,tb_name,oldInfo,newInfo);
        setResult(RESULT_OK, null);
        finish();
    }

    // Show Map
    public void showMap(View view) {
        List titleList = new ArrayList();
        List dateList = new ArrayList();
        List markerImageUuidList = new ArrayList();
        List longitudeList = new ArrayList();
        List latitudeList = new ArrayList();

        titleList.add(oldInfo.getTitle());
        dateList.add(oldInfo.getDate());
        markerImageUuidList.add(oldInfo.getMarkerImageUuid());
        longitudeList.add(oldInfo.getLongitude());
        latitudeList.add(oldInfo.getLatitude());

        Log.d("test",oldInfo.getTitle());
        Log.d("test",oldInfo.getDate());
        Log.d("test",oldInfo.getMarkerImageUuid());
        Log.d("test",oldInfo.getLongitude().toString());
        Log.d("test",oldInfo.getLatitude().toString());

        Intent intentMap = new Intent(EditActivity.this, MapsActivity.class);
        intentMap.putExtra("entrance", "showSelectedPin");
        intentMap.putExtra("title", (Serializable) titleList);
        intentMap.putExtra("date", (Serializable) dateList);
        intentMap.putExtra("markerImageUuid", (Serializable) markerImageUuidList);
        intentMap.putExtra("longitude", (Serializable) longitudeList);
        intentMap.putExtra("latitude", (Serializable) latitudeList);
        startActivityForResult(intentMap, 101);
    }
}
