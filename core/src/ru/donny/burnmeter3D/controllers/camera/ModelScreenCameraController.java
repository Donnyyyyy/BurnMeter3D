package ru.donny.burnmeter3D.controllers.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

import ru.donny.burnmeter3D.engine.objects.model.ModelAbstraction;

public class ModelScreenCameraController extends CameraInputController {

	private CameraModeController modeController;
	private ModelAbstraction modelAbstraction;

	public ModelScreenCameraController(Camera camera, CameraModeController modeController, ModelAbstraction model) {
		super(camera);
		this.modeController = modeController;
		this.modelAbstraction = model;
	}

	public ModelScreenCameraController(CameraGestureListener gestureListener, Camera camera,
			CameraModeController modeController) {
		super(gestureListener, camera);
		this.modeController = modeController;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (modeController.getMode() == CameraModeController.TOUCH_MODE_ROTATE)
			return super.touchDown(screenX, screenY, pointer, button);
		else
			return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (modeController.getMode() == CameraModeController.TOUCH_MODE_ROTATE)
			return super.touchUp(screenX, screenY, pointer, button);
		else
			return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		boolean isHandled;
		if (modeController.getMode() == CameraModeController.TOUCH_MODE_ROTATE)
			isHandled = super.touchDragged(screenX, screenY, pointer);
		else
			isHandled = false;
		modelAbstraction.updateProjections(camera);

		return isHandled;
	}

	@Override
	public boolean scrolled(int amount) {
		boolean isHandled = super.scrolled(amount);
		modelAbstraction.updateProjections(camera);

		return isHandled;
	}

}
