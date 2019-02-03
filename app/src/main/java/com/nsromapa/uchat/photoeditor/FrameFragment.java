package com.nsromapa.uchat.photoeditor;


import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nsromapa.uchat.photoeditor.Interfaces.AddFrameListener;
import com.nsromapa.uchat.photoeditor.adapter.FrameAdapter;
import com.nsromapa.uchat.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FrameFragment extends BottomSheetDialogFragment implements FrameAdapter.FrameAdapterListener {

    RecyclerView recycler_frame;
    Button btn_add_frame;

    static  FrameFragment instance;

    public void setListener(AddFrameListener listener) {
        this.listener = listener;
    }

    AddFrameListener listener;

    int frame_selected= -1;

    public static FrameFragment getInstance() {
        if (instance==null)
            instance = new FrameFragment();
        return instance;
    }

    public FrameFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frame, container, false);

        recycler_frame = (RecyclerView)view.findViewById(R.id.frame_recycler);
        btn_add_frame = (Button)view.findViewById(R.id.but_add_frame);

        recycler_frame.setHasFixedSize(true);
        recycler_frame.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        recycler_frame.setAdapter(new FrameAdapter(getContext(),this));

        btn_add_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAddFrame(frame_selected);
            }
        });

        return view;
    }

    @Override
    public void onFrameSelected(int frame) {
        frame_selected = frame;
    }
}
