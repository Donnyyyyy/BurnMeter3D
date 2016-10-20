package ru.donny.burnmeter3D.graphics.gui.widgets;

public interface DataInterface {
	
	public void addTag(String tag, String resultType, String units, float defaultValue);
	
	public void setResult(String tag, float value);

	public float getWeight();
}
