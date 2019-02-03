package com.nsromapa.uchat.photoeditor;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.nsromapa.uchat.photoeditor.Interfaces.EditImageFragmentListner;
import com.nsromapa.uchat.R;


public class EditImageFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    private EditImageFragmentListner listner;
    SeekBar seekBar_brightness, seekBar_contrast,seekBar_saturation;

    static EditImageFragment instance;

    public static EditImageFragment getInstance(){
        if (instance==null)
            instance = new EditImageFragment();
        return instance;
    }


    public void setListner(EditImageFragmentListner listner) {
        this.listner = listner;
    }

    public EditImageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView =  inflater.inflate(R.layout.fragment_filter__edit_image, container, false);

        seekBar_brightness = (SeekBar) itemView.findViewById(R.id.filter_seekbar_brightness);
        seekBar_contrast = (SeekBar) itemView.findViewById(R.id.filter_seekbar_contrast);
        seekBar_saturation = (SeekBar) itemView.findViewById(R.id.filter_seekbar_saturation);

        seekBar_brightness.setMax(200);
        seekBar_brightness.setProgress(100);

        seekBar_contrast.setMax(20);
        seekBar_contrast.setProgress(0);

        seekBar_saturation.setMax(30);
        seekBar_saturation.setProgress(10);

        seekBar_brightness.setOnSeekBarChangeListener(this);
        seekBar_contrast.setOnSeekBarChangeListener(this);
        seekBar_saturation.setOnSeekBarChangeListener(this);

        return itemView;

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (listner!=null){
            if (seekBar.getId() == R.id.filter_seekbar_brightness){

                listner.onBrightnessChanged(progress-100);
            }

            else if (seekBar.getId() == R.id.filter_seekbar_contrast){
                progress+=10;
                float value = .10f*progress;
                listner.onContrastChanged(value);
            }

            else if (seekBar.getId() == R.id.filter_seekbar_saturation){
                float value = .10f*progress;
                listner.onSaturationChanged(value);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (listner!=null)
            listner.onEditStarted();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (listner!=null)
            listner.onEditCompleted();
    }




    public void resetControls(){
        seekBar_brightness.setProgress(100);
        seekBar_contrast.setProgress(0);
        seekBar_saturation.setProgress(10);
    }

}
