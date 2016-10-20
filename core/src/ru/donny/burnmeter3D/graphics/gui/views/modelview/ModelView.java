package ru.donny.burnmeter3D.graphics.gui.views.modelview;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.donny.burnmeter3D.controllers.Preferences;
import ru.donny.burnmeter3D.data.Burn.BurnType;
import ru.donny.burnmeter3D.engine.objects.burns.BurnsStorage;
import ru.donny.burnmeter3D.engine.objects.burns.BurnsWrapper;
import ru.donny.burnmeter3D.engine.objects.model.ModelAbstraction;
import ru.donny.burnmeter3D.engine.objects.model.ModelWrapper;
import ru.donny.burnmeter3D.engine.objects.model.PatientModelInstance;
import ru.donny.burnmeter3D.graphics.gui.widgets.DataInterface;
import ru.donny.burnmeter3D.graphics.gui.widgets.NavigationMap.NavigationMapEventListener;

public class ModelView implements InputProcessor, NavigationMapEventListener {

	private CameraInputController cameraController;
	private Camera camera;
	private Environment environment;
	private PatientModelInstance modelInstance;
	private ModelBatch modelBatch;
	private InputMultiplexer inputMultiplexer;
	private Map<BurnSelectionMode, BurnSelectionHandler> selectionHandlers;
	private BurnSelectionHandler activeBurnSelectionHandler;
	private ModelAbstraction modelAbstraction;
	private ModelViewController controller;

	public ModelView(ModelWrapper modelWrapper, InputProcessor navigationChangedListener,
			DataInterface resultDisplayer) {
		super();
		this.modelInstance = new PatientModelInstance(modelWrapper.getModel(), modelWrapper.getName(),
				new BurnsWrapper(resultDisplayer, modelWrapper.getModelAbstraction().getSquare()));

		this.modelAbstraction = modelWrapper.getModelAbstraction();
		initEnvironment();
		initCamera();
		initCameraInputController(getCamera());
		controller = new ModelViewController(BurnType.I);
		initInputAdapter(controller, cameraController, navigationChangedListener);
	}

	private void initInputAdapter(ModelViewController controller, CameraInputController cameraController,
			InputProcessor navigationChangedListener) {
		inputMultiplexer = new InputMultiplexer(controller);
		if (navigationChangedListener != null)
			inputMultiplexer.addProcessor(navigationChangedListener);

		inputMultiplexer.addProcessor(cameraController);
	}

	public boolean setupBurnSelection(Map<BurnSelectionMode, BurnSelectionHandler> selectionHandlers,
			BurnSelectionMode defaultMode) {
		this.selectionHandlers = selectionHandlers;

		return setBurnSelectionMode(defaultMode);
	}

	public boolean setBurnSelectionMode(BurnSelectionMode mode) {
		if (mode == BurnSelectionMode.none) {
			inputMultiplexer.removeProcessor(activeBurnSelectionHandler);
		}

		if (selectionHandlers == null)
			return false;

		BurnSelectionHandler newBurnSelectionHandler = selectionHandlers.get(mode);
		if (newBurnSelectionHandler != null) {
			inputMultiplexer.removeProcessor(activeBurnSelectionHandler);
			activeBurnSelectionHandler = newBurnSelectionHandler;
			activeBurnSelectionHandler.setType(controller.getSelectedType());
			inputMultiplexer.addProcessor(0, activeBurnSelectionHandler);
			return true;
		}

		return false;
	}

