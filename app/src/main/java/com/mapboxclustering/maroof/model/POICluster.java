package com.mapboxclustering.maroof.model;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.cluster.clustering.ClusterItem;

/**
 * Created by Maroof Ahmed Siddique on 11/12/17.
 */

public class POICluster implements ClusterItem {

    private final LatLng position;
    private String title;
    private String snippet;
    private Icon icon;

    public POICluster(double lat, double lng) {
        position = new LatLng(lat, lng);
        title = null;
        snippet = null;
    }

    public POICluster(double lat, double lng, String title, String snippet, Icon icon) {
        position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
        this.icon = icon;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
}
