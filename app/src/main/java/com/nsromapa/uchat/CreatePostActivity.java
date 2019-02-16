package com.nsromapa.uchat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nsromapa.uchat.databases.DBObjects;
import com.nsromapa.uchat.databases.DBOperations;
import com.nsromapa.uchat.recyclerfeeds.SendPostBackground;
import com.nsromapa.uchat.usersInfos.MyUserInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreatePostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreatePostActivity";
    private Spinner select_shareWith, select_fontFamily_for_Text, select_fontSize_for_Text;
    private ImageView post_background_transparent, post_background_black;
    private ImageView post_background_navy, post_background_green;
    private ImageView post_background_pink, post_background_maroon;
    private ImageView post_background_shadesofgrey, post_background_jupiter;
    private ImageView post_background_sherbert, post_background_firewatch;
    private ImageView post_background_grapefruitsunset, post_background_deepspace;

    private CircleImageView post_profileImageView;
    private TextView user_profile_name;

    private EditText postTest_Caption;
    private FrameLayout file_postFrame;
    private ImageView image_video_imageView, post_VideoThumbnail_play, post_imageVideoRemove;
    private LinearLayout SelectImage_Video,Tag_a_friend,add_location,fontSize_and_fontfamily;
    private HorizontalScrollView customize_EditText_Background;

    private Button create_post;

    private String shareWithText = "Followers/Friends";
    private String fontFamily = "sans_.ttf";
    private String fontSize = "16";
    private String backgroundSelected = "post_background_transparent";
    private String ImageVideoSelected="";
    private String selectedFileType = "";

    private FirebaseAuth mAuth;
    private DatabaseReference mPostRef;

    private static final int SELECTED_FROM_GALLERY_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mAuth = FirebaseAuth.getInstance();
        mPostRef = FirebaseDatabase.getInstance().getReference();

        select_shareWith = findViewById(R.id.select_shareWith);
        select_fontFamily_for_Text = findViewById(R.id.select_fontFamily_for_Text);
        select_fontSize_for_Text = findViewById(R.id.select_fontSize_for_Text);
        postTest_Caption = findViewById(R.id.postTest_Caption);

        file_postFrame = findViewById(R.id.file_postFrame);
        image_video_imageView = findViewById(R.id.image_video_imageView);
        post_VideoThumbnail_play = findViewById(R.id.post_VideoThumbnail_play);
        post_imageVideoRemove = findViewById(R.id.post_imageVideoRemove);

        create_post = findViewById(R.id.create_post);

        SelectImage_Video = findViewById(R.id.SelectImage_Video);
        Tag_a_friend = findViewById(R.id.Tag_a_friend);
        add_location = findViewById(R.id.add_location);
        fontSize_and_fontfamily= findViewById(R.id.fontSize_and_fontfamily);
        customize_EditText_Background = findViewById(R.id.customize_EditText_Background);

        user_profile_name = findViewById(R.id.user_profile_name);
        post_profileImageView = findViewById(R.id.post_profileImageView);

        post_background_transparent = findViewById(R.id.post_background_transparent);
        post_background_transparent.setOnClickListener(this);
        post_background_black = findViewById(R.id.post_background_black);
        post_background_black.setOnClickListener(this);
        post_background_navy = findViewById(R.id.post_background_navy);
        post_background_navy.setOnClickListener(this);
        post_background_green = findViewById(R.id.post_background_green);
        post_background_green.setOnClickListener(this);
        post_background_maroon = findViewById(R.id.post_background_maroon);
        post_background_maroon.setOnClickListener(this);
        post_background_pink = findViewById(R.id.post_background_pink);
        post_background_pink.setOnClickListener(this);
        post_background_shadesofgrey = findViewById(R.id.post_background_shadesofgrey);
        post_background_shadesofgrey.setOnClickListener(this);
        post_background_jupiter = findViewById(R.id.post_background_jupiter);
        post_background_jupiter.setOnClickListener(this);
        post_background_sherbert = findViewById(R.id.post_background_sherbert);
        post_background_sherbert.setOnClickListener(this);
        post_background_firewatch = findViewById(R.id.post_background_firewatch);
        post_background_firewatch.setOnClickListener(this);
        post_background_deepspace = findViewById(R.id.post_background_deepspace);
        post_background_deepspace.setOnClickListener(this);
        post_background_grapefruitsunset = findViewById(R.id.post_background_grapefruitsunset);
        post_background_grapefruitsunset.setOnClickListener(this);


        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(),
                "fonts/sans_.ttf");
        postTest_Caption.setTypeface(typeface);

        String[] itemsforShareWith = new String[]{"Followers/Friends", "Friends only"};
        String[] itemsforFontSize = new String[]{"14", "16", "18", "20", "22", "24"};
        final String[] itemsforFont = new String[]{"AlexBrush_Regular.ttf", "amita_bold.ttf", "berkshireswash_regular.ttf",
                "bokr35w.ttf", "Bookmndi.TTF", "Carrington.ttf", "CHASB___.TTF", "Cookie_Regular.ttf",
                "DancingScript_Regular.otf", "Diploma.TTF", "FREEBSCA.ttf", "ITCKrist.TTF", "JohnHandy.TTF","Jokerman.TTF", "kidnap.TTF",
                "latha.ttf", "lucasr.ttf", "Nautilus.otf", "NEWS701I.TTF", "OpenSans-Bold.ttf","Orange.TTF", "sans_.ttf", "times.ttf"};


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, itemsforShareWith);

        ArrayAdapter<String> FontAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, itemsforFont);

        ArrayAdapter<String> FontSize = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, itemsforFontSize);

        select_shareWith.setAdapter(adapter);
        select_fontFamily_for_Text.setAdapter(FontAdapter);
        select_fontSize_for_Text.setAdapter(FontSize);

        select_shareWith.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        shareWithText = "Followers/Friends";
                        break;
                    case 1:
                        shareWithText = "Friends";
                        break;
                    default:
                        shareWithText = "Followers/Friends";
                        break;
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreatePostActivity.this, "no share with selected", Toast.LENGTH_SHORT).show();
            }
        });

        select_fontFamily_for_Text.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(),
                        "fonts/" + itemsforFont[position]);
                postTest_Caption.setTypeface(typeface);

                fontFamily = itemsforFont[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreatePostActivity.this, "Nothing selected...", Toast.LENGTH_SHORT).show();
            }
        });

        select_fontSize_for_Text.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        postTest_Caption.setTextSize(14f);
                        fontSize = "14";
                        break;
                    case 1:
                        postTest_Caption.setTextSize(16f);
                        fontSize = "16";
                        break;
                    case 2:
                        postTest_Caption.setTextSize(18f);
                        fontSize = "18";
                        break;
                    case 3:
                        postTest_Caption.setTextSize(20f);
                        fontSize = "20";
                        break;
                    case 4:
                        postTest_Caption.setTextSize(22f);
                        fontSize = "22";
                        break;
                    case 5:
                        postTest_Caption.setTextSize(22f);
                        fontSize = "24";
                        break;
                    default:
                        postTest_Caption.setTextSize(16f);
                        fontSize = "16";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreatePostActivity.this, "Notin selected....", Toast.LENGTH_SHORT).show();
            }
        });

        SelectImage_Video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGellary();
            }
        });
        create_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(selectedFileType)){
                    createNewPost("PostText");
                }else{
                    createNewPost(selectedFileType);
                }
            }
        });

    }


    private void createNewPost(String type) {
        String textPost_caption = postTest_Caption.getText().toString();
        
        if (!TextUtils.isEmpty(textPost_caption.trim()) || !TextUtils.isEmpty(ImageVideoSelected.trim())){

            Calendar calendarFordate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            String _date = currentDateFormat.format(calendarFordate.getTime());

            Calendar calendarForTime= Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            String _time = currentTimeFormat.format(calendarForTime.getTime());

            SendPostBackground sendPostBackground = new SendPostBackground(CreatePostActivity.this);

            if (!TextUtils.isEmpty(textPost_caption.trim())&& TextUtils.isEmpty(ImageVideoSelected.trim()) && type.equals("PostText")){

                sendPostBackground.execute(type,textPost_caption,shareWithText,fontFamily,fontSize,
                        backgroundSelected,_time,_date);
            }else if (!TextUtils.isEmpty(ImageVideoSelected.trim()) && (type.equals("image") || type.equals("video"))){

                sendPostBackground.execute(type,textPost_caption,shareWithText,fontFamily,fontSize,
                        backgroundSelected,_time,_date,type,ImageVideoSelected);

            }else{
                Toast.makeText(this, "Error: Unknow post type", Toast.LENGTH_SHORT).show();
            }


        }else{
            Log.d(TAG, "createNewPost: no file selected nor any text entered....");
            Toast.makeText(this, "Please add something and try again", Toast.LENGTH_SHORT).show();
        }
    }


    private void openGellary() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*,video/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*,video/*"});
        startActivityForResult(intent, SELECTED_FROM_GALLERY_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){

            assert data.getData() !=null;
            if (requestCode == SELECTED_FROM_GALLERY_CODE){
                getAttachmentFile(data.getData());
            }
        }
    }

    private void getAttachmentFile(Uri data) {
        if (data!=null){
            String sourceFilename= getRealPathFromUri(this,data,selectedFileType);

            setEditTexttoDefault();
            ImageVideoSelected = sourceFilename;
            customize_EditText_Background.setVisibility(View.GONE);

            String fileType = getContentResolver().getType(data);
            assert fileType != null;
            if (fileType.contains("image")){
                file_postFrame.setVisibility(View.VISIBLE);
                post_VideoThumbnail_play.setVisibility(View.GONE);
                selectedFileType="image";

                Glide.with(CreatePostActivity.this)
                        .asBitmap()
                        .load(data.getPath())
                        .apply(new RequestOptions().placeholder(R.drawable.profile_image))
                        .into(image_video_imageView);

            }else if (fileType.contains("video")){
                file_postFrame.setVisibility(View.VISIBLE);
                post_VideoThumbnail_play.setVisibility(View.VISIBLE);
                selectedFileType="video";
            }else{
                customize_EditText_Background.setVisibility(View.VISIBLE);
                file_postFrame.setVisibility(View.GONE);
                post_VideoThumbnail_play.setVisibility(View.GONE);
                selectedFileType="";
                ImageVideoSelected="";
            }

            post_imageVideoRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    file_postFrame.setVisibility(View.GONE);
                    post_VideoThumbnail_play.setVisibility(View.GONE);
                    image_video_imageView.setImageBitmap(null);
                    ImageVideoSelected="";
                    selectedFileType="";
                    customize_EditText_Background.setVisibility(View.VISIBLE);
                }
            });



            Glide.with(CreatePostActivity.this)
                    .asBitmap()
                    .load(sourceFilename)
                    .apply(new RequestOptions().placeholder(R.drawable.profile_image))
                    .into(image_video_imageView);

        }
    }


    public static String getRealPathFromUri(Context context, Uri contentUri, String fileMediaType) {
        Cursor cursor = null;


        switch (fileMediaType){
            case "image":{
                try {
                    String[] proj = { MediaStore.Images.Media.DATA };
                    cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    return cursor.getString(column_index);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            case "video":{
                try {
                    String[] proj = { MediaStore.Video.Media.DATA };
                    cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                    cursor.moveToFirst();
                    return cursor.getString(column_index);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            default:{
                try {
                    String[] proj = { MediaStore.Files.FileColumns.DATA };
                    cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                    cursor.moveToFirst();
                    return cursor.getString(column_index);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }

    }


    @Override
    public void onClick(View v) {
        post_background_transparent.setImageBitmap(null);
        post_background_black.setImageBitmap(null);
        post_background_navy.setImageBitmap(null);
        post_background_green.setImageBitmap(null);
        post_background_maroon.setImageBitmap(null);
        post_background_pink.setImageBitmap(null);
        post_background_shadesofgrey.setImageBitmap(null);
        post_background_jupiter.setImageBitmap(null);
        post_background_sherbert.setImageBitmap(null);
        post_background_firewatch.setImageBitmap(null);
        post_background_deepspace.setImageBitmap(null);
        post_background_grapefruitsunset.setImageBitmap(null);

        switch (v.getId()){
            case R.id.post_background_transparent:{
               setEditTexttoDefault();
            }
            break;
            default:
            {
                setEditTexttoCustomized();
            }
            break;
        }

        switch (v.getId()) {
            case R.id.post_background_transparent: {
                post_background_transparent.setImageResource(R.drawable.ic_done_black_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_transparent);
                backgroundSelected = "post_background_transparent";
            }
            break;

            case R.id.post_background_black: {
                post_background_black.setImageResource(R.drawable.ic_done_white_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_black);
                backgroundSelected = "post_background_black";
            }
            break;

            case R.id.post_background_navy: {
                post_background_navy.setImageResource(R.drawable.ic_done_white_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_navy);
                backgroundSelected = "post_background_navy";
            }
            break;

            case R.id.post_background_green: {
                post_background_green.setImageResource(R.drawable.ic_done_white_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_green);
                backgroundSelected = "post_background_green";
            }
            break;

            case R.id.post_background_maroon: {
                post_background_maroon.setImageResource(R.drawable.ic_done_white_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_maroon);
                backgroundSelected = "post_background_maroon";
            }
            break;

            case R.id.post_background_pink: {
                post_background_pink.setImageResource(R.drawable.ic_done_white_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_pink);
                backgroundSelected = "post_background_pink";
            }
            break;

            case R.id.post_background_shadesofgrey: {
                post_background_shadesofgrey.setImageResource(R.drawable.ic_done_white_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_shadesofgrey);
                backgroundSelected = "post_background_shadesofgrey";
            }
            break;

            case R.id.post_background_jupiter: {
                post_background_jupiter.setImageResource(R.drawable.ic_done_white_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_jupiter);
                backgroundSelected = "post_background_jupiter";
            }
            break;

            case R.id.post_background_sherbert: {
                post_background_sherbert.setImageResource(R.drawable.ic_done_white_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_sherbert);
                backgroundSelected = "post_background_sherbert";
            }
            break;

            case R.id.post_background_firewatch: {
                post_background_firewatch.setImageResource(R.drawable.ic_done_white_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_firewatch);
                backgroundSelected = "post_background_firewatch";
            }
            break;

            case R.id.post_background_deepspace: {
                post_background_deepspace.setImageResource(R.drawable.ic_done_white_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_deepspace);
                backgroundSelected = "post_background_deepspace";
            }
            break;

            case R.id.post_background_grapefruitsunset: {
                post_background_grapefruitsunset.setImageResource(R.drawable.ic_done_white_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_grapefruitsunset);
                backgroundSelected = "post_background_grapefruitsunset";
            }
            break;

            default: {
                post_background_transparent.setImageResource(R.drawable.ic_done_black_24dp);
                postTest_Caption.setBackgroundResource(R.drawable.post_background_transparent);
                backgroundSelected = "post_background_transparent";
            }
        }
    }

    private void setEditTexttoCustomized() {
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(),
                "fonts/AlexBrush_Regular.ttf");
        postTest_Caption.setTypeface(typeface);
        postTest_Caption.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        postTest_Caption.setTextColor(ContextCompat.getColor(this,R.color.white));
        fontFamily = "AlexBrush_Regular.ttf";
        select_fontFamily_for_Text.setSelection(0);
        select_fontSize_for_Text.setSelection(0);
        fontSize_and_fontfamily.setVisibility(View.VISIBLE);
    }

    private void setEditTexttoDefault() {
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(),
                "fonts/sans_.ttf");
        postTest_Caption.setTypeface(typeface);
        postTest_Caption.setTextSize(14f);
        postTest_Caption.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        postTest_Caption.setTextColor(ContextCompat.getColor(this,R.color.black));
        fontSize_and_fontfamily.setVisibility(View.GONE);
        fontFamily = "sans_.ttf";
    }
}
