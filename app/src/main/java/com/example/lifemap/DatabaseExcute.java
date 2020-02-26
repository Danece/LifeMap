package com.example.lifemap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;

import com.example.lifemap.model_view.PinDetail;

import java.util.ArrayList;
import java.util.List;

public class DatabaseExcute extends AppCompatActivity {

    // 刪除
    public void delete(SQLiteDatabase db, String tb_name, PinDetail pinDetail) {
        db.delete(tb_name, "title=?"+
                        "and date=?" +
                        "and country=?" +
                        "and markerType=?" ,
                new String[]{pinDetail.getTitle(),
                        pinDetail.getDate(),
                        pinDetail.getCountry(),
                        pinDetail.getMarkerType()
                });
        db.close();
    }

    // 更新-activity_edit_info
    public void updateForEditPinInfo(SQLiteDatabase db, String tb_name, PinDetail oldInfo, PinDetail newInfo) {

        ContentValues values = new ContentValues();
        values.put("title", newInfo.getTitle());
        values.put("country", newInfo.getCountry());
        values.put("markerType", newInfo.getMarkerType());

        db.update(tb_name,values,
                "title='" + oldInfo.getTitle() + "'" +
                        " AND country='" + oldInfo.getCountry() + "'" +
                        " AND markerType='" + oldInfo.getMarkerType() + "'" ,
                null);
        db.close();
    }

    public Cursor queryForShowMap(SQLiteDatabase db, String tb_name, String countrys, String markerTypes) {

        String condition = " WHERE ";
        condition = condition + "country IN (" + countrys + ") AND markerType IN (" + markerTypes + ")";

        Cursor cursor = db.rawQuery("SELECT * FROM "+ tb_name + condition, null);

        return cursor;
    }
}
