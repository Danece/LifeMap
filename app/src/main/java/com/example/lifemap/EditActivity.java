package com.example.lifemap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lifemap.DIY_Kit.PickerView;
import com.example.lifemap.model_view.PinDetail;

import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity {

    SQLiteDatabase db;
    private DatabaseExcute databaseExcute;
    static final String db_name = "pinDB";      // 資料庫名稱
    static final String tb_name = "pinDetail";  // 資料表名稱
    private PinDetail oldInfo;
    PickerView country_pv ;

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
        if("台灣".equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.taiwan_flag_icon);
        } else if("日本".equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.japan_flag_icon);
        } else if("南韓".equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.south_korea_flag_icon);
        } else if("英國".equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.united_kingdom_flag_icon);
        } else if("美國".equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.united_states_of_america_flag_icon);
        } else if("越南".equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.vietnam_flag_icon);
        } else if("中國".equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.chin_flag_icon);
        } else if("法國".equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.france_flag_icon);
        } else if("加拿大".equals(intent.getStringExtra("country"))) {
            imageView.setImageResource(R.mipmap.canada_flag_icon);
        }
        RadioGroup marker_type_group = (RadioGroup) findViewById(R.id.marker_type_group);
        if("景點".equals(intent.getStringExtra("markerType"))) {
            marker_type_group.check(findViewById(R.id.attractions_radioButton).getId());
        } else {
            marker_type_group.check(findViewById(R.id.food_radioButton).getId());
        }


        oldInfo = new PinDetail();
        oldInfo.setTitle(intent.getStringExtra("title"));
        oldInfo.setCountry(intent.getStringExtra("country"));
        oldInfo.setMarkerType(intent.getStringExtra("markerType"));
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

    // 國家選單
    public void selectCountry(View view) {
        LayoutInflater inflater = LayoutInflater.from(EditActivity.this);
        final View v = inflater.inflate(R.layout.pickerview_country, null);
        final String[] selected = new String[1];
        country_pv  = (PickerView)  v.findViewById(R.id.country_pv);
        List data  = new ArrayList();
        data.add("台灣");
        data.add("日本");
        data.add("南韓");
        data.add("英國");
        data.add("美國");
        data.add("越南");
        data.add("中國");
        data.add("法國");
        data.add("加拿大");
        country_pv . setData ( data );
        country_pv . setOnSelectListener ( new  PickerView.onSelectListener()
        {
            @Override
            public void  onSelect ( String  text )
            {
                selected[0] = text;
                Toast. makeText ( EditActivity.this , "選擇了" +  text ,
                        Toast . LENGTH_SHORT ). show ();
            }
        });

        AlertDialog.Builder dialog = new AlertDialog.Builder(EditActivity.this);
        dialog.setTitle("國家選擇");
        dialog.setMessage("請選擇目前所在國家");
        dialog.setView(v);
        dialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(EditActivity.this, "未做修改",Toast.LENGTH_SHORT).show();
            }

        });
        dialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                String result =  country_pv.getSelected();
                ImageView imageView = (ImageView) findViewById(R.id.countryLogoIv);
                Button button = (Button) findViewById(R.id.selectCountryBtn);
                if("台灣" == result) {
                    imageView.setImageResource(R.mipmap.taiwan_flag_icon);
                    button.setText("台灣");
                } else if("日本" == result) {
                    imageView.setImageResource(R.mipmap.japan_flag_icon);
                    button.setText("日本");
                } else if("南韓" == result) {
                    imageView.setImageResource(R.mipmap.south_korea_flag_icon);
                    button.setText("南韓");
                } else if("英國" == result) {
                    imageView.setImageResource(R.mipmap.united_kingdom_flag_icon);
                    button.setText("英國");
                } else if("美國" == result) {
                    imageView.setImageResource(R.mipmap.united_states_of_america_flag_icon);
                    button.setText("美國");
                } else if("越南" == result) {
                    imageView.setImageResource(R.mipmap.vietnam_flag_icon);
                    button.setText("越南");
                } else if("中國" == result) {
                    imageView.setImageResource(R.mipmap.chin_flag_icon);
                    button.setText("中國");
                } else if("法國" == result) {
                    imageView.setImageResource(R.mipmap.france_flag_icon);
                    button.setText("法國");
                } else if("加拿大" == result) {
                    imageView.setImageResource(R.mipmap.canada_flag_icon);
                    button.setText("加拿大");
                }
            }
        });
        dialog.show();
    }

    // 返回
    public void goBack(View view) {
        finish();
    }
}
