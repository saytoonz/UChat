package com.nsromapa.uchat.LocationUtil;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.nsromapa.uchat.R;

import java.util.HashMap;
import java.util.Objects;

public class SingLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lng,lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (getIntent()!=null){
            lng = getIntent().getDoubleExtra("long",0);
            lat = getIntent().getDoubleExtra("lat",0);

            LatLng markedLocation = new LatLng(lat,lng);

            Location location = new Location("");
            location.setLongitude(lng);
            location.setLatitude(lat);
            mMap.addMarker(new MarkerOptions()
                    .position(markedLocation)
                    .title("Grabed Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markedLocation,16.0f));
        }
    }

    private void updateStatus(String state) {

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("state", state);

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("userState")
                .updateChildren(onlineState);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateStatus("online");
        }
    }
}
