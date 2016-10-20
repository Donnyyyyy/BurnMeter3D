package ru.donny.burnmeter3d.android;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ru.donny.burnmeter3D.controllers.ApplicationController;
import ru.donny.burnmeter3D.resources.XmlTextResources;
import ru.donny.burnmeter3d.android.database.AndroidGraphStorageProvider;
import ru.donny.burnmeter3d.android.database.PatientDatabase;

public class AndroidLauncher extends AndroidApplication {

	private static final String PATIENT_DATABASE_NAME = "PatientStorage";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(createGdxView());
	}

	private View createGdxView(){
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useGLSurfaceView20API18 = false;
		config.r = 8;
		config.g = 8;
		config.b = 8;
		config.a = 8;

		View gdxView = initializeForView(new ApplicationController(new PatientDatabase(this, PATIENT_DATABASE_NAME),
				new AndroidGraphStorageProvider(this),
				new AndroidDeviceCameraController(this),
				new XmlTextResources("strings/ru.xml")), config);

		if (graphics.getView() instanceof SurfaceView) {
			SurfaceView glView = (SurfaceView) graphics.getView();
			glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		}

		return gdxView;
	}

	public void post(Runnable r) {
		handler.post(r);
	}

}
