package com.lifeMap.lifemap.cluster;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.lifeMap.lifemap.R;

public class CustomClusterRenderer extends DefaultClusterRenderer<MyClusterItem> {

    private final IconGenerator mClusterIconGenerator;
    private final Context mContext;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<MyClusterItem> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        mClusterIconGenerator = new IconGenerator(mContext);
    }

    @Override
    protected void onBeforeClusterItemRendered(MyClusterItem item, MarkerOptions markerOptions) {
        /// 根據條件設置每個標記的外觀
        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromBitmap(item.getMarkerIcon());
        markerOptions.icon(markerDescriptor).title(item.getTitle()).snippet(item.getSnippet());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<MyClusterItem> cluster, MarkerOptions markerOptions) {
        // 根據條件設置每一個聚集的外觀
        if(5 <= cluster.getSize() && 30 > cluster.getSize()) {
            mClusterIconGenerator.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gp1));
            mClusterIconGenerator.setContentPadding(30,15,30,20);
        }else if(30 <= cluster.getSize() && 50 > cluster.getSize()) {
            mClusterIconGenerator.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gp2));
            mClusterIconGenerator.setContentPadding(30,15,30,20);
        } else if(50 <= cluster.getSize()){
            mClusterIconGenerator.setBackground(ContextCompat.getDrawable(mContext, R.drawable.gp3));
            mClusterIconGenerator.setContentPadding(30,15,30,20);
        }
        mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);
        String clusterTitle = String.valueOf(cluster.getSize());
        if(99 < cluster.getSize()) clusterTitle = " N";
        if(10 > cluster.getSize()) clusterTitle = " " + clusterTitle;
        Bitmap icon = mClusterIconGenerator.makeIcon(clusterTitle);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }
}
