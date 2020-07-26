package com.lifeMap.lifemap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lifeMap.lifemap.model_view.PinDetail;

public class DatabaseExcute extends AppCompatActivity {


    // 檢查並建立資料庫
    public void checkAndCreateDB(SQLiteDatabase db, String tb_name) {
        // 開啟或建立資料庫
        String createTable = "CREATE TABLE IF NOT EXISTS " + tb_name +
                "(title VARCHAR(15), " +
                "date VARCHAR(10), " +
                "country VARCHAR(20), " +
                "markerStyle VARCHAR(4), " +
                "markerType VARCHAR(4), " +
                "markerImageUuid VARCHAR(40), " +
                "longitude DOUBLE(20), " +
                "latitude DOUBLE(20))" ;
        db.execSQL(createTable);
    }

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

    public Cursor queryDbInfo(SQLiteDatabase db, String tb_name, String title, String longitude, String latitude) {
        String condition = " WHERE ";
        condition = condition + "title == (" + title + ") "
                + "AND longitude == (" + longitude + ")"
                + "AND latitude == (" + latitude + ")"
                ;

        Cursor cursor = db.rawQuery("SELECT * FROM "+ tb_name + condition, null);

        return cursor;
    }

    public Cursor getDbAllInfo(SQLiteDatabase db, String tb_name, String countrys,  String markerTypes, String date) {

        String condition = " WHERE ";
        condition = condition + "country IN (" + countrys + ") AND markerType IN (" + markerTypes + ") AND date >= date(" + date + ")" ;

        Cursor cursor = db.rawQuery("SELECT * FROM "+ tb_name + condition , null);
        Log.d("XX_databaseExcute", condition);
        return cursor;
    }

}
