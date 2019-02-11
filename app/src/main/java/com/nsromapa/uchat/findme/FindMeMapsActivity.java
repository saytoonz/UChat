package com.nsromapa.uchat.findme;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nsromapa.uchat.ChatActivity;
import com.nsromapa.uchat.MainActivity;
import com.nsromapa.uchat.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class FindMeMapsActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        OnMapReadyCallback,
        OnMyLocationClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int PLAY_SERVICES_RES_REQUEST = 2;
    private final static int UPDATE_INTERVAL = 5000;
    private final static int FASTEST_INTERVAL = 3000;
    private final static int DISTANCE = 10;

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation=new Location("");


    private ImageView toggleMapView,toggleNavigation, toggleFindMe;
    private TextView notificationView;


    private LatLng latLng;
    private MarkerOptions options;

    String friendUid, friendName, myUid, connectAuto;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;

    ProgressDialog progressDialog;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_me_maps);

        progressDialog = new ProgressDialog(this);;

        toggleMapView = findViewById(R.id.toggle_mapView);
        toggleNavigation = findViewById(R.id.toggle_navigation);
        toggleFindMe = findViewById(R.id.toggle_FindMe);
        notificationView = findViewById(R.id.findMe_notification_textView);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        myUid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");


        if (getIntent()==null){
            Toast.makeText(this, "There was an error...", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }else{
            friendUid = getIntent().getStringExtra("friend_uid");
            friendName = getIntent().getStringExtra("friend_name");
            connectAuto = getIntent().getStringExtra("connect_auto");

        }

        if (ContextCompat.checkSelfPermission(FindMeMapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED  &&
                ContextCompat.checkSelfPermission(FindMeMapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {

            if (checkPlayServices()){
                createLocationRequest();
            }

        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        toggleFindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFindMe(friendUid);
            }
        });
        toggleMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        });
        map.setOnMyLocationClickListener(this);
        buildGoogleClient();
        if (!TextUtils.isEmpty(friendUid))
            checkIfFriendIsOnline(friendUid);

        if (connectAuto.equals("yes"))toggleFindMe(friendUid);
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {
            Toast.makeText(this, "Could not get location...", Toast.LENGTH_SHORT).show();
            return;
        } else {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mLastLocation = location;
            displayLocation();

        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();
        displayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }





    private void toggleFindMe(final String friendUid){
        progressDialog.setMessage("Please wait...");
        progressDialog.show();


        usersRef.child(myUid).child("findMe")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("findMeState")){
                            String findMeState = dataSnapshot.child("findMeState").getValue().toString();
                            if (findMeState.equals(friendUid)){
                                usersRef.child(myUid).child("findMe").child("findMeState")
                                        .removeValue();
                                toggleFindMe.setImageResource(R.drawable.ic_perm_contact_calendar_red_24dp);
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            }else{
                                tryAddFriendOnFindMe("connected");
                            }
                        }else{
                            tryAddFriendOnFindMe("free");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void tryAddFriendOnFindMe(final String userState) {
        usersRef.child(friendUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("userState").hasChild("state")){
                            String state=dataSnapshot.child("userState").child("state").getValue().toString();
                            if (state.equals("online")) {
                                
                                
                                if (userState.equals("connected")) {
                                    if (progressDialog.isShowing()){
                                        progressDialog.dismiss();
                                    }
                                    map.clear();
                                    Toast.makeText(FindMeMapsActivity.this,
                                            friendName + " is on FindMe with someone else...",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    hasFriendConnectedMeAlready();
                                }

                            }else {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                map.clear();
                                String userState = "Sorry, "+friendName+" is offline...";
                                Toast.makeText(FindMeMapsActivity.this, userState, Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            map.clear();
                            String userState = friendName+" is not available for now.";
                            Toast.makeText(FindMeMapsActivity.this, userState, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        map.clear();
                        String userState = "Sorry, "+friendName+" is unreachable...";
                        Toast.makeText(FindMeMapsActivity.this, userState, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void hasFriendConnectedMeAlready() {
        usersRef.child(friendUid).child("findMe")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap<String,Object> friendUidMap = new HashMap<>();
                        friendUidMap.put("findMeState",friendUid);
                        usersRef.child(myUid).child("findMe").updateChildren(friendUidMap);
                        toggleFindMe.setImageResource(R.drawable.ic_perm_contact_calendar_black_24dp);

                        if (dataSnapshot.hasChild("findMeState")){
                            if (!dataSnapshot.child("findMeState").getValue().toString().equals(myUid)){
                                sendUserMessage();
                            }
                        }else{
                            sendUserMessage();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(FindMeMapsActivity.this, "Can't connect with "+friendName+" at this momment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendUserMessage() {
        SendMessage(myUid,"findMe","active");
    }
    public void SendMessage(String messageText,String type, String caption ) {
        if (!TextUtils.isEmpty(messageText.trim())){
            Calendar calendarFordate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            String currentDate = currentDateFormat.format(calendarFordate.getTime());

            Calendar calendarForTime= Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            String currentTime = currentTimeFormat.format(calendarForTime.getTime());



            DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
            //Firebase Database References for messages
            DatabaseReference theseUsersMessageTableRef = mRootRef.child("Messages")
                    .child(myUid).child(friendUid);

            String messagePushKey = theseUsersMessageTableRef.push().getKey();


            Map<String,Object> messageTextBody = new HashMap<>();
            messageTextBody.put("messageID",messagePushKey);
            messageTextBody.put("message",messageText);
            messageTextBody.put("caption",caption);
            messageTextBody.put("type",type);
            messageTextBody.put("from",myUid);
            messageTextBody.put("date",currentDate);
            messageTextBody.put("time",currentTime);




            Map<String, Object> messageBodyDetails = new HashMap<>();
            messageBodyDetails.put("messages/"+ myUid +"/"+ friendUid + "/" + messagePushKey, messageTextBody);
            messageBodyDetails.put("messages/"+ friendUid +"/"+ myUid + "/" + messagePushKey, messageTextBody);

            mRootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(FindMeMapsActivity.this, "Message delivered to "+friendName+", wait till he connect you back", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(FindMeMapsActivity.this, "Message could not send....", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void checkIfFriendIsOnline(final String friendUid){
        usersRef.child(friendUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("userState").hasChild("state")){
                            String state=dataSnapshot.child("userState").child("state").getValue().toString();

                            if (state.equals("online"))
                                checkIfImSharingLocationWithFriend(friendUid);
                            else {
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                map.clear();
                                String userState = "Sorry, "+friendName+" is offline...";
                                notificationView.setText(userState);

                            }

                        }else{
                            map.clear();
                            String userState = friendName+" is not available for now.";
                            notificationView.setText(userState);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        map.clear();

                        String userState = "Sorry, "+friendName+" is unreachable...";
                        notificationView.setText(userState);
                    }
                });
    }

    private void checkIfImSharingLocationWithFriend(final String friendUid){
        usersRef.child(myUid).child("findMe")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("findMeState")){
                            String findMeState = dataSnapshot.child("findMeState").getValue().toString();
                            if (findMeState.equals(friendUid)){
                                checkIfFriendisSharingLocationWithMe(friendUid);
                            }else{
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                map.clear();
                                notificationView.setText("You are not sharing your location with "+friendName);
                            }
                        }else{
                            if (progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            map.clear();
                            notificationView.setText("Your Location is not shared yet.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void checkIfFriendisSharingLocationWithMe(final String friendUid){
        usersRef.child(friendUid).child("findMe")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("findMeState")){
                            String findMeState = dataSnapshot.child("findMeState").getValue().toString();
                            if (findMeState.equals(myUid)){
                                loadLocationForFriend(friendUid);
                            }else{
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                map.clear();
                                notificationView.setText(friendName+" is on FindMe with someone else...");

                            }
                        }else{
                            if (progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            map.clear();
                            notificationView.setText(friendName+"'s Location is not shared yet.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadLocationForFriend(String friendUid) {

        usersRef.child(friendUid).child("findMe")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                            String frndLat = dataSnapshot.child("lat").getValue().toString();
                            String frndLng = dataSnapshot.child("lng").getValue().toString();

                            //Adding Marker for Friend's Location
                            LatLng friendLocation =  new LatLng(Double.parseDouble(frndLat),
                                                                Double.parseDouble(frndLng));

                            //Create Location for Freind
                            Location friend = new Location("");
                            friend.setLatitude(Double.parseDouble(frndLat));
                            friend.setLongitude(Double.parseDouble(frndLng));

                            //Create Location for current User
                            Location currentUser = mLastLocation;

                            //Display current user location info on the notification view
                            notificationView.setText(
                                    getAddressInfo(Double.parseDouble(frndLat), Double.parseDouble(frndLng))
                            );

                            ///Create function to calculate the distance between locations
//                            distance(currentUser,friend);

                            //Clear old Markers
                            map.clear();
                            //Add friend Marker on Map
                            map.addMarker(new MarkerOptions()
                                        .position(friendLocation)
                                        .title(friendName)
                                        .snippet("Distance "+new DecimalFormat("#.#")
                                         .format(currentUser.distanceTo(friend)/1000)+"km")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(friend.getLatitude(),
                                                                            friend.getLongitude()), 12.0f));
                            
                            
                            toggleNavigation.setEnabled(true);
                            toggleNavigation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(FindMeMapsActivity.this, "Toggle Navigation", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    private boolean checkPlayServices() {
        int requestCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (requestCode != ConnectionResult.SUCCESS){

            if (GooglePlayServicesUtil.isUserRecoverableError(requestCode)){
                GooglePlayServicesUtil.getErrorDialog(requestCode,this,PLAY_SERVICES_RES_REQUEST).show();
            }else{
                Toast.makeText(this, "This device is not supported!!!", Toast.LENGTH_SHORT).show();
            }

            return false;
        }
        return true;
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest().create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setSmallestDisplacement(DISTANCE);
    }

    private void buildGoogleClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            HashMap<String, Object> newLocationUpdatee = new HashMap<>();
            newLocationUpdatee.put("lat",String.valueOf(mLastLocation.getLatitude()));
            newLocationUpdatee.put("lng",String.valueOf(mLastLocation.getLongitude()));

            usersRef.child(myUid).child("findMe").updateChildren(newLocationUpdatee);
        } else {
            Toast.makeText(this, "Couldn't get location...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (map != null) map.setMyLocationEnabled(true);


    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
    }



    public Address getAddress(double latitude, double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getAddressInfo(double latit,double longi) {

        Address locationAddress =  getAddress(latit,longi);

        if(locationAddress!=null)
        {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            String currentLocation;

            if(!TextUtils.isEmpty(address))
            {
                currentLocation=address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation+="\n"+address1;

                if (!TextUtils.isEmpty(city))
                {
                    currentLocation+="\n"+city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+=" - "+postalCode;
                }
                else
                {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+="\n"+postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation+="\n"+state;

                if (!TextUtils.isEmpty(country))
                    currentLocation+="\n"+country;

              return currentLocation;

            }
            return null;

        }

        return null;

    }

    private void updateStatus(String state){

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("state",state);

        usersRef.child(myUid).child("userState")
                .updateChildren(onlineState);

    }





    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      switch (requestCode){
          case LOCATION_PERMISSION_REQUEST_CODE:
          {
              if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                  if (checkPlayServices())
                      recreate();
              }
          }
              break;

          case PLAY_SERVICES_RES_REQUEST:
              break;

      }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient!=null)
            mGoogleApiClient.connect();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateStatus("online");
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient!=null)
            mGoogleApiClient.disconnect();

        usersRef.child(myUid).child("findMe").child("findMeState")
                .removeValue();

        super.onStop();
    }

    @Override
    protected void onDestroy(){
        usersRef.child(myUid).child("findMe").child("findMeState")
                .removeValue();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

}
