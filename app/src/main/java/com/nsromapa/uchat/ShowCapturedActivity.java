package com.nsromapa.uchat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

//import com.bumptech.glide.request.RequestOptions;
//import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nsromapa.uchat.cameraUtils.Config;
import com.nsromapa.uchat.photoeditor.PhotoEditorMainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class ShowCapturedActivity extends Activity  {
    com.jsibbold.zoomage.ZoomageView image_frame;
    String image_or_video_path ;

    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    private SimpleExoPlayerView mExoPlayerView;
    private MediaSource mVideoSource;
    private boolean mExoPlayerFullscreen = false;
    private FrameLayout mFullScreenButton;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;

    private int mResumeWindow;
    private long mResumePosition;
    private FrameLayout main_media_frame;

    ProgressBar loading_progress;
    String message_type;


    Bitmap bitmap=null;

    ImageView saveImage, setWallPaper, editImage;
    ImageView asc_insert_Emoji;
    EmojiconEditText asc_add_caption;
    EmojIconActions emojIconActions;
    View rootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_capture);

        image_or_video_path=getIntent().getStringExtra(Config.KeyName.FILEPATH);

        setUI();
        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }




        asc_insert_Emoji = findViewById(R.id.asc_insert_Emoji);
        asc_add_caption = findViewById(R.id.asc_add_caption);
        rootView = findViewById(R.id.asc_root_view);
        emojIconActions = new EmojIconActions(this,rootView,asc_add_caption,asc_insert_Emoji);
        emojIconActions.ShowEmojIcon();
        emojIconActions.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });




        FloatingActionButton mSend = findViewById(R.id.send);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChooseReceiverActivity.class);
                intent.putExtra("caption",asc_add_caption.getText().toString());
                intent.putExtra("fileType",message_type);
                intent.putExtra("fileLoc",image_or_video_path);
                startActivity(intent);
                finish();

            }
        });



        setWallPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setwallpaper();
            }
        });
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowCapturedActivity.this,PhotoEditorMainActivity.class);
                intent.putExtra("imageLoc",image_or_video_path);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setUI() {

        loading_progress=findViewById(R.id.loading_progress);
        loading_progress.setVisibility(View.VISIBLE);
        image_frame=findViewById(R.id.image_frame);
        main_media_frame=findViewById(R.id.main_media_frame);

        setWallPaper = findViewById(R.id.set_image_as_wallpaper);
        saveImage = findViewById(R.id.save_image);
        editImage = findViewById(R.id.Edit_Image);


        if (image_or_video_path.contains(".mp4")){
            message_type=Config.MessageType.VIDEO;
            image_frame.setVisibility(View.GONE);
            main_media_frame.setVisibility(View.VISIBLE);

            setWallPaper.setVisibility(View.GONE);
            editImage.setVisibility(View.GONE);
            saveImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveVideo();
                }
            });
        }else {
            bitmap = BitmapFactory.decodeFile((image_or_video_path));
            message_type=Config.MessageType.IMAGE;
            image_frame.setVisibility(View.VISIBLE);
            main_media_frame.setVisibility(View.GONE);
            setImage();

            if (bitmap!=null){
                saveImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveImage();
                    }
                });
            }
        }

