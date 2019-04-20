package com.example.lifemap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifemap.DIY_Kit.BitmapCut;
import com.example.lifemap.DIY_Kit.PickerView;

import java.sql.SQLData;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateNewPin extends AppCompatActivity {
    DecimalFormat df = new DecimalFormat("##.000000");
    static final String db_name = "pinDB";      // 資料庫名稱
    static final String tb_name = "pinDetail";  // 資料表名稱
    SQLiteDatabase db;  // 資料庫物件
    String longitude;   // 經度
    String latitude;    // 緯度
    String today;       // 當天日期
    PickerView country_pv ;
    Bitmap resultPicture;   // 照片
    String errorMessage;    // 錯誤訊息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_pin);
        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 取當下日期
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        today = df.format(date);
        TextView dateTv =  findViewById(R.id.dateTv);
        dateTv.setText(today);
        // 初始化下拉選單
//        Spinner spinner = (Spinner) findViewById(R.id.country_Spinner);
        // 建立數據源
//        List<Country> countries = new ArrayList<Country>();
//        countries.add(new Country("Taiwan"));
//        countries.add(new Country("Japan"));
//        countries.add(new Country("South Korea"));
//        countries.add(new Country("United Kingdom"));
//        countries.add(new Country("USA"));
        // 建立 Adapter 綁定數據源
//        MyAdapter _MyAdapter = new MyAdapter(this, countries);
        // 綁定 Adapter
//        spinner.setAdapter(_MyAdapter);

    }

    // 開啟 Google Map
    public void openGoogleMap(View view) {
        try{
            RadioGroup markerStyle = (RadioGroup) findViewById(R.id.marker_style_group);
            Bitmap background = null;
            Bitmap foreground = resultPicture;
            switch (markerStyle.getCheckedRadioButtonId()) {
                case R.id.cube_radioButton:
                    foreground = BitmapCut.imageToSquare(foreground, foreground.isRecycled());
                    foreground = BitmapCut.removeCorner(foreground, 30);
                    background = BitmapFactory.decodeResource(getResources(),R.mipmap.marker_background);
                    foreground = BitmapCut.toConformBitmap(background, foreground, 0);
                    break;
                case R.id.sphere_radioButton:
                    foreground = BitmapCut.imageToCircle(foreground);
                    background = BitmapFactory.decodeResource(getResources(),R.mipmap.marker_background2);
                    foreground = BitmapCut.toConformBitmap(background, foreground, 1);
                    break;
            }
//            ImageView imageView = (ImageView) findViewById(R.id.resultPhotoIv);
//            Bitmap bmp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            Bundle bundle = new Bundle();
            bundle.putParcelable("bitmap", foreground);

            Intent intentMap = new Intent(CreateNewPin.this, MapsActivity.class);
            // 設定儲存的座標
            intentMap.putExtras(bundle);
            startActivityForResult(intentMap, 101);
        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    // 手指觸碰螢幕時觸發關閉虛擬鍵盤
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(null != getCurrentFocus() && null != getCurrentFocus().getWindowToken()) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return true;
    }

    // 開啟相機功能
    public void openCamere(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }

    // Activity 回傳結果
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(Activity.RESULT_OK == resultCode) {
            // 拍照
            if(100 == requestCode) {
                RadioGroup markerStyle = (RadioGroup) findViewById(R.id.marker_style_group);
                Bitmap background = null;
                Bundle extras = intent.getExtras();
                Bitmap bmp = (Bitmap) extras.get("data");
                resultPicture = bmp;
                // 開啟"定位"按鈕
                Button positioningBtn = (Button) findViewById(R.id.positioningBtn);
                positioningBtn.setEnabled(true);
//                ImageView imv = (ImageView) findViewById(R.id.resultPhotoIv);
//
//                switch (markerStyle.getCheckedRadioButtonId()) {
//                    case R.id.cube_radioButton:
//                        bmp = BitmapCut.imageToSquare(bmp, bmp.isRecycled());
//                        bmp = BitmapCut.removeCorner(bmp, 30);
//                        background = BitmapFactory.decodeResource(getResources(),R.mipmap.marker_background);
//                        bmp = BitmapCut.toConformBitmap(background, bmp, 0);
//                        break;
//                    case R.id.sphere_radioButton:
//                        bmp = BitmapCut.imageToCircle(bmp);
//                        background = BitmapFactory.decodeResource(getResources(),R.mipmap.marker_background2);
//                        bmp = BitmapCut.toConformBitmap(background, bmp, 1);
//                        break;
//                }
//                imv.setImageBitmap(bmp);
            }
            // GPS
            if(101 == requestCode) {
                Bundle extras = intent.getExtras();
//                TextView lng = (TextView) findViewById(R.id.lng_value);
//                lng.setText(String.valueOf(extras.getDouble("longitude")));
                longitude = String.valueOf(df.format(extras.getDouble("longitude")));
//                TextView lat = (TextView) findViewById(R.id.lat_value);
//                lat.setText(String.valueOf(extras.getDouble("latitude")));
                latitude = String.valueOf(df.format(extras.getDouble("latitude")));
            }
        } else {
            if(100 == requestCode) {
                Toast.makeText(this, "沒有拍到照片", Toast.LENGTH_LONG).show();
            } else if(101 == requestCode) {
                Toast.makeText(this, "GPS返回", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Picker View
    public void selectCountry(View view) {
        {
            LayoutInflater inflater = LayoutInflater.from(CreateNewPin.this);
            final View v = inflater.inflate(R.layout.pickerview_country, null);
            final String[] selected = new String[1];
            country_pv  = (PickerView)  v.findViewById(R.id.country_pv);
            List data  = new ArrayList();
            data.add("Taiwan");
            data.add("Japan");
            data.add("South Korea");
            data.add("United Kingdom");
            data.add("USA");
            country_pv . setData ( data );
            country_pv . setOnSelectListener ( new  PickerView.onSelectListener()
            {
                @Override
                public void  onSelect ( String  text )
                {
                    selected[0] = text;
                    Toast. makeText ( CreateNewPin.this , "選擇了" +  text  + "分" ,
                            Toast . LENGTH_SHORT ). show ();
                }
            });

            AlertDialog.Builder dialog = new AlertDialog.Builder(CreateNewPin.this);
            dialog.setTitle("國家選擇");
            dialog.setMessage("請選擇目前所在國家");
            dialog.setView(v);
            dialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    Toast.makeText(CreateNewPin.this, "未做修改",Toast.LENGTH_SHORT).show();
                }

            });
            dialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    String result =  country_pv.getSelected();
                    ImageView imageView = (ImageView) findViewById(R.id.countryLogoIv);
                    Button button = (Button) findViewById(R.id.selectCountryBtn);
                    if("Taiwan" == result) {
                        imageView.setImageResource(R.mipmap.taiwan_logo);
                        button.setText("Taiwan");
                    } else if("Japan" == result) {
                        imageView.setImageResource(R.mipmap.japan_logo);
                        button.setText("Japan");
                    } else if("South Korea" == result) {
                        imageView.setImageResource(R.mipmap.south_korea_logo);
                        button.setText("South Korea");
                    } else if("United Kingdom" == result) {
                        imageView.setImageResource(R.mipmap.united_kingdom_logo);
                        button.setText("United Kingdom");
                    } else if("USA" == result) {
                        imageView.setImageResource(R.mipmap.usa_logo);
                        button.setText("USA");
                    }

                    Toast.makeText(CreateNewPin.this, "選擇:"+result,Toast.LENGTH_SHORT).show();
                }

            });
            dialog.show();
        }
    }

    // 建立資料庫
    public void checkAndCreateDB() {
        // 開啟或建立資料庫
        db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
        String createTable = "CREATE TABLE IF NOT EXISTS " + tb_name +
                "(title VARCHAR(15), " +
                "content VARCHAR(60), " +
                "date VARCHAR(10), " +
                "country VARCHAR(20), " +
                "markerStyle VARCHAR(4), " +
                "markerType VARCHAR(4), " +
                "markerImageName VARCHAR(6), " +
                "longitude DOUBLE(20), " +
                "latitude DOUBLE(20))" ;
        db.execSQL(createTable);
        db.close();
    }

    // 寫入資料庫
    public void writeDB() {
        EditText title = (EditText) findViewById(R.id.titleEt);
        EditText content = (EditText) findViewById(R.id.contentEt);
        String country =  country_pv.getSelected();
        String markerStyle = null;
        RadioGroup markerStyleGroup = (RadioGroup) findViewById(R.id.marker_style_group);
        switch (markerStyleGroup.getCheckedRadioButtonId()) {
            case R.id.cube_radioButton:
                markerStyle = "方形";
                break;
            case R.id.sphere_radioButton:
                markerStyle = "圓形";
                break;
        }
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

        ContentValues cv = new ContentValues(9);
        cv.put("title", title.getText().toString());
        cv.put("content", content.getText().toString());
        cv.put("date", today);
        cv.put("country", country);
        cv.put("markerStyle", markerStyle);
        cv.put("markerType", markerType);
//        cv.put("markerImageName");
        cv.put("longitude", longitude);
        cv.put("latitude", latitude);
    }

    //

    // 檢查必填
    public boolean checkKeyValue() {
        // 標題
        EditText title = (EditText) findViewById(R.id.titleEt);
        if("".equals(title.getText().toString())) {
            errorMessage = "標題為必填";
            return false;
        }
        // 拍照
        if(null == resultPicture) {
            errorMessage = "請拍攝照片";
            return false;
        }
        // 定位
        if(null == longitude && null == latitude) {
            errorMessage = "請定位目前位置";
            return false;
        }
        return true;
    }

    // 取消按鈕
    public void doCancel(View view) {
        finish();
    }

    // 確認按鈕
    public void doConfirm(View view) {
        // 必填檢核
        if(checkKeyValue()) {
            // 資料庫建立
            checkAndCreateDB();
            finish();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(CreateNewPin.this);
            dialog.setMessage(errorMessage);
            dialog.setNegativeButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
        }
    }

}
