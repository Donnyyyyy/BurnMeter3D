package ru.donny.burnmeter3D.graphics.screens;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.donny.burnmeter3D.controllers.DeviceCameraControl;
import ru.donny.burnmeter3D.controllers.Logger;
import ru.donny.burnmeter3D.controllers.Preferences;
import ru.donny.burnmeter3D.controllers.camera.BurnByContourPainter;
import ru.donny.burnmeter3D.controllers.camera.BurnPainter;
import ru.donny.burnmeter3D.controllers.camera.CameraModeController;
import ru.donny.burnmeter3D.data.Burn;
import ru.donny.burnmeter3D.data.Patient;
import ru.donny.burnmeter3D.data.PatientStorage;
import ru.donny.burnmeter3D.data.graphstorage.ModelGraphStorage;
import ru.donny.burnmeter3D.engine.objects.burns.BurnsWrapper;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.engine.objects.model.BodyPart;
import ru.donny.burnmeter3D.engine.objects.model.ModelAbstraction;
import ru.donny.burnmeter3D.engine.objects.model.ModelSeparationBuilder;
import ru.donny.burnmeter3D.engine.objects.model.ModelWrapper;
import ru.donny.burnmeter3D.graphics.gui.RussianFont;
import ru.donny.burnmeter3D.graphics.gui.views.modelview.BurnSelectionHandler;
import ru.donny.burnmeter3D.graphics.gui.views.modelview.BurnSelectionMode;
import ru.donny.burnmeter3D.graphics.gui.views.modelview.ModelView;
import ru.donny.burnmeter3D.graphics.gui.widgets.ColorSelector;
import ru.donny.burnmeter3D.graphics.gui.widgets.DataInterface;
import ru.donny.burnmeter3D.graphics.gui.widgets.NavigationMap;
import ru.donny.burnmeter3D.graphics.gui.widgets.ResultLabel;
import ru.donny.burnmeter3D.resources.Resources;
import ru.donny.burnmeter3D.resources.TextResources;

public class ModelScreen extends StandartScreen {

	private ModelView modelView;
	private ScreenChangedListener screenChanger;
	private PatientStorage patientStorage;
	private InputMultiplexer inputController;
	private CameraModeController modeController;
	private ModelBatch modelBatch;
	private ModelAbstraction modelAbstraction;
	private ModelWrapper modelWrapper;
	private ModelUi userInterface;
	private AssetManager assetManager;
	private DeviceCameraControl cameraControl;
	private NavigationMap miniMap;

	// TODO sort out
	public ModelScreen(ModelWrapper modelWrapper, ModelGraphStorage modelDataBase, String modelName,
			ScreenChangedListener screenChanger, PatientStorage patientStorage,
			DeviceCameraControl deviceCameraControl) {
		this.screenChanger = screenChanger;
		this.patientStorage = patientStorage;
		this.modelWrapper = modelWrapper;
		// TODO remove me
		this.assetManager = new AssetManager();
		this.cameraControl = deviceCameraControl;

		this.modelBatch = new ModelBatch();

		initModelAbstraction(modelWrapper, assetManager, modelDataBase, modelName);
		this.modelWrapper.setModelAbstraction(modelAbstraction);

		initInterface();
		initMap();
		initModelView(modelWrapper, miniMap, modelAbstraction, userInterface);
		miniMap.addListener(modelView);
		miniMap.setModelInstance(modelView.getModelInstance());
		initInputControllers(modelView.getCamera(), modeController, modelAbstraction, userInterface);
	}

	// FIXME replace with modelScreen
	private void initMap() {
		miniMap = new NavigationMap(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 3f, null, modelWrapper);
	}

	private void initModelView(ModelWrapper model, NavigationMap minimap, ModelAbstraction modelAbstraction,
			ModelUi ui) {
		modelView = new ModelView(model, minimap, ui);

		HashMap<BurnSelectionMode, BurnSelectionHandler> burnSelectinHandlers = new HashMap<BurnSelectionMode, BurnSelectionHandler>();

		BurnPainter painter = new BurnPainter(modelView.getCamera(), modelAbstraction, modelAbstraction.getGraph(),
				Preferences.HOVER_RANGE);
		painter.setStorage(modelView.getBurnsStorage());
		burnSelectinHandlers.put(BurnSelectionMode.drawing, painter);

		BurnByContourPainter contourSelector = new BurnByContourPainter(modelAbstraction, modelView.getCamera());
		contourSelector.setStorage(modelView.getBurnsStorage());
		burnSelectinHandlers.put(BurnSelectionMode.contourSelection, contourSelector);

		modelView.setupBurnSelection(burnSelectinHandlers, BurnSelectionMode.contourSelection);
	}

