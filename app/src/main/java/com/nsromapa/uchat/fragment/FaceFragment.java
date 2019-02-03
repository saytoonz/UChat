package com.nsromapa.uchat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.nsromapa.uchat.R;

public class FaceFragment extends BaseFragment {

    public static FaceFragment create(){
        return new FaceFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_face;
    }

    @Override
    public void inOnCreateView(View view, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {

    }
}
