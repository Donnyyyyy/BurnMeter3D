package ru.donny.burnmeter3D.controllers.camera;

public interface CameraModeController {

	public static final short TOUCH_MODE_ROTATE = 0;
	public static final short TOUCH_MODE_SELECT = 1;
	
	public int getMode();
}
