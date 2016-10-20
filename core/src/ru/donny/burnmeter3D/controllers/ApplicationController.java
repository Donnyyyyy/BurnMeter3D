package ru.donny.burnmeter3D.controllers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import ru.donny.burnmeter3D.data.PatientStorage;
import ru.donny.burnmeter3D.data.graphstorage.GraphStorage;
import ru.donny.burnmeter3D.data.graphstorage.GraphStorageProvider;
import ru.donny.burnmeter3D.engine.objects.model.ModelChooser;
import ru.donny.burnmeter3D.engine.objects.model.ModelWrapper;
import ru.donny.burnmeter3D.graphics.screens.LoadingScreen;
import ru.donny.burnmeter3D.graphics.screens.MainMenuScreen;
import ru.donny.burnmeter3D.graphics.screens.ModelScreen;
import ru.donny.burnmeter3D.graphics.screens.ModelSelectionScreen;
import ru.donny.burnmeter3D.graphics.screens.PatientStorageScreen;
import ru.donny.burnmeter3D.graphics.screens.ScreenChangedListener;
import ru.donny.burnmeter3D.resources.Resources;
import ru.donny.burnmeter3D.resources.TextResources;

public class ApplicationController extends Game implements ScreenChangedListener {

    private static ApplicationController controllerInstance;

    private PatientStorage patientStorage;
    private GraphStorageProvider graphStorageProvider;
    private DeviceCameraControl cameraController;

    private ModelWrapper modelWrapper;

    public ApplicationController(PatientStorage patientStorage, GraphStorageProvider graphStorageProvider,
                                 DeviceCameraControl cameraControl, TextResources textResources) {
        this.patientStorage = patientStorage;
        this.graphStorageProvider = graphStorageProvider;
        this.cameraController = cameraControl;
        Resources.init(textResources);

        controllerInstance = this;
    }

    @Override
    public void create() {
        //TODO implement model chooser screen
        setScreen(new LoadingScreen());
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    @Override
    public void dispose() {
        modelWrapper.getModel().dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void changeScreen(@SuppressWarnings("rawtypes") Class screenClass) {
        if (screenClass == ModelScreen.class)
            setScreen(new ModelScreen(modelWrapper, new GraphStorage(graphStorageProvider), modelWrapper.getName(),
                    this, patientStorage, cameraController));
        else if (screenClass == PatientStorageScreen.class)
            setScreen(new PatientStorageScreen(patientStorage, this));
        else if (screenClass == MainMenuScreen.class)
            setScreen(new MainMenuScreen(this, modelWrapper.getModel()));

    }

    @Override
    public void changeScreenTo(Screen screen) {
        setScreen(screen);
    }

    /**
     * Called when {@link Resources} are loaded
     */
    public static void doneLoading() {
        controllerInstance.modelWrapper = Resources.getModel(ModelWrapper.PRETTY_MODEL_NAME);
        controllerInstance.setScreen(new MainMenuScreen(controllerInstance, controllerInstance.modelWrapper.getModel()));
    }

    public static void selectModel() {
        controllerInstance.setScreen(new ModelSelectionScreen(new ModelChooser() {
            @Override
            public String choose(Gender gender, boolean isChild) {
                if (isChild)
                    return ModelWrapper.CHILD_MODEL_NAME;

                if (gender == Gender.Male)
                    return ModelWrapper.PRETTY_MODEL_NAME;
                else
                    return ModelWrapper.FEMALE_MODEL_NAME;
            }

            @Override
            public String getDefault() {
                return ModelWrapper.PRETTY_MODEL_NAME;
            }
        }));
    }

    public static void makeSelection(String modelName) {
        ModelWrapper model = Resources.getModel(modelName);

        controllerInstance.setScreen(new ModelScreen(model, new GraphStorage(controllerInstance.graphStorageProvider),
                model.getName(), controllerInstance, controllerInstance.patientStorage, controllerInstance.cameraController));
    }
}
