package com.nsromapa.uchat.LocationUtil;

import android.graphics.Point;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.nsromapa.uchat.R;

import java.util.HashMap;

public class MapboxSingleLocationActivity extends AppCompatActivity {
    private MapView mapView;
    private MapboxMap map;

    private Marker grabedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,
                "pk.eyJ1Ijoic2F5dG9vbnowNSIsImEiOiJjanFyYTR1aHkwNG5rM3hxZmV2YWd2b2ZiIn0.eA0TWwLPMDX57-3uAY4daw");
        setContentView(R.layout.activity_mapbox_single_location);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        map = mapboxMap;
                        if (grabedLocation != null){
                            grabedLocation.remove();
                        }
                        if (getIntent() != null){
                            double lng = getIntent().getDoubleExtra("long",0);
                            double lat = getIntent().getDoubleExtra("lat",0);
                            String frndName = getIntent().getStringExtra("friendName");

                            grabedLocation = mapboxMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat,lng))
                                    .setTitle(frndName)
                                    .setSnippet("Date and time..."));
                            setCameraPosition(new LatLng(lat,lng));
                        }

                    }
                });
            }
        });

    }

    private void setCameraPosition(LatLng location){
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location,15.0));
    }
    private void updateStatus(String state) {

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("state", state);

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userState")
                .updateChildren(onlineState);

    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();
//        if (locationEngine!=null){
//            locationEngine.requestLocationUpdates();
//            if (locationLayerPlugin!=null){
//                locationLayerPlugin.onStart();
//            }
//        }
        mapView.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateStatus("online");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
//        if (locationEngine!=null){
//            locationEngine.removeLocationUpdates();
//        }
//        if (locationLayerPlugin!=null){
//            locationLayerPlugin.onStop();
//        }
        mapView.onStop();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (locationEngine!=null){
//            locationEngine.deactivate();
//        }
        mapView.onDestroy();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


}