package com.example.lifemap.cluster;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyClusterItem implements ClusterItem {

    private String title;
    private String snippet;
    private LatLng latLng;
    private Bitmap markerIcon;

    public MyClusterItem(String title, String snippet, LatLng latLng, Bitmap markerIcon) {
        this.title = title;
        this.snippet = snippet;
        this.latLng = latLng;
        this.markerIcon = markerIcon;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public Bitmap getMarkerIcon() {
        return markerIcon;
    }
}
