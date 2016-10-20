package ru.donny.burnmeter3D.graphics.gui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ResultLabel extends Label {
	
	private String type;
	private String units;
	private float defaultValue;
	private float value;
	
	public ResultLabel(Skin skin, String type, String units, float defaultValue, BitmapFont font, Color color) {
		super(type + ": " + defaultValue + units, skin);
		getStyle().font = font;
		getStyle().fontColor = color;
		this.type = type;
		this.units = units;
		this.defaultValue = defaultValue;
	}

	public ResultLabel(LabelStyle style, String type, String units, float defaultValue) {
		super(type + ": " + defaultValue + units, style);
		this.type = type;
		this.units = units;
		this.defaultValue = defaultValue;
	}

	public ResultLabel(CharSequence text, Skin skin, String styleName) {
		super(text, skin, styleName);
	}

	public ResultLabel(CharSequence text, Skin skin, String fontName, Color color) {
		super(text, skin, fontName, color);
	}

	public ResultLabel(CharSequence text, Skin skin, String fontName, String colorName) {
		super(text, skin, fontName, colorName);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public float getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(float defaultValue) {
		this.defaultValue = defaultValue;
	}

	public float getValue() {
		return value;
	}

	public void setResult(float value) {
		this.value = value;
		setText(type + ": " + value + units);
	}
	
}
