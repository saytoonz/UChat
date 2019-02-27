package com.nsromapa.uchat;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nsromapa.say.emogifstickerkeyboard.widget.EmoticonTextView;
import com.nsromapa.uchat.utils.FormatterUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPostActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String postId;
    private String postType;
    private String posterName;
    private String posterImage;

    private EmoticonTextView posterNameView;
    private TextView postTimeView;
    private CircleImageView posterImageView;

    @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        toolbar = findViewById(R.id.view_post_app_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.custom_view_post_bar,null);
        actionBar.setCustomView(actionBarView);

        posterNameView = findViewById(R.id.custom_bar_poster_name);
        postTimeView = findViewById(R.id.custom_bar_post_time);
        posterImageView = findViewById(R.id.custom_bar_poster_image_view);





        if(getIntent()!=null){
            postId = getIntent().getStringExtra("postId");
            postType = getIntent().getStringExtra("postType");
            posterName = getIntent().getStringExtra("posterName");
            posterImage = getIntent().getStringExtra("posterImage");

            posterNameView.setText(posterName);
            postTimeView.setText(FormatterUtil.getRelativeTimeSpanStringShort(this,Long.parseLong(postId)));
            Glide.with(this)
                    .asBitmap()
                    .apply(new RequestOptions().placeholder(R.drawable.profile_image))
                    .load(posterImage)
                    .into(posterImageView);


        }else{
            finish();
        }
    }
}
