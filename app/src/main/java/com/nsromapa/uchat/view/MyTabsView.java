package com.nsromapa.uchat.view;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nsromapa.uchat.R;

public class MyTabsView extends FrameLayout implements ViewPager.OnPageChangeListener {

    private ImageView mHomeImage;
    private ImageView mCamerImage;
    private ImageView mStories_Image;
    private ImageView mFaceImage;
    private ImageView mChat_Image;

    private ArgbEvaluator mArgbEvaluator;
    private int mCenterColor;
    private int mSideColor;

    private int mEndViewTranslationX;
    private int mIndicatorTranslationX;
    private int mCenterTranslationY;


    public MyTabsView(@NonNull Context context) {
        this(context, null);
    }
    public MyTabsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MyTabsView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }





    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position==0){
            setColor(positionOffset,mSideColor);
//            moveViews(1+positionOffset);

//            moveAndScaleCenter((1-positionOffset));

        }
    }
    @Override
    public void onPageSelected(int position) {

    }
    @Override
    public void onPageScrollStateChanged(int state) {

    }




    private void moveAndScaleCenter(float fractionFromCenter){
        float scale = .7f + ((1+ fractionFromCenter) * .3f);
;
        mHomeImage.setScaleX(scale);
        mHomeImage.setScaleY(scale);




        int  transilation = (int) (fractionFromCenter * mCenterTranslationY);
        mCamerImage.setTranslationY(transilation);


        mStories_Image.setTranslationY(10+transilation);
        mChat_Image.setTranslationY(10+transilation);


    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_my_bottom_tabs,this ,true);

        mHomeImage = findViewById(R.id.vmt_home_image);
        mCamerImage = findViewById(R.id.vmt_camera_image);
        mStories_Image = findViewById(R.id.vmt_stories_image);
        mChat_Image = findViewById(R.id.vmt_chats_image);
        mFaceImage = findViewById(R.id.vmt_facemash_image);

        mCenterColor = ContextCompat.getColor(getContext(), R.color.white);
        mSideColor = ContextCompat.getColor(getContext(), R.color.dark_grey);

        mArgbEvaluator = new ArgbEvaluator();

        mIndicatorTranslationX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,80,getResources().getDisplayMetrics());

//        mBottomImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mEndViewTranslationX = (int) (((mBottomImage.getX()) - mCamerImage.getX()) - mIndicatorTranslationX);
//                mBottomImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//
//                mCenterTranslationY = getHeight()- mBottomImage.getBottom();
//
//            }
//        });
    }

    private void setColor(float fractionFromCenter,int mSideColor){
       int color = (int) mArgbEvaluator.evaluate(fractionFromCenter,mCenterColor,mSideColor);

        mHomeImage.setColorFilter(color);
        mCamerImage.setColorFilter(color);
        mFaceImage.setColorFilter(color);

        mStories_Image.setColorFilter(color);
        mChat_Image.setColorFilter(color);
    }

    public void setUpWithVeiewPager(final ViewPager viewPager){
        viewPager.addOnPageChangeListener(this);

        mCamerImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()!= 0)
                    viewPager.setCurrentItem(0);
            }
        });
        mStories_Image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()!= 1)
                    viewPager.setCurrentItem(1);
            }
        });

        mHomeImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()!= 2)
                    viewPager.setCurrentItem(2);
            }
        });

        mChat_Image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()!= 3)
                    viewPager.setCurrentItem(3);
            }
        });

        mFaceImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem()!= 4)
                    viewPager.setCurrentItem(4);
            }
        });


    }


}
