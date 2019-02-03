package com.nsromapa.uchat;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nsromapa.uchat.customizations.CustomIntent;
import com.nsromapa.uchat.usersInfos.MyStories;
import com.nsromapa.uchat.usersInfos.UserInformation;
import com.nsromapa.uchat.adapter.MainPagerAdapter;
import com.nsromapa.uchat.loginsignupsplash.LoginActivity;
import com.nsromapa.uchat.view.MyTabsView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private View mDivider;
    private EditText mSearchEditText;
    private ImageView cameraFacingORMenuBtn;
    ViewGroup toolbar;

    FrameLayout ma_toolbar_menu_framelayout;
    TextView ma_menu_findfriends;
    TextView ma_menu_createGroup;
    TextView ma_menu_Profile;
    TextView ma_menu_logout;

    FirebaseAuth mAuth;
    DatabaseReference mRootRef;

    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.main_activity_toolbar);

        mAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();



        final View background = findViewById(R.id.am_background_view);
        final ViewPager viewPager = findViewById(R.id.am_view_pager);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        setUpMyTaoolbar();


        final MyTabsView myTabsView = findViewById(R.id.am_my_tabs);
        myTabsView.setUpWithVeiewPager(viewPager);
        viewPager.setCurrentItem(2);

        ma_toolbar_menu_framelayout = findViewById(R.id.ma_toolbar_menu_framelayout);
        ma_menu_findfriends = findViewById(R.id.ma_menu_findfriends);
        ma_menu_createGroup = findViewById(R.id.ma_menu_createGroup);
        ma_menu_Profile = findViewById(R.id.ma_menu_Profile);
        ma_menu_logout = findViewById(R.id.ma_menu_logout);
        cameraFacingORMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0){
                    ma_toolbar_menu_framelayout.setAlpha(0);
                    ma_toolbar_menu_framelayout.setVisibility(View.GONE);

                }else{
                    if (ma_toolbar_menu_framelayout.getVisibility() == View.GONE) {
                        ma_toolbar_menu_framelayout.setAlpha(1);
                        ma_toolbar_menu_framelayout.setVisibility(View.VISIBLE);
                    }else {
                        ma_toolbar_menu_framelayout.setAlpha(0);
                        ma_toolbar_menu_framelayout.setVisibility(View.GONE);
                    }
                }
            }
        });
        ma_menu_findfriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FindUsersActivity.class);
                CustomIntent.customType(MainActivity.this, "up-to-bottom");
                startActivity(intent);
            }
        });
        ma_menu_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });



        final int colorblue = ContextCompat.getColor(this, R.color.light_blue);
        final int colorpurple = ContextCompat.getColor(this, R.color.light_purple);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                cameraFacingORMenuBtn.setImageResource(R.drawable.ic_more_vert_24dp);

                if (position == 0) {
                    background.setAlpha(positionOffset);
                    mDivider.setAlpha(1-positionOffset);

                    cameraFacingORMenuBtn.setImageResource(R.drawable.ic_image_24dp);
                    ma_toolbar_menu_framelayout.setAlpha(0);

                    myTabsView.setVisibility(View.GONE);


                } else if (position == 1) {
                    background.setBackgroundColor(colorblue);
                    background.setAlpha(1 - positionOffset);

                    mSearchEditText.setEnabled(true);
                    mSearchEditText.setAlpha(1 - positionOffset);


                    myTabsView.setVisibility(View.VISIBLE);

                } else if (position == 2) {
                    background.setBackgroundColor(colorpurple);
                    background.setAlpha(positionOffset);


                    //Hide search box
                    mSearchEditText.setEnabled(false);
                    mSearchEditText.setAlpha(positionOffset);

                    myTabsView.setVisibility(View.VISIBLE);

                } else if (position == 3) {
                    background.setAlpha(1 - positionOffset);
                    mSearchEditText.setEnabled(true);

                    myTabsView.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int position) {
                ma_toolbar_menu_framelayout.setAlpha(0);
                ma_toolbar_menu_framelayout.setVisibility(View.GONE);
            }
        });



        UserInformation userInformationListener = new UserInformation();
        userInformationListener.startFetchingFollowing();
        userInformationListener.startFetchingFollowers();
        userInformationListener.startFetchingFriends();

        MyStories myStories = new MyStories();
        myStories.startFetchingStories();

        downloadStickers();
        downloadSounds();


    }




    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateStatus("online");
            updateDeviceToken(FirebaseInstanceId.getInstance().getToken());
        }
    }

    @Override
    protected void onStop() {
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
           updateStatus("offline");
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            updateStatus("offline");
        }
        super.onDestroy();

    }


    private void updateDeviceToken(String deviceToken){
        String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        mRootRef.child("users").child(currentUserId).child("device_token")
                .setValue(deviceToken);
    }

    private void setUpMyTaoolbar(){
        mDivider  = findViewById(R.id.toolbar_bottom_divider);
        mSearchEditText = findViewById(R.id.main_toolbar_search_edittext);
        cameraFacingORMenuBtn = findViewById(R.id.main_toolbar_switch_camera_or_menu_icon);

        mSearchEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = mSearchEditText.getText().toString();
                if(text.equals("Search") || text.equals("Stories") || text.equals("Chat"))
                    mSearchEditText.setText("");
            }

        });



    }

    private void updateStatus(String state){
        String saveCurrentTime, saveCurrentDate;
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentDate = currentDate.format(calendar.getTime());
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("time",saveCurrentTime);
        onlineState.put("date",saveCurrentDate);
        onlineState.put("state",state);

        mRootRef.child("users").child(currentUserId).child("userState")
                .updateChildren(onlineState);



    }

    private void logOut() {
        updateDeviceToken("");
        updateStatus("offline");
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        CustomIntent.customType(MainActivity.this,"bottom-to-up");
        finish();
    }


    private void downloadStickers(){

        mRootRef.child("stickers")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        if (dataSnapshot.exists()){
                            final String stickerName = dataSnapshot.getRef().getKey();
                            File stickerFile =new File(String.valueOf(getExternalFilesDir("/Images/Stickers/"+stickerName+".png")));

                            if (!(stickerFile.exists()) || (!stickerFile.isFile()) ) {
                                stickerFile.delete();

                                String loc = dataSnapshot.child("loc").getValue().toString();
                                Glide.with(MainActivity.this)
                                        .asBitmap()
                                        .load(loc)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                saveImage(resource, stickerName,"/Images/Stickers/");
                                            }
                                        });



                            }

                        }


                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()){
                            final String stickerName = dataSnapshot.getRef().getKey();
                            File stickerFile =new File(String.valueOf(getExternalFilesDir("/Images/Stickers/"+stickerName+".png")));
                            if (stickerFile.exists() && stickerFile.isFile()){
                                stickerFile.delete();
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            final String stickerName = dataSnapshot.getRef().getKey();
                            File stickerFile =new File(String.valueOf(getExternalFilesDir("/Images/Stickers/"+stickerName+".png")));
                            if (stickerFile.exists() && stickerFile.isFile()){
                                stickerFile.delete();
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()){
                            final String stickerName = dataSnapshot.getRef().getKey();
                            File stickerFile =new File(String.valueOf(getExternalFilesDir("/Images/Stickers/"+stickerName+".png")));
                            if (stickerFile.exists() && stickerFile.isFile()){
                                stickerFile.delete();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void downloadSounds(){

        mRootRef.child("sounds")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        if (dataSnapshot.exists()){
                            final String soundName = dataSnapshot.getRef().getKey();
                            File soundImageFile =new File(String.valueOf(getExternalFilesDir("/Sounds/SoundImages/"+soundName+".png")));

                            if (!(soundImageFile.exists()) || (!soundImageFile.isFile()) ) {
                                soundImageFile.delete();

                                String imageUrl= dataSnapshot.child("image").getValue().toString();
                                final String audioUrl = dataSnapshot.child("audio").getValue().toString();
                                Glide.with(MainActivity.this)
                                        .asBitmap()
                                        .load(imageUrl)
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource,
                                                                        @Nullable Transition<? super Bitmap> transition) {
                                                saveImage(resource, soundName, "/Sounds/SoundImages/");

                                                File soundAudioFile = new File(String.valueOf(getExternalFilesDir("/Sounds/SoundAudios/" + soundName + ".mp3")));

                                                if (!(soundAudioFile.exists()) || (!soundAudioFile.isFile())) {
                                                    soundAudioFile.delete();
                                                    downloadAndSaveSound(audioUrl, soundName + ".mp3", "/Sounds/SoundAudios/");
                                                }

                                            }
                                        });



                            }

                        }


                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    private void downloadAndSaveSound(String url, String soundName, String localLocation) {
        DownloadManager downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
        request.setDestinationInExternalFilesDir(this,localLocation,soundName);

        downloadManager.enqueue(request);


    }

    private void saveImage(Bitmap image,String imageFileName, String directory) {
        File storageDir = new File(String.valueOf(this.getExternalFilesDir(directory)));
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName+".png");
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Toast.makeText(this, "saved "+imageFileName,Toast.LENGTH_SHORT).show();
        }
    }

}

