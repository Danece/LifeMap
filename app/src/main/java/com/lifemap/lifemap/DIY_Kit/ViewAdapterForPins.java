package com.lifeMap.lifemap.DIY_Kit;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lifeMap.lifemap.R;

import java.util.ArrayList;
import java.util.List;


public class ViewAdapterForPins extends BaseAdapter {


    List titleList = new ArrayList();
    List dateList = new ArrayList();
    List countryList = new ArrayList();
    List markerTypeList = new ArrayList();
    List markerImageUuidList = new ArrayList();
    private int mCurrentItem=0;
    private boolean isClick=false;
    private LayoutInflater inflater;    //加載layout
    private Activity context;
    private String dir = null;

    static class ViewHolder {
        TextView title;
        TextView date;
        ImageView image;
    }

    public ViewAdapterForPins(Activity context ,List titleList, List dateList, List countryList, List markerType, List markerImageUuidList, String dir) {
        this.titleList = titleList;
        this.dateList = dateList;
        this.countryList = countryList;
        this.markerTypeList = markerType;
        this.context = context;
        this.markerImageUuidList = markerImageUuidList;
        this.dir = dir;
    }

    @Override
    public int getCount() {
        return titleList.size();
    }

    @Override
    public Object getItem(int position) {
        return titleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        inflater = context.getLayoutInflater();
        if(null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pins_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.title_Tv);
            holder.date = (TextView) convertView.findViewById(R.id.date_Tv);
            holder.image = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(titleList.get(position).toString() + "("+markerTypeList.get(position).toString()+")");
        holder.date.setText(dateList.get(position).toString() + "  國家:"+countryList.get(position).toString());
        String uuid = markerImageUuidList.get(position).toString();
        holder.image.setImageBitmap(BitmapFactory.decodeFile(dir + "/LifeMap/markerImage/" + uuid + "_edit.png"));

        LinearLayout linearLayout= (LinearLayout) convertView.findViewById(R.id.List_view_item);
        parent.setBackgroundColor(Color.parseColor("#ffffff"));
        if (mCurrentItem==position&&isClick){
            linearLayout.setBackgroundColor(Color.parseColor("#bebebe"));
        }else{
            linearLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        return convertView;
    }

    public void setCurrentItem(int currentItem){
        this.mCurrentItem=currentItem;
    }

    public void setClick(boolean click){
        this.isClick=click;
    }
}
