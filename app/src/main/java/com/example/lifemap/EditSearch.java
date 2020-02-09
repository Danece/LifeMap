package com.example.lifemap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lifemap.DIY_Kit.PickerView;

import java.util.ArrayList;
import java.util.List;

public class EditSearch extends AppCompatActivity {

    private Spinner type_selection_spinner ;
    private ArrayAdapter<String> adapter;
    PickerView country_pv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_search);
        // 設定螢幕不旋轉
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        // 設定螢幕直向顯示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

    }

    // 返回
    public void goBack(View view) {
        finish();
        Intent intent = new Intent(EditSearch.this, MainActivity.class);
        startActivity(intent);
    }

    // 編輯標記
    public void editPins(View view) {
        try{
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
                Toast. makeText ( EditSearch.this , "選擇了" +  text  + "分" ,
                        Toast . LENGTH_SHORT ). show ();
            }
        });

        AlertDialog.Builder dialog = new AlertDialog.Builder(EditSearch.this);
        dialog.setTitle("國家選擇");
        dialog.setMessage("請選擇目前所在國家");
        dialog.setView(v);
        dialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(EditSearch.this, "未做修改",Toast.LENGTH_SHORT).show();
            }

        });
        dialog.setPositiveButton("YES",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                String result =  country_pv.getSelected();
                ImageView imageView = (ImageView) findViewById(R.id.country_logo);
                Button button = (Button) findViewById(R.id.select_country_btn);
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

                Toast.makeText(EditSearch.this, "選擇:"+result,Toast.LENGTH_SHORT).show();
            }

        });
        dialog.show();

    }
}
