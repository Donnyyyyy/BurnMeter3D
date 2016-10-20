package ru.donny.burnmeter3D.graphics.screens;

import com.badlogic.gdx.Screen;

public interface ScreenChangedListener {
	public void changeScreen(@SuppressWarnings("rawtypes") Class screenClass);

	public void changeScreenTo(Screen screen);
}
