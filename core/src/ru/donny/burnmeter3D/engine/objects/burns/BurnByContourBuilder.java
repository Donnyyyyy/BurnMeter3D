package ru.donny.burnmeter3D.engine.objects.burns;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.collision.BoundingBox;

import ru.donny.burnmeter3D.controllers.Logger;
import ru.donny.burnmeter3D.data.Burn.BurnType;
import ru.donny.burnmeter3D.engine.MathEngine;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.engine.objects.model.ModelAbstraction;
import ru.donny.burnmeter3D.engine.objects.model.graph.Graph;
import ru.donny.burnmeter3D.graphics.renderable.BurnInstance;
import ru.donny.burnmeter3D.graphics.renderable.BurnModelBuilder;
import ru.donny.burnmeter3D.objects.Touching;

public class BurnByContourBuilder {

	private Camera camera;
	private Graph modelGraph;
	private ModelAbstraction modelAbstraction;

	private boolean rememberDetachedAreas = false;
	private ArrayList<BurnInstance> detachedAreas;

	public BurnByContourBuilder(Camera camera, Graph modelGraph, ModelAbstraction modelAbstraction) {
		super();
		this.camera = camera;
		this.modelGraph = modelGraph;
		this.modelAbstraction = modelAbstraction;
	}

	public BurnInstance build(Touching screenArea, BurnType degree) {
		Logger.printTimeMessage("Start burn calculation");
		ArrayList<Triangle> burnTriangles = detrmineSelectedArea(screenArea);

		return BurnModelBuilder.build(burnTriangles, degree.getColor());
	}

	private ArrayList<Triangle> detrmineSelectedArea(Touching screenArea) {
		HashSet<Integer> innerTrianglesId = null;

		MathEngine intersector = new MathEngine();
		ArrayList<Triangle> intersectedTriangles = intersector.getIntersectedTriangles(modelAbstraction, screenArea.getAllPoints(),
				camera);
		Logger.printTimeMessage("intersections calculated");

		ArrayList<Triangle> selectedTriangles = getSelectedPart(intersectedTriangles, innerTrianglesId,
				new ArrayList<Triangle>(intersector.getExtremeTriangles()), screenArea, camera);
		Logger.printTimeMessage("path calculated");

		return selectedTriangles;
	}

	private ArrayList<Triangle> getSelectedPart(ArrayList<Triangle> intersectedTriangles,
			HashSet<Integer> innerTriangles, ArrayList<Triangle> extremeTriangles, Touching touching,
			Camera projector) {
		if (intersectedTriangles.size() <= 0)
			return new ArrayList<Triangle>();

		ArrayList<Triangle> selectedArea = getSelectionBorders(intersectedTriangles, innerTriangles, extremeTriangles,
				touching, projector);
		Logger.printTimeMessage("Borders calculated");

		selectedArea.addAll(fillArea(selectedArea));
		Logger.printTimeMessage("Insides calculated");

		return selectedArea;
	}

	private ArrayList<Triangle> getSelectionBorders(ArrayList<Triangle> borderTriangles,
			HashSet<Integer> innerTriangles, ArrayList<Triangle> extremeTriangles, Touching touching,
			Camera projector) {
		HashSet<Triangle> selectionBorders = new HashSet<Triangle>();

		modelGraph.setActiveVertices(innerTriangles);

		// Adding borders of the selection.
		int startId;
		int endId;
		ArrayList<Integer> particalBorderIdPath;
		for (int i = 0; i < borderTriangles.size() - 1; i++) {
			startId = borderTriangles.get(i).getId();
			endId = borderTriangles.get(i + 1).getId();

			particalBorderIdPath = modelGraph.getPath(startId, endId);
			selectionBorders.addAll(modelAbstraction.getTriangles(particalBorderIdPath));
		}

		// compound start & end
		startId = borderTriangles.get(0).getId();
		endId = borderTriangles.get(borderTriangles.size() - 1).getId();
		particalBorderIdPath = modelGraph.getPath(startId, endId);
		selectionBorders.addAll(modelAbstraction.getTriangles(particalBorderIdPath));

		return new ArrayList<Triangle>(selectionBorders);
	}

	@SuppressWarnings("unused")
	private ArrayList<Triangle> compoundExtremeTriangles(ArrayList<Triangle> extremeTriangles, Triangle start,
			Triangle end, Touching touching, Camera projector) {
		if (extremeTriangles == null) {
			ArrayList<Integer> startEndIdPath = modelGraph.getPath(start.getId(), end.getId());
			return modelAbstraction.getTriangles(startEndIdPath);
		}
		if (extremeTriangles.size() == 0) {
			ArrayList<Integer> startEndIdPath = modelGraph.getPath(start.getId(), end.getId());
			return modelAbstraction.getTriangles(startEndIdPath);
		}
		ArrayList<Integer> startEndIdPath = modelGraph.getPath(start.getId(), end.getId());
		return modelAbstraction.getTriangles(startEndIdPath);
	}

	private ArrayList<Triangle> fillArea(ArrayList<Triangle> circle) {
		if (circle.size() == 1)
			return new ArrayList<Triangle>();
		HashSet<Integer> selectionBorders = new HashSet<Integer>();
		for (Triangle i : circle)
			selectionBorders.add(i.getId());

		ArrayList<HashSet<Integer>> deatachedAreas = modelGraph.getDetachedAreas(selectionBorders);

		if (isRememberDetachedAreas())
			this.detachedAreas.clear();

		ArrayList<Triangle> circleFilling = new ArrayList<Triangle>();
		for (HashSet<Integer> i : deatachedAreas)
			circleFilling.addAll(fillDetachedArea(new ArrayList<Integer>(i)));

		return circleFilling;
	}

	private ArrayList<Triangle> fillDetachedArea(ArrayList<Integer> detachedArea) {
		ArrayList<Triangle> detachedTriangles = modelAbstraction.getTriangles(detachedArea);
		BoundingBox boundingBox = MathEngine.constructBoundingBox(detachedTriangles);

		if (isRememberDetachedAreas()) {
			detachedAreas.add(BurnModelBuilder.build(detachedTriangles, new Color((float) (0.5f + Math.random() * 0.5),
					(float) (0.5f + Math.random() * 0.5), (float) (0.5f + Math.random() * 0.5), 1f)));
		}

		if (boundingBox.max.equals(modelAbstraction.getBounds().max)
				|| boundingBox.min.equals(modelAbstraction.getBounds().min))
			return new ArrayList<Triangle>();
		else
			return detachedTriangles;
	}

	public boolean isRememberDetachedAreas() {
		return rememberDetachedAreas;
	}

	public void setRememberDetachedAreas(boolean rememberDetachedAreas, ArrayList<BurnInstance> detachedAreasStorage) {
		this.rememberDetachedAreas = rememberDetachedAreas;
		this.detachedAreas = detachedAreasStorage;
	}

}