	private void initModelAbstraction(ModelWrapper modelWrapper, AssetManager assetManager, ModelGraphStorage dataBase,
			String modelName) {
		this.modelAbstraction = new ModelAbstraction(modelWrapper, assetManager, dataBase, modelName);
		this.modelAbstraction.setRenderVertices(false);
		this.modelAbstraction.setRenderDetachedAreas(false, null);
		this.modelAbstraction.balanceSquare(modelWrapper.getTable());
	}

	private void initInterface() {
		userInterface = new ModelUi();
		userInterface.addTag(BurnsWrapper.SQUARE_RESULT_TAG,
				Resources.getTextResources().getString(TextResources.StringResource.burnsPercentage), "%", 0);

		userInterface.addTag(BurnsWrapper.THERAPY_RESULT_TAG,
				Resources.getTextResources().getString(TextResources.StringResource.therapyVolume),
				Resources.getTextResources().getString(TextResources.StringResource.ml), 0);

		modeController = userInterface.getCameraModeController();
	}

	private void initInputControllers(Camera camera, CameraModeController modeController,
			ModelAbstraction modelAbstraction, ModelUi ui) {
		mainStage.addActor(ui);
		inputController = new InputMultiplexer(mainStage, modelView);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputController);
		Logger.printTimeMessage("show");
		cameraControl.prepareCameraAsync();
		modelView.show();

		return;
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		Gdx.gl.glClearColor(.5f, .5f, .5f, 0.0f);

		if (cameraControl.isReady()) {
			cameraControl.startPreviewAsync();
		}

		modelBatch.begin(modelView.getCamera());
		// if (burns != null)
		// for (ModelInstance i : burns)
		// modelBatch.render(i, modelView.getEnvironment());

		modelView.draw();
		modelBatch.end();

		modelAbstraction.render(modelBatch, modelView.getEnvironment());
		miniMap.draw();

