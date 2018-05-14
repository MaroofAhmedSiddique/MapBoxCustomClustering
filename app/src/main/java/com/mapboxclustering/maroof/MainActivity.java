package com.mapboxclustering.maroof;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.cluster.clustering.ClusterManagerPlugin;
import com.mapboxclustering.maroof.model.POICluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private static final int MULTIPLE_PERMISSION_REQUEST_CODE = 11;
    private MapboxMap mMapboxMap;
    private ClusterManagerPlugin<POICluster> clusterManagerPlugin;
    private List<POICluster> poiClusterList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        checkAndRequestPermissions();
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        mMapboxMap = mapboxMap;

        mMapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(27.176670,78.008075), 8));
        clusterManagerPlugin = new ClusterManagerPlugin<>(this, mMapboxMap);

        mMapboxMap.addOnCameraIdleListener(clusterManagerPlugin);

        if (poiClusterList.size() > 0) {
            poiClusterList.clear();
        }

        IconFactory iconFactory = IconFactory.getInstance(this);

        Icon iconRed = null, iconPurple = null, iconBlue = null;
        iconRed = iconFactory.fromResource(R.drawable.pin_red);
        iconPurple = iconFactory.fromResource(R.drawable.pin_purple);
        iconBlue = iconFactory.fromResource(R.drawable.pin_blue);


        // static lat/lng
        poiClusterList.add(new POICluster(27.176670, 78.008075, "Agra", "India", iconRed));
        poiClusterList.add(new POICluster(28.700987, 77.279359, "New Delhi", "India", iconPurple));
        poiClusterList.add(new POICluster(23.406714, 76.151514, "Indore", "India", iconBlue));
        poiClusterList.add(new POICluster(26.316970, 78.107080, "Gwalior", "India", iconRed));
        poiClusterList.add(new POICluster(32.011725, 76.766748, "Himachal", "India", iconPurple));
        poiClusterList.add(new POICluster(34.095468, 75.975733, "Kashmir", "India", iconBlue));


        if (poiClusterList != null && poiClusterList.size() > 0) {
            clusterManagerPlugin.addItems(poiClusterList);
        }

    }

    private void setupMap() {
        try {
            mapView.getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkAndRequestPermissions() {

        int LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            setupMap();
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSION_REQUEST_CODE: {

                Map<String, Integer> perms = new HashMap<>();

                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        setupMap();

                    } else {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK(getResources().getString(R.string.some_req_permissions));
                        } else {
                            explain(getResources().getString(R.string.open_settings));
                        }
                    }
                }
            }
            break;

            default:
                break;

        }
    }

    private void showDialogOK(String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setCancelable(false);

        dialog.setPositiveButton(
                getResources().getString(R.string.okay),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkAndRequestPermissions();
                        dialog.cancel();
                    }
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alert = dialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }


    }

    private void explain(String msg) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(msg);
        dialog.setCancelable(false);

        dialog.setPositiveButton(
                getResources().getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(uri);
                        startActivityForResult(intent, MULTIPLE_PERMISSION_REQUEST_CODE);

                        dialog.cancel();
                    }
                });

        dialog.setNegativeButton(
                getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alert = dialog.create();

        if (!alert.isShowing()) {
            alert.show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
