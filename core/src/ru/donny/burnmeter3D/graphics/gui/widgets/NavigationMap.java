package ru.donny.burnmeter3D.graphics.gui.widgets;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import ru.donny.burnmeter3D.controllers.Preferences;
import ru.donny.burnmeter3D.engine.MathEngine;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.engine.objects.geometry.objects2d.Point2D;
import ru.donny.burnmeter3D.engine.objects.geometry.point.ModelIntersection;
import ru.donny.burnmeter3D.engine.objects.model.ModelWrapper;
import ru.donny.burnmeter3D.graphics.renderable.DebugModelBuilder;

public class NavigationMap implements InputProcessor {

	private Camera mapCamera;
	private ModelWrapper navigatedModelWrapper;
	private ModelInstance modelInstance;
	private ArrayList<NavigationMapEventListener> eventListeners;
	private ModelBatch modelBatch;
	private Viewport viewport;
	private ModelInstance intersectedTriangle;
	private Environment environment;

	public NavigationMap(float width, float height, ModelInstance modelInstance, ModelWrapper navigatedModelWrapper) {
		super();
		eventListeners = new ArrayList<NavigationMap.NavigationMapEventListener>();
		viewport = new FitViewport(width, height);
		viewport.setScreenBounds((int) (Gdx.graphics.getWidth() - width), (int) (Gdx.graphics.getHeight() - height),
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.modelInstance = modelInstance;
		this.navigatedModelWrapper = navigatedModelWrapper;
		modelBatch = new ModelBatch();
		initMapCamera();
		initEnvironment();
	}

	private void initMapCamera() {
		mapCamera = new PerspectiveCamera(Preferences.DEFAULT_CAMERA_FIELD_OF_VIEW, viewport.getWorldWidth(),
				viewport.getWorldHeight());

		// set up the camera center
		applyModelInstance(modelInstance);

		mapCamera.near = 1f;
		mapCamera.far = 1000f;
		mapCamera.update();
	}

	private void setCameraCenter(Vector3 center) {
		mapCamera.position.set(center.x, center.y, center.z + 50 * 10f);
		mapCamera.lookAt(center.x - 45f * 10f, center.y - 20f * 10f, center.z);
		mapCamera.update();
	}

	private void initEnvironment() {
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.8f, -0.2f));
	}

	public void draw() {
		modelBatch.begin(mapCamera);

		if (modelInstance != null)
			modelBatch.render(getModelInstance(), environment);

		if (intersectedTriangle != null)
			modelBatch.render(intersectedTriangle);

		modelBatch.end();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		ModelIntersection pickedPoint = MathEngine.getIntersection(navigatedModelWrapper.getModelAbstraction(),
				new Point2D(screenX, screenY), mapCamera);
		if (pickedPoint == null)
			return false;

		Triangle pointed = pickedPoint.getTriangle();
		intersectedTriangle = new ModelInstance(
				DebugModelBuilder.build(pointed.getCenter(), 3f, new Color(1f, 25f / 255f, 206f / 255f, 1f)));

		for (NavigationMapEventListener i : eventListeners)
			i.targetChanged(pointed.getCenter());

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public interface NavigationMapEventListener {

		public void targetChanged(Vector3 newTarget);
	}

	public void addListener(NavigationMapEventListener listener) {
		eventListeners.add(listener);
	}

	private void applyModelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;

		Vector3 center = (modelInstance != null)
				? modelInstance.calculateBoundingBox(new BoundingBox()).getCenter(new Vector3())
				: new Vector3(0f, 0f, 0f);

		setCameraCenter(center);
	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}

	public void setModelInstance(ModelInstance modelInstance) {
		applyModelInstance(modelInstance);
	}
}
