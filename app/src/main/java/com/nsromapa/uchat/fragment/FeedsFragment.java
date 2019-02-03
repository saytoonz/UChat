package com.nsromapa.uchat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public void inOnCreateView(View view, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {
        UserInformation userInformationListener = new UserInformation();
        userInformationListener.startFetchingFollowing();
    }
}
