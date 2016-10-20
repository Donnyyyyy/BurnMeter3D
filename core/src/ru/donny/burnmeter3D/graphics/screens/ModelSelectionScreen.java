package ru.donny.burnmeter3D.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

import ru.donny.burnmeter3D.controllers.ApplicationController;
import ru.donny.burnmeter3D.engine.objects.model.ModelChooser;
import ru.donny.burnmeter3D.engine.objects.model.ModelWrapper;
import ru.donny.burnmeter3D.graphics.gui.MaterialUi;
import ru.donny.burnmeter3D.graphics.gui.ScreenArea;
import ru.donny.burnmeter3D.graphics.gui.widgets.ModelPreview;
import ru.donny.burnmeter3D.resources.Resources;
import ru.donny.burnmeter3D.resources.TextResources;

public class ModelSelectionScreen implements Screen {

    private Color backgroundColor = Resources.getDefaultBackgroundColor();

    private MaterialUi ui;
    private ButtonGroup<CheckBox> genderRadioGroup;
    private ButtonGroup<CheckBox> ageRadioGroup;

    private ModelPreview selectedModelPreview;

    /**
     * Describes relations of Model name and its index in the preview
     */
    private Map<String, Integer> modelsMap;
    private ModelChooser modelChooser;

    private ModelChooser.Gender selectedGender = ModelChooser.Gender.Male;
    private boolean isChild = false;

    public ModelSelectionScreen(ModelChooser modelChooser) {
        this.modelChooser = modelChooser;
    }

    @Override
    public void show() {
        initPreview();
        initUi();
        Gdx.input.setInputProcessor(ui);
    }

    private void initUi() {
        ui = new MaterialUi();

        ClickListener updateGenderSelection = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String selectedGender = genderRadioGroup.getChecked().getText().toString();
                ModelSelectionScreen.this.selectedGender = ModelChooser.Gender.valueOf(selectedGender);
                updatePreview();
            }
        };

        ui.startRadioGroup();
        ui.addCheckbox(ModelChooser.Gender.Male.toString(), updateGenderSelection).expandX().left().padLeft(70).padTop(200).row();
        ui.addCheckbox(ModelChooser.Gender.Female.toString(), updateGenderSelection).expandX().left().padLeft(70).row();
        genderRadioGroup = ui.finishRadioGroup();

        ClickListener updateAgeSelection = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String age = ageRadioGroup.getChecked().getText().toString();
                isChild = age.equals(Resources.getTextResources().getString(TextResources.StringResource.child));
                updatePreview();
            }
        };

        ui.startRadioGroup();
        ui.addCheckbox(Resources.getTextResources().getString(TextResources.StringResource.adult), updateAgeSelection)
                .expandX().left().padLeft(70).padTop(100).row();
        ui.addCheckbox(Resources.getTextResources().getString(TextResources.StringResource.child), updateAgeSelection)
                .expandX().left().padLeft(70).row();
        ageRadioGroup = ui.finishRadioGroup();

        ui.addIcon("data/drawable/checked.png", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ApplicationController.makeSelection(modelChooser.choose(selectedGender, isChild));
            }
        }).expandX().expandY().right().bottom().padBottom(70).padRight(70);
    }

    private void initPreview() {
        modelsMap = new HashMap<String, Integer>();
        Array<ModelInstance> aviableModels = new Array<ModelInstance>();
        int index = 0;
        for (ModelWrapper modelWrapper : Resources.getModels()) {
            aviableModels.add(new ModelInstance(modelWrapper.getModel()));

            modelsMap.put(modelWrapper.getName(), index);
            index++;
        }

        float aspectRatio = (float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
        int width = (int) (Gdx.graphics.getWidth() / 1.5f);
        int height = (int) ((float) width * aspectRatio);
        int startX = Gdx.graphics.getWidth() - width;
        int startY = (Gdx.graphics.getHeight() - height) / 2;
        ScreenArea modelViewArea = new ScreenArea(startX, startY, width, height);

        String defaultModelName = modelChooser.choose(selectedGender, isChild);
        int defaultModelIndex = modelsMap.get(defaultModelName);
        selectedModelPreview = new ModelPreview(modelViewArea, aviableModels, defaultModelIndex);
    }

    private void updatePreview() {
        String chosenModel = modelChooser.choose(selectedGender, isChild);

        selectedModelPreview.setSelected(modelsMap.get(chosenModel));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        selectedModelPreview.draw();

        ui.act();
        ui.draw();
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
        selectedModelPreview.dispose();
        ui.dispose();
    }
}
