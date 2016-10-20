package ru.donny.burnmeter3d.android;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

@SuppressWarnings("deprecation")
public class CameraSurface extends SurfaceView implements Callback {
	private Camera camera;

	public CameraSurface(Context context) {
		super(context);
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Camera.Parameters p = camera.getParameters();
		p.setPreviewSize(width, height);
		camera.setParameters(p);

		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public Camera getCamera() {
		return camera;
	}

}