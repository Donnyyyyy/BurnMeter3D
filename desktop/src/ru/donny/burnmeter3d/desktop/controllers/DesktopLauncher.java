package ru.donny.burnmeter3d.desktop.controllers;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;

import java.awt.Dimension;
import java.awt.Toolkit;

import ru.donny.burnmeter3D.controllers.ApplicationController;
import ru.donny.burnmeter3D.controllers.DeviceCameraControl;
import ru.donny.burnmeter3D.resources.XmlTextResources;
import ru.donny.burnmeter3d.desktop.database.DesktopGraphStorageProvider;
import ru.donny.burnmeter3d.desktop.database.PatientDatabase;

public class DesktopLauncher {

    private static final String PATIENT_STORAGE_NAME = "PatientStorage";
    private static final String GRAPH_STORAGE_FILE_FOLDER = "res";

    public static void main(String[] arg) {

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = false;

        config.addIcon("data/drawable/icon32x32.png", FileType.Internal);
        config.title = "Burn Meter 3D";

        new LwjglApplication(new ApplicationController(new PatientDatabase(PATIENT_STORAGE_NAME),
                new DesktopGraphStorageProvider(GRAPH_STORAGE_FILE_FOLDER), new DeviceCameraControl() {

            @Override
            public byte[] takePictureAsync(long timeout) {
                return null;
            }

            @Override
            public void takePicture() {

            }

            @Override
            public void stopPreviewAsync() {

            }

            @Override
            public void stopPreview() {

            }

            @Override
            public void startPreviewAsync() {

            }

            @Override
            public void startPreview() {

            }

            @Override
            public void saveAsJpeg(FileHandle jpgfile, Pixmap cameraPixmap) {

            }

            @Override
            public void prepareCameraAsync() {

            }

            @Override
            public void prepareCamera() {

            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public byte[] getPictureData() {
                return null;
            }
        }, new XmlTextResources("strings/ru.xml")), config);
    }
}
