package ru.donny.burnmeter3D.controllers.camera;

import java.util.HashSet;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import ru.donny.burnmeter3D.data.Burn.BurnType;
import ru.donny.burnmeter3D.engine.objects.burns.BurnsStorage;
import ru.donny.burnmeter3D.engine.objects.geometry.Triangle;
import ru.donny.burnmeter3D.engine.objects.model.ModelAbstraction;
import ru.donny.burnmeter3D.engine.objects.model.graph.Graph;
import ru.donny.burnmeter3D.graphics.gui.views.modelview.BurnSelectionHandler;
import ru.donny.burnmeter3D.graphics.renderable.BurnInstance;
import ru.donny.burnmeter3D.graphics.renderable.BurnModelBuilder;
import ru.donny.burnmeter3D.graphics.renderable.DynamicPolygonSet;

public class BurnPainter extends HoverController implements BurnSelectionHandler {

	private BurnsStorage burnStorage;
	private BurnType burnType;
	private HashSet<Triangle> burnTriangles = new HashSet<Triangle>();
	private DynamicPolygonSet burnPreview;
	private ModelBatch renderer;
	private boolean isInDrawingState = false;

	public BurnPainter(Camera camera, ModelAbstraction modelAbstraction, Graph graph, int lightingRange) {
		super(camera, modelAbstraction, graph, lightingRange, Color.RED);
		this.renderer = new ModelBatch();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		super.touchDown(screenX, screenY, pointer, button);
		if (getHovered().size() > 0) {
			startBurnSelection();
			return true;
		} else
			return false;
	}

	private void startBurnSelection() {
		burnTriangles.addAll(getHovered());
		burnPreview = new DynamicPolygonSet(burnType.getColor(), burnTriangles);
		isInDrawingState = true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		super.touchDragged(screenX, screenY, pointer);
		if (isInDrawingState) {
			updateBurnSelection();
			return true;
		} else
			return false;
	}

	private void updateBurnSelection() {
		burnTriangles.addAll(getHovered());
		updateRenderable();
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		super.touchUp(screenX, screenY, pointer, button);
		if (isInDrawingState) {
			finishBurnSelection();
			return true;
		} else
			return false;
	}

	private void finishBurnSelection() {
		storeBurn();
		cleanPreview();
		isInDrawingState = false;
	}

	// FIXME replace this algorithm because of each-time renderable
	// full-rebuilding.
	private void updateRenderable() {
		burnPreview = new DynamicPolygonSet(burnType.getColor(), burnTriangles);
	}

	private void storeBurn() {
		BurnInstance burnInstance = BurnModelBuilder.build(burnTriangles, burnType);
		burnStorage.add(burnInstance);
	}

	private void cleanPreview() {
		burnTriangles.clear();
		burnPreview = null;
	}

	@Override
	public void setStorage(BurnsStorage storage) {
		this.burnStorage = storage;
	}

	@Override
	public void setType(BurnType type) {
		this.burnType = type;
		setColor(burnType.getColor());
	}

	@Override
	public void drawPreview() {
		renderer.begin(camera);
		renderer.render(isInDrawingState ? burnPreview : getRenderingTriangles());
		renderer.end();
	}
}
