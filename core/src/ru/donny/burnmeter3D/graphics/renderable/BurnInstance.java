package ru.donny.burnmeter3D.graphics.renderable;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import ru.donny.burnmeter3D.data.Burn.BurnType;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;

public class BurnInstance extends ModelInstance {

	private ArrayList<Triangle> burnTriangles;
	private BurnType type;
	private Float square = null;

	public BurnInstance(Model model, ArrayList<Triangle> burnTriangles, BurnType degree) {
		super(model);
		this.setBurnTriangles(burnTriangles);
		setType(degree);
	}

	public BurnInstance(ModelInstance copyFrom) {
		super(copyFrom);
	}

	public BurnInstance(Model model, String... rootNodeIds) {
		super(model, rootNodeIds);
	}

	public BurnInstance(Model model, Array<String> rootNodeIds) {
		super(model, rootNodeIds);
	}

	public BurnInstance(Model model, Vector3 position) {
		super(model, position);
	}

	public BurnInstance(Model model, Matrix4 transform) {
		super(model, transform);
	}

	public BurnInstance(ModelInstance copyFrom, Matrix4 transform) {
		super(copyFrom, transform);
	}

	public BurnInstance(Model model, String nodeId, boolean mergeTransform) {
		super(model, nodeId, mergeTransform);
	}

	public BurnInstance(Model model, Matrix4 transform, String... rootNodeIds) {
		super(model, transform, rootNodeIds);
	}

	public BurnInstance(Model model, Matrix4 transform, Array<String> rootNodeIds) {
		super(model, transform, rootNodeIds);
	}

	public BurnInstance(ModelInstance copyFrom, Matrix4 transform, boolean shareKeyframes) {
		super(copyFrom, transform, shareKeyframes);
	}

	public BurnInstance(Model model, Matrix4 transform, String nodeId, boolean mergeTransform) {
		super(model, transform, nodeId, mergeTransform);
	}

	public BurnInstance(Model model, String nodeId, boolean parentTransform, boolean mergeTransform) {
		super(model, nodeId, parentTransform, mergeTransform);
	}

	public BurnInstance(Model model, Matrix4 transform, Array<String> rootNodeIds, boolean shareKeyframes) {
		super(model, transform, rootNodeIds, shareKeyframes);
	}

	public BurnInstance(Model model, float x, float y, float z) {
		super(model, x, y, z);
	}

	public BurnInstance(Model model, Matrix4 transform, String nodeId, boolean parentTransform,
			boolean mergeTransform) {
		super(model, transform, nodeId, parentTransform, mergeTransform);
	}

	public BurnInstance(Model model, String nodeId, boolean recursive, boolean parentTransform,
			boolean mergeTransform) {
		super(model, nodeId, recursive, parentTransform, mergeTransform);
	}

	public BurnInstance(Model model, Matrix4 transform, String nodeId, boolean recursive, boolean parentTransform,
			boolean mergeTransform) {
		super(model, transform, nodeId, recursive, parentTransform, mergeTransform);
	}

	public BurnInstance(Model model, Matrix4 transform, String nodeId, boolean recursive, boolean parentTransform,
			boolean mergeTransform, boolean shareKeyframes) {
		super(model, transform, nodeId, recursive, parentTransform, mergeTransform, shareKeyframes);
	}

	private void calculateSquare() {
		for (Triangle i : burnTriangles)
			square += i.getSquare();
	}

	public ArrayList<Triangle> getBurnTriangles() {
		return burnTriangles;
	}

	public float getSquare() {
		if (square == null)
			calculateSquare();

		return square;
	}

	public void setBurnTriangles(ArrayList<Triangle> burnTriangles) {
		this.burnTriangles = burnTriangles;
	}

	public BurnType getType() {
		return type;
	}

	public void setType(BurnType type) {
		this.type = type;
	}

}
