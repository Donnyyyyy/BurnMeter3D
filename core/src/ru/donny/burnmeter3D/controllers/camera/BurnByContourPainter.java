package ru.donny.burnmeter3D.controllers.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import ru.donny.burnmeter3D.data.Burn.BurnType;
import ru.donny.burnmeter3D.engine.MathEngine;
import ru.donny.burnmeter3D.engine.objects.burns.BurnByContourBuilder;
import ru.donny.burnmeter3D.engine.objects.burns.BurnsStorage;
import ru.donny.burnmeter3D.engine.objects.geometry.objects2d.Point2D;
import ru.donny.burnmeter3D.engine.objects.model.ModelAbstraction;
import ru.donny.burnmeter3D.graphics.gui.views.modelview.BurnSelectionHandler;
import ru.donny.burnmeter3D.graphics.renderable.BurnInstance;
import ru.donny.burnmeter3D.objects.Touching;

public class BurnByContourPainter implements BurnSelectionHandler {

	private ModelAbstraction modelAbstraction;
	private BurnsStorage storage;
	private BurnType selectedType;
	private Touching touching;
	private Camera camera;
	private boolean isPicked;
	private ShapeRenderer renderer;
	private BurnByContourBuilder builder;

	public BurnByContourPainter(ModelAbstraction modelAbstraction, Camera camera) {
		super();
		this.modelAbstraction = modelAbstraction;
		this.camera = camera;

		renderer = new ShapeRenderer();
		builder = new BurnByContourBuilder(camera, modelAbstraction.getGraph(), modelAbstraction);
	}

	@Override
	public void setStorage(BurnsStorage storage) {
		this.storage = storage;
	}

	@Override
	public void setType(BurnType type) {
		selectedType = type;
	}

	@Override
	public void drawPreview() {
		if (touching == null)
			return;

		renderer.begin(ShapeType.Line);
		touching.render(renderer);
		renderer.end();
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
		Point2D pointerPosition = new Point2D(screenX, screenY);
		isPicked = isPicked(pointerPosition);

		if (isPicked) {
			touching = new Touching(pointerPosition, pointer, button, camera);
			touching.setColor(selectedType.getColor());
		}

		return isPicked;
	}

	private boolean isPicked(Point2D point) {
		return MathEngine.getIntersection(modelAbstraction, point, camera) != null;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (isPicked) {
			Point2D pointerPosition = new Point2D(screenX, screenY);
			touching.touchUp(pointerPosition);
			BurnInstance built = builder.build(touching, selectedType);
			storage.add(built);

			touching = null;
			isPicked = false;
		}

		return isPicked;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (isPicked) {
			Point2D pointerPosition = new Point2D(screenX, screenY);
			touching.dragInto(pointerPosition, pointer);
		}

		return isPicked;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
