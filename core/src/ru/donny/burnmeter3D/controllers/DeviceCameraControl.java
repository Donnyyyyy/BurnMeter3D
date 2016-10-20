package ru.donny.burnmeter3D.controllers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;

public interface DeviceCameraControl {

    void prepareCamera();

    void startPreview();

    void stopPreview();

    void takePicture();

    byte[] getPictureData();

    void startPreviewAsync();

    void stopPreviewAsync();

    byte[] takePictureAsync(long timeout);

    void saveAsJpeg(FileHandle jpgfile, Pixmap cameraPixmap);

    boolean isReady();

    void prepareCameraAsync();
}