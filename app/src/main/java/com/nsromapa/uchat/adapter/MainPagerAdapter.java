package com.nsromapa.uchat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nsromapa.uchat.fragment.ChatFragment;
import com.nsromapa.uchat.fragment.CameraFragment;
import com.nsromapa.uchat.fragment.FaceFragment;
import com.nsromapa.uchat.fragment.FeedsFragment;
import com.nsromapa.uchat.fragment.StoryFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {


    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return CameraFragment.create();
            case 1:
                return StoryFragment.create();
            case 2:
                return FeedsFragment.create();
            case 3:
                return ChatFragment.create();
            case 4:
                return FaceFragment.create();
        }
        return null;
    }


    @Override
    public int getCount() {
        return 5;
    }
}
