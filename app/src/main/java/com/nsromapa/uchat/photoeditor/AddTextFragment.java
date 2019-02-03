package com.nsromapa.uchat.photoeditor;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nsromapa.uchat.photoeditor.Interfaces.AddTextFragmentListner;
import com.nsromapa.uchat.photoeditor.adapter.ColorAdapter;
import com.nsromapa.uchat.photoeditor.adapter.FontAdapter;
import com.nsromapa.uchat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTextFragment extends BottomSheetDialogFragment implements ColorAdapter.ColorAdapterListner, FontAdapter.FontAdapterClickListener {

    int colorSelected = Color.parseColor("#000000"); //Set default color to black

    public void setListner(AddTextFragmentListner listner) {
        this.listner = listner;
    }

    AddTextFragmentListner listner;

    EditText edit_add_text;
    RecyclerView recycler_color,recycler_font;
    Button btn_done;

    Typeface typefaceSelected = Typeface.DEFAULT;


    static AddTextFragment instance;

    public static AddTextFragment getInstance(){
        if (instance==null)
            instance=new AddTextFragment();
        return instance;
    }

    public AddTextFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_text, container, false);

        edit_add_text = (EditText)view.findViewById(R.id.edit_add_text);
        btn_done = (Button)view.findViewById(R.id.btn_done);
        recycler_color = (RecyclerView)view.findViewById(R.id.recycler_color);
        recycler_color.setHasFixedSize(true);
        recycler_color.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        recycler_font = (RecyclerView)view.findViewById(R.id.recycler_font);
        recycler_font.setHasFixedSize(true);
        recycler_font.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        ColorAdapter colorAdapter = new ColorAdapter(getContext(),this);
        recycler_color.setAdapter(colorAdapter);

        FontAdapter fontAdapter = new FontAdapter(getContext(),this);
        recycler_font.setAdapter(fontAdapter);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listner.onAddTextButtonClicked(typefaceSelected,edit_add_text.getText().toString(),colorSelected);
            }
        });

        return view;
    }

    @Override
    public void onColorSelected(int color) {
        colorSelected = color; //Set new color when its selected
    }

    @Override
    public void onFontSelected(String fontName) {
        typefaceSelected = Typeface.createFromAsset(getContext().getAssets(),new StringBuilder("fonts/")
                .append(fontName).toString());
    }
}
