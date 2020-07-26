package com.lifeMap.lifemap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.lifeMap.lifemap.R;
import com.lifeMap.lifemap.DIY_Kit.PickerView;

import java.util.ArrayList;
import java.util.List;

public class EditSearch extends AppCompatActivity {

    private Spinner type_selection_spinner ;
    private ArrayAdapter<String> adapter;
    PickerView country_pv ;
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
        setContentView(R.layout.activity_edit_search);
        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Loading close
        mLoadingBar = (ProgressBar) this.findViewById(R.id.loadingProcessBar_search);
        mLoadingBar.setVisibility(View.GONE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        String[] type_group = getResources().getStringArray(R.array.type_selections);
        //將可選内容與ArrayAdapter連接起來
        adapter = new ArrayAdapter<String>(this,R.layout.spinner_selection_setting,type_group);
        //對應控件
        type_selection_spinner = (Spinner) findViewById(R.id.select_type);
        //設置下拉列表的風格
        adapter.setDropDownViewResource(R.layout.spinner_selection_setting);
        //將adapter 添加到spinner中
        type_selection_spinner.setAdapter(adapter);

        TextView toolbarText = (TextView) findViewById(R.id.toolbarText_editSearch);
        AssetManager mgr=getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/jf-open.ttf");//根據路徑得到Typeface
        toolbarText.setTypeface(tf);//設定字型

        // 取消 ActionBar
        getSupportActionBar().hide();
        // 取消狀態欄
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    // 返回
    public void goBack(View view) {
        finish();
        Intent intent = new Intent(EditSearch.this, MainActivity.class);
        startActivity(intent);
    }

    // 編輯標記
    public void goEditPins(View view) {
        try{
            // Loading open
            mLoadingBar.setVisibility(View.VISIBLE);

            String country =  (null == country_pv ? "" : country_pv.getSelected());
            EditText title = (EditText) findViewById(R.id.titleEt);
            Spinner Selected_type = (Spinner) findViewById(R.id.select_type);

            Intent intentMap = new Intent(EditSearch.this, EditPins.class);
            intentMap.putExtra("title_search", title.getText().toString());
            intentMap.putExtra("country_search", country);
            intentMap.putExtra("type_search", Selected_type.getSelectedItem().toString());
            startActivityForResult(intentMap, 101);

        } catch (Exception e) {
            Log.d("Error", e.toString());
        }
    }

    // Picker View
    public void selectCountry(View view) {
        LayoutInflater inflater = LayoutInflater.from(EditSearch.this);
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

        AlertDialog.Builder dialog = new AlertDialog.Builder(EditSearch.this);
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
                //Toast.makeText(EditSearch.this, "未做修改",Toast.LENGTH_SHORT).show();
            }

        });
        dialog.setPositiveButton(getApplicationContext().getResources().getString(R.string.yes),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                String result =  country_pv.getSelected();
                ImageView imageView = (ImageView) findViewById(R.id.country_logo);
                Button button = (Button) findViewById(R.id.select_country_btn);
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
}
