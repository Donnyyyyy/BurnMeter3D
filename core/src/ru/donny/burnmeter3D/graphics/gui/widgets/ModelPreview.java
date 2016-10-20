package ru.donny.burnmeter3D.graphics.gui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import ru.donny.burnmeter3D.controllers.Preferences;
import ru.donny.burnmeter3D.graphics.gui.ScreenArea;

public class ModelPreview extends Widget implements Disposable {

    private ModelInstance selectedInstance;
    private ModelBatch modelBatch;
    private Camera camera;
    private Environment environment;

    private Array<ModelInstance> modelsPull;

    public ModelPreview(ScreenArea drawArea, ModelInstance modelInstance) {
        super(drawArea);
        selectedInstance = modelInstance;
        modelBatch = new ModelBatch();
        initEnvironment();
        initCamera();
    }

    public ModelPreview(ScreenArea drawArea, Array<ModelInstance> modelInstance, int selectedInstanceIndex) {
        super(drawArea);
        modelsPull = modelInstance;
        setSelected(selectedInstanceIndex);
        modelBatch = new ModelBatch();
        initEnvironment();
        initCamera();
        validateModels();
    }

    public void setSelected(int index) {
        if (modelsPull != null) {
            selectedInstance = modelsPull.get(index);
        }
    }

    private void initEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.8f, -0.2f));
    }

    private void initCamera() {
        camera = new PerspectiveCamera(Preferences.DEFAULT_CAMERA_FIELD_OF_VIEW, Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

        BoundingBox modelBounds = new BoundingBox();
        selectedInstance.calculateBoundingBox(modelBounds);

        camera.position.set(modelBounds.getCenterX(), modelBounds.getCenterY(), 18f);
        camera.lookAt(modelBounds.getCenterX(), modelBounds.getCenterY(), modelBounds.getCenterZ());
        camera.near = 1f;
        camera.far = 300f;
        camera.update();
    }

    private void validateModels() {
        for (ModelInstance modelInstance : modelsPull) {
            BoundingBox modelBounds = new BoundingBox();
            modelInstance.calculateBoundingBox(modelBounds);

            //translate model to the start
            modelInstance.transform.translate(-((modelBounds.min.x + modelBounds.max.x) / 2f),
                    -modelBounds.min.y,
                    -((modelBounds.min.z + modelBounds.max.z) / 2f));

            modelInstance.calculateTransforms();
        }
    }

    @Override
    protected void onDraw() {
        selectedInstance.transform.rotate(0, 1, 0, .5f);
        selectedInstance.calculateTransforms();

        if (selectedInstance != null) {
            modelBatch.begin(camera);
            modelBatch.render(selectedInstance, environment);
            modelBatch.end();
        }
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
    }
}
