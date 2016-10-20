package ru.donny.burnmeter3D.graphics.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import ru.donny.burnmeter3D.controllers.Preferences;

public abstract class RussianFont{

	private static BitmapFont instance = createFont();

	private static BitmapFont createFont(){
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/skins/russian.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.characters = FONT_CHARS;
		parameter.size = Gdx.graphics.getWidth() / 40;
		parameter.color = Preferences.FONT_COLOR;
		BitmapFont russianFont = generator.generateFont(parameter);
		generator.dispose();
		return russianFont;
	}

	public static final String FONT_CHARS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";

	public static BitmapFont get() {
		return instance;
	}
}
