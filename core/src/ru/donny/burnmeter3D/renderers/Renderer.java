package ru.donny.burnmeter3D.renderers;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface Renderer {
	
	public void render(ModelBatch modelBatch, Environment environment);
}