		userInterface.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		modelView.getCamera().update();
	}

	@Override
	public void pause() {
		Logger.printTimeMessage("pause");
	}

	@Override
	public void resume() {
		Logger.printTimeMessage("resume");
	}

	@Override
	public void hide() {
		Logger.printTimeMessage("hide");
	}

	@Override
	public void dispose() {
		Logger.printTimeMessage("dispose");
		modelBatch.dispose();
		cameraControl.stopPreviewAsync();
	}

	private class ModelUi extends Table implements DataInterface, ColorSelector {

		public static final String PARAM_WEIGHT = "weight";

		private final int HUMAN_PARAMS_HEIGHT = (int) (Gdx.graphics.getWidth() * 0.3f);

		private Table resultTable;
		private SelectBox<SelectBoxItem> colorSelector;
		private CameraSelector modeSelector;

		public ModelUi() {
			super();
			initialize();
			add(createForm(HUMAN_PARAMS_HEIGHT,
					Resources.getTextResources().getString(TextResources.StringResource.height), 165)).left();
			initializeSelectBox();
			add().width(getPreferedSelectBoxWidth()).expandX().right().row();
			add(createForm(HUMAN_PARAMS_HEIGHT,
					Resources.getTextResources().getString(TextResources.StringResource.height), 50, PARAM_WEIGHT))
							.left();

			initMap();
			initializeCameraSelector();
			add(initializeResultTable()).expandY().bottom().left();
			initializeManageButtons();
		}

		protected void initialize() {
			setFillParent(true);
			left().top();
			setFillParent(true);
		}

		private Actor initializeResultTable() {
			resultTable = new Table();
			row();
			return resultTable;
		}

		private Actor initializeSelectBox() {
			colorSelector = new SelectBox<SelectBoxItem>(defaultSkin) {

				@Override
				public void setSelected(SelectBoxItem item) {
					super.setSelected(item);
					colorSelector.getStyle().fontColor = item.getValue();
				}

				@Override
				public void setSelectedIndex(int index) {
					super.setSelectedIndex(index);
					colorSelector.getStyle().fontColor = getItems().get(index).getValue();
				}
			};

			colorSelector.setItems(
					new SelectBoxItem[] { new SelectBoxItem(Burn.BurnType.I.toString(), Burn.BurnType.I.getColor()),
							new SelectBoxItem(Burn.BurnType.II.toString(), Burn.BurnType.II.getColor()),
							new SelectBoxItem(Burn.BurnType.III.toString(), Burn.BurnType.III.getColor()) });
			colorSelector.pack();

			colorSelector.getStyle().font = russianFont;
			colorSelector.getStyle().listStyle.font = russianFont;

			return colorSelector;
		}

		private float getPreferedSelectBoxWidth() {
			float maxWidth = 0;

			for (SelectBoxItem i : colorSelector.getItems())
				if (maxWidth < i.getPrefWidth())
					maxWidth = i.getPrefWidth();

			return maxWidth + colorSelector.getPrefWidth();
		}

		public class SelectBoxItem extends Label {

			private Color value;

			public SelectBoxItem(String text, Color value) {
				super(text, defaultSkin);
				getStyle().font = russianFont;
				getStyle().fontColor = Preferences.FONT_COLOR;
				this.setValue(value);
			}

			@Override
			public String toString() {
				return getText().toString();
			}

			public Color getValue() {
				return value;
			}

			public void setValue(Color value) {
				this.value = value;
			}
		}

		private Table createForm(int width, String formName, float hintValue) {
			return createForm(width, formName, hintValue, "");
		}

		private Table createForm(int width, String formName, float hintValue, String valueName) {
			Label formLabel = createLabel(formName);
			TextField formValue = new TextField("", defaultSkin);
			formValue.setMessageText(Float.toString(hintValue));
			formValue.setName(valueName);

			Table form = new Table();
			form.add(formLabel).width(formLabel.getPrefWidth());
			form.add(formValue).width(width - formLabel.getPrefWidth());

			return form;
		}

		private class CameraSelector extends TextButton implements CameraModeController {

			private int mode;

			public CameraSelector(final String rotateText, final String selectText, Skin skin) {
				super(selectText, skin);
				mode = TOUCH_MODE_ROTATE;

				addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						super.clicked(event, x, y);
						if (mode == TOUCH_MODE_ROTATE) {
							mode = TOUCH_MODE_SELECT;
							setText(rotateText);
						} else {
							mode = TOUCH_MODE_ROTATE;
							setText(selectText);
						}
					}
				});
			}

			@Override
			public int getMode() {
				return mode;
			}

		}

		private Actor initializeCameraSelector() {
			TextButton trash = new TextButton("", defaultSkin);
			trash.getStyle().font = russianFont;
			trash.getStyle().fontColor = Preferences.FONT_COLOR;

			modeSelector = new CameraSelector(
					Resources.getTextResources().getString(TextResources.StringResource.rotate),
					Resources.getTextResources().getString(TextResources.StringResource.select), defaultSkin);
			modeSelector.getStyle().font = russianFont;
			modeSelector.getStyle().fontColor = Preferences.FONT_COLOR;

			return modeSelector;
			// rootTable.add(modeSelector).right().top();
		}

		private void initializeManageButtons() {
			Table manageButtonsTable = new Table();

			manageButtonsTable.add(createBackButton());
			manageButtonsTable.add(createSettingsButton());
			manageButtonsTable.add(createSaveButton());

			add(manageButtonsTable).right().bottom();
		}

		private TextButton createBackButton() {
			TextButton back = createTextButton(
					Resources.getTextResources().getString(TextResources.StringResource.back));

			back.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					super.clicked(event, x, y);
					ModelScreen.this.screenChanger.changeScreen(MainMenuScreen.class);
				}
			});

			return back;
		}

		private TextButton createSettingsButton() {
			TextButton back = createTextButton(
					Resources.getTextResources().getString(TextResources.StringResource.back));

			back.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					super.clicked(event, x, y);

					PreferenceScreen prefs = new PreferenceScreen();
					ModelScreen.this.screenChanger.changeScreenTo(prefs);
				}
			});

			return back;
		}

		private TextButton createSaveButton() {
			TextButton save = createTextButton(
					Resources.getTextResources().getString(TextResources.StringResource.save));

			save.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					super.clicked(event, x, y);

					Patient createdPatient = new Patient("", "", "", "",
							modelView.getBurnsStorage().toBurns(modelAbstraction), new GregorianCalendar());
					ModelScreen.this.screenChanger
							.changeScreenTo(new PatientModificationScreen(createdPatient, patientStorage, screenChanger,
									PatientModificationScreen.Action.insert, MainMenuScreen.class));
				}
			});

			return save;
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
		}

		@Override
		public void addTag(String tag, String resultType, String units, float defaultValue) {
			ResultLabel resultLabel = new ResultLabel(defaultSkin, resultType, units, defaultValue, russianFont,
					Color.WHITE);
			resultLabel.getStyle().fontColor = Color.WHITE;
			resultLabel.setName(tag);
			resultTable.add(resultLabel).left();
			resultTable.row();
		}

		@Override
		public void setResult(String tag, float value) {
			Actor resultLabel = resultTable.findActor(tag);
			if ((resultLabel != null) && (resultLabel.getClass() == ResultLabel.class))
				((ResultLabel) resultLabel).setResult(value);
		}

		public CameraModeController getCameraModeController() {
			return modeSelector;
		}

		@Override
		public Color getSelectedColor() {
			return colorSelector.getSelected().getValue();
		}

		@Override
		public float getWeight() {
			TextField weightField = ((TextField) findActor(PARAM_WEIGHT));
			try {
				return Float.parseFloat(weightField.getText());
			} catch (Exception e) {
				System.out.println("Can't parse weight (" + e.getMessage() + ").");
				return Float.parseFloat(weightField.getMessageText());
			}

		}
	}

	private class PreferenceScreen extends StandartScreen {

		private Table rootTable;
		private ModelSeparationBuilder separationBuilder;
		private Table calibrationUi;

		@Override
		public void show() {
			super.show();
			initializeInterface();
		}

		private void initializeInterface() {
			russianFont = RussianFont.get();
			rootTable = new Table();
			rootTable.setFillParent(true);

			rootTable.add(createCalibrateButton()).center().row();

			Button back = createTextButton(Resources.getTextResources().getString(TextResources.StringResource.back));
			back.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent event, float x, float y) {
					super.clicked(event, x, y);

					screenChanger.changeScreenTo(ModelScreen.this);
				}
			});
			rootTable.add(back).center().row();

			mainStage.addActor(rootTable);
		}

		private Button createCalibrateButton() {
			Button calibrate = createTextButton(
					Resources.getTextResources().getString(TextResources.StringResource.calibrate));

			calibrate.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					super.clicked(event, x, y);

					separationBuilder = new ModelSeparationBuilder();

					ModelScreen parent = ModelScreen.this;
					parent.mainStage.addActor(createCalibrationUi());
					screenChanger.changeScreenTo(parent);
				}
			});
			return calibrate;
		}

		private Table createCalibrationUi() {
			calibrationUi = new Table();
			calibrationUi.setFillParent(true);

			calibrationUi.add(createPartSelectedButton("head selected", BodyPart.head)).expandX().right().row();
			calibrationUi.add(createPartSelectedButton("torso selected", BodyPart.torso)).expandX().right().row();
			calibrationUi.add(createPartSelectedButton("arms selected", BodyPart.arms)).expandX().right().row();
			calibrationUi.add(createPartSelectedButton("legs selected", BodyPart.legs)).expandX().right().row();

			return calibrationUi;
		}

		private Button createPartSelectedButton(String text, final BodyPart selectedPart) {
			Button partSelected = createTextButton(text);
			partSelected.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					super.clicked(event, x, y);
					ArrayList<Triangle> selected = modelView.getBurnsStorage().getUniqueTriangles();
					separationBuilder.addPart(selectedPart, selected);
					modelView.getBurnsStorage().clear();

					if (separationBuilder.isFilled()) {
						System.out.println("Filled!");
						calibrationUi.remove();
						ModelScreen.this.modelWrapper.setModelSeparation(separationBuilder.build());
					}
				}
			});
			return partSelected;
		}

	}

}