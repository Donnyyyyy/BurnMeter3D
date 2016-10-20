package ru.donny.burnmeter3D.controllers.camera;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;

import ru.donny.burnmeter3D.engine.MathEngine;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.engine.objects.geometry.objects2d.Point2D;
import ru.donny.burnmeter3D.engine.objects.geometry.point.ModelIntersection;
import ru.donny.burnmeter3D.engine.objects.model.ModelAbstraction;
import ru.donny.burnmeter3D.engine.objects.model.graph.Graph;
import ru.donny.burnmeter3D.graphics.renderable.DynamicPolygonSet;
import ru.donny.burnmeter3D.objects.Pair;

public class HoverController extends InputAdapter {

	private static final float CRITICAL_RANGE = 7f;

	protected int lightingRange;
	private DynamicPolygonSet hoveredRenderable;
	private HashSet<Triangle> hovered = new HashSet<Triangle>();
	protected ModelAbstraction modelAbstraction;
	protected Graph graph;
	protected Camera camera;
	private Color color;

	public HoverController(Camera camera, ModelAbstraction modelAbstraction, Graph graph, int lightingRange,
			Color color) {
		super();
		this.modelAbstraction = modelAbstraction;
		this.camera = camera;
		this.graph = graph;
		this.lightingRange = lightingRange;

		hoveredRenderable = new DynamicPolygonSet(color, new ArrayList<Triangle>());
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		updateHoveredArea(new Point2D(screenX, screenY));
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		updateHoveredArea(new Point2D(screenX, screenY));
		return false;
	}

	private void updateHoveredArea(Point2D screenPoint) {
		// updateHoveredTrianglesByGraph(screenPoint);
		updateHoveredTrianglesByModelAbstraction(screenPoint);
		hoveredRenderable.update(hovered);
	}

	@SuppressWarnings("unused")
	private void updateHoveredTrianglesByGraph(Point2D screenPoint) {
		ModelIntersection modelIntersection = MathEngine.getIntersection(modelAbstraction, screenPoint, camera);

		if (modelIntersection != null) {
			hovered = new HashSet<Triangle>(modelAbstraction
					.getTriangles(graph.getTrianglesInRange(modelIntersection.getTriangle().getId(), lightingRange)));
		} else {
			hovered.clear();
		}
	}

	private void updateHoveredTrianglesByModelAbstraction(Point2D screenPoint) {
		hovered.clear();
		screenPoint.setY(Gdx.graphics.getHeight() - screenPoint.getY());
		ArrayList<Pair<Integer, Float>> sortedRanges = MathEngine.computeRanges(screenPoint,
				modelAbstraction.getScreenProjections());

		if (sortedRanges.size() == 0) {
			System.out.println("Something goes wrong with ranges (screen - model projections) computing");
			return;
		}

		if (sortedRanges.get(0).getB() > CRITICAL_RANGE)
			return;

		for (int i = 0; i < lightingRange; i++) {
			hovered.add(modelAbstraction.get(sortedRanges.get(i).getA()));
		}
	}

	public DynamicPolygonSet getRenderingTriangles() {
		return hoveredRenderable;
	}

	public HashSet<Triangle> getHovered() {
		return hovered;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		hoveredRenderable.setColor(color);
	}
}
