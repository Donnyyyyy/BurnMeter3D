package ru.donny.burnmeter3D.renderers;

import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class PointRenderer implements Renderer {

	public static final Vector3 SCALE = new Vector3(0.1f, 0.1f, 0.1f);

	private ArrayList<? extends Vector3> renderPoints;
	private Model sphere;
	private Array<ModelInstance> instances;
	private Color color;

	public PointRenderer(AssetManager assetManager) {
		loadModel("data/sphere/Sphere.g3db", assetManager);
	}

	private void loadModel(String path, AssetManager assetManager) {
		assetManager.load(path, Model.class);
		assetManager.finishLoading();
		sphere = assetManager.get(path, Model.class);
	}

	private void createModelInstances() {
		instances = new Array<ModelInstance>();
		for (Vector3 i : renderPoints) {
			ModelInstance sphereInstance = new ModelInstance(sphere);
			sphereInstance.transform.setToTranslationAndScaling(i, SCALE);
			sphereInstance.materials.get(0).set(ColorAttribute.createDiffuse(color));
			instances.add(sphereInstance);
		}
	}

	@Override
	public void render(ModelBatch modelBatch, Environment environment) {
		modelBatch.render(instances, environment);
	}

	public ArrayList<? extends Vector3> getRenderPoints() {
		return renderPoints;
	}

	public void setRenderPoints(ArrayList<? extends Vector3> renderPoints) {
		this.renderPoints = renderPoints;
		createModelInstances();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