//        File file = new File(image_or_video_path);
//        int file_size = Integer.parseInt(String.valueOf((file.length()/1024)/1024));
     //   image_frame.setOnTouchListener(this);
   //     Toast.makeText(ShowCapturedActivity.this,"File is "+file_size +" mb",Toast.LENGTH_SHORT).show();
    }


    private void setImage(){
        loading_progress.setVisibility(View.GONE);
        Glide.with(image_frame.getContext())
                .asBitmap()
                .load(image_or_video_path)
                .apply( new RequestOptions().signature(new ObjectKey(Calendar.getInstance().getTime()))
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        )
                .into(image_frame);
//        image_frame.setImageBitmap(bitmap);
    }
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);

        super.onSaveInstanceState(outState);
    }


    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }
    private void openFullscreenDialog() {

        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(ShowCapturedActivity.this, R.drawable.ic_fullscreen_exit_white_24dp));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }
    private void closeFullscreenDialog() {

        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(mExoPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(ShowCapturedActivity.this, R.drawable.ic_fullscreen));
    }
    private void initFullscreenButton() {

        PlaybackControlView controlView = mExoPlayerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }
    private void initExoPlayer() {
        if (loading_progress!=null) {
            loading_progress.setVisibility(View.GONE);
        }
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControl);
        mExoPlayerView.setPlayer(player);

        boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            mExoPlayerView.getPlayer().seekTo(mResumeWindow, mResumePosition);
        }

        player.prepare(mVideoSource);
        mExoPlayerView.getPlayer().setPlayWhenReady(true);
    }




    private void saveVideo() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){
                            FileOutputStream fileOutputStream;
                            File file = getExternalDirectory_andFolder("Uchat/Video/Saved");

                            if (!file.exists() && !file.mkdirs()) {
                                Toast.makeText(ShowCapturedActivity.this, "Can't create Directory to save video", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
                            String date = simpleDateFormat.format(new Date());
                            String vidName = "uchat_" + date + ".mp4";
                            String file_name = file.getAbsolutePath() + "/" + vidName;
                            File new_file = new File(file_name);

                            try {

                                fileOutputStream = new FileOutputStream(new_file);

//                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

                                Toast.makeText(ShowCapturedActivity.this, "Video Saved Successfully", Toast.LENGTH_LONG).show();

                                fileOutputStream.flush();
                                fileOutputStream.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            refreshingGallery(new_file);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(ShowCapturedActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();



    }
    private void saveImage() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){
                            FileOutputStream fileOutputStream;
                            File file = getExternalDirectory_andFolder("Uchat/Image/Saved");

                            if (!file.exists() && !file.mkdirs()) {
                                Toast.makeText(ShowCapturedActivity.this, "Can't create Directory to save image", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
                            String date = simpleDateFormat.format(new Date());
                            String imgName = "uchat_" + date + ".jpg";
                            String file_name = file.getAbsolutePath() + "/" + imgName;
                            File new_file = new File(file_name);

                            try {

                                fileOutputStream = new FileOutputStream(new_file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

                                Toast.makeText(ShowCapturedActivity.this, "Image Saved Successfully", Toast.LENGTH_LONG).show();

                                fileOutputStream.flush();
                                fileOutputStream.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            refreshingGallery(new_file);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(ShowCapturedActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();



    }
    private void refreshingGallery(File new_file) {
        Intent intent= new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new_file));
        sendBroadcast(intent);
    }
    private File getExternalDirectory_andFolder(String folder){
        File file = Environment.getExternalStorageDirectory();
        return new File(file,folder);
    }

    private void setwallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(this,"Wallpaper applied to home screen.",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Sorry there was an error.",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStatus(String state) {

        HashMap<String, Object> onlineState = new HashMap<>();
        onlineState.put("state", state);

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userState")
                .updateChildren(onlineState);


    }


    @Override
    protected void onResume() {

        super.onResume();
        if (image_or_video_path.contains(".mp4")){

            if (mExoPlayerView == null) {

                mExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.exoplayer);
                // initFullscreenDialog();
                //initFullscreenButton();

                // String streamUrl = "https://mnmedias.api.telequebec.tv/m3u8/29880.m3u8";
                String userAgent = Util.getUserAgent(ShowCapturedActivity.this, getApplicationContext().getApplicationInfo().packageName);
                DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent, null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS, DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(ShowCapturedActivity.this, null, httpDataSourceFactory);
                //   Uri daUri = Uri.parse(streamUrl);

                // mVideoSource = new HlsMediaSource(daUri, dataSourceFactory, 1, null, null);
                DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

                mVideoSource = new ExtractorMediaSource(Uri.parse(image_or_video_path),
                        dataSourceFactory, extractorsFactory, null, null);
                initExoPlayer();
            }

            else {
                mExoPlayerView.getPlayer().setPlayWhenReady(true);
            }
            //  initExoPlayer();
/*
            if (mExoPlayerFullscreen) {
                ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
                mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(UploadFeedActivity.this, R.drawable.exo_controls_next));
                mFullScreenDialog.show();
            }*/
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        if (mExoPlayerView != null && mExoPlayerView.getPlayer() != null) {
            mExoPlayerView.getPlayer().setPlayWhenReady(false);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            updateStatus("online");
        }
    }
}
