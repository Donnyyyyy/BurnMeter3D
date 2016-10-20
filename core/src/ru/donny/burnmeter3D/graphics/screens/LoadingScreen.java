package ru.donny.burnmeter3D.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Map;

import ru.donny.burnmeter3D.controllers.ApplicationController;
import ru.donny.burnmeter3D.resources.Resources;

public class LoadingScreen implements Screen {

    Texture logo;
    Batch batch;

    public LoadingScreen() {
    }

    @Override
    public void show() {
        Resources.startLoading();
        batch = new SpriteBatch();
        logo = new Texture("data/drawable/logo.png");
    }


    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(logo, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        if (!Resources.isLoadingInProgress())
            ApplicationController.doneLoading();
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
        batch.dispose();
        logo.dispose();
    }
}