	private void initEnvironment() {
		setEnvironment(new Environment());
		getEnvironment().set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		getEnvironment().add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		getEnvironment().add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.8f, -0.2f));
	}

	/**
	 * initializes parameters of camera.
	 */
	private void initCamera() {
		setCamera(new PerspectiveCamera(Preferences.DEFAULT_CAMERA_FIELD_OF_VIEW, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight()));
		getCamera().position.set(10f, 10f, 10f);

		BoundingBox modelBounds = new BoundingBox();
		modelInstance.calculateBoundingBox(modelBounds);
		getCamera().lookAt(modelBounds.getCenterX(), modelBounds.getCenterY(), modelBounds.getCenterZ());
		getCamera().near = 1f;
		getCamera().far = 300f;
		getCamera().update();
	}

	private void initCameraInputController(Camera camera) {
		cameraController = new CameraInputController(camera);
		BoundingBox modelBounds = new BoundingBox();
		modelInstance.calculateBoundingBox(modelBounds);
		modelBounds.getCenter(cameraController.target);
	}

	public void show() {
		modelBatch = new ModelBatch();
	}

	public void draw() {
		modelBatch.begin(getCamera());
		modelBatch.render(modelInstance, getEnvironment());
		modelBatch.render(modelInstance.getBurns(), getEnvironment());
		modelBatch.end();

		activeBurnSelectionHandler.drawPreview();

		controller.act();
		controller.draw();
	}

	public void pause() {
		modelBatch.dispose();
	}

	public void dispose() {
		modelBatch.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if ((Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) && (keycode == Input.Keys.Z)) {
			modelInstance.removeLast();
			return true;
		}

		return inputMultiplexer.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		return inputMultiplexer.keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		return inputMultiplexer.keyTyped(character);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		boolean result = inputMultiplexer.touchDown(screenX, screenY, pointer, button);
		return result;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		boolean result = inputMultiplexer.touchUp(screenX, screenY, pointer, button);
		return result;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		boolean result = inputMultiplexer.touchDragged(screenX, screenY, pointer);
		return result;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		boolean result = inputMultiplexer.mouseMoved(screenX, screenY);
		return result;
	}

	@Override
	public boolean scrolled(int amount) {
		boolean result = inputMultiplexer.scrolled(amount);
		return result;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void targetChanged(Vector3 newTarget) {
		camera.position.set(newTarget.x, newTarget.y, camera.position.z);
		camera.lookAt(newTarget);
		cameraController.target.set(newTarget);
		camera.update();
		cameraController.update();
		modelAbstraction.updateProjections(camera);
	}

	public PatientModelInstance getModelInstance() {
		return modelInstance;
	}

	public void setModelInstance(PatientModelInstance modelInstance) {
		this.modelInstance = modelInstance;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public BurnsStorage getBurnsStorage() {
		return modelInstance.getBurns();
	}

	protected class ModelViewController extends Stage {

		private Table rootTable;
		private Table colorSelectionTable;
		private Skin skin = new Skin(Gdx.files.internal("data/skins/model_view_controller.json"));
		private BurnType selectedType;

		public ModelViewController(BurnType defaultType) {
			initRootTable();

			setSelectedType(defaultType);
			rootTable.add(initSelectionTable()).expand().top();
		}

		private Table initSelectionTable() {
			colorSelectionTable = new Table();

			for (Actor i : createBurnTypeSelectors())
				colorSelectionTable.add(i).height(35).width(35).padLeft(5);

			return colorSelectionTable;
		}

		private ArrayList<? extends Actor> createBurnTypeSelectors() {
			ArrayList<Button> selectors = new ArrayList<Button>();

			ImageButton degree1 = new ImageButton(skin.get("i", ImageButtonStyle.class));
			degree1.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (activeBurnSelectionHandler != null)
						activeBurnSelectionHandler.setType(BurnType.I);
				}
			});
			selectors.add(degree1);

			ImageButton degree2 = new ImageButton(skin.get("ii", ImageButtonStyle.class));
			degree2.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (activeBurnSelectionHandler != null)
						activeBurnSelectionHandler.setType(BurnType.II);
				}
			});
			selectors.add(degree2);

			ImageButton degree3 = new ImageButton(skin.get("iii", ImageButtonStyle.class));
			degree3.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (activeBurnSelectionHandler != null)
						activeBurnSelectionHandler.setType(BurnType.III);
				}
			});
			selectors.add(degree3);
			// ImageButton eraser = new ImageButton(skin.get("eraser",
			// ImageButtonStyle.class));
			// selectors.add(eraser);

			return selectors;
		}

		private void initRootTable() {
			rootTable = new Table();
			rootTable.setFillParent(true);
			addActor(rootTable);
		}

		public BurnType getSelectedType() {
			return selectedType;
		}

		public void setSelectedType(BurnType selectedType) {
			this.selectedType = selectedType;
		}
	}
}
