package com.lifeMap.lifemap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lifeMap.lifemap.R;
import com.lifeMap.lifemap.DIY_Kit.BitmapCut;
import com.lifeMap.lifemap.DIY_Kit.PickerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CreateNewPin extends AppCompatActivity {
    private final String PERMISSION_WRITE_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    DecimalFormat df = new DecimalFormat("##.000000");
    static final String db_name = "pinDB";      // 資料庫名稱
    static final String tb_name = "pinDetail";  // 資料表名稱
    SQLiteDatabase db;  // 資料庫物件
    String longitude;   // 經度
    String latitude;    // 緯度
    String today;       // 當天日期
    PickerView country_pv ;
    Bitmap resultPicture;   // 照片
    Bitmap markerImage;     // 標記圖片
    Bitmap markerImageForEdit;
    String markerImageUuid; // 標記圖片UUID
    String errorMessage;    // 錯誤訊息
    String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/captureImage/";// GPS
    DatabaseExcute databaseExcute;

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

        TextView toolbarText = (TextView) findViewById(R.id.toolbarText_createNewPin);
        AssetManager mgr=getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/jf-open.ttf");//根據路徑得到Typeface
        toolbarText.setTypeface(tf);//設定字型

        // 取消 ActionBar
        getSupportActionBar().hide();
        // 取消狀態欄
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // 開啟 Google Map
    public void openGoogleMap(View view) {
        try{
            RadioGroup markerStyle = (RadioGroup) findViewById(R.id.marker_style_group);
            Bitmap background = null;
            Bitmap foreground = resultPicture;
            Bitmap image = resultPicture;
            image = BitmapCut.imageToSquare(image, image.isRecycled());
            image = BitmapCut.removeCorner(image, 30);
            markerImageForEdit = image;
            switch (markerStyle.getCheckedRadioButtonId()) {
                case R.id.cube_radioButton:
                    foreground = BitmapCut.imageToSquare(foreground, foreground.isRecycled());
                    foreground = BitmapCut.removeCorner(foreground, 30);
                    background = BitmapFactory.decodeResource(getResources(),R.mipmap.marker_background);
                    foreground = BitmapCut.toConformBitmap(background, foreground, 0);
                    break;
                case R.id.sphere_radioButton:
                    foreground = BitmapCut.imageToSquare(foreground, foreground.isRecycled());
                    foreground = BitmapCut.imageToCircle(foreground);
                    background = BitmapFactory.decodeResource(getResources(),R.mipmap.marker_background2);
                    foreground = BitmapCut.toConformBitmap(background, foreground, 1);
                    break;
            }
            markerImage = foreground;
            Bundle bundle = new Bundle();
            bundle.putParcelable("bitmap", foreground);

            Intent intentMap = new Intent(CreateNewPin.this, MapsActivity.class);
            // 設定儲存的 Marker
            intentMap.putExtras(bundle);
            intentMap.putExtra("entrance", "createPin");
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
    public void openCamera(View view) {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent intent = new Intent(CreateNewPin.this, Camera2Activity.class);
        startActivityForResult(intent, 100);
    }

    // Activity 回傳結果
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(Activity.RESULT_OK == resultCode) {
            // 拍照
            if(100 == requestCode) {
                Bitmap bmp = getSmallBitmap(dir + "capture.jpeg");
//                Bitmap bmp = readFile(new File(dir + "capture.jpeg"));
                if (null != bmp) {
                    resultPicture = bmp;
                    // 開啟"定位"按鈕
                    Button positioningBtn = (Button) findViewById(R.id.positioningBtn);
                    positioningBtn.setEnabled(true);
                }
            }
            // GPS
            if(101 == requestCode) {
                Bundle extras = intent.getExtras();
                longitude = String.valueOf(df.format(extras.getDouble("longitude")));
                latitude = String.valueOf(df.format(extras.getDouble("latitude")));

            }
        } else {
            if(100 == requestCode) {
                String result = getApplicationContext().getResources().getString(R.string.take_picture_fail);
                Toast toast = Toast.makeText(CreateNewPin.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else if(101 == requestCode) {
                String result = getApplicationContext().getResources().getString(R.string.gps_fail);
                Toast toast = Toast.makeText(CreateNewPin.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    // Picker View
    public void selectCountry(View view) {
        LayoutInflater inflater = LayoutInflater.from(CreateNewPin.this);
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

        AlertDialog.Builder dialog = new AlertDialog.Builder(CreateNewPin.this);
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
                // TODO Auto-generated method stub
                String result = getApplicationContext().getResources().getString(R.string.edit_none);
                Toast toast = Toast.makeText(CreateNewPin.this, result, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        });
        dialog.setPositiveButton(getApplicationContext().getResources().getString(R.string.yes),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
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

    // 寫入資料庫
    public void writeDB() {
        EditText title = (EditText) findViewById(R.id.titleEt);
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
        cv.put("date", today);
        cv.put("country", country);
        cv.put("markerStyle", markerStyle);
        cv.put("markerType", markerType);
        cv.put("markerImageUuid", markerImageUuid);
        cv.put("longitude", longitude);
        cv.put("latitude", latitude);

        db.insert(tb_name, null, cv);
        db.close();
    }

    // 建立存放圖片的資料夾 & 存放圖片
    public void saveMarkerImage() {

        // Check LifeMap Folder Exist
        String dirMain = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap";
        File mainFile = new File(dirMain);

        // 資料夾是否存在，不存在則建立資料夾
        if(!mainFile.exists()) {
            mainFile.mkdirs();
        }

        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LifeMap/markerImage";
        File markerImageFile = new File(dir);
        markerImageUuid = UUID.randomUUID().toString();

        // 資料夾是否存在，不存在則建立資料夾
        if(!markerImageFile.exists()) {
            markerImageFile.mkdirs();
        }

        try {
            File file = new File(dir + "/" + markerImageUuid + ".png");
            FileOutputStream outputStream = new FileOutputStream(file);
            markerImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            File file2 = new File(dir + "/" + markerImageUuid + "_edit.png");
            FileOutputStream outputStream2 = new FileOutputStream(file2);
            markerImageForEdit.compress(Bitmap.CompressFormat.PNG, 100, outputStream2);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 確認是否需要請求寫入 File 的權限
    private boolean needCheckPermission() {
        //MarshMallow(API-23)之後要在 Runtime 詢問權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {PERMISSION_WRITE_STORAGE};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
            return true;
        }

        return false;
    }

    // 是否已請求過寫入 File 的權限
    private boolean hasPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return(ActivityCompat.checkSelfPermission(this, PERMISSION_WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    // 授權取得
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(">>>", "取得授權，可以執行動作了");
                    saveMarkerImage();
                }
            }
        }
    }

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
        // 國家
        String country =  country_pv.getSelected();
        if("choose".equals(country)) {
            errorMessage = "請選擇國家";
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
            //checkAndCreateDB();
            db = openOrCreateDatabase(db_name, Context.MODE_PRIVATE, null);
            databaseExcute = new DatabaseExcute();
            databaseExcute.checkAndCreateDB(db, tb_name);
            // 儲存 MarkerImage
            if (!hasPermission()) {
                if (needCheckPermission()) {
                    //如果須要檢查權限，由於這個步驟要等待使用者確認，
                    //所以不能立即執行儲存的動作，
                    //必須在 onRequestPermissionsResult 回應中才執行
                    return;
                }
            } else {
                saveMarkerImage();
            }
            // 寫入資料庫
            writeDB();
            String result = getApplicationContext().getResources().getString(R.string.create_success);
            Toast toast = Toast.makeText(CreateNewPin.this, result, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
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

    //**************
    // 讀檔案 (沒用到)
    public static Bitmap readFile(File file) {
        Bitmap bitmap = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //計算圖片的縮放值
    public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        Log.d("calculateInSampleSize", String.valueOf(inSampleSize));
        return inSampleSize;
    }

    // 根據路徑獲得圖片並壓縮，返回bitmap用於顯示
    public static Bitmap getSmallBitmap(String filePath) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            // Calculate inSampleSize
           // options.inSampleSize = calculateInSampleSize(options, 100, 330); // 480-800--100 330

            int yRatio = (int) Math.ceil(options.outHeight / 500);
            int xRatio = (int) Math.ceil(options.outWidth / 500);
            if (yRatio > 1 || xRatio > 1) {
                if (yRatio > xRatio) {
                    options.inSampleSize = yRatio;
                } else {
                    options.inSampleSize = xRatio;
                }
            }
            Log.d("XX-xRatio", String.valueOf(options.outHeight));
            Log.d("XX-yRatio", String.valueOf(options.outWidth));
            Log.d("XX-getSmallBitmap", String.valueOf(options.inSampleSize));

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            int degree = readPictureDegree(filePath);
            Bitmap editBitmap = BitmapFactory.decodeFile(filePath, options);
            Bitmap newbitmap = rotaingImageView(degree, editBitmap);
            return newbitmap;
        } catch (OutOfMemoryError e) {
            Log.d("getSmallBitmap-", "OutOfMemoryError");
            return null;
        }
    }

    /**
     * 讀取圖片屬性：旋轉的角度
     * @param path 圖片絕對路徑
     * @return degree旋轉的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    /*
     * 旋轉圖片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋轉圖片 動作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 建立新的圖片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }
}
