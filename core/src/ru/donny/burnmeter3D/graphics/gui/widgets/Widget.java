package ru.donny.burnmeter3D.graphics.gui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.geom.Dimension2D;

import ru.donny.burnmeter3D.graphics.gui.ScreenArea;

public abstract class Widget {

    private ScreenArea drawArea;

    public Widget(ScreenArea drawArea) {
        this.drawArea = drawArea;
    }

    public final void draw() {
        Gdx.gl.glViewport(drawArea.startX, drawArea.startY, drawArea.width, drawArea.height);
        onDraw();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    protected abstract void onDraw();

    public ScreenArea getDrawArea() {
        return drawArea;
    }


}
