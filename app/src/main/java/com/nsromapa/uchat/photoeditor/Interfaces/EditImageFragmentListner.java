package com.nsromapa.uchat.photoeditor.Interfaces;

public interface EditImageFragmentListner {
    void onBrightnessChanged(int Brightness);
    void onSaturationChanged(float Saturation);
    void onContrastChanged(float Contrast);
    void onEditStarted();
    void onEditCompleted();
}
