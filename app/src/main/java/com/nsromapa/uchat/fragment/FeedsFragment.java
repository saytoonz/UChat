package com.nsromapa.uchat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;

import com.nsromapa.uchat.CreatePostActivity;
import com.nsromapa.uchat.R;
import com.nsromapa.uchat.usersInfos.UserInformation;

public class FeedsFragment extends BaseFragment {

    public static FeedsFragment create(){
        return new FeedsFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_feeds;
    }


    private FloatingActionButton fab_addPost;

    @Override
    public void inOnCreateView(View view, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {

        fab_addPost = view.findViewById(R.id.fab_addPost);
        fab_addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreatePostActivity.class);
                startActivity(intent);
            }
        });


    }
}
