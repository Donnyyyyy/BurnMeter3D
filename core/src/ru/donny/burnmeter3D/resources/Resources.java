package ru.donny.burnmeter3D.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;

import ru.donny.burnmeter3D.engine.objects.model.ModelWrapper;

public class Resources {

	private static final String[] activeModels = { ModelWrapper.CHILD_MODEL_NAME, ModelWrapper.FEMALE_MODEL_NAME,
			ModelWrapper.HUMAN_MODEL_NAME, ModelWrapper.PRETTY_MODEL_NAME, ModelWrapper.PILOT_MODEL_NAME };

	private static Resources resourcesHolder = new Resources();

	public static Resources getResources() {
		return resourcesHolder;
	}

	public static TextResources getTextResources() {
		return resourcesHolder.strings;
	}

	public static Array<ModelWrapper> getModels() {
		return resourcesHolder.models;
	}

	public static Color getDefaultBackgroundColor() {
		return new Color(96f / 255f, 125f / 255f, 139f / 255f, 0f);
	}

	public static ModelWrapper getModel(String name) {
		for (ModelWrapper modelWrapper : resourcesHolder.models) {
			if (modelWrapper.getName().equals(name))
				return modelWrapper;
		}
		return null;
	}

	public static void init(TextResources textPack) {
		resourcesHolder.strings = textPack;
	}

	public static void startLoading() {
		for (String modelName : activeModels) {
			String modelPath = ModelWrapper.getModelFilePath(modelName);
			resourcesHolder.assetManager.load(modelPath, Model.class);
		}

		resourcesHolder.loadingInProgress = true;
	}

	public static boolean isLoadingInProgress() {
		if (resourcesHolder.loadingInProgress) {
			if (resourcesHolder.assetManager.update()) {
				resourcesHolder.extractModels();
				resourcesHolder.loadingInProgress = false;
			}

		}
		return resourcesHolder.loadingInProgress;
	}

	private void extractModels() {
		resourcesHolder.models = new Array<ModelWrapper>();

		for (String modelName : activeModels) {
			String modelPath = ModelWrapper.getModelFilePath(modelName);
			Model model = resourcesHolder.assetManager.get(modelPath);

			resourcesHolder.models.add(new ModelWrapper(modelName, model));

			Gdx.app.debug("debug", "Loaded " + modelName);
		}

		return;
	}

	private TextResources strings;
	private Array<ModelWrapper> models;

	private AssetManager assetManager = new AssetManager();
	private boolean loadingInProgress = false;

	private Resources() {
	}
}
