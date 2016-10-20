package ru.donny.burnmeter3D.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

import ru.donny.burnmeter3D.controllers.ApplicationController;
import ru.donny.burnmeter3D.graphics.gui.KeywordListener;
import ru.donny.burnmeter3D.graphics.gui.KeywordListener.Callback;
import ru.donny.burnmeter3D.graphics.gui.MaterialUi;
import ru.donny.burnmeter3D.graphics.gui.ScreenArea;
import ru.donny.burnmeter3D.graphics.gui.widgets.ModelPreview;
import ru.donny.burnmeter3D.resources.Resources;

public class MainMenuScreen implements Screen {

    private Color backgroundColor = Resources.getDefaultBackgroundColor();
    private ArrayList<KeywordListener> hiddenToolsListeners = new ArrayList<KeywordListener>();
    private ScreenChangedListener screenChangedListener;

    private MaterialUi ui;
    private ModelPreview modelBackground;
    private Model model;

    public MainMenuScreen(ScreenChangedListener screenChangedListener, Model backgroundModel) {
        this.screenChangedListener = screenChangedListener;
        this.model = backgroundModel;
    }

    public void openStorage() {
        screenChangedListener.changeScreen(PatientStorageScreen.class);
    }

    @Override
    public void show() {
        initHiddenTools();
        initGui();
        initModelBackgroud();
        Gdx.input.setInputProcessor(ui);
    }

    private void initGui() {
        ui = new MaterialUi();

        ui.addTextButton("New patient", new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ApplicationController.selectModel();
            }
        }).expandX().right().padRight(70).row();

        ui.addTextButton("Storage", new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openStorage();
            }
        }).expandX().right().padRight(70);
    }

    private void initModelBackgroud() {
        float aspectRatio = (float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
        int width = (int) (Gdx.graphics.getWidth() / 1.5f);
        int height = (int) ((float) width * aspectRatio);

        int startX = 0;
        int startY = (Gdx.graphics.getHeight() - height) / 2;

        ScreenArea modelViewArea = new ScreenArea(startX, startY, width, height);
        modelBackground = new ModelPreview(modelViewArea, new ModelInstance(model));
    }

    private void initHiddenTools() {
        KeywordListener bulletTestListener = new KeywordListener(400, "bullet", new Callback() {

            @Override
            public void actionPerformed() {
                screenChangedListener.changeScreenTo(new CollisionDetectionTestScreen());
            }
        });

        hiddenToolsListeners.add(bulletTestListener);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);

        ui.act();
        ui.draw();
        modelBackground.draw();
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
    public void hide() {
    }

    @Override
    public void dispose() {
        modelBackground.dispose();
        model.dispose();
    }
}
