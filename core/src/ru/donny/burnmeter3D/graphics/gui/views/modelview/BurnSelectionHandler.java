package ru.donny.burnmeter3D.graphics.gui.views.modelview;

import com.badlogic.gdx.InputProcessor;

import ru.donny.burnmeter3D.data.Burn.BurnType;
import ru.donny.burnmeter3D.engine.objects.burns.BurnsStorage;

public interface BurnSelectionHandler extends InputProcessor {

	public void setStorage(BurnsStorage storage);

	public void setType(BurnType type);

	public void drawPreview();
}
